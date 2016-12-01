/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * VMS 4.7.9: Local Variable Table Attribute.
 *
 * This attribute represents a mapping between he Java Virtual Machine code
 * array and the line number in the original Java source file
 */
public class LocalVariableTable extends Attribute {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Create a line number table attribute.
   */
  public LocalVariableTable(LocalVariableInfo[] entries) {
    this.entries = entries;
  }

  /**
   * Constructs a line number table attribute from a class file stream.
   *
   * @param	in		the stream to read from
   * @param	cp		the constant pool
   * @param	insns		(sparse) array of instructions
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  public LocalVariableTable(DataInput in, ConstantPool cp, Instruction[] insns)
    throws IOException
  {
    in.readInt();	// ignore

    this.entries = new LocalVariableInfo[in.readUnsignedShort()];
    for (int i = 0; i < this.entries.length; i++) {
      this.entries[i] = new LocalVariableInfo(in, cp, insns);
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the attribute's tag
   */
  /*package*/ int getTag() {
    return Constants.ATT_LOCALVARIABLETABLE;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ int getSize() {
    return 2 + 4 + 2 + 10*entries.length;
  }

  /**
   * Returns line number information
   */
  /*package*/ LocalVariableInfo[] getLocalVariables() {
    return entries;
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  /*package*/ void resolveConstants(ConstantPool cp)  throws ClassFileFormatException {
    cp.addItem(attr);

    for (int i = 0; i < entries.length; i++) {
      entries[i].resolveConstants(cp);
    }
  }

  /**
   * Write this class into the the file (out) getting data position from
   * the constant pool
   *
   * @param	cp		the constant pool that contain all data
   * @param	out		the file where to write this object info
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  /*package*/ void write(ConstantPool cp, DataOutput out) throws IOException {
    out.writeShort(attr.getIndex());
    out.writeInt(getSize() - 6);
    out.writeShort(entries.length);
    for (int i = 0; i < entries.length; i++) {
      entries[i].write(cp, out);
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static AsciiConstant		attr = new AsciiConstant("LocalVariableTable");
  private LocalVariableInfo[]		entries;
}
