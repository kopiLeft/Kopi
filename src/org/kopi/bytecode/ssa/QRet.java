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
import org.kopi.bytecode.classfile.LocalVarInstruction;

/**
 * A class to represent a ret instruction
 */
public class QRet extends QInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a ret instruction
   *
   * @param adress the variable containing the destination adress of the
   * subroutine
   */
  public QRet(QVar address) {
    this.address = new QOperandBox(address, this);
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

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {address };
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return  "ret " + address;
  }

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this quadruple instruction
   *
   * @param codeGen the code generator
   */
  public  void generateInstructions(CodeGenerator codeGen) {
    codeGen.addInstruction(new LocalVarInstruction(Constants.opc_ret,
                                                   ((QVar)address.getOperand()).getRegister()));
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox address;
}
