/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

/**
 * This class represents class type load from a binary class file 
 * The class of this type is only loaded if necessary. This type need no check
 */
public class CBinaryType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class or interface type from binary class
   */
  public CBinaryType(String qualifiedName, ClassReader classReader, TypeFactory typeFactory) {
    super();
    this.qualifiedName = qualifiedName;
    this.classReader = classReader;
    this.typeFactory = typeFactory;
    this.arguments = EMPTY_ARG;
  }

  /**
   * Returns the class object associated with this type
   *
   * If this type was never checked (read from class files)
   * check it!
   *
   * @return the class object associated with this type
   */
  public CClass getCClass() {
    if (!isChecked()) {
      setClass(classReader.loadClass(typeFactory, qualifiedName));
      qualifiedName = null;
      classReader = null;
      typeFactory = null;
    }

    return super.getCClass();
  }

  /**
   * returns the arguments for this class without the type 
   * arguments for the outer classes.
   */
  public CReferenceType[] getArguments() {
    CClass      clazz = getCClass();

    if (!clazz.isGenericClass()) {
      return CReferenceType.EMPTY;
    } else {
      return clazz.getTypeVariables();
    }
  }

  /**
   * returns the arguments for the outer classes too.
   */
  public CReferenceType[][] getAllArguments() {
    CClass      clazz = getCClass();

    if (!clazz.isGenericClass()) {
      return CReferenceType.EMPTY_ARG;
    } else {
      return new CReferenceType[][] {clazz.getTypeVariables()};
    }
  }

  /**
   *
   */
  public String getQualifiedName() {
    return qualifiedName == null ? super.getQualifiedName() : qualifiedName;
  }

  private String qualifiedName; 
  private ClassReader classReader; 
  private TypeFactory typeFactory;
}
