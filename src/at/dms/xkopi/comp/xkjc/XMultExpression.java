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
 * $Id: XMultExpression.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.JMultExpression;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.CExpressionContext;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class implements '*' specific operations
 * Plus operand may be String, numbers
 */
public class XMultExpression extends JMultExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public XMultExpression(TokenReference where,
			  JExpression left,
			  JExpression right) {
    super(where, left, right);
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
      return XUtils.fetchBinaryOverloadedOperator(XConstants.OPE_STAR,
						  context,
						  left,
						  right,
						  getTokenReference(),
						  e);
    }
  }
}
