/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: Type.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.lib.type;

/**
 * This class is the super-class for Kopi types
 */
public abstract class Type implements Comparable {

  /**
   * Compares two objects
   */
  public abstract boolean equals(Object other);

  /**
   * Format the object depending on the current language
   */
  public final String toString() {
    return toString(java.util.Locale.getDefault());
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public abstract String toString(java.util.Locale locale);

  /**
   * Represents the value in sql
   */
  public abstract String toSql();
}
