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

import com.kopiright.bytecode.classfile.SwitchInstruction;

/**
 * A class to represent switch instruction
 */
public class QSwitch extends QAbstractJumpInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a switch instruction
   *
   * @param sourceSwitch the switch in the source code
   * @param var the variable used in the switch.
   */
  public QSwitch(SwitchInstruction sourceSwitch, QOperand var) {
    this.switchInst = sourceSwitch;
    this.operand = new QOperandBox(var, this);
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the current instruction may throw an exception.
   */
  public  boolean mayThrowException() {
    return false;
  }

  /**
   * Test if the instruction define a local variable.
   */
  public boolean defVar() {
    // TO SEE
    return false;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {operand};
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return "switch (" + operand + ")";
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
    operand.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(switchInst);
  }

  /**
   * Simplify all consecutive jumps
   */
  public void simplifyAllJumps() {
    for (int k=-1;k<switchInst.getSwitchCount();k++) {
      simplifyJump(((EdgeLabel)switchInst.getTarget(k)).getEdge());
    }
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected SwitchInstruction switchInst;
  protected QOperandBox operand;
}
