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
 * This class represents kopi month types
 */
public class Month extends Type {

  /**
   * Constructs a Month with a year and a month in this year
   */
  /*package*/ Month(int year, int month) {
    this.scalar = year * 12 + month - 1;
  }

  /**
   * Constructs a Month from a Date
   */
  /*package*/ Month(Date date) {
    this(date.getYear(), date.getMonth());
  }

  /**
   * Current month
   */
  public static NotNullMonth now() {
    Calendar	now = Calendar.getInstance();

    return new NotNullMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1);
  }

  /**
   * Parse the string to build the corresponding month using the
   * default locale
   *
   * @param     input   the date to parse
   * @param     format  the format of the date
   */
  public static NotNullMonth parse(String input, String format)
  {
    return parse(input, format, Locale.getDefault());
  }

  /**
   * Parse the string to build the corresponding month using the given
   * Locale
   *
   * @param     input   the date to parse
   * @param     format  the format of the date
   * @param     locale  the Locale to use
   */
  public static NotNullMonth parse(String input, String format, Locale locale)
  {
    GregorianCalendar   cal = new GregorianCalendar();

    try {
      cal.setTime((new SimpleDateFormat(format, locale)).parse(input));
    } catch (ParseException e) {
      throw new IllegalArgumentException();
    }
    return new NotNullMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
  }

  /**
   * Formats the month according to the given format using the default
   * Locale
   *
   * @param     format  the format. see SimpleDateFormat
   */
  public String format(String format) {
    return format(format, Locale.getDefault());
  }

  /**
   * Formats the month according to the given format and Locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the Locale to use
   */
  public String format(String format, Locale locale) {
    GregorianCalendar   cal = new GregorianCalendar();

    cal.set(Calendar.YEAR, getYear());
    cal.set(Calendar.MONTH, getMonth() - 1);
    cal.set(Calendar.DAY_OF_MONTH, 1);

    return new SimpleDateFormat(format, locale).format(cal.getTime());
  }

  /**
   * Clones this object.
   */
  public NotNullMonth copy() {
    return new NotNullMonth(scalar / 12, scalar % 12 + 1);
  }

  // ----------------------------------------------------------------------
  // IN PLACE OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Adds the specified number of months to this month.
   */
  public void addTo(int months) {
    this.scalar += months;
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Returns a Month with the specified number of months added to this Month.
   */
  public NotNullMonth add(int months) {
    return new NotNullMonth((scalar + months) / 12, (scalar + months) % 12 + 1);
  }

  /**
   * subtract
   * @returns the number of month between two Months
   */
  public Integer subtract(Month other) {
    return other == null ? null : new Integer(subtract((NotNullMonth)other));
  }

  /**
   * subtract
   * @returns the number of month between two Months
   */
  public int subtract(NotNullMonth other) {
    return scalar - ((Month)other).scalar;
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Compares to another month.
   *
   * @param	other	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public int compareTo(Month other) {
    int		v1 = this.scalar;
    int		v2 = other.scalar;

    return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
  }


  public int compareTo(Object other) {
    return compareTo((Month) other);
  }
  /**
   * Returns the year of the month (by example 1999 or may be 2000 on year after)
   */
  public int getYear() {
    return scalar / 12;
  }

  /**
   * Returns the month number (starts at 1, ends at 12)
   */
  public int getMonth() {
    return scalar % 12 + 1; // month to start at 1
  }

  /**
   * Returns the first day of this month.
   */
  public NotNullDate getFirstDay() {
    return new NotNullDate(scalar / 12, scalar % 12 + 1, 1);
  }

  /**
   * Returns the last day of this month.
   */
  public NotNullDate getLastDay() {
    // this is the first day of the next month - 1 day.
    return new NotNullDate((scalar + 1) / 12, (scalar + 1) % 12 + 1, 1).add(-1);
  }

  /**
   * Transforms this month in a date (the first day of the month)
   * @deprecated
   */
  public NotNullDate getDate() {
    return getFirstDay();
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Compares two objects
   */
  public boolean equals(Object other) {
    return (other instanceof Month) && this.scalar == ((Month)other).scalar;
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public String toString(Locale locale) {
    StringBuffer	buffer = new StringBuffer();
    int			year = scalar / 12;
    int			month = scalar % 12 + 1;

    if (locale == Locale.GERMAN ||
	locale == Locale.FRENCH ||
	locale == Locale.US ||
	locale == Locale.ENGLISH) {
      buffer.append(month / 10);
      buffer.append(month % 10);
      buffer.append('.');
      buffer.append(year);
    } else {
      buffer.append(year);
      buffer.append('.');
      buffer.append(month / 10);
      buffer.append(month % 10);
    }

    return buffer.toString();
  }

  /**
   * Represents the value in sql
   */
  public String toSql() {
    int			year = scalar / 12;
    int			month = scalar % 12 + 1;

    return "{fn MONTH(" + year + ", " + month + ")}";
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int		scalar;

  // --------------------------------------------------------------------
  // CONSTANTS
  // --------------------------------------------------------------------

  public static final NotNullMonth	DEFAULT = new NotNullMonth(1900, 1);
}
