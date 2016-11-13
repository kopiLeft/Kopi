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

package org.kopi.bytecode.ssa;

import org.kopi.util.base.InconsistencyException;
import org.kopi.bytecode.classfile.Constants;
import org.kopi.bytecode.classfile.NoArgInstruction;
import org.kopi.bytecode.classfile.PushLiteralInstruction;


/**
 * A class to represent constant operand of quadruple instructions
 */
public class QConstant extends QOperand {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a null constant.
   */
  public QConstant() {
    this.nullConstant = true;
    this.type = Constants.TYP_REFERENCE;
  }

  /**
   * Construct a constant
   *
   * @param value the value of the constant
   * @param type type of the constant
   */
  public QConstant(Object value, byte type) {
    this.value = value;
    if (value instanceof Double) {
      // Because for the instruction ldc2_w,
      // PushLiteralInstruction.getReturnType() always return TYP_LONG.
      this.type = Constants.TYP_DOUBLE;
    } else if (value instanceof String) {
      //Because PushLiteralInstruction return TYP_INT
      // for a String.
      //TO REMOVE if PushLiteralInstruction change.
      this.type = Constants.TYP_REFERENCE;
    } else {
      this.type = type;
    }
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the operand may throw an exception
   */
  public boolean mayThrowException() {
    return false;
  }

  /**
   * Test if the operand has side effects.
   */
  public boolean hasSideEffects() {
    return false;
  }

  /**
   * Return the type of the operand.
   */
  public byte getType() {
    return type;
  }

  /**
   * Test if the operand is constant
   */
  public boolean isConstant() {
    return true;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    if (nullConstant) {
      return "null";
    } else if (value instanceof Integer) {
      return "" +  ((Integer) value).intValue();
    } else if (value instanceof Long) {
      return "" +  ((Long) value).longValue();
    } else if (value instanceof Float) {
      return "" +  ((Float) value).floatValue();
    } else if (value instanceof Double) {
      return "" +  ((Double) value).doubleValue();
    } else if (value instanceof String) {
      return "" +  (String) value;
    } else {
      return "constant";
    }
  }

  /**
   * Test if this is the null constant
   */
  public boolean isNull() {
    return nullConstant;
  }

  /**
   * Test if the constant numeric and equal to 0.
   *
   * @return true iff the constant is 0;
   */
  public boolean isZero() {
    if (!nullConstant) {
      if (value instanceof Integer) {
        return ((Integer)value).equals(new Integer(0));
      } else if (value instanceof Long) {
        return ((Long)value).equals(new Long(0));
      } else if (value instanceof Float) {
        return ((Float)value).equals(new Float(0));
      } else if (value instanceof Double) {
        return ((Double)value).equals(new Double(0));
      }
    }
    return false;
  }

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this operand
   *
   * @param codeGen the code generator
   */
  public void generateInstructions(CodeGenerator codeGen) {
    if (nullConstant) {
      codeGen.addInstruction(new NoArgInstruction(Constants.opc_aconst_null));
    } else {
      if (value instanceof Integer) {
        codeGen.addInstruction(new PushLiteralInstruction(((Integer)value).intValue()));
      } else if (value instanceof Long) {
        codeGen.addInstruction(new PushLiteralInstruction(((Long)value).longValue()));
      } else if (value instanceof Float) {
        codeGen.addInstruction(new PushLiteralInstruction(((Float)value).floatValue()));
      } else if (value instanceof Double) {
        codeGen.addInstruction(new PushLiteralInstruction(((Double)value).doubleValue()));
      } else if (value instanceof String) {
        codeGen.addInstruction(new PushLiteralInstruction((String)value));
      }
    }
  }

  /**
   * Generate the classfile instructions for save in
   * this operand the value actually son the stack.
   *
   * This method must not be use with a constant.
   */
  public void generateStore(CodeGenerator codeGen) {
    throw new InconsistencyException("Cannot store in constant value");
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected boolean nullConstant;
  protected Object value;
  protected byte type;
}
