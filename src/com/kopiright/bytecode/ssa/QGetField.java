/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import com.kopiright.bytecode.classfile.FieldRefConstant;
import com.kopiright.bytecode.classfile.FieldRefInstruction;

/**
 * A class to represent an access to a field (static or not).
 */
public class QGetField extends QCallReturn {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param field the field
   * @param ref the object reference
   *            null for a static field
   * @param type type of the field.
   * @param opcode the opcode source
   */
  public QGetField(FieldRefConstant field, QOperand ref,
                   byte type, int opcode) {
    this.field = field;
    this.ref = new QOperandBox(ref, this);
    this.type = type;
    this.opcode = opcode;
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Return the type of the expression
   */
  public byte getType() {
    return type;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {ref };
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    if (ref == null || ref.getOperand() == null) {
      return field.getName();
    } else {
      return ref + "." + field.getName();
    }
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
    if (ref.getOperand() != null) {
      ref.getOperand().generateInstructions(codeGen);
    }
    codeGen.addInstruction(new FieldRefInstruction(opcode, field));
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected FieldRefConstant field;
  protected QOperandBox ref;
  protected byte type;
  protected int opcode;
}
