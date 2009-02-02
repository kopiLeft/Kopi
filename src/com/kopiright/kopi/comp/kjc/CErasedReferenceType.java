/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class represents class type in the type structure
 */
public class CErasedReferenceType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a class type
   * @param	clazz		the class that will represent this type
   */
  public CErasedReferenceType(CClass clazz) {
    super(clazz);
  }

  protected boolean isChecked() {
    return true;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * equals
   */
  public boolean equals(CType other) {
    return (!other.isClassType() || other.isArrayType()) ?
      false :
      ((CReferenceType)other).getCClass() == getCClass();
  }

  public boolean equals(CType other, CReferenceType[] substitution) {
    return equals(other);
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @param	context		the context (may be be null)
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    return this;
  }

  // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    return isAssignableTo(context, dest, false);
  }
  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest, boolean inst) {
    if (!(dest.isClassType() && !dest.isArrayType())) {
      return false;
    } else {
      return getCClass().descendsFrom(dest.getCClass());
    }
    //  return true;
  }

  public boolean isAssignableTo(CTypeContext context, CType dest, CReferenceType[] substitution) {
    return isAssignableTo(context, dest, false);
  }



  // ----------------------------------------------------------------------
  // INITIALIZERS METHODS
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
}
