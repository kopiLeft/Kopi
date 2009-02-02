/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import java.io.DataOutput;
import java.io.IOException;

/**
 * this is an abstraction to contain all the constant items
 * that can be created.
 */
public abstract class PooledConstant implements Constants {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new pooled constant.
   *
   * @param	uniq		a string that identifies this constant.
   */
  public PooledConstant() {}

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the number of slots in the constant pool used by this entry
   * This number is normally 1. Constants which use more than one slot should
   * override this method.
   */
  /*package*/ int getSlotsUsed() {
    return 1;
  }

  /**
   * Returns the associated literal
   */
  /*package*/ abstract Object getLiteral();

  // --------------------------------------------------------------------
  // POOLING
  // --------------------------------------------------------------------

  /**
   * hashCode (a fast comparison)
   * CONVENTION: return XXXXXXXXXXXX << 4 + Y
   * with Y = ident of the type of the pooled constant
   */
  public abstract int hashCode();

  /**
   * equals (an exact comparison)
   */
  public abstract boolean equals(Object o);

  public final short getIndex() {
    return index;
  }

  public final void setIndex(short index) {
    this.index = index;
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  /*package*/ abstract void resolveConstants(ConstantPool cp) throws ClassFileFormatException;

  /**
   * Check location of constant value on constant pool
   *
   * @param	pc		the already in pooled constant
   */
  /*package*/ abstract void resolveConstants(PooledConstant pc) throws ClassFileFormatException;

  /**
   * Write this class into the the file (out) getting data position from
   * the constant pool
   *
   * @param	cp		the constant pool that contain all data
   * @param	out		the file where to write this object info
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  /*package*/ abstract void write(ConstantPool cp, DataOutput out) throws ClassFileFormatException, IOException;


  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private short			index;
}
