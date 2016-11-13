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

/**
 * FieldRefConstant's are used to refer to a field in a particular
 * class.
 *
 */

package org.kopi.bytecode.classfile;

public class FieldRefConstant extends ReferenceConstant {

  /**
   * Constructs a field reference constant.
   *
   * @param	name		the qualified name of the referenced field
   * @param	type		the signature of the referenced field
   */
  public FieldRefConstant(String name, String type) {
    super(CST_FIELD, name, type);
  }

  /**
   * Constructs a field reference constant.
   *
   * @param	owner		the qualified name of the class containing the field
   * @param	name		the simple name of the referenced field
   * @param	type		the signature of the referenced field
   */
  public FieldRefConstant(String owner, String name, String type) {
    super(CST_FIELD, owner, name, type);
  }

  /**
   * Constructs a field reference constant.
   *
   * @param	clazz		the class that defines the referenced field
   * @param	nametype	the simple name and signature of the referenced field
   */
  public FieldRefConstant(ClassConstant clazz, NameAndTypeConstant nametype) {
    super(CST_FIELD, clazz, nametype);
  }
}
