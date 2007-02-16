/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class implements the assignment operation
 */
public class XAssignmentExpression extends JAssignmentExpression {

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
  public XAssignmentExpression(TokenReference where,
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
    TypeFactory         factory = context.getTypeFactory();

    left = left.analyse(new CExpressionContext(context, context.getEnvironment(), true, true));
    check(context, left.isLValue(context), KjcMessages.ASSIGNMENT_NOTLVALUE);

    right = right.analyse(new CExpressionContext(context, context.getEnvironment(), false, false));

   
      left.setInitialized(context);
    

    check(context,
	  right.getType(factory).equals(left.getType(factory))
	  || (right.getType(factory) instanceof XPrimitiveClassType)
	  || !(right.getType(factory).isClassType())
	  || !(left.getType(factory) instanceof XPrimitiveClassType),
	  XKjcMessages.ASSIGNMENT_TO_NOT_NULL, right.getType(factory));

    if (right.isAssignableTo(context, left.getType(factory))) {
      type = left.getType(factory);
      right = right.convertType(context, type);

      if (left.requiresAccessor()) {
        JExpression accessorExpr = left.getAccessor(new JExpression[]{right}, OPE_SIMPLE);
        accessorExpr.analyse(context);
        return accessorExpr;
      } else {
        return this;
      }
    } else {
      JExpression	param;

      if (left.getType(factory).isReference()) {
	param = new JCastExpression(getTokenReference(),
				     new JNullLiteral(getTokenReference()),
				     left.getType(factory));
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BYTE)) {
	param = new JByteLiteral(getTokenReference(), (byte)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_SHORT)) {
	param = new JShortLiteral(getTokenReference(), (short)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_CHAR)) {
	param = new JCharLiteral(getTokenReference(), (char)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_INT)) {
	param = new JIntLiteral(getTokenReference(), (int)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
	param = new JLongLiteral(getTokenReference(), (long)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_FLOAT)) {
	param = new JFloatLiteral(getTokenReference(), (float)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_DOUBLE)) {
	param = new JDoubleLiteral(getTokenReference(),(double)0);
      } else if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN)) {
	param = new JBooleanLiteral(getTokenReference(), true);
      } else {
	param = null;
	check(context, false, KjcMessages.ASSIGNMENT_BADTYPE, right.getType(factory), left.getType(factory));
      }

      XOverloadedMethodCallExpression	overload;

      overload = new XOverloadedMethodCallExpression(getTokenReference(),
						     null,
						     "operator$" + Constants.OPE_SIMPLE,
						     param != null ?
						     new JExpression[]{ param, right } :
						     new JExpression[]{ right }
						     );

      try {
	JExpression	convert = XUtils.fetchOverloadedOperator(context, overload);

	if (convert != null) {
	  JAssignmentExpression	assign = new JAssignmentExpression(getTokenReference(),
								   left,
								   convert);

	  return assign.analyse(context);
	}
      } catch (PositionedError error) {
	//error.printStackTrace();
	//context.reportTrouble(error); // prevent user that there is a probleme with overloading
      }
      check(context, false, KjcMessages.ASSIGNMENT_BADTYPE, right.getType(factory), left.getType(factory));
      return null; //never accessed
    }
  }
}
