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
import java.io.IOException;
import java.io.DataOutput;

public class PreconditionAttribute extends Attribute {

  public PreconditionAttribute() {
  }

  public PreconditionAttribute(DataInput in, ConstantPool cp) throws IOException{
    byte[]      data = new byte[in.readInt()];

    in.readFully(data);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the attribute's tag
   */
  /*package*/ int getTag() {
    return Constants.ATT_PRECONDITION;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ int getSize() {
    return 2 + 4;
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  /*package*/ void resolveConstants(ConstantPool cp) throws ClassFileFormatException  {
    cp.addItem(attr);
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
    out.writeInt(0);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static final String            NAME = "Precondition";
  private static AsciiConstant		attr = new AsciiConstant(NAME);
}
