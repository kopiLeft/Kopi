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

import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.SimpleStringBuffer;

/**
 * This class represents the Java type "double".
 * There is only one instance of this type.
 */
public class CDoubleType extends CNumericType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new instance.
   */
  public CDoubleType() {
    super(TID_DOUBLE);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of this type.
   */
  public String toString() {
    return "double";
  }

  /**
   * Returns the VM signature of this type.
   */
  public String getSignature() {
    return "D";
  }

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  protected void appendSignature(SimpleStringBuffer buffer) {
    buffer.append('D');
  }

  /**
   * Returns the stack size used by a value of this type.
   */
  public int getSize() {
    return 2;
  }

  /**
   * Is this type ordinal ?
   */
  public boolean isOrdinal() {
    return false;
  }

  /**
   * Is this a floating point type ?
   */
  public boolean isFloatingPoint() {
    return true;
  }

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    if (dest == this) {
      // JLS 5.1.1 Identity Conversion
      return true;
    } else {
      // JLS 5.1.2 Widening Primitive Conversion
      return false;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a bytecode sequence to convert a value of this type to the
   * specified destination type.
   * @param	dest		the destination type
   * @param	code		the code sequence
   */
  public void genCastTo(CNumericType dest, GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    if (dest != this) {
      switch (dest.type) {
      case TID_BYTE:
	code.plantNoArgInstruction(opc_d2i);
	code.plantNoArgInstruction(opc_i2b);
	break;

      case TID_CHAR:
	code.plantNoArgInstruction(opc_d2i);
	code.plantNoArgInstruction(opc_i2c);
	break;

      case TID_SHORT:
	code.plantNoArgInstruction(opc_d2i);
	code.plantNoArgInstruction(opc_i2s);
	break;

      case TID_INT:
	code.plantNoArgInstruction(opc_d2i);
	break;

      case TID_LONG:
	code.plantNoArgInstruction(opc_d2l);
	break;

      case TID_FLOAT:
	code.plantNoArgInstruction(opc_d2f);
	break;

      default:
	throw new InconsistencyException();
      }
    }
  }
}
