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
import at.dms.compiler.base.NumberParser;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.InconsistencyException;

/**
 * A simple character constant
 */
public class JCharLiteral extends JLiteral {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	image		the string representation of this literal
   */
  public JCharLiteral(TokenReference where, String image)
    throws PositionedError
  {
    super(where);
    try {
      value = decodeChar(image);
    } catch (UnpositionedError e) {
      throw e.addPosition(where);
    }
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	image		the string representation of this literal
   */
  public JCharLiteral(TokenReference where, char value) {
    super(where);
    this.value = value;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return factory.getPrimitiveType(TypeFactory.PRM_CHAR);
  }

  /**
   * Returns the constant value of the expression.
   */
  public char charValue() {
    return value;
  }

  /**
   * Returns true iff the value of this literal is the
   * default value for this type (JLS 4.5.5).
   */
  public boolean isDefault() {
    return value == '\u0000';
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Returns the character represented by the specified image.
   * @exception	UnpositionedError	if the image is invalid.
   */
  private static char decodeChar(String image) throws UnpositionedError {
    char	value;

    if (image.startsWith("\\u")) {
      value = (char)NumberParser.decodeHexInt(false, image.substring(2));
    } else if (image.startsWith("\\") && image.length() > 1) {
      if ((image.charAt(1) >= '0') && (image.charAt(1) <= '9')) {
	value = (char)NumberParser.decodeOctInt(false, image.substring(1));
	if (value > 377) {
	  throw new UnpositionedError(KjcMessages.INVALID_OCTAL_CHAR, image);
	}
      } else if (image.equals("\\b")) {
	value = '\b';
      } else if (image.equals("\\r")) {
	value = '\r';
      } else if (image.equals("\\t")) {
	value = '\t';
      } else if (image.equals("\\n")) {
	value = '\n';
      } else if (image.equals("\\f")) {
	value = '\f';
      } else if (image.equals("\\\"")) {
	value = '\"';
      } else if (image.equals("\\\'")) {
	value = '\'';
      } else if (image.equals("\\\\")) {
	value = '\\';
      } else {
	throw new UnpositionedError(KjcMessages.INVALID_ESCAPE_SEQUENCE, image);
      }
    } else {
      value = image.charAt(0);
    }

    return value;
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
      return true;
    default:
      return context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_CHAR).isAssignableTo(context, dest);
    }
  }

  /**
   * convertType
   * changes the type of this expression to an other
   * @param  dest the destination type
   */
  public JExpression convertType(CExpressionContext context, CType dest) throws PositionedError {
    switch (dest.getTypeID()) {
    case TID_BYTE:
      return new JByteLiteral(getTokenReference(), (byte)value);
    case TID_SHORT:
      return new JShortLiteral(getTokenReference(), (short)value);
    case TID_CHAR:
      return this;
    case TID_INT:
      return new JIntLiteral(getTokenReference(), (int)value);
    case TID_LONG:
      return new JLongLiteral(getTokenReference(), (long)value);
    case TID_FLOAT:
      return new JFloatLiteral(getTokenReference(), (float)value);
    case TID_DOUBLE:
      return new JDoubleLiteral(getTokenReference(), (double)value);
    case TID_CLASS:
      if (dest != context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING)) {
	throw new InconsistencyException("cannot convert from char to " + dest);
      }
      return new JStringLiteral(getTokenReference(), "" + value);
    default:
      throw new InconsistencyException("cannot convert from char to " + dest);
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
    p.visitCharLiteral(value);
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

  private char		value;
}
