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

package com.kopiright.xkopi.lib.type;

/**
 * This class represents kopi date types
 */
public final class NotNullDate extends Date {

  public NotNullDate(int year, int month, int day) {
    super(year, month, day);
  }

  public NotNullDate(java.sql.Date date) {
    super(date);
  }

  public NotNullDate(String image) {
    super(image);
  }

  public NotNullDate(java.util.Calendar calendar) {
    super(calendar);
  }

  /**
   * Constructs a Date from a scalar representation.
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  public NotNullDate(int scalar) {
    super(scalar);
  }

  public static NotNullDate castToNotNull(Date value) {
    return (NotNullDate)value;
  }
}
