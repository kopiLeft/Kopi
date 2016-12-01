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
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.JEqualityExpression;
import org.kopi.kopi.comp.kjc.JExpression;

/**
 * This class implements '==' specific operations
 * Plus operand may be String, numbers
 */
public class XEqualityExpression extends JEqualityExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	equal		is the operator '==' ?
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public XEqualityExpression(TokenReference where,
			     boolean equal,
			     JExpression left,
			     JExpression right)
  {
    super(where, equal, left, right);
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
    if (XUtils.overloadEqual) {
      try {
	return XUtils.fetchBinaryOverloadedOperator(equal ? OPE_EQ : OPE_NE,
						    context,
						    left,
						    right,
						    getTokenReference(),
						    new PositionedError(getTokenReference(), XKjcMessages.DUMMY, null, null));
      } catch (PositionedError e) {
	return super.analyse(context);
      }
    } else {
      try {
	return super.analyse(context);
      } catch (PositionedError e) {
	return XUtils.fetchBinaryOverloadedOperator(equal ? OPE_EQ : OPE_NE,
						    context,
						    left,
						    right,
						    getTokenReference(),
						    e);
      }
    }
  }
}
