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

package com.kopiright.bytecode.ssa;

/**
 * An instruction to represent initialisation of variable.
 */
public class QDeclareInitialised extends QInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction.
   *
   * @param var the variable to initialize
   * @param generate true iff the instruction generate a store
   *                 (for exception for example)
   */
  public QDeclareInitialised(QOperand var, boolean generate) {
    this.var = new QOperandBox(var, this, true);
    this.generate = generate;
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the current instruction may throw an exception.
   */
  public boolean mayThrowException() {
    return false;
  }

  /**
   * Test if the instruction has side effects.
   */
  public boolean hasSideEffects() {
    return generate;
  }

  /**
   * Test if the instruction define a local variable.
   */
  public boolean defVar() {
    return true;
  }

  /**
   * Get the operand defined
   */
  public QOperandBox getDefined() {
    return var;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[0];
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return  " --> " + var;
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
    if (generate) {
      var.getOperand().generateStore(codeGen);
    }
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox var;
  protected boolean generate;
}
