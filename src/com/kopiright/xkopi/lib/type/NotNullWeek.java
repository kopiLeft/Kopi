/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
 * This class represents kopi week types
 */
public final class NotNullWeek extends Week {

  /**
   * Constructs a Week with a year and a week in this year.
   */
  public NotNullWeek(int year, int week) {
    super(year, week);
  }

  /**
   * Constructs a Week from a Date.
   */
  public NotNullWeek(Date date) {
    super(date);
  }

  public static NotNullWeek castToNotNull(Week value) {
    return (NotNullWeek)value;
  }
}
