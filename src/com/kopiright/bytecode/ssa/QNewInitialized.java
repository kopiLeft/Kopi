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
import com.kopiright.bytecode.classfile.ReferenceConstant;
import com.kopiright.bytecode.classfile.MethodRefConstant;
import com.kopiright.bytecode.classfile.NoArgInstruction;
import com.kopiright.bytecode.classfile.MethodRefInstruction;


/**
 * A class to represent instruction new
 */
public class QNewInitialized extends QCallReturn {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param className the name of the class to create an instance.
   * @param method the method
   * @param ops object and parameter of the method
   */
  public QNewInitialized(ClassConstant className, ReferenceConstant method,
                         QOperandBox[] ops) {
    this.className = className;
    this.method = method;
    operands = new QOperandBox[ops.length - 1];

    //remove this
    for (int i = 0; i < operands.length; ++i) {
      operands[i] = ops[i + 1];
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
    return operands;
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    String tmp = "new " + className.getName();
    tmp += method.getName() + "(";
    for (int i = 0; i < operands.length; ++i) {
      tmp += operands[i];
      if (i != operands.length - 1) {
        tmp += ", ";
      }
    }
    tmp += ")";
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
    // new
    // dup
    // param1
    // ...
    // paramn
    // invokespecial <init>(param1, ..., paramn)
    codeGen.addInstruction(new ClassRefInstruction(Constants.opc_new,
                                                   className));
    codeGen.addInstruction(new NoArgInstruction(Constants.opc_dup));
    for (int i = 0; i < operands.length; ++i) {
      operands[i].getOperand().generateInstructions(codeGen);
    }
    codeGen.addInstruction(new MethodRefInstruction(Constants.opc_invokespecial, (MethodRefConstant) method));

  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected ClassConstant className;
  protected ReferenceConstant method;
  protected QOperandBox[] operands;
}
