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

package org.kopi.xkopi.comp.xkjc;

/**
 * This class represents a primitive Fixed type
 */
public class XFixedType extends XPrimitiveClassType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a class type
   * @param	qualifiedName	the class qualified name of the class
   */
  public XFixedType() {
    super(org.kopi.xkopi.lib.type.NotNullFixed.class.getName().replace('.','/'));
  }

  /**
   * Check if a type is a class type
   * @return is it a subtype of ClassType ?
   */
  public boolean isPrimitive() {
    return true;
  }

  public String toString() {
    return "fixed";
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF XPRIMITIVETYPE
  // ----------------------------------------------------------------------

  public org.kopi.kopi.comp.kjc.CReferenceType getNotNullableClass() {
    return XStdType.Fixed;
  }
}
