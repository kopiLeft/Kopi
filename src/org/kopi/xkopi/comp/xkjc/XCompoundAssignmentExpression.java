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
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JAddExpression;
import org.kopi.kopi.comp.kjc.JArrayAccessExpression;
import org.kopi.kopi.comp.kjc.JBitwiseExpression;
import org.kopi.kopi.comp.kjc.JCheckedExpression;
import org.kopi.kopi.comp.kjc.JCompoundAssignmentExpression;
import org.kopi.kopi.comp.kjc.JDivideExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldAccessExpression;
import org.kopi.kopi.comp.kjc.JLocalVariableExpression;
import org.kopi.kopi.comp.kjc.JMinusExpression;
import org.kopi.kopi.comp.kjc.JModuloExpression;
import org.kopi.kopi.comp.kjc.JMultExpression;
import org.kopi.kopi.comp.kjc.JShiftExpression;
import org.kopi.kopi.comp.kjc.KjcMessages;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.InconsistencyException;

/**
 * This class implements the assignment operation
 */
public class XCompoundAssignmentExpression extends JCompoundAssignmentExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	oper		the assignment operator
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public XCompoundAssignmentExpression(TokenReference where,
				       int oper,
				       JExpression left,
				       JExpression right) {
    super(where, oper, left, right);
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

    // discardValue = false: check if initialized
    left = left.analyse(new CExpressionContext(context, context.getEnvironment(), true, false));
    check(context, left.isLValue(context), KjcMessages.ASSIGNMENT_NOTLVALUE);

    // try to assign: check lhs is not final
   
      left.setInitialized(context);
   

    right = right.analyse(new CExpressionContext(context, context.getEnvironment(), false, false));

    boolean convertRight = true;

    try {
      switch (oper) {
      case OPE_STAR:
	type = JMultExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_SLASH:
	type = JDivideExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_PERCENT:
	type = JModuloExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_PLUS:
        TypeFactory     tf = context.getTypeFactory();
        CReferenceType  stringType = tf.createReferenceType(TypeFactory.RFT_STRING);
        CType           voidType = tf.getVoidType();

	if (left.getType(factory).equals(stringType)) {
	  if (right.getType(factory).getTypeID() == TID_VOID) {
	    throw new UnpositionedError(KjcMessages.ADD_BADTYPE,
					stringType,
					voidType);
	  }
	  type = stringType;
	  convertRight = false;
	} else {
          type = JAddExpression.computeType(context, left.getType(factory), right.getType(factory));
	}
	break;
      case OPE_MINUS:
	type = JMinusExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_SL:
      case OPE_SR:
      case OPE_BSR:
	type = JShiftExpression.computeType(context, left.getType(factory), right.getType(factory));
	convertRight = false;
	break;
      case OPE_BAND:
      case OPE_BXOR:
      case OPE_BOR:
	type = JBitwiseExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      default:
	throw new InconsistencyException("UNDEFINED OPERATOR:" + oper);
      }
      check(context,
            type.isCastableTo(context, left.getType(factory)),
            KjcMessages.ASSIGNMENT_BADTYPE, right.getType(factory), left.getType(factory));

      type = left.getType(factory);

      if (convertRight) {
	right = right.convertType(context, type).analyse(context);
      }

      if (left.requiresAccessor()) {
        JExpression accessorExpr = left.getAccessor(new JExpression[]{right}, oper);
        accessorExpr.analyse(context);
        return accessorExpr;
      } else {
        return this;
      }
    } catch (UnpositionedError ce) {
      try {
	return tryExpanded(context).analyse(context);
      } catch (PositionedError e) {
        throw ce.addPosition(getTokenReference());
      }
    } catch (PositionedError ce) {
      try {
	return tryExpanded(context).analyse(context);
      } catch (PositionedError e) {
        throw ce;
      }
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private JExpression tryExpanded(CExpressionContext context) throws PositionedError {
    // better check: hasSideEffect() !!!
    check(context,
	  left instanceof JLocalVariableExpression ||
	  left instanceof JFieldAccessExpression ||
	  left instanceof JArrayAccessExpression, XKjcMessages.DUMMY);//!!! FALSE

    TokenReference	ref = getTokenReference();
    JExpression		expr;

    switch (oper) {
    case OPE_STAR:
      expr = new XMultExpression(ref,
				 new JCheckedExpression(ref, left),
				 new JCheckedExpression(ref, right));
      break;
    case OPE_SLASH:
      expr = new XDivideExpression(ref,
				   new JCheckedExpression(ref, left),
				   new JCheckedExpression(ref, right));
      break;
    case OPE_PERCENT:
      expr = new XModuloExpression(ref,
				   new JCheckedExpression(ref, left),
				   new JCheckedExpression(ref, right));
      break;
    case OPE_PLUS:
      expr = new XAddExpression(ref,
				new JCheckedExpression(ref, left),
				new JCheckedExpression(ref, right));
      break;
    case OPE_MINUS:
      expr = new XMinusExpression(ref,
				  new JCheckedExpression(ref, left),
				  new JCheckedExpression(ref, right));
      break;
    case OPE_SL:
    case OPE_SR:
    case OPE_BSR:
      expr = new XShiftExpression(ref,
				  oper,
				  new JCheckedExpression(ref, left),
				  new JCheckedExpression(ref, right));
      break;
    case OPE_BAND:
    case OPE_BXOR:
    case OPE_BOR:
      expr = new XBitwiseExpression(ref,
				    oper,
				    new JCheckedExpression(ref, left),
				    new JCheckedExpression(ref, right));
      break;
    default:
      throw new InconsistencyException("UNDEFINED OPERATOR:" + oper);
    }

    return new XAssignmentExpression(ref, left, expr);
  }
}
