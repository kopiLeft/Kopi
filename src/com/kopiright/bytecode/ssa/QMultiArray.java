/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.bytecode.ssa;

import com.kopiright.bytecode.classfile.MultiarrayInstruction;
import com.kopiright.bytecode.classfile.Constants;
/**
 * A class to represent instruction multiarray
 */
public class QMultiArray extends QCallReturn {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param mai the multi array instruction in the source.
   * @param dimensions the dimensions of the array
   */
  public QMultiArray(MultiarrayInstruction mai, QOperand[] dimensions) {
    this.mai = mai;
    this.dimensions = new QOperandBox[dimensions.length];
    for (int i = 0; i < dimensions.length; ++i) {
      this.dimensions[i] = new QOperandBox(dimensions[i], this);
    }
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the instruction has side effects.
   */
  public boolean hasSideEffects() {
    return true;
  }

  /**
   * Return the type of the expression
   */
  public byte getType() {
    return Constants.TYP_REFERENCE;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return dimensions;
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    String tmp = "new " + mai.getType() + " ";
    for (int i = 0; i < dimensions.length; ++i) {
      tmp += "[" + dimensions[i] + "]";
    }
    return tmp;
  }

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this quadruple instruction
   *
   * @param codeGen the code generator
   */
  public void generateInstructions(CodeGenerator codeGen) {
    for (int i = 0; i < dimensions.length; ++i) {
      dimensions[i].getOperand().generateInstructions(codeGen);
    }
    codeGen.addInstruction(mai);
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected MultiarrayInstruction mai;
  protected QOperandBox[] dimensions;
}
