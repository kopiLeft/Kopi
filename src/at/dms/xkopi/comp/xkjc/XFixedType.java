/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: XFixedType.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

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
    super(at.dms.xkopi.lib.type.NotNullFixed.class.getName().replace('.','/'));
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

  public at.dms.kopi.comp.kjc.CReferenceType getNotNullableClass() {
    return XStdType.Fixed;
  }
}
