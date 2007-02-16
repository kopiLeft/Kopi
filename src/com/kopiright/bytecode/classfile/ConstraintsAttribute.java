/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.bytecode.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class ConstraintsAttribute extends Attribute {

  public ConstraintsAttribute(MethodDescription pre, MethodDescription post) {
    if (pre != null) {
      precondition = new MethodRefConstant(pre.getName(), pre.getType());
    } else {
      precondition = null;
    }
    if (post != null) {
      postcondition = new MethodRefConstant(post.getName(), post.getType());
    } else {
      postcondition = null;
    }
  }

  public ConstraintsAttribute(DataInput in, ConstantPool cp) 
    throws IOException, ClassFileFormatException 
  {
    if (in.readInt() != 4) {
      throw new ClassFileFormatException("bad attribute length");
    }

    int       idxPre = in.readUnsignedShort();
    int       idxPost = in.readUnsignedShort();

    precondition = (idxPre==0) ? null : (MethodRefConstant)cp.getEntryAt(idxPre);
    postcondition = (idxPost==0) ? null : (MethodRefConstant)cp.getEntryAt(idxPost);
  }


  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the attribute's tag
   */
  /*package*/ int getTag() {
    return Constants.ATT_CONSTRAINTS;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ int getSize() {
    return 2 + 4 + 4;
  }


  /**
   * @return null if there is no Precondition
   */
  public MethodDescription getPrecondition() {
    if (precondition == null) {
      return null;
    } else {
      return new MethodDescription(precondition.getTypeName(),
                                   precondition.getType(),
                                   precondition.getClassName());
    }
  }

  /**
   * @return null if there is no Postcondition
   */
  public MethodDescription getPostcondition() {
    if (postcondition == null) {
      return null;
    } else {
      return new MethodDescription(postcondition.getTypeName(),
                                   postcondition.getType(),
                                   postcondition.getClassName());
    }
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
   void resolveConstants(ConstantPool cp) throws ClassFileFormatException {
    cp.addItem(attr);
    if (precondition != null) {
      cp.addItem(precondition);
    }
    if (postcondition != null) {
      cp.addItem(postcondition);
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
    out.writeInt(4);
    out.writeShort((precondition == null) ? 0 : precondition.getIndex());
    out.writeShort((postcondition == null) ?  0 : postcondition.getIndex());
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static final String    NAME = "Constraints";
  private static AsciiConstant  attr = new AsciiConstant(NAME);

  private MethodRefConstant     precondition;
  private MethodRefConstant     postcondition;
}
