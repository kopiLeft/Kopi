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

import org.kopi.kopi.comp.kjc.Constants;
import org.kopi.kopi.comp.kjc.JBitwiseComplementExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * This class represents unary expressions.
 */
public class XBitwiseComplementExpression extends JBitwiseComplementExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the operand
   */
  public XBitwiseComplementExpression(TokenReference where, JExpression expr) {
    super(where, expr);
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
    try {
      return super.analyse(context);
    } catch (PositionedError e) {
      return XUtils.fetchUnaryOverloadedOperator(Constants.OPE_BNOT,
						 context,
						 expr,
						 getTokenReference(),
						 e);
    }
  }
}
