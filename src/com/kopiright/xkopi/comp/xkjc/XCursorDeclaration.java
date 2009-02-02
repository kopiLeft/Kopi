/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.xkjc;

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.JavadocComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CContext;
import com.kopiright.kopi.comp.kjc.CParseClassContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CTypeVariable;
import com.kopiright.kopi.comp.kjc.JAssignmentExpression;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JClassDeclaration;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JExpressionStatement;
import com.kopiright.kopi.comp.kjc.JFieldDeclaration;
import com.kopiright.kopi.comp.kjc.JFormalParameter;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JMethodDeclaration;
import com.kopiright.kopi.comp.kjc.JNameExpression;
import com.kopiright.kopi.comp.kjc.JPhylum;
import com.kopiright.kopi.comp.kjc.JReturnStatement;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JThisExpression;
import com.kopiright.kopi.comp.kjc.JTypeDeclaration;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.kopi.comp.kjc.JVariableDeclarationStatement;
import com.kopiright.kopi.comp.kjc.JVariableDefinition;
import com.kopiright.kopi.comp.kjc.KjcVisitor;
import com.kopiright.kopi.comp.kjc.TypeFactory;

/**
 * This class represents cursor definition in kopi grammar
 */
public class XCursorDeclaration extends JClassDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	context		the context in which this class is defined
   * @param	modifiers	the list of modifiers of this class
   * @param	ident		the short name of this class
   * @param	superName	the name of super class of this class
   * @param	interfaces	the name of this class's interfaces
   * @param	deprecated	is this class deprecated
   */
  public XCursorDeclaration(TokenReference where,
			    int modifiers,
			    String ident,
			    JFieldDeclaration[] fields,
			    JMethodDeclaration[] methods,
			    JTypeDeclaration[] inners,
			    JPhylum[] initializers,
			    XSelectStatement select,
			    JFormalParameter[] parameters,
			    JavadocComment javadoc,
			    JavaStyleComment[] comments)
  {
    super(where,
	  modifiers,
	  ident == null ? "Cursor" : ident,
          CTypeVariable.EMPTY,
	  CReferenceType.lookup(XConstants.XKJ_CURSOR),
	  CReferenceType.EMPTY,
	  fields,
	  methods,
	  inners,
	  initializers,
	  javadoc,
	  comments);

    this.select = select;
    this.parameters = parameters;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public void setPositionInBlock(int pos) {
    setIdent(("Cursor" + pos).intern());
  }

  public String getIdent() {
    return ident;
  }

  /**
   *
   */
  public static void addImplicitSelect(CParseClassContext context,
				       String className,
				       XSelectStatement select,
				       JFormalParameter[] parameters,
                                       TypeFactory factory)
  {
    TokenReference	ref = select.getTokenReference();
    XSqlExpr		expr = new XSqlExpr(ref, select.getSql());

    context.addMethodDeclaration(new JMethodDeclaration(ref,
							ACC_PUBLIC | ACC_FINAL,
                                                        CTypeVariable.EMPTY,
							factory.getVoidType(),
							"open",
							parameters,
							new CReferenceType[]{
							  CReferenceType.lookup(XConstants.JAV_SQLEXCEPTION)
							},
                                                        new JBlock(ref,
                                                                   new JStatement[] {
                                                                         new JExpressionStatement(ref,
                                                                                   new JMethodCallExpression(ref,
                                                                                                             new JThisExpression(ref),
                                                                                                             "openWOCursor",
                                                                                                             new JExpression[] {expr}), null)},
                                                                   null),
							null, null));

    // Build the field list
    XTypedSelectElem[]      elems = select.getTypedSelectElem();
    XCursorFieldDeclaration[] fields = new XCursorFieldDeclaration[elems.length];
    for (int i = 0; i < elems.length; i++) {
      XCursorFieldDeclaration	field = new XCursorFieldDeclaration(ref,
								    elems[i].getVariableDefinition(),
								    null,
								    null);
      fields[i] = field;
      field.setPosition(i);
      context.addFieldDeclaration(field);
    }

    addCreateCopy(ref, context, className, factory);
    addCreateEmptyRow(ref, context, className, factory);
  }

  public static void addCreateEmptyRow(TokenReference ref,
                                       CParseClassContext context,
                                       String className,
                                       TypeFactory factory)
  {
    // Add a method to create an empty row.

    if (className != null) {
      JStatement                returnStatement;
      JExpression               inst;
      JMethodDeclaration        decl;

      inst = new JUnqualifiedInstanceCreation(ref,
					      factory.createType(className + "/KopiRow",
								 false),
					      JExpression.EMPTY);

      returnStatement = new JReturnStatement(ref, inst, null);

      decl = new JMethodDeclaration(ref,
				    ACC_PUBLIC,
				    CTypeVariable.EMPTY,
				    factory.createType("com/kopiright/xkopi/lib/base/Cursor", false),
				    "createEmptyRow",
				    JFormalParameter.EMPTY,
				    new CReferenceType[]{
				      CReferenceType.lookup(XConstants.JAV_SQLEXCEPTION)
				    },
				    new JBlock(ref,
					       new JStatement[] {returnStatement},
					       null),
				    null,
				    null);
      context.addMethodDeclaration(decl);
    }
  }

  public static void addCreateCopy(TokenReference ref,
                                   CParseClassContext context,
                                   String className,
                                   TypeFactory factory)
  {
    // Add a method to copy all the values
    JFieldDeclaration[]        fields = context.getFields();

    if (className != null) {
      JStatement[]	stmts = new JStatement[fields.length + 4];
      JNameExpression	name = new JNameExpression(ref, "temp");
      for (int i = 0; i < fields.length; i++) {
        JFieldDeclaration field = fields[i];
        JNameExpression	left = new JNameExpression(ref,
						   name,
						   field.getVariable().getIdent());
        JNameExpression	right = new XNameExpression(ref,
						    new JThisExpression(ref),
						    field.getVariable().getIdent());
        JAssignmentExpression ass = new JAssignmentExpression(ref,
                                                              left,
                                                              right);
        stmts[i + 1] = new JExpressionStatement(ref, ass, null);
      }
      // rset
      JNameExpression	left = new JNameExpression(ref,
						   name,
						   "rset");
      JNameExpression	right = new JNameExpression(ref,
						    new JThisExpression(ref),
						    "rset");
      JAssignmentExpression ass = new JAssignmentExpression(ref,
                                                            left,
                                                            right);
      stmts[stmts.length - 3] = new JExpressionStatement(ref, ass, null);
      //isFetched
      left = new JNameExpression(ref,
                                 name,
                                 "isFetched");
      right = new JNameExpression(ref,
                                  new JThisExpression(ref),
                                  "isFetched");
      ass = new JAssignmentExpression(ref,
                                                            left,
                                                            right);
      stmts[stmts.length - 2] = new JExpressionStatement(ref, ass, null);

      // return
      JExpression	inst;
      inst = new JUnqualifiedInstanceCreation(ref,
					      factory.createType(className + "/KopiRow",
								 false),
					      JExpression.EMPTY);


      JVariableDefinition	def;
      def = new JVariableDefinition(ref,
				    0,
				    factory.createType(className + "/KopiRow", false),
				    "temp",
				    inst);
      stmts[0] = new JVariableDeclarationStatement(ref, def, null);
      stmts[stmts.length - 1] = new JReturnStatement(ref,
						     new JNameExpression(ref,
									 null,
									 "temp"),
						     null);
      JMethodDeclaration decl;
      decl = new JMethodDeclaration(ref,
				    ACC_PUBLIC,
				    CTypeVariable.EMPTY,
				    factory.createType("com/kopiright/xkopi/lib/base/Cursor", false),
				    "createCopy",
				    JFormalParameter.EMPTY,
				    new CReferenceType[]{
				      CReferenceType.lookup(XConstants.JAV_SQLEXCEPTION)
				    },
				    new JBlock(ref,
					       stmts,
					       null),
				    null,
				    null);
	context.addMethodDeclaration(decl);
        addRowDefinition(ref, context, className, factory);
    }
  }

  public static void addRowDefinition(TokenReference ref,
                                      CParseClassContext context,
                                      String className,
                                      TypeFactory factory)
  {
    JClassDeclaration           decl;

    decl = new JClassDeclaration(ref,
                                 ACC_PUBLIC | ACC_FINAL,
                                 "KopiRow",
                                 CTypeVariable.EMPTY,
                                 factory.createType(className, false),
                                 CReferenceType.EMPTY,
                                 JFieldDeclaration.EMPTY,
                                 JMethodDeclaration.EMPTY,
                                 JTypeDeclaration.EMPTY,
                                 JPhylum.EMPTY,
                                 null,
                                 null);
    context.addInnerDeclaration(decl);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------


  /**
   * does nothing, asserts are not used in cursers
   */
  protected void createAssertFields(final CContext context) throws PositionedError {
  }

  /**
   * checkTypeBody
   * Check expression and evaluate and alter context
   * @param context the actual context of analyse
   * @return  a pure java expression including promote node
   * @exception	PositionedError	an error with reference to the source file
   */
  public void checkTypeBody(CContext context) throws PositionedError {
    super.checkTypeBody(context);

    checked = true;
  }

  protected final boolean isChecked() {
    return checked;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void accept(KjcVisitor p) {
    if (checked) {
      super.accept(p);
    } else {
      genComments(p);
      if (select != null) {
	((XKjcPrettyPrinter)p).visitImplicitCursorBody(select, parameters);
      } else {
	((XKjcPrettyPrinter)p).visitExplicitCursorBody(getFields());
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  private boolean                       checked;
  protected final XSelectStatement	select;
  protected final JFormalParameter[]	parameters;
}
