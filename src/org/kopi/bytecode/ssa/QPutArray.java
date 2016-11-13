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

import org.kopi.bytecode.classfile.NoArgInstruction;

/**
 * A class to represent a value store in an array
 */
public class QPutArray extends QCallVoid {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param array the reference to the array
   * @param index index in the array
   * @param value value to put in the array
   * @param opcode the opcode source
   */
  public QPutArray(QOperand array, QOperand index, QOperand value,
                   int opcode) {
    this.array = new QOperandBox(array, this);
    this.index = new QOperandBox(index, this);
    this.value = new QOperandBox(value, this);
    this.opcode = opcode;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {array, index, value };
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return  array + "[" + index + "] = " + value;
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
    array.getOperand().generateInstructions(codeGen);
    index.getOperand().generateInstructions(codeGen);
    value.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(new NoArgInstruction(opcode));
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox array;
  protected QOperandBox index;
  protected QOperandBox value;
  protected int opcode;
}
