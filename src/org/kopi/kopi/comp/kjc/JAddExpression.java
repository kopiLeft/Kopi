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

/**
 * This class implements '+ - * /' specific operations
 * Plus operand may be String, numbers
 */
public class JAddExpression extends JBinaryArithmeticExpression {

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
  public JAddExpression(TokenReference where,
			JExpression left,
			JExpression right)
  {
    super(where, left, right);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of this object.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JAddExpression[");
    buffer.append(left.toString());
    buffer.append(", ");
    buffer.append(right.toString());
    buffer.append("]");
    return buffer.toString();
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
    check(context, left.getType(factory).getTypeID() != TID_VOID && right.getType(factory).getTypeID() != TID_VOID,
	  KjcMessages.ADD_BADTYPE, left.getType(factory), right.getType(factory));

    check(context, !(left instanceof JTypeNameExpression) && !(right instanceof JTypeNameExpression),
	  KjcMessages.ADD_BADTYPE, left.getType(factory), right.getType(factory));

    try {
      type = computeType(context, left.getType(factory), right.getType(factory));
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    CReferenceType  stringType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING);

    // programming trick: no conversion for strings here: will be done in code generation
    if (!type.equals(stringType)) {
      left = left.convertType(context, type);
      right = right.convertType(context, type);
   }

    if (left.isConstant() && right.isConstant()) {
      if (type.equals(stringType)) {
	// in this case we have to convert the operands
	left = left.convertType(context, type);
	right = right.convertType(context, type);
	return new JStringLiteral(getTokenReference(), left.stringValue() + right.stringValue());
      } else {
	return constantFolding(factory);
      }
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
    CType stringType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING);

    if (leftType.equals(stringType)) {
      if (rightType.getTypeID() == TID_VOID) {
	throw new UnpositionedError(KjcMessages.ADD_BADTYPE, leftType, rightType);
      }
      return stringType;
    } else if (rightType.equals(stringType)) {
      if (leftType.getTypeID() == TID_VOID) {
	throw new UnpositionedError(KjcMessages.ADD_BADTYPE, leftType, rightType);
      }
      return stringType;
    } else {
      if (leftType.isNumeric() && rightType.isNumeric()) {
	return CNumericType.binaryPromote(context, leftType, rightType);
      }

      throw new UnpositionedError(KjcMessages.ADD_BADTYPE, leftType, rightType);
    }
  }

  // ----------------------------------------------------------------------
  // CONSTANT FOLDING
  // ----------------------------------------------------------------------

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public int compute(int left, int right) {
    return left + right;
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public long compute(long left, long right) {
    return left + right;
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public float compute(float left, float right) {
    return left + right;
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public double compute(double left, double right) {
    return left + right;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitBinaryExpression(this, "+", left, right);
  }

  /**
   * @param	type		the type of result
   * @return	the type of opcode for this operation
   */
  public static int getOpcode(CType type) {
    switch (type.getTypeID()) {
    case TID_FLOAT:
      return opc_fadd;
    case TID_LONG:
      return opc_ladd;
    case TID_DOUBLE:
      return opc_dadd;
    default:
      return opc_iadd;
    }
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

    if (type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
      code.plantClassRefInstruction(opc_new, "java/lang/StringBuffer");
      code.plantNoArgInstruction(opc_dup);
      code.plantMethodRefInstruction(opc_invokespecial,
				     "java/lang/StringBuffer",
				     JAV_CONSTRUCTOR,
				     "()V");
      left.genAppendToStringBuffer(context);
      right.genAppendToStringBuffer(context);
      code.plantMethodRefInstruction(opc_invokevirtual,
				     "java/lang/StringBuffer",
				     "toString",
				     "()Ljava/lang/String;");
    } else {
      left.genCode(context, false);
      right.genCode(context, false);

      code.plantNoArgInstruction(getOpcode(getType(factory)));
    }

    if (discardValue) {
      code.plantPopInstruction(getType(factory));
    }
  }

  /**
   * Generates JVM bytecode which appends a string representation
   * to a string buffer. Before the operation, a reference to the
   * string buffer is on top of stack; after the operation, the
   * reference will again be on top of stack.
   *
   * @param	context		the information about where to store the generated code
   */
  public void genAppendToStringBuffer(GenerationContext context) {
    TypeFactory         factory = context.getTypeFactory();

    if (getType(factory).equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
      left.genAppendToStringBuffer(context);
      right.genAppendToStringBuffer(context);
    } else {
      super.genAppendToStringBuffer(context);
    }
  }
}
