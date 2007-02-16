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

import com.kopiright.bytecode.classfile.ClassConstant;
import com.kopiright.bytecode.classfile.ClassRefInstruction;
import com.kopiright.bytecode.classfile.Constants;
/**
 * A class to represent a check cast.
 */
public class QCheckCast extends QCallReturn {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param className the name of the class to cast in.
   * @param op the reference to an object.
   */
  public QCheckCast(ClassConstant className, QOperand op) {
    this.className = className;
    this.operand = new QOperandBox(op, this);
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
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this quadruple instruction
   *
   * @param codeGen the code generator
   */
  public void generateInstructions(CodeGenerator codeGen) {
    operand.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(new ClassRefInstruction(Constants.opc_checkcast,
                                                   className));
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
    return "(" + className.getName() + ") " + operand;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected ClassConstant className;
  protected QOperandBox operand;
}
