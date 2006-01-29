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

import com.kopiright.bytecode.classfile.ClassConstant;
import com.kopiright.bytecode.classfile.ClassRefInstruction;
import com.kopiright.bytecode.classfile.Constants;

/**
 * A class to represent an access to the instanceof instruction
 */
public class QInstanceOf extends QCallReturn {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param ref the reference to test
   * @param className the name of the class
   */
  public QInstanceOf(QOperand ref, ClassConstant className) {
    this.ref = new QOperandBox(ref, this);
    this.className = className;
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Return the type of the expression
   */
  public byte getType() {
    return Constants.TYP_INT;
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
    return ref + " instance of " + className;
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
    ref.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(new ClassRefInstruction(Constants.opc_instanceof,
                                                   className));

  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox ref;
  protected ClassConstant className;

}
