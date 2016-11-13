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

package org.kopi.xkopi.comp.xkjc;

import java.util.Vector;
import org.kopi.kopi.comp.kjc.*;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.JavaStyleComment;

/**
 * This class represents cursor declaration in code part
 */
public class XLocalCursorDeclaration extends XStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public XLocalCursorDeclaration(TokenReference ref,
				 XCursorDeclaration d,
				 JavaStyleComment[] comments) {
    super(ref, comments);
    decl = d;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * The connection is explicitly set.
   */
  public void setConnection(JExpression conn) {
    this.conn = conn;
  }

  /**
   *
   */
  public void addDeclaration(String ident) {
    names.addElement(ident);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   *
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public JStatement checkAndConvert(CBodyContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    if (conn == null) {
      check(context,
            context.getClassContext().getCClass().descendsFrom(factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER).getCClass()),
	    XKjcMessages.BAD_DEFAULT_CONTEXT, context.getClassContext().getCClass().getQualifiedName());
    } else {
      conn = conn.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (!conn.isAssignableTo(context,
                               factory.createReferenceType(XTypeFactory.RFT_CONNECTION))) {
	check(context,
              conn.isAssignableTo(context,
                                  factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER)),
	      XKjcMessages.BAD_CONNECTION, conn.getType(factory).getCClass().getQualifiedName());
	// CONSTRUCT THE CONNECTION ACCESS
	conn = XProtectedStatement.buildConnection(getTokenReference(), conn).analyse(new CExpressionContext(context, context.getEnvironment()));
      }
    }

    decl.setPositionInBlock(context.getClassContext().getNextSyntheticIndex());
    JStatement[]	defs = new JStatement[1 + 2 * names.size()];

    defs[0] = new JTypeDeclarationStatement(decl.getTokenReference(), decl);

    // CONSTRUCT TREE
    TokenReference                      ref = getTokenReference();
    JVariableDeclarationStatement[]     stmts = new JVariableDeclarationStatement[names.size()];

    for (int i = 0; i < stmts.length; i++) {
      String            ident = (String)names.elementAt(i);
      TypeFactory       tf = context.getTypeFactory();

      defs[1 + i] = new JVariableDeclarationStatement(ref,
                                                      new XVariableDefinition(ref, ACC_FINAL, tf.createType(decl.getIdent(), false), ident,
                                                                              new JUnqualifiedInstanceCreation(ref,
                                                                                                               tf.createType(decl.getIdent(), false),
                                                                                                               JExpression.EMPTY)), 
                                                      null);
    }

    if (conn == null) {
      conn = new JMethodCallExpression(ref, 
                                       new JMethodCallExpression(ref, 
                                                                 new JThisExpression(ref), 
                                                                 "getDBContext", 
                                                                 JExpression.EMPTY), 
                                       "getDefaultConnection", 
                                       JExpression.EMPTY);
    }

    for (int i = 0; i < stmts.length; i++) {
      defs[1 + stmts.length + i] =
	new JExpressionStatement(ref,
				 new JMethodCallExpression(ref,
							   new JNameExpression(ref, ((JVariableDeclarationStatement)defs[1 + i]).getVars()[0].getIdent()),
							   "setDefaultConnection",
							   new JExpression[]{ conn }),
				 null);
    }
    JCompoundStatement compound = new JCompoundStatement(getTokenReference(), defs);

    compound.analyse(context);

    return compound;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genXKjcCode(XKjcPrettyPrinter p) {
    p.visitLocalCursorDeclaration(this, conn, decl, names);
  }

  /**
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public final void visitOther(KjcVisitor p) {
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  XCursorDeclaration		decl;
  JExpression			conn;
  Vector			names = new Vector(); //String
}
