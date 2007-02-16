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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class represents kopi week types
 */
public class Week extends Type {

  /**
   * Constructs a Week with a year and a week in this year
   * @param	year		the year
   * @param	week		the week of year (1 .. 53)
   */
  /*package*/ Week(int year, int week) {
    this.scalar = year * 53 + week - 1;
  }

  /**
   * Constructs a Week from a Date
   */
  /*package*/ Week(Date date) {
    this.scalar = iso8601(date.getYear(), date.getMonth(), date.getDay());
  }

  /**
   * Current week
   */
  public static NotNullWeek now() {
    Calendar	now = Calendar.getInstance();

    return new NotNullWeek(now.get(Calendar.YEAR), now.get(Calendar.WEEK_OF_YEAR));
  }

  /**
   * Clones this object.
   */
  public NotNullWeek copy() {
    return new NotNullWeek(scalar / 53, scalar % 53 + 1);
  }

  // ----------------------------------------------------------------------
  // IN PLACE OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Adds the specified number of months to this month.
   */
  public void addTo(int weeks) {
    this.scalar += weeks;
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Returns a Week with the specified number of weeks added to this Week.
   */
  public NotNullWeek add(int weeks) {
    return new NotNullWeek((scalar + weeks) / 53, (scalar + weeks) % 53 + 1);
  }

  /**
   * subtract
   * @returns the number of weeks between two Weeks
   */
  public Integer subtract(Week other) {
    return other == null ? null : new Integer(subtract((NotNullWeek)other));
  }

  /**
   * subtract
   * @returns the number of weeks between two Weeks
   */
  public int subtract(NotNullWeek other) {
    return scalar - ((Week)other).scalar;
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * Compares to another week.
   *
   * @param	other	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public int compareTo(Week other) {
    int		v1 = this.scalar;
    int		v2 = other.scalar;

    return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
  }

  public int compareTo(Object other) {
    return compareTo((Week) other);
  }

  /**
   * Returns the week number (starts at 1, ends at 53)
   */
  public int getWeek() {
    return scalar % 53 + 1; // week to start at 1
  }

  /**
   * Returns the year of the week (by example 1999 or may be 2000 on year after)
   */
  public int getYear() {
    return scalar / 53;
  }

  /**
   * Returns the date specified by this week and a day of week.
   * @param	weekday		the day of week (monday = 1, sunday = 7)
   */
  public NotNullDate getDate(int weekday) {
    int		year;
    int		month;
    int		day;

    synchronized (calendar) {
      calendar.clear();
      calendar.set(Calendar.YEAR, scalar / 53);
      calendar.set(Calendar.WEEK_OF_YEAR, scalar % 53 + 1);
      calendar.set(Calendar.DAY_OF_WEEK, (weekday % 7) + 1);	// Calendar.MONDAY = 2

      year = calendar.get(Calendar.YEAR);
      month = calendar.get(Calendar.MONTH) + 1;
      day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    return new NotNullDate(year, month, day);
  }

  /**
   * Returns the first day of this week.
   */
  public NotNullDate getFirstDay() {
    return getDate(1);
  }

  /**
   * Returns the last day of this week.
   */
  public NotNullDate getLastDay() {
    return getDate(7);
  }

  /**
   * Transforms this week into a date (the first day of the week)
   * @deprecated
   */
  public NotNullDate getDate() {
    return getDate(1);
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Compares two objects
   */
  public boolean equals(Object other) {
    return (other instanceof Week) && this.scalar == ((Week)other).scalar;
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public String toString(java.util.Locale locale) {
    int year = scalar / 53;
    int	week = scalar % 53 + 1;

    return (week < 10 ? "0" + week : "" + week) + "." + year;
  }

  /**
   * Represents the value in sql
   */
  public String toSql() {
    int year = scalar / 53;
    int	week = scalar % 53 + 1;

    return "{fn WEEK(" + year + ", " + week + ")}";
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------

  /**
   * Returns a scalar representation of the Year/Week combination
   * for the specified date.
   * @param	year		the year
   * @param	month		the month (1 .. 12)
   * @param	day		the day of month ( 1 .. 31)
   * @return	year * 53 + (week - 1)
   */
  private static int iso8601(int year, int month, int day) {
    // 2. Find if Y is LeapYear
    //    if (Y % 4 = 0  and  Y % 100 != 0) or Y % 400 = 0
    //       then
    //          Y is LeapYear
    //       else
    //          Y is not LeapYear
    boolean	leapYear = isLeapYear(year);

    // 3. Find if Y-1 is LeapYear
    boolean	leapYear_m_1 = isLeapYear(year - 1);

    // 4. Find the DayOfYearNumber for Y M D
    //    Mnth[1] = 0    Mnth[4] = 90    Mnth[7] = 181   Mnth[10] = 273
    //    Mnth[2] = 31   Mnth[5] = 120   Mnth[8] = 212   Mnth[11] = 304
    //    Mnth[3] = 59   Mnth[6] = 151   Mnth[9] = 243   Mnth[12] = 334
    //    DayOfYearNumber = D + Mnth[M]
    //    if Y is LeapYear and M > 2
    //       then
    //          DayOfYearNumber += 1
    int		dayOfYearNumber = day + DAYS_BEFORE_MONTH[month - 1];

    if (leapYear && month > 2) {
      dayOfYearNumber += 1;
    }

    // 5. Find the Jan1Weekday for Y (Monday=1, Sunday=7)
    //    YY = (Y-1) % 100
    //    C = (Y-1) - YY
    //    G = YY + YY/4
    //    Jan1Weekday = 1 + (((((C / 100) % 4) x 5) + G) % 7)
    int		yy = (year - 1) % 100;
    int		c = (year - 1) - yy;
    int		g = yy + yy / 4;
    int		jan1Weekday = 1 + (((((c / 100) % 4) * 5) + g) % 7);

    // 6. Find the Weekday for Y M D
    //    H = DayOfYearNumber + (Jan1Weekday - 1)
    //    Weekday = 1 + ((H -1) % 7)
    int		h = dayOfYearNumber + (jan1Weekday - 1);
    int		weekday = 1 + ((h -1) % 7);

    // 7. Find if Y M D falls in YearNumber Y-1, WeekNumber 52 or 53
    //    if DayOfYearNumber <= (8-Jan1Weekday) and Jan1Weekday > 4
    //       then
    //          YearNumber = Y - 1
    //          if Jan1Weekday = 5 or (Jan1Weekday = 6 and Y-1 is LeapYear)
    //             then
    //                WeekNumber = 53
    //             else
    //                WeekNumber = 52
    //       else
    //          YearNumber = Y
    int		yearNumber;
    int		weekNumber;
    if (dayOfYearNumber <= (8 - jan1Weekday) && jan1Weekday > 4) {
      yearNumber = year - 1;
      weekNumber = jan1Weekday == 5 || (jan1Weekday == 6 && leapYear_m_1) ? 53 : 52;
    } else {
      yearNumber = year;
      weekNumber = -10000000;
    }

    // 8. Find if Y M D falls in YearNumber Y+1, WeekNumber 1
    //    if Y is LeapYear
    //       then
    //          I = 366
    //       else
    //          I = 365
    //     if (I - DayOfYearNumber) < (4 - Weekday)
    //        then
    //           YearNumber = Y + 1
    //           WeekNumber = 1
    //        else
    //           YearNumber = Y
    int		i = leapYear ? 366 : 365;
    if ((i - dayOfYearNumber) < (4 - weekday)) {
      yearNumber = year + 1;
      weekNumber = 1;
    } /*else {
      yearNumber = year;
    }*/

    // 9. Find if Y M D falls in YearNumber Y, WeekNumber 1 through 53
    //    if YearNumber = Y
    //       then
    //          J = DayOfYearNumber + (7 - Weekday) + (Jan1Weekday -1)
    //          WeekNumber = J / 7
    //          if Jan1Weekday > 4
    //                WeekNumber -= 1
    if (yearNumber == year) {
      int	j = dayOfYearNumber + (7 - weekday) + (jan1Weekday - 1);
      weekNumber = j / 7;
      if (jan1Weekday > 4) {
	weekNumber -= 1;
      }
    }

    // 10. Output ISO Week Date:
    //    if WeekNumber < 10
    //       then
    //          WeekNumber = "0" & WeekNumber  (WeekNumber requires 2 digits)
    //    YearNumber - WeekNumber - Weekday    (Optional: "W" & WeekNumber)

    return yearNumber * 53 + weekNumber - 1;
  }

  /**
   * Returns true iff the specified year is a leap year.
   */
  private static boolean isLeapYear(int year) {
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static final int[]		DAYS_BEFORE_MONTH = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};

  private static GregorianCalendar	calendar;

  static {
    calendar = new GregorianCalendar();
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    calendar.setMinimalDaysInFirstWeek(4);
  }

  private int				scalar;

  // --------------------------------------------------------------------
  // CONSTANTS
  // --------------------------------------------------------------------

  public static final NotNullWeek	DEFAULT = new NotNullWeek(0, 0);
}
