/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.kopi.comp.kjc;

import at.dms.bytecode.classfile.PushLiteralInstruction;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.util.base.InconsistencyException;

/**
 * JLS 15.15.5 Bitwise Complement Operator ~
 */
public class JBitwiseComplementExpression extends JUnaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the operand
   */
  public JBitwiseComplementExpression(TokenReference where, JExpression expr) {
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
    check(context, expr.getType(factory).isOrdinal(), KjcMessages.UNARY_BADTYPE_BNOT, expr.getType(factory));
    type = CNumericType.unaryPromote(context, expr.getType(factory));
    expr = expr.convertType(context, type);

    if (expr.isConstant()) {
    if (type == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
	expr = new JLongLiteral(getTokenReference(), ~expr.longValue());
      } else if (type == factory.getPrimitiveType(TypeFactory.PRM_INT)) {
	expr = new JIntLiteral(getTokenReference(), ~expr.intValue());
      } else {
	throw new InconsistencyException();
      }
      return expr;
    } else {
      return this;
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
    p.visitBitwiseComplementExpression(this, expr);
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
    if (type.getTypeID() == TID_LONG) {
      code.plantInstruction(new PushLiteralInstruction((long)-1));
      code.plantNoArgInstruction(opc_lxor);
    } else {
      code.plantInstruction(new PushLiteralInstruction((int)-1));
      code.plantNoArgInstruction(opc_ixor);
    }

    if (discardValue) {
      code.plantPopInstruction(getType(factory));
    }
  }
}
