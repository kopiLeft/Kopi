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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This class represents kopi time types
 */
public class Time extends Type {

  /*package*/ Time(int hours, int minutes, int seconds) {
    this.scalar = (hours * 3600 + minutes * 60 + seconds)  % (3600 * 24);
  }

  /*package*/ Time(int hours, int minutes) {
    this(hours, minutes, 0);
  }

  /*package*/ Time(java.sql.Time time) {
    int		hours;
    int		minutes;
    int		seconds;

    synchronized (calendar) {
      calendar.setTime(time);
      hours = calendar.get(Calendar.HOUR_OF_DAY);
      minutes = calendar.get(Calendar.MINUTE);
      seconds = calendar.get(Calendar.SECOND);
    }

    this.scalar = (hours * 3600 + minutes * 60 + seconds) % (3600 * 24);
  }

  /*package*/ Time(String image) {
    this(java.sql.Time.valueOf(image));
  }

  /**
   * Current time
   */
  public static NotNullTime now() {
    Calendar	now = Calendar.getInstance();

    return new NotNullTime(now.get(Calendar.HOUR_OF_DAY),
			   now.get(Calendar.MINUTE),
			   now.get(Calendar.SECOND));
  }

  /**
   * Parse the string to build the corresponding time using the
   * default Locale
   *
   * @param     input   the time parse
   * @param     format  the format of the date
   */
  public static NotNullTime parse(String input, String format)
  {
    return parse(input, format, Locale.getDefault());
  }

  /**
   * Parse the string to build the corresponding time using the
   * default Locale
   *
   * @param     input   the time parse
   * @param     format  the format of the date
   * @param     locale  the Locale to use
   */
  public static NotNullTime parse(String input, String format, Locale locale)
  {
    GregorianCalendar   cal = new GregorianCalendar();

    try {
      cal.setTime((new SimpleDateFormat(format, locale)).parse(input));
    } catch (ParseException e) {
      throw new IllegalArgumentException();
    }
    return new NotNullTime(cal.get(Calendar.HOUR_OF_DAY),
                           cal.get(Calendar.MINUTE),
                           cal.get(Calendar.SECOND));
  }

  /**
   * Formats the time according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  public String format(String format) {
    return format(format, Locale.getDefault());
  }

  /**
   * Formats the time according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  public String format(String format, Locale locale) {
    GregorianCalendar   cal = new GregorianCalendar();

    cal.set(Calendar.HOUR_OF_DAY, getHours());
    cal.set(Calendar.MINUTE, getMinutes());
    cal.set(Calendar.SECOND, getSeconds());

    return new SimpleDateFormat(format).format(cal.getTime());
  }

  // ----------------------------------------------------------------------
  // COMPILER METHODS - DO NOT USE OUTSIDE OF THE LIBRARY
  // ----------------------------------------------------------------------

  /**
   * Constructs a time from a scalar
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  /*package*/ Time(int scalar) {
    this.scalar = scalar % (3600 * 24);
  }

  /**
   * Sets the base value for this object
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  public void setScalar(int scalar) {
    this.scalar = scalar;
  }

  /**
   * Returns the base value for this object
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  public int getScalar() {
    return scalar;
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------

  public NotNullTime add(int seconds) {
    return new NotNullTime(this.scalar + seconds);
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Compares to another time.
   *
   * @param	other	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public int compareTo(Time other) {
    int		v1 = this.scalar;
    int		v2 = other.scalar;

    return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
  }

  public int compareTo(Object other) {
    return compareTo((Time) other);
  }

  /**
   * Returns the hour represented by this object.
   */
  public int getHours() {
    return scalar / 3600;
  }

  /**
   * Returns the minutes past the hour represented by this object.
   */
  public int getMinutes() {
    return (scalar / 60) % 60;
  }

  /**
   * Returns the sconds past the minute represented by this object.
   */
  public int getSeconds() {
    return scalar % 60;
  }

  // !!! TO BE REMOVED
  public java.sql.Time getSqlTime() {
    synchronized (calendar) {
      calendar.set(Calendar.HOUR_OF_DAY, scalar / 3600);
      calendar.set(Calendar.MINUTE, (scalar / 60) % 60);
      calendar.set(Calendar.SECOND, scalar % 60);

      return new java.sql.Time(calendar.getTime().getTime());
    }
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Compares two objects
   */
  public boolean equals(Object other) {
    return (other instanceof Time) && ((Time)other).scalar == scalar;
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public String toString(Locale locale) {
    StringBuffer	buffer = new StringBuffer();
    int			hours = scalar / 3600;
    int			minutes = (scalar / 60) % 60;

    buffer.append(hours / 10);
    buffer.append(hours % 10);
    buffer.append(':');
    buffer.append(minutes / 10);
    buffer.append(minutes % 10);

    return buffer.toString();
  }

  /**
   * Represents the value in sql
   */
  public String toSql() {
    StringBuffer	buffer = new StringBuffer();
    int			hours = scalar / 3600;
    int			minutes = (scalar / 60) % 60;
    int			seconds = scalar % 60;

    buffer.append("{t '");
    buffer.append(hours / 10);
    buffer.append(hours % 10);
    buffer.append(':');
    buffer.append(minutes / 10);
    buffer.append(minutes % 10);
    buffer.append(':');
    buffer.append(seconds / 10);
    buffer.append(seconds % 10);
    buffer.append("'}");

    return buffer.toString();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static GregorianCalendar	calendar = new GregorianCalendar();

  private int				scalar;

  // --------------------------------------------------------------------
  // CONSTANTS
  // --------------------------------------------------------------------

  public static final Time	DEFAULT = new Time(0, 0, 0);
}
