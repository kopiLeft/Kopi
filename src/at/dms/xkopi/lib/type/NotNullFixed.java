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

package at.dms.xkopi.lib.type;

import com.ibm.math.BigDecimal;

/**
 * This class represents kopi fixed type
 */
public class NotNullFixed extends Fixed {

  public NotNullFixed(BigDecimal b) {
    super(b);
  }

  public NotNullFixed(java.math.BigDecimal b) {
    super(b);
  }

  public NotNullFixed(java.math.BigInteger b) {
    super(b);
  }

  public NotNullFixed(java.math.BigInteger b, int l) {
    super(b);
  }

  public NotNullFixed(long value, int scale) {
    super(value, scale);
  }

  public NotNullFixed(double d) {
    super(d);
  }

  public NotNullFixed(String s) {
    super(s);
  }

  public static NotNullFixed castToNotNull(Fixed value) {
    return (NotNullFixed)value;
  }

  /**
   * Checks whether this object is equal to the specified object.
   */
  public boolean equals(Object other) {
    return (other instanceof NotNullFixed)
      && super.equals(other);
  }
}
