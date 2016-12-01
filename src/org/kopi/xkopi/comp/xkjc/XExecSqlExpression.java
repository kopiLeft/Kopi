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

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.JArrayInitializer;
import org.kopi.kopi.comp.kjc.JCheckedExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JNewArrayExpression;
import org.kopi.kopi.comp.kjc.JNullLiteral;
import org.kopi.kopi.comp.kjc.JThisExpression;
import org.kopi.kopi.comp.kjc.JTypeNameExpression;
import org.kopi.kopi.comp.kjc.TypeFactory;

/**
 * This class represents an sql expression in java code
 */
public class XExecSqlExpression extends XExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public XExecSqlExpression(TokenReference ref, JExpression conn, JExpression cursor, XSqlExpr expr) {
    super(ref);
    this.expr = expr;
    this.conn = conn;
    this.cursor = cursor;
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

    if (cursor == null) {
      if (conn == null) {
	check(context,
              context.getClassContext().getCClass().descendsFrom(factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER).getCClass()),
	      XKjcMessages.BAD_DEFAULT_CONTEXT, context.getClassContext().getCClass().getQualifiedName());
      } else {
	conn = conn.analyse(context);
	if (!conn.isAssignableTo(context,
                                 factory.createReferenceType(XTypeFactory.RFT_CONNECTION))) {
	  check(context,
                conn.isAssignableTo(context,
                                    factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER)),
		XKjcMessages.BAD_CONNECTION, conn.getType(factory).getCClass().getQualifiedName());
	  // CONSTRUCT THE CONNECTION ACCESS
	  conn = XProtectedStatement.buildConnection(getTokenReference(), conn).analyse(context);
	}
      }
    } else {
      check(context, conn == null, XKjcMessages.BAD_DEFAULT_CONTEXT);
    }

    JExpression expr = this.expr.analyse(context);

    TokenReference ref = getTokenReference();
    JExpression		blobs;
    JExpression[]	exprs = this.expr.getBlobs();
    if (exprs.length > 0) {
      blobs = new JNewArrayExpression(ref,
				      factory.createReferenceType(TypeFactory.RFT_OBJECT),
				      new JExpression[]{ null },
				      new JArrayInitializer(ref, exprs));
    } else {
      blobs = new JNullLiteral(ref);
    }

    if (cursor == null) {
      if (conn == null) {
        conn = new JMethodCallExpression(ref, 
                                         new JMethodCallExpression(ref, 
                                                                   new JThisExpression(ref),
                                                                   "getDBContext", 
                                                                   JExpression.EMPTY), 
                                         "getDefaultConnection", 
                                         JExpression.EMPTY);
      }
      CReferenceType    utilType = (CReferenceType) context.getTypeFactory().createType(org.kopi.xkopi.lib.base.KopiUtils.class.getName().replace('.','/'), true);

      try {
        utilType = (CReferenceType) utilType.checkType(context);
      } catch (UnpositionedError ue) {
        throw ue.addPosition(ref);
      }

      return new JMethodCallExpression(ref, 
                                       new JTypeNameExpression(ref, utilType), 
                                       "executeUpdate", 
                                       new JExpression[] {conn, expr, blobs}).analyse(context);
    } else {
      cursor = new JCheckedExpression(ref, cursor.analyse(context));

      return new JMethodCallExpression(ref, 
                                       cursor, 
                                       "update", 
                                       new JExpression[] {expr, blobs}).analyse(context);
    }
  }

  /**
   * Returns true iff this expression can be used as a statement (JLS 14.8)
   */
  public boolean isStatementExpression() {
    return true;
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
    p.visitExecSqlExpression(this, expr, conn, cursor);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  XSqlExpr			expr;
  JExpression			conn;
  JExpression			cursor;
}
