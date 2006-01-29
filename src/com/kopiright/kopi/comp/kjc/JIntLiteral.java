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

import com.kopiright.bytecode.classfile.PushLiteralInstruction;
import com.kopiright.compiler.base.NumberParser;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.InconsistencyException;

/**
 * JLS 3.10.1 Integer Literals. This class represents int literals.
 */
public class JIntLiteral extends JLiteral {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a literal expression from a textual representation.
   * @param	where		the line of this node in the source code
   * @param	image		the textual representation of this literal
   */
  public JIntLiteral(TokenReference where, String image)
    throws PositionedError
  {
    super(where);
    if (image.startsWith("0")) {
      // octal or hexadecimal
      try {
	this.value = NumberParser.decodeInt(image);
      } catch (NumberFormatException e) {
	throw new PositionedError(where, KjcMessages.INVALID_INT_LITERAL, image);
      }
      this.invert = false;
    } else {
      // decimal
      int	value;

      try {
	value = NumberParser.decodeInt("-" + image);
      } catch (NumberFormatException e) {
	throw new PositionedError(where, KjcMessages.INVALID_INT_LITERAL, image);
      }
      if (value == Integer.MIN_VALUE) {
	this.value = value;
	this.invert = true;
      } else {
	this.value = -value;
	this.invert = false;
      }
    }
  }

  /**
   * Constructs a literal expression from a constant value.
   * @param	where		the line of this node in the source code
   * @param	value		the constant value
   */
  public JIntLiteral(TokenReference where, int value) {
    super(where);
    this.value = value;
    this.invert = false;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a literal with the sign inverted.
   * This is needed to handle 2147483648 which cannot be stored
   * in a variable of type int.
   *
   * JLS 3.10.1 :
   * The largest decimal literal of type int is 2147483648 (2^31). All
   * decimal literals from 0 to 2147483647 may appear anywhere an int
   * literal may appear, but the literal 2147483648 may appear only as the
   * operand of the unary negation operator -.
   */
  public JIntLiteral getOppositeLiteral() throws PositionedError {
    return new JIntLiteral(getTokenReference(), invert ? Integer.MIN_VALUE : -value);
  }

  /**
   * Returns the type of this expression.
   */
  public CType getType(TypeFactory factory) {
    return factory.getPrimitiveType(TypeFactory.PRM_INT);
  }

  /**
   * Returns the constant value of the expression.
   */
  public int intValue() {
    if (this.invert) {
      throw new InconsistencyException();
    }
    return value;
  }

  /**
   * Returns true iff the value of this literal is the
   * default value for this type (JLS 4.5.5).
   */
  public boolean isDefault() {
    return value == 0;
  }

  /**
   * Returns a string representation of this literal.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JIntLiteral[");
    if (invert) {
      buffer.append("2147483648 (= 2^31)");
    } else {
      buffer.append(value);
    }
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
    check(context, !this.invert, KjcMessages.INVALID_INT_LITERAL, "2147483648 (= 2^31)");
    return this;
  }

  /**
   * Can this expression be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    switch (dest.getTypeID()) {
    case TID_BYTE:
      return (byte)value == value;
    case TID_SHORT:
      return (short)value == value;
    case TID_CHAR:
      return (char)value == value;
    default:
      return context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT).isAssignableTo(context, dest);
    }
  }

  /**
   * convertType
   * changes the type of this expression to an other
   * @param  dest the destination type
   */
  public JExpression convertType(CExpressionContext context, CType dest) {
    if (this.invert) {
      throw new InconsistencyException();
    }

    switch (dest.getTypeID()) {
    case TID_BYTE:
      return new JByteLiteral(getTokenReference(), (byte)value);
    case TID_SHORT:
      return new JShortLiteral(getTokenReference(), (short)value);
    case TID_CHAR:
      return new JCharLiteral(getTokenReference(), (char)value);
    case TID_INT:
      return this;
    case TID_LONG:
      return new JLongLiteral(getTokenReference(), (long)value);
    case TID_FLOAT:
      return new JFloatLiteral(getTokenReference(), (float)value);
    case TID_DOUBLE:
      return new JDoubleLiteral(getTokenReference(), (double)value);
    case TID_CLASS:
      if (dest != context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING)) {
	throw new InconsistencyException("cannot convert from int to " + dest);
      }
      return new JStringLiteral(getTokenReference(), "" + value);
    default:
      throw new InconsistencyException("cannot convert from int to " + dest);
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
    p.visitIntLiteral(value);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    if (! discardValue) {
      setLineNumber(code);
      code.plantInstruction(new PushLiteralInstruction(value));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

	// value = MAX_VALUE + 1, valid only as argument to unary minus
  private final boolean		invert;
  private final int		value;
}
