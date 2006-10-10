/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This class represents kopi date types
 */
public class Date extends Type {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /*package*/ Date(int year, int month, int day) {
    this.scalar = gregorianToJulian(year, month, day);
  }

  /*package*/ Date(java.sql.Date date) {
    synchronized(calendar) {
      calendar.setTime(date);
      this.scalar = gregorianToJulian(calendar.get(Calendar.YEAR),
				      calendar.get(Calendar.MONTH) + 1,
				      calendar.get(Calendar.DAY_OF_MONTH));
    }
  }

  /**
   * Parses a date of format 'yyyy.MM.dd' or 'yyyy-MM-dd'
   */
  /*package*/ Date(String image) {
    Pattern 	pattern;
    Matcher 	matcher;

    pattern = Pattern.compile("(\\d\\d\\d\\d)[-.]{1}(\\d\\d?)[-.]{1}(\\d\\d?)");
    matcher = pattern.matcher(image);
    if (matcher.matches()) {
      String[] res = Pattern.compile("[-.]").split(image);
    this.scalar = gregorianToJulian(Integer.parseInt(res[0]),
				    Integer.parseInt(res[1]),
				    Integer.parseInt(res[2]));

    } else {
        throw new IllegalArgumentException("invalid date string " + image);
    }
  }

  /**
   * Today's date
   */
  public static NotNullDate now() {
    return new NotNullDate((int)(System.currentTimeMillis() / 86400000L) + ((Date)ORIGIN).scalar);
  }

  /**
   * Parse the string to build the corresponding date using the
   * default Locale
   *
   * @param     input   the date to parse
   * @param     format  the format of the date
   */
  public static NotNullDate parse(String input, String format)
  {
    return parse(input, format, Locale.getDefault());
  }

