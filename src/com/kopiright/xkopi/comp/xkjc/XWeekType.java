/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.xkjc;

/**
 * This class represents a primitive Week type
 */
public class XWeekType extends XPrimitiveClassType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a class type
   * @param	qualifiedName	the class qualified name of the class
   */
  public XWeekType() {
    super(com.kopiright.xkopi.lib.type.NotNullWeek.class.getName().replace('.','/'));
  }

  public String toString() {
    return "week";
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF XPRIMITIVETYPE
  // ----------------------------------------------------------------------

  public com.kopiright.kopi.comp.kjc.CReferenceType getNotNullableClass() {
    return XStdType.Week;
  }
}
