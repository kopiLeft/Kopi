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

package at.dms.bytecode.ssa;

import at.dms.bytecode.classfile.FieldRefConstant;
import at.dms.bytecode.classfile.FieldRefInstruction;
/**
 * A class to represent a put in a field (static or not).
 */
public class QPutField extends QCallVoid {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a put static field
   *
   * @param field the field
   * @param value the value to store in.
   * @param opcode the opcode source
   */
  public QPutField(FieldRefConstant field, QOperand value,
                   int opcode) {
    this(field,null, value, opcode);
  }

  /**
   * Construct the instruction
   *
   * @param field the field
   * @param ref the object reference
   *            null for a static field
   * @param value the value to store in.
   * @param opcode the opcode source
   */
  public QPutField(FieldRefConstant field, QOperand ref,
                   QOperand value,
                   int opcode) {
    this.field = field;
    this.ref = new QOperandBox(ref, this);
    this.operand = new QOperandBox(value, this);
    this.opcode = opcode;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {ref, operand};
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
    if (ref.getOperand() != null) {
      ref.getOperand().generateInstructions(codeGen);
    }
    operand.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(new FieldRefInstruction(opcode, field));
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return ref + "." + field.getName() +  " = " + operand;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox ref;
  protected FieldRefConstant field;
  protected QOperandBox operand;
  protected int opcode;
}