  /**
   * Parse the string to build the corresponding date
   *
   * @param     input   the date to parse
   * @param     format  the format of the date
   * @param     locale  the Locale to use
   */
  public static NotNullDate parse(String input,
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
    return new NotNullDate(cal.get(Calendar.YEAR),
                           cal.get(Calendar.MONTH) + 1,
                           cal.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Formats the date according to the given format using the default
   * locale
   *
   * @param     format  the format. see SimpleDateFormat
   */
  public String format(String format) {
    return format(format, Locale.getDefault());
  }

  /**
   * Formats the date according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  public String format(String format, Locale locale) {
    GregorianCalendar   cal = new GregorianCalendar();

    cal.set(Calendar.YEAR, getYear());
    cal.set(Calendar.MONTH, getMonth() - 1);
    cal.set(Calendar.DAY_OF_MONTH, getDay());

    return new SimpleDateFormat(format, locale).format(cal.getTime());
  }


  // ----------------------------------------------------------------------
  // COMPILER METHODS - DO NOT USE OUTSIDE OF THE LIBRARY
  // ----------------------------------------------------------------------

  /**
   * Constructs a Date from a scalar
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  /*package*/ Date(int scalar) {
    this.scalar = scalar;
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
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the year of the month (by example 1999 or may be 2000 one year after)
   */
  public int getYear() {
    return julianToGregorian(this.scalar)[0];
  }

  /**
   * Returns the month number (starts at 1, ends at 12)
   */
  public int getMonth() {
    return julianToGregorian(this.scalar)[1];
  }

  /**
   * Returns the day number (starts at 1, ends at 31)
   */
  public int getDay() {
    return julianToGregorian(this.scalar)[2];
  }

  /**
   * Returns the day number (starts at 1, ends at 7)
   */
  public int getWeekday() {
    synchronized(calendar) {
      int[]	gregorian = julianToGregorian(this.scalar);

      calendar.set(Calendar.YEAR, gregorian[0]);
      calendar.set(Calendar.MONTH, gregorian[1] - 1);
      calendar.set(Calendar.DAY_OF_MONTH, gregorian[2]);

      return calendar.get(Calendar.DAY_OF_WEEK);
    }
  }

  // ----------------------------------------------------------------------
  // ARITHMETIC OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Returns a Date with the specified number of days added to this Date.
   */
  public NotNullDate add(int days) {
    return new NotNullDate(this.scalar + days);
  }

  /**
   * Returns the number of days between two dates.
   */
  public Integer subtract(Date other) {
    return other == null ? null : new Integer(subtract((NotNullDate)other));
  }

  /**
   * Returns the number of days between two dates.
   */
  public int subtract(NotNullDate other) {
    return this.scalar - ((Date)other).scalar;
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Compares two objects
   */
  public boolean equals(Object other) {
    return (other instanceof Date) && this.scalar == ((Date)other).scalar;
  }

  /**
   * Compares to another date.
   *
   * @param	other	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public int compareTo(Date other) {
    int		v1 = this.scalar;
    int		v2 = other.scalar;

    return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
  }

  public int compareTo(Object other) {
    return compareTo((Date) other);
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public String toString(Locale locale) {
    StringBuffer	buffer = new StringBuffer();
    int[]		gregorian = julianToGregorian(this.scalar);

    // !!! taoufik 20061010
    // LOCALIZATION NOT HANDLED
    buffer.append(gregorian[2] / 10);
    buffer.append(gregorian[2] % 10);
    buffer.append('.');
    buffer.append(gregorian[1] / 10);
    buffer.append(gregorian[1] % 10);
    buffer.append('.');
    buffer.append(gregorian[0]);
      
    return buffer.toString();
  }

  /**
   * Represents the value in sql
   */
  public String toSql() {
    StringBuffer	buffer = new StringBuffer();
    int[]		gregorian = julianToGregorian(this.scalar);

    buffer.append("{d '");
    buffer.append(gregorian[0]);
    buffer.append('-');
    buffer.append(gregorian[1] / 10);
    buffer.append(gregorian[1] % 10);
    buffer.append('-');
    buffer.append(gregorian[2] / 10);
    buffer.append(gregorian[2] % 10);
    buffer.append("'}");

    return buffer.toString();
  }

  // --------------------------------------------------------------------
  // JULIAN DATES
  // --------------------------------------------------------------------

  /*
   * NOTE:
   * Gregorian calendar dates are converted to the corresponding Julian
   * day number according to Algorithm 199 from
   * Communications of the ACM, Volume 6, No. 8, (Aug. 1963), p. 444.
   * Gregorian calendar started on Sep. 14, 1752.
   * The corresponding function are not valid before that.
   */

  /*
   * Returns the julian day number of the date specified by year, month, day
   */
  private static int gregorianToJulian(int y, int m, int d) {
    int		c;

    if (m > 2)
      m -= 3;
    else {
      m += 9;
      y -= 1;
    }

    c = y / 100;
    y = y % 100;

    return ((146097*c)>>2) + ((1461*y)>>2) + (153*m + 2)/5 + d + 1721119;
  }

  /*
   * Returns the date specified by a julian day number as year, month, day.
   */
  private static int[] julianToGregorian(int julian) {
    int		y;
    int		m;
    int		d;
    int		j;

    j = julian - 1721119;
    y = ((j<<2) - 1) / 146097;
    j = (j<<2) - 1 - 146097*y;
    d = j>>2;
    j = ((d<<2) + 3) / 1461;
    d = (d<<2) + 3 - 1461*j;
    d = (d + 4)>>2;
    m = (5*d - 3)/153;
    d = 5*d - 3 - 153*m;
    d = (d + 5)/5;
    y = 100*y + j;

    if (m < 10)
    m += 3;
    else {
      m -= 9;
      y += 1;
    }

    return new int[]{ y, m, d };
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int				scalar;

  private static GregorianCalendar	calendar = new GregorianCalendar();

  static {
    calendar.setMinimalDaysInFirstWeek(4);
  }

  private static final NotNullDate	ORIGIN = new NotNullDate(1970, 1, 1);
  public static final NotNullDate	DEFAULT = new NotNullDate(0);
}
