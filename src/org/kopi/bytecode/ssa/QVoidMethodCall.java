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

import org.kopi.bytecode.classfile.Constants;
import org.kopi.bytecode.classfile.InterfaceConstant;
import org.kopi.bytecode.classfile.InvokeinterfaceInstruction;
import org.kopi.bytecode.classfile.MethodRefConstant;
import org.kopi.bytecode.classfile.MethodRefInstruction;
import org.kopi.bytecode.classfile.ReferenceConstant;

/**
 * A class to represent a method call without result (method or interface).
 */
public class QVoidMethodCall extends QCallVoid {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param method the method
   * @param ops object and parameter of the method
   * @param opcode source code operation
   */
  public QVoidMethodCall(ReferenceConstant method, QOperand[] ops,
                         int opcode) {
    this.method = method;
    operands = new QOperandBox[ops.length];
    for (int i = 0; i < operands.length; ++i) {
      operands[i] = new QOperandBox(ops[i], this);
    }
    this.opcode = opcode;
    this.interfaceNbArgs = -1;
  }

  /**
   * Construct the instruction for an invoke interface
   *
   * @param method the method
   * @param ops object and parameter of the method
   * @param opcode source code operation
   * @param nbargs number of arguments of the interface.
   */
  public QVoidMethodCall(ReferenceConstant method, QOperand[] ops,
                         int opcode, int nbargs) {
    this.method = method;
    operands = new QOperandBox[ops.length];
    for (int i = 0; i < operands.length; ++i) {
      operands[i] = new QOperandBox(ops[i], this);
    }
    this.opcode = opcode;
    this.interfaceNbArgs = nbargs;
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
    String tmp = "";
    int i = 0;
    if (opcode != Constants.opc_invokestatic) {
      tmp += operands[0] + ".";
      ++i;
    }
    tmp += method.getName() + "(";
    for (; i < operands.length; ++i) {
      tmp += operands[i];
      if (i != operands.length - 1) {
        tmp += ", ";
      }
    }
    tmp += ")";
    return tmp;
  }

  /**
   * Test if the instruction is an invokespecial
   */
  public boolean isInvokespecial() {
    return opcode == Constants.opc_invokespecial;
  }

  /**
   * Get the reference constant of the method
   */
  public ReferenceConstant getReferenceConstant() {
    return method;
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
    for (int i = 0; i < operands.length; ++i) {
      operands[i].getOperand().generateInstructions(codeGen);
    }
    if (opcode == Constants.opc_invokeinterface) {
      codeGen.addInstruction(new InvokeinterfaceInstruction((InterfaceConstant) method, interfaceNbArgs));
    } else {
      codeGen.addInstruction(new MethodRefInstruction(opcode, (MethodRefConstant) method));
    }
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected ReferenceConstant method;
  protected QOperandBox[] operands;
  protected int opcode;
  protected int interfaceNbArgs;
}
