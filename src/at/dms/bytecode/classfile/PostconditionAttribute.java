/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.bytecode.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class PostconditionAttribute extends Attribute {

  public PostconditionAttribute() {
    this(null);
  }

  public PostconditionAttribute(String name) {
    oldValueClass = (name == null) ? null : new ClassConstant(name);
  }

  /**
   * Constructs a Signature (generic) attribute from a class file stream.
   *
   * @param	in		the stream to read from
   * @param	cp		the constant pool
   *
   * @exception	java.io.IOException	an io problem has occured
   * @exception	ClassFileFormatException	attempt to
   *					write a bad classfile info
   */
  public PostconditionAttribute(DataInput in, ConstantPool cp) 
    throws IOException, ClassFileFormatException 
  {
    if (in.readInt() != 2) {
      throw new ClassFileFormatException("bad attribute length (Postcondition Attribute)");
    }
    int         idx = in.readUnsignedShort();

    oldValueClass = (idx == 0) ? null : (ClassConstant) cp.getEntryAt(idx);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the attribute's tag
   */
  /*package*/ int getTag() {
    return Constants.ATT_POSTCONDITION;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ int getSize() {
    return 2 + 4 + 2;
  }

  public String getOldValueStore() {
    return (oldValueClass == null) ? null : oldValueClass.getName();
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
    if (oldValueClass != null) cp.addItem(oldValueClass);
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
    out.writeInt(2);
    out.writeShort((oldValueClass == null) ? 0 : oldValueClass.getIndex());
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static final String            NAME = "Postcondition";
  private static AsciiConstant		attr = new AsciiConstant(NAME);
  private ClassConstant                 oldValueClass;
}
