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

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * JLS 15.25 Conditional Operator ? :
 */
public class JConditionalExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	cond		the condition operand
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JConditionalExpression(TokenReference where,
				JExpression cond,
				JExpression left,
				JExpression right)
  {
    super(where);
    this.cond = cond;
    this.left = left;
    this.right = right;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  /**
   * Returns a string representation of this literal.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JConditionalExpression[");
    buffer.append(cond.toString());
    buffer.append(", ");
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
    CType               primBoolean = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);

    cond = cond.analyse(context);

    // left side
    CBodyContext        leftBodyContext = new CSimpleBodyContext(context.getBodyContext(), 
                                                                 context.getEnvironment(), 
                                                                 context.getBodyContext());
    CExpressionContext  leftExprContext = new CExpressionContext(leftBodyContext, 
                                                                 context.getEnvironment(), 
                                                                 context.isLeftSide(), 
                                                                 context.discardValue());

    left = left.analyse(leftExprContext);

    // right side
    CBodyContext        rightBodyContext = new CSimpleBodyContext(context.getBodyContext(), 
                                                                  context.getEnvironment(), 
                                                                  context.getBodyContext());
    CExpressionContext  rightExprContext = new CExpressionContext(rightBodyContext, 
                                                                  context.getEnvironment(), 
                                                                  context.isLeftSide(), 
                                                                  context.discardValue());
    right = right.analyse(rightExprContext);
    rightBodyContext.merge(leftBodyContext);

    context.getBodyContext().adopt(rightBodyContext);
 
    check(context, cond.getType(factory) == primBoolean, KjcMessages.TRINARY_BADCOND);

    CType               leftType        = left.getType(factory);
    CType               rightType       = right.getType(factory);
    // !!!  Thomas : A VERIFIER -> on remplace tout par des CPrimitiveType ou on fait un cast ?
    //     CShortType          primShort       = factory.getPrimitiveType(TypeFactory.PRM_SHORT);
    //     CByteType           primByte        = factory.getPrimitiveType(TypeFactory.PRM_BYTE);
    //     CIntType            primInt         = factory.getPrimitiveType(TypeFactory.PRM_INT);
    //     CCharType           primChar        = factory.getPrimitiveType(TypeFactory.PRM_CHAR);
    CPrimitiveType      primShort       = factory.getPrimitiveType(TypeFactory.PRM_SHORT);
    CPrimitiveType      primByte        = factory.getPrimitiveType(TypeFactory.PRM_BYTE);
    CPrimitiveType      primInt         = factory.getPrimitiveType(TypeFactory.PRM_INT);
    CPrimitiveType      primChar        = factory.getPrimitiveType(TypeFactory.PRM_CHAR);

    // JLS 15.25 :
    // The type of a conditional expression is determined as follows:
    if (leftType.equals(rightType)) {
      // - If the second and third operands have the same type (which may
      //   be the null type), then that is the type of the conditional
      //   expression.
      type = leftType;
    } else if (leftType.isNumeric() && rightType.isNumeric()) {
      // - Otherwise, if the second and third operands have numeric type,
      //   then there are several cases:
      //   * If one of the operands is of type byte and the other is of
      //     type short, then the type of the conditional expression is short.
      //   * If one of the operands is of type T where T is byte, short,
      //     or char, and the other operand is a constant expression of type
      //     int whose value is representable in type T, then the type of
      //     the conditional expression is T.
      //   * Otherwise, binary numeric promotion is applied to the operand
      //     types, and the type of the conditional expression is the promoted
      //     type of the second and third operands. Note that binary numeric
      //     promotion performs value set conversion.
      if ((leftType == primByte && rightType == primShort)
	  || (rightType == primByte && leftType == primShort)) {
	type = primShort;
      } else if ((leftType == primByte
		  || leftType == primShort
		  || leftType == primChar)
		 && rightType == primInt
		 && right.isConstant()
		 && right.isAssignableTo(context, leftType)) {
	type = leftType;
      } else if ((rightType == primByte
		  || rightType == primShort
		  || rightType == primChar)
		 && leftType == primInt
		 && left.isConstant()
		 && left.isAssignableTo(context, rightType)) {
	type = rightType;
      } else {
	type = CNumericType.binaryPromote(context, leftType, rightType);
	check(context,
	      type != null,
	      KjcMessages.TRINARY_INCOMP, leftType, rightType);
      }
      left = left.convertType(context, type);
      right = right.convertType(context, type);
    } else if (leftType.isReference() && rightType.isReference()) {
      // - If one of the second and third operands is of the null type and the
      //   type of the other is a reference type, then the type of the
      //   conditional expression is that reference type.
      // - If the second and third operands are of different reference types,
      //   then it must be possible to convert one of the types to the other type
      //   (call this latter type T) by assignment conversion ; the type of the
      //   conditional expression is T.
      //   It is a compile-time error if neither type is assignment
      //   compatible with the other type.
      CType     nullType = factory.getNullType();

      if (leftType == nullType) {
	type = rightType;
      } else if (rightType == nullType) {
	type = leftType;
      } else if (leftType.isAssignableTo(context,rightType)) {
	type = rightType;
      } else if (rightType.isAssignableTo(context,leftType)) {
	type = leftType;
      } else {
	check(context, false, KjcMessages.TRINARY_INCOMP, leftType, rightType);
      }
    } else {
      check(context, false, KjcMessages.TRINARY_INCOMP, leftType, rightType);
    }

    // JLS 15.28: Constant Expression ?
    if (cond.isConstant() && left.isConstant() && right.isConstant()) {
      return cond.booleanValue() ? left : right;
    } else {
      return this;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**K
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitConditionalExpression(this, cond, left, right);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		rightLabel = new CodeLabel();
    CodeLabel		nextLabel = new CodeLabel();

    cond.genBranch(false, context, rightLabel);		//		COND IFEQ right
    left.genCode(context, discardValue);			//		LEFT CODE
    code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
    code.plantLabel(rightLabel);			//	right:
    right.genCode(context, discardValue);			//		RIGHT CODE
    code.plantLabel(nextLabel);				//	next:	...
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CType		type;
  private JExpression   cond;
  private JExpression   left;
  private JExpression   right;
}
