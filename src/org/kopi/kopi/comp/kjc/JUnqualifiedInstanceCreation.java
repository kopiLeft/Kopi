/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
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
 * $Id$
 */

package org.kopi.kopi.comp.kjc;

import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;

/**
 * JLS 15.9 Class Instance Creation Expressions.
 *
 * This class represents an unqualified class instance creation expression.
 */
public class JUnqualifiedInstanceCreation extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	type		the type of the object to be created
   * @param	params		parameters to be passed to constructor
   */
  public JUnqualifiedInstanceCreation(TokenReference where,
				      CReferenceType type,
				      JExpression[] params)
  {
    super(where);

    this.type = type;
    this.params = params;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  /**
   * Returns true iff this expression can be used as a statement (JLS 14.8)
   */
  public boolean isStatementExpression() {
    return true;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    local = context.getClassContext().getCClass();

    // JLS 15.9.1 Determining the Class being Instantiated

    // If the class instance creation expression is an unqualified class
    // instance creation expression, then the ClassOrInterfaceType must name
    // a class that is accessible and not abstract, or a compile-time error
    // occurs. In this case, the class being instantiated is the class
    // denoted by ClassOrInterfaceType.
    try {
      type = (CReferenceType)type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    check(context, !type.isTypeVariable(), KjcMessages.NEW_TVPE_VARIABLE, type);
    check(context, !type.getCClass().isAbstract(), KjcMessages.NEW_ABSTRACT, type);
    check(context, !type.getCClass().isInterface(), KjcMessages.NEW_INTERFACE, type);
    check(context, 
          type.getCClass().isAccessible(local),
          KjcMessages.CLASS_NOACCESS, 
          type.getCClass());

    /////////////////////////////////////////////////////////////////////////

    CType[] argsType = new CType[params.length];

    for (int i = 0; i < argsType.length; i++) {
      params[i] = params[i].analyse(context);
      argsType[i] = params[i].getType(factory);
      verify(argsType[i] != null);
    }

    //!!! review and create test cases
    context = new CExpressionContext(context, context.getEnvironment());
    
    try {
      constructor = type.getCClass().lookupMethod(context, local, null, JAV_CONSTRUCTOR, argsType, type.getArguments());
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    
    if (constructor == null || constructor.getOwner() != type.getCClass()) {
      // do not want a super constructor !
      throw new CMethodNotFoundError(getTokenReference(), null, type.toString(), argsType);
    }

    // check access
    check(context, constructor.isAccessible(local), KjcMessages.CONSTRUCTOR_NOACCESS, type);
    // JLS 6.6.2.2  Qualified Access to a protected constructor
    // very special case for protected constructors
    check(context, 
          !constructor.isProtected() || constructor.getOwner().getPackage() == local.getPackage(), 
          KjcMessages.CONSTRUCTOR_NOACCESS, 
          type);


    // deprecated? 
    KjcEnvironment      environment = context.getEnvironment();

    if (constructor.isDeprecated()) {
      if (environment.showDeprecated()) {  
        context.reportTrouble(new CWarning(getTokenReference(),
                                           KjcMessages.USE_DEPRECATED_CONSTRUCTOR,
                                           type.getCClass().getJavaName()));
      }
      environment.setDeprecatedUsed();
    }

    if (constructor.getOwner().isNested()) {
      check(context, 
            !constructor.getOwner().hasOuterThis() 
            || (!context.isStaticContext()
                && (inCorrectOuter(local, constructor.getOwner().getOwner()))),
	    KjcMessages.INNER_INHERITENCE, constructor.getOwnerType(), local.getAbstractType());
      if (constructor.getOwner().hasOuterThis() && !local.descendsFrom(constructor.getOwner().getOwner())) {
        CClass          itsOuterOfThis = local;

        while (!itsOuterOfThis.getOwner().descendsFrom(constructor.getOwner().getOwner())) {
          itsOuterOfThis = itsOuterOfThis.getOwner();
        }
        // anlayse creates accessor(s) to the correct this$0 field
        outerPrefix = new JFieldAccessExpression(getTokenReference(), 
                                                 new JOwnerExpression(getTokenReference(),itsOuterOfThis), 
                                                 JAV_OUTER_THIS).analyse(context);
      }

    }

    CReferenceType[]	exceptions = constructor.getThrowables();
    for (int i = 0; i < exceptions.length; i++) {
      context.getBodyContext().addThrowable(new CThrowableInfo(exceptions[i], this));
    }

    argsType = constructor.getParameters();

    for (int i = 0; i < params.length; i++) {
      params[i] = params[i].convertType(context, argsType[i]);
    }

    return this;
  }

  private boolean inCorrectOuter(CClass local, CClass outer) {
    while (local != null) {
      if (local.descendsFrom(outer)) {
        return true;
      }
      local = local.getOwner();
    }
    return false;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitUnqualifiedInstanceCreation(this, type, params);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.plantClassRefInstruction(opc_new, type.getCClass().getQualifiedName());

    if (!discardValue) {
      code.plantNoArgInstruction(opc_dup);
    }

    if (constructor.getOwner().isNested() 
        && !constructor.getOwner().isStatic()
        && constructor.getOwner().hasOuterThis()) {
      // inner class
      if (outerPrefix == null && !(local.getOwner() != null && local.getOwner().descendsFrom(constructor.getOwner().getOwner()))) {
        code.plantLoadThis();
      } else {
        // create inner class in inner class
        if (outerPrefix == null) {
          code.plantLoadThis();
          code.plantFieldRefInstruction(opc_getfield,
                                        local.getAbstractType().getSignature().substring(1, local.getAbstractType().getSignature().length() - 1),
                                        JAV_OUTER_THIS,
                                        local.getOwnerType().getSignature());
        } else {
          outerPrefix.genCode(context, false);
        }
      }
    }

    for (int i = 0; i < params.length; i++) {
      params[i].genCode(context, false);
    }

    constructor.getOwner().genOuterSyntheticParams(context);

    constructor.genCode(context, true);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression[]		params;
  private JExpression		outerPrefix;
  protected CClass		local;
  protected CMethod		constructor;
  protected CReferenceType      type;
}
