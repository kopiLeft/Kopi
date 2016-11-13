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

package org.kopi.kopi.comp.kjc;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.util.base.InconsistencyException;

/**
 * This class implements '+ - * /' specific operations
 * Plus operand may be String, numbers
 */
public class JShiftExpression extends JBinaryArithmeticExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	oper		the operator
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JShiftExpression(TokenReference where,
			  int oper,
			  JExpression left,
			  JExpression right)
  {
    super(where, left, right);
    this.oper = oper;
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

    left = left.analyse(context);
    right = right.analyse(context);

    try {
      type = computeType(context, left.getType(factory), right.getType(factory));
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    left = left.convertType(context, type);
    right = right.convertType(context, context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT));

    if (left.isConstant() && right.isConstant()) {
      return constantFolding(factory);
    } else {
      return this;
    }
  }

  /**
   * compute the type of this expression according to operands
   * @param	leftType		the type of left operand
   * @param	rightType		the type of right operand
   * @return	the type computed for this binary operation
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public static CType computeType(CExpressionContext context, 
                                  CType	leftType, 
                                  CType rightType) throws UnpositionedError {
    //if (rightType == factory.getPrimitiveType(TypeFactory.PRM_LONG))
    // check the spec  throw new UnpositionedError(KjcMessages.shift-badtype, leftType, rightType);

    if (leftType.isOrdinal() && rightType.isOrdinal()) {
      return leftType = CNumericType.unaryPromote(context, leftType);
    }

    throw new UnpositionedError(KjcMessages.SHIFT_BADTYPE, leftType, rightType);
  }

  /**
   * @param	left		the left literal
   * @param	right		the right literal
   * @return	a literal resulting of an operation over two literals
   */
  public JExpression constantFolding(TypeFactory factory) {
    if (left.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
      long	result;

      if (right.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
	result = compute(left.longValue(), (int)right.longValue());
      } else {
	result = compute(left.longValue(), right.intValue());
      }
      return new JLongLiteral(getTokenReference(), result);
    } else {
      int	result;

      if (right.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
	result = compute(left.intValue(), (int)right.longValue());
      } else {
	result = compute(left.intValue(), right.intValue());
      }
      return new JIntLiteral(getTokenReference(), result);
    }
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public int compute(int left, int right) {
    switch (oper) {
    case OPE_SL:
      return left << right;
    case OPE_SR:
      return left >> right;
    case OPE_BSR:
      return left >>> right;
    default:
      throw new InconsistencyException();
    }
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public long compute(long left, int right) {
    switch (oper) {
    case OPE_SL:
      return left << right;
    case OPE_SR:
      return left >> right;
    case OPE_BSR:
      return left >>> right;
    default:
      throw new InconsistencyException();
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
    p.visitShiftExpression(this, oper, left, right);
  }


  /**
   * @param	type		the type of result
   * @return	the type of opcode for this operation
   */
  public static int getOpcode(int oper, CType type) {
    if (type.getTypeID() == TID_LONG) {
      switch (oper) {
      case OPE_SL:
	return opc_lshl;
      case OPE_SR:
	return opc_lshr;
      case OPE_BSR:
	return opc_lushr;
      }
    } else {
      switch (oper) {
      case OPE_SL:
	return opc_ishl;
      case OPE_SR:
	return opc_ishr;
      case OPE_BSR:
	return opc_iushr;
      }
    }
    return -1;
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

    left.genCode(context, false);
    right.genCode(context, false);
    code.plantNoArgInstruction(getOpcode(oper, getType(factory)));

    if (discardValue) {
      code.plantPopInstruction(getType(factory));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected	int			oper;
}
