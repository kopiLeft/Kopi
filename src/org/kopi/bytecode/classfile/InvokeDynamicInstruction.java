/*
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.bytecode.classfile;

import java.io.DataOutput;
import java.io.IOException;

import org.kopi.util.base.InconsistencyException;

/**
 * Instruction that invokes a dynamic method
 * opc_invokedynamic
 */
public class InvokeDynamicInstruction extends Instruction {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new invoke dynamic instruction from a class file
   *
   * @param	opcode		the opcode of the instruction
   * @param	method		the method reference (as index to run-time pool)
   */
  public InvokeDynamicInstruction(int opcode, int method) {
    super(opcode);

    this.method = method;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns true iff control flow can reach the next instruction
   * in textual order.
   */
  public boolean canComplete() {
    return true;
  }

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  /*package*/ void resolveConstants(ConstantPool cp) throws ClassFileFormatException  {
  }

  /**
   * Returns the number of bytes used by the the instruction in the code array.
   */
  /*package*/ int getSize() {
    return 1 + 2 + 1 + 1;
  }

  // --------------------------------------------------------------------
  // CHECK CONTROL FLOW
  // --------------------------------------------------------------------

  /**
   * Returns the type pushed on the stack
   */
  public byte getReturnType() {
    //!!! COMPLETEME graf 20220428
    return TYP_VOID;
  }

  /**
   * Returns the size of data pushed on the stack by this instruction
   */
  public int getPushedOnStack() {
    //!!! COMPLETEME graf 20220428
    return 0;
  }

  /**
   * Return the amount of stack (positive or negative) used by this instruction
   */
  public int getStack() {
    //!!! COMPLETEME graf 20220428
    return 0;
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Write this instruction into a file
   *
   * @param	cp		the constant pool that contain all data
   * @param	out		the file where to write this object info
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  /*package*/ void write(ConstantPool cp, DataOutput out) throws IOException {
    out.writeByte((byte)getOpcode());
    out.writeShort(method);
    out.writeByte(0);
    out.writeByte(0);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int           method;
}
