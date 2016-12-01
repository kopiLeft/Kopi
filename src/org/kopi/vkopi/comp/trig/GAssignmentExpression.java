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

package org.kopi.vkopi.comp.trig;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.Constants;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.xkopi.comp.xkjc.XAssignmentExpression;

/**
 * This class implements the assignment operation
 */
public class GAssignmentExpression extends XAssignmentExpression {

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
  public GAssignmentExpression(TokenReference where,
			       JExpression left,
			       JExpression right)
  {
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
    if (left instanceof GVKAccess) {
      return ((GVKAccess)left).generateAssignment(right, Constants.OPE_SIMPLE, context);
    } else {
      return super.analyse(context);
    }
  }
}
