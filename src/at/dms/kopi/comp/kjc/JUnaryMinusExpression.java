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
 * $Id: JUnaryMinusExpression.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.util.base.InconsistencyException;

/**
 * JLS 15.15.4 Unary Minus Operator -
 */
public class JUnaryMinusExpression extends JUnaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the operand
   */
  public JUnaryMinusExpression(TokenReference where, JExpression expr) {
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
    TypeFactory         factory = context.getTypeFactory();

    // special case for MIN_INT and MIN_LONG literals
    if (expr instanceof JIntLiteral) {
      return ((JIntLiteral)expr).getOppositeLiteral();
    } else if (expr instanceof JLongLiteral) {
      return ((JLongLiteral)expr).getOppositeLiteral();
    } else {
      expr = expr.analyse(context);
      check(context, expr.getType(factory).isNumeric(), KjcMessages.UNARY_BADTYPE_PM, expr.getType(factory));
      type = CNumericType.unaryPromote(context, expr.getType(factory));
      expr = expr.convertType(context, type);

      if (expr.isConstant()) {
	switch (type.getTypeID()) {
	case TID_INT:
	  expr = new JIntLiteral(getTokenReference(), -expr.intValue());
	  break;
	case TID_LONG:
	  expr = new JLongLiteral(getTokenReference(), -expr.longValue());
	  break;
	case TID_FLOAT:
	  expr = new JFloatLiteral(getTokenReference(), -expr.floatValue());
	  break;
	case TID_DOUBLE:
	  expr = new JDoubleLiteral(getTokenReference(), -expr.doubleValue());
	  break;
	default:
	  throw new InconsistencyException("unexpected type " + type);
	}

	return expr;
      } else {
	return this;
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitUnaryMinusExpression(this, expr);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    expr.genCode(context, false);
    switch (type.getTypeID()) {
    case TID_FLOAT:
      code.plantNoArgInstruction(opc_fneg);
      break;
    case TID_LONG:
      code.plantNoArgInstruction(opc_lneg);
      break;
    case TID_DOUBLE:
      code.plantNoArgInstruction(opc_dneg);
      break;
    default:
      code.plantNoArgInstruction(opc_ineg);
    }
    if (discardValue) {
      code.plantPopInstruction(getType(factory));
    }
  }
}
