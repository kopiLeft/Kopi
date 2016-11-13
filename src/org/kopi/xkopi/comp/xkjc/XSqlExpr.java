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

import org.kopi.kopi.comp.kjc.*;
import org.kopi.xkopi.comp.sqlc.SqlPhylum;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * an sql text with java inserted
 */
public class XSqlExpr extends XExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public XSqlExpr(TokenReference where, SqlPhylum text) {
    super(where);
    this.text = text;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return factory.createReferenceType(TypeFactory.RFT_STRING);
  }

  /**
   * Tests whether this expression denotes a compile-time constant (JLS 15.28).
   *
   * @return	true iff this expression is constant
   */
  public boolean isConstant() {
    return false;
  }

  /**
   * Returns the SqlStatement
   */
  public SqlPhylum getSql() {
    return text;
  }

  /**
   *
   */
  public JExpression[] getBlobs() {
    return sql.getBlobs();
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
    XSqlChecker		checker = new XSqlChecker(sql = new JavaSqlContext(context.getBodyContext()));

    text.accept(checker);

    return checker.toJavaExpression(getTokenReference()).analyse(context);
  }

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(SqlContext sql) throws PositionedError {
    CBodyContext	context = XSqlChecker.getJavaContext(sql).getBodyContext();
    XSqlChecker		checker = new XSqlChecker(sql/* = new JavaSqlContext(context)*/);

    text.accept(checker);

    return checker.toJavaExpression(getTokenReference()).analyse(new CExpressionContext(context, context.getEnvironment()));
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
    p.visitSql(text);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  private SqlPhylum		text;
  private JavaSqlContext	sql;
}
