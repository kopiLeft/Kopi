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

import com.kopiright.bytecode.classfile.NoArgInstruction;
import com.kopiright.bytecode.classfile.Constants;

/**
 * A class to represent a exception throw instruction in 3-adress code.
 */
public class QThrow extends QInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a throw instruction
   *
   * @param op the operand of the instruction.
   */
  public QThrow(QOperand op) {
    this.op = new QOperandBox(op, this);
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the current instruction may throw an exception.
   */
  public boolean mayThrowException() {
    return true;
  }

  /**
   * Test if the instruction has side effects.
   */
  public boolean hasSideEffects() {
    return true;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {op };
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return "throw " + op;
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
    op.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(new NoArgInstruction(Constants.opc_athrow));
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  public QOperandBox op;
}
