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
 * $Id: NotNullTime.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.lib.type;

/**
 * This class represents kopi time types
 */
public final class NotNullTime extends Time {

  public NotNullTime(int hours, int minutes, int seconds) {
    super(hours, minutes, seconds);
  }

  public NotNullTime(int hours, int minutes) {
    super(hours, minutes);
  }

  public NotNullTime(java.sql.Time time) {
    super(time);
  }

  public NotNullTime(String image) {
    super(image);
  }

  /**
   * Constructs a time from a scalar representation.
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  public NotNullTime(int scalar) {
    super(scalar);
  }

  public static NotNullTime castToNotNull(Time value) {
    return (NotNullTime)value;
  }
}
