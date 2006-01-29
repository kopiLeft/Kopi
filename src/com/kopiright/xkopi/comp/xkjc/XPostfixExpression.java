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

package com.kopiright.xkopi.comp.xkjc;

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents unary expressions.
 */
public class XPostfixExpression extends JPostfixExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	oper		the operator
   * @param	expr		the operand
   */
  public XPostfixExpression(TokenReference where, int oper, JExpression expr) {
    super(where, oper, expr);
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
    PositionedError	typeError = null;

    try {
      try {
	return super.analyse(context);
      } catch (PositionedError e) {
	typeError = e;
      }
      return XUtils.fetchUnaryOverloadedOperator(oper,
						 context,
						 expr,
						 getTokenReference(),
						 typeError);
    } catch (Exception e) {
      try {
	return tryExpanded(context).analyse(context);
      } catch (PositionedError f) {
	throw typeError;
      }
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private JExpression tryExpanded(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    // better check: hasSideEffect() !!!
    check(context, expr instanceof JLocalVariableExpression || expr instanceof JFieldAccessExpression,
	  XKjcMessages.DUMMY);
    check(context, expr.getType(factory).isReference(), XKjcMessages.DUMMY);

    TokenReference	ref = getTokenReference();
    JExpression	left = new JCheckedExpression(ref, expr);
    JExpression	right = new XCompoundAssignmentExpression(ref, OPE_PLUS,
							  new JCheckedExpression(ref, expr),
							  new JIntLiteral(ref, oper == OPE_POSTINC ? 1 : -1));

    /* remove if it works / dont forget to remove com/kopiright/xkopi/lib/base/XKjcRtUtils
    CReferenceType    utilType = (CReferenceType) context.getTypeFactory().createType(com.kopiright.xkopi.lib.base.XKjcRtUtils.class.getName().replace('.','/'), true);

    try {
      utilType = (CReferenceType) utilType.checkType(context);
    } catch (UnpositionedError ue) {
      throw ue.addPosition(ref);
    }
    
    return new JCastExpression(ref,
                               new JMethodCallExpression(ref, 
                                                         new JTypeNameExpression(ref, utilType),
                                                         "returnMe", 
                                                         new JExpression[] {left, right}),
			       expr.getType());
    */
    return left;
  }
}
