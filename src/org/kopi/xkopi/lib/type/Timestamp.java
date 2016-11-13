/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.xkopi.lib.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This class represents kopi timestamp types
 */
public class Timestamp extends Type {

  /*package*/ Timestamp(java.sql.Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  /*package*/ Timestamp(String image) {
    this.timestamp = java.sql.Timestamp.valueOf(image);
  }

  /*package*/ Timestamp(long millis) {
    timestamp = new java.sql.Timestamp(millis);
  }

  /*package*/ Timestamp(Calendar calendar) {
    if (calendar != null) {
      timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
    }
  }

  /**
   * now's timestamp
   */
  public static NotNullTimestamp now() {
    return new NotNullTimestamp(System.currentTimeMillis());
  }

  /**
   * Parse the string to build the corresponding timestamp using the
   * default Locale
   *
   * @param     input   the timestamp to parse
   * @param     format  the format of the timestamp
   */
  public static NotNullTimestamp parse(String input, String format)
  {
    return parse(input, format, Locale.getDefault());
  }

  /**
   * Parse the string to build the corresponding timestamp
   *
   * @param     input   the timestamp to parse
   * @param     format  the format of the timestamp
   * @param     locale  the Locale to use
   */
  public static NotNullTimestamp parse(String input,
                                       String format,
                                       Locale locale)
  {
    GregorianCalendar   cal = new GregorianCalendar();

    try {
      cal.setTime((new SimpleDateFormat(format, locale)).parse(input));
    } catch (ParseException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e.getMessage());
    }
    return new NotNullTimestamp(cal.getTimeInMillis());
  }

  /**
   * Formats the timestamp according to the given format using the default
   * locale
   *
   * @param     format  the format. see SimpleDateFormat
   */
  public String format(String format) {
    return format(format, Locale.getDefault());
  }

  /**
   * Formats the timestamp according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  public String format(String format, Locale locale) {
    return new SimpleDateFormat(format, locale).format(timestamp);
  }
  
  /**
   * create an instance of calendar to represent timestamp.
   */
  public GregorianCalendar toCalendar() {
    GregorianCalendar   calendar = new GregorianCalendar();
    
    calendar.clear();
    calendar.setTimeInMillis(timestamp.getTime());
    return calendar;
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------

  public NotNullTimestamp add(long millis) {
    return new NotNullTimestamp(timestamp.getTime() + millis);
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
  public int compareTo(Timestamp other) {
    return timestamp.compareTo(other.timestamp);
  }

  // !!! TO BE REMOVED
  public int compareTo(Object other) {
    return compareTo((Timestamp) other);
  }

  public java.sql.Timestamp getSqlTimestamp() {
    return timestamp;
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Compares two objects
   */
  public boolean equals(Object other) {
    return (other instanceof Timestamp) &&
      ((Timestamp)other).timestamp.equals(timestamp);
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public String toString(java.util.Locale locale) {
    StringBuffer        tmp = new StringBuffer(normal.format(timestamp));
    final int           nanos = timestamp.getNanos();

    if (nanos >= 100) {
      tmp.append(nanos);
    } else if (nanos >= 10) {
      tmp.append("0" + nanos);
    } else {
      tmp.append("00" + nanos);
    }

    return tmp.toString();
  }

  /**
   * Represents the value in sql
   */
  public String toSql() {
    StringBuffer        tmp = new StringBuffer();
    String              micro;

    micro = String.valueOf((int) (timestamp.getNanos() /1000));
    tmp.append("00000".substring(0, 6-micro.length()));
    tmp.append(micro);

    return new SimpleDateFormat("'{ts '''yyyy'-'MM'-'dd' 'HH':'mm':'ss'." + tmp.toString() + "''}'").format(timestamp);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static final Timestamp		DEFAULT = new Timestamp(0);

  private static SimpleDateFormat	normal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  private java.sql.Timestamp		timestamp;
}
