/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: JFieldDeclaration.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.JavadocComment;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a java class in the syntax tree
 */
public class JFieldDeclaration extends JMemberDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   *
   * @param	where		the line of this node in the source code
   * @param	variable	the variable definition
   * @param	javadoc		is this field deprecated
   * @param	comments	comments in the source text
   */
  public JFieldDeclaration(TokenReference where,
			   JVariableDefinition variable,
			   JavadocComment javadoc,
			   JavaStyleComment[] comments)
  {
    super(where, javadoc, comments);

    this.variable = variable;
    this.synthetic = false;
  }

  /**
   * Construct a node in the parsing tree
   *
   * @param	where		the line of this node in the source code
   * @param	variable	the variable definition
   * @param	sysntehic	true iff it is  synthec field
   * @param	javadoc		is this field deprecated
   * @param	comments	comments in the source text
   */
  public JFieldDeclaration(TokenReference where,
			   JVariableDefinition variable,
                           boolean synthetic,
			   JavadocComment javadoc,
			   JavaStyleComment[] comments)
  {
    super(where, javadoc, comments);

    this.variable = variable;
    this.synthetic = synthetic;
  }
  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * Returns true if this field declarator has initializer (should be initialized)
   */
  public boolean hasInitializer() {
    return variable.hasInitializer();
  }

  /**
   * Returns the type of this field
   */
  public CType getType(TypeFactory factory) {
    return variable.getType();
  }

  /**
   * Returns true if this field need to be initialized
   * WARNING: this method return true when initial value corresponds to a default value
   * ====> a second check should be made after calling "analyse" to ensure that
   * an initialization is really needed
   */
  public boolean needInitialization() {
    return hasInitializer();
  }

  public JVariableDefinition getVariable() {
    return variable;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   *  sub classes must check modifiers and call checkInterface(super)
   * @param v a vector to collect fields
   * @return true iff sub tree is correct enought to check code
   * @exception	PositionedError	an error with reference to the source file
   */
  public CSourceField checkInterface(CClassContext context) throws PositionedError {
    int		modifiers = variable.getModifiers();
    TypeFactory factory = context.getTypeFactory();

    classContext = context;

    /* JLS 8.3.1
       A compile-time error occurs if a final variable is also declared volatile. */
    check(context,
          CModifier.getSubsetSize(modifiers,
                                  Constants.ACC_VOLATILE
                                  | Constants.ACC_FINAL) <= 1,
          KjcMessages.FIELD_VOLATILE_FINAL,
          variable.getIdent());

    if (! context.getCClass().isInterface()) {
      // JLS 8.3.1 : Class Field Modifiers

      // Syntactically valid field modifiers
      check(context,
	    CModifier.isSubsetOf(modifiers,
				 ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE
				 | ACC_STATIC | ACC_FINAL | ACC_TRANSIENT
				 | ACC_VOLATILE),
	    KjcMessages.NOT_CLASS_FIELD_MODIFIERS,
	    CModifier.toString(CModifier.notElementsOf(modifiers,
						       ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE
						       | ACC_STATIC | ACC_FINAL | ACC_TRANSIENT
						       | ACC_VOLATILE)));
    } else {
      // JLS 9.3 : Interface Field (Constant) Declarations

      // Every field declaration in the body of an interface is
      // implicitly public, static, and final.
      modifiers |= ACC_PUBLIC | ACC_STATIC | ACC_FINAL;

      // Syntactically valid interface field modifiers
      check(context,
	    CModifier.isSubsetOf(modifiers, ACC_PUBLIC | ACC_FINAL | ACC_STATIC),
	    KjcMessages.NOT_INTERFACE_FIELD_MODIFIERS,
	    CModifier.toString(CModifier.notElementsOf(modifiers,
						       ACC_PUBLIC | ACC_FINAL | ACC_STATIC)));
    }
    variable.checkInterface(context);
    setInterface(new CSourceField(context.getCClass(),
				  modifiers,
				  variable.getIdent(),
				  variable.getType(),
				  isDeprecated(),
                                  synthetic)); // not synthetic

    return (CSourceField)getField();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    TypeFactory factory = context.getTypeFactory();

    variable.analyse(context);
    // JLS 8.1.2 : Inner classes may inherit static members that
    // are not compile-time constants even though they may not declare
    // them. Inner classes may not declare static members, unless
    // they are compile-time constant fields
    if (!context.getClassContext().getCClass().canDeclareStatic()
        && getField().isStatic()
        && !getField().isSynthetic()) {
      check(context,
	    variable.isConstant(),
	    KjcMessages.INNER_DECL_STATIC,
            getField().getIdent());
    }
    //JSR 41 Tht type of a static field can't be a type variable
    check(context,
          !variable.isStatic() || !variable.getType().isTypeVariable(),
          KjcMessages.TV_STATIC_FIELD,
          getField().getIdent());


    if (hasInitializer() && getField().isFinal()) {
      JExpression	value = variable.getValue();

      if (value.isConstant()) {
	getField().setValue(value.getLiteral());

	if (! getField().isStatic()) {
	  context.reportTrouble(new CWarning(getTokenReference(),
					     KjcMessages.FINAL_FIELD_IMPLICITLY_STATIC,
					     getField().getIdent()));
	}
      }
    }

    if (hasInitializer()) {
      context.setFieldInfo(((CSourceField)getField()).getPosition(), CVariableInfo.INITIALIZED);
    }

    if (! (getField().isPublic() || getField().isPrivate())) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.PACKAGE_PROTECTED_ATTRIBUTE,
					 getField().getIdent()));
    }
  }


  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    super.accept(p);

    p.visitFieldDeclaration(this,
			    variable.getModifiers(),
			    variable.getType(),
			    variable.getIdent(),
			    variable.getValue());
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    if (variable.getValue() != null) {
      setLineNumber(code);

      if (!getField().isStatic()) {
	code.plantLoadThis();
      }
      variable.getValue().genCode(context, false);
      getField().genStore(context);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private CClassContext                         classContext;
  private boolean                               synthetic;
  protected JVariableDefinition                 variable;
  public static final JFieldDeclaration[]       EMPTY = new JFieldDeclaration[0];
}
