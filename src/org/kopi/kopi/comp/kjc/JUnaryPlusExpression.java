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

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * JLS 15.15.3 Unary Plus Operator +
 */
public class JUnaryPlusExpression extends JUnaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the operand
   */
  public JUnaryPlusExpression(TokenReference where, JExpression expr) {
    super(where, expr);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

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
    TypeFactory factory = context.getTypeFactory();

    expr = expr.analyse(context);
    check(context, expr.getType(factory).isNumeric(), KjcMessages.UNARY_BADTYPE_PM, expr.getType(factory));
    type = CNumericType.unaryPromote(context, expr.getType(factory));
    expr = expr.convertType(context, type);

    return expr;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitUnaryPlusExpression(this, expr);
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
    expr.genCode(context, discardValue);
  }
}
