/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * Used in postconditions to access the return value of the method. 
 * It is the node for the operator "@@()".
 */
public class KopiReturnValueExpression extends JNameExpression{
  public KopiReturnValueExpression(TokenReference ref) {
    super(ref, IDENT_RETURN);
  }

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    check(context, context.getMethodContext().getCMethod().isPostcondition(), KjcMessages.WRONG_RETURN_VALUE);

    // is $return defined ? or is teh return type of the orginal method void?
    JFormalParameter[]  parameter = context.getMethodContext().getFormalParameter();
    boolean             hasReturnType = false;

    for (int i = 0; i < parameter.length && i < 3; i++) {
      if (parameter[i].getIdent() == IDENT_RETURN) {
        hasReturnType = true;
        break;
      }
    }
    check(context, hasReturnType, KjcMessages.RETURN_VALUE_WITHOUT);

    return super.analyse(context);
  }
}
