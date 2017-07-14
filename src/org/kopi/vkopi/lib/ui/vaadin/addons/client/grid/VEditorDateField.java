/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import java.util.Date;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * The widget implementation of a date editor field. 
 */
public class VEditorDateField extends VEditorTextField {
  
  @Override
  public EditorDateFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorDateFieldConnector.class);
  }
  
  @Override
  protected boolean check(String text) {
    for (int i = 0; i < text.length(); i++) {
      char      c = text.charAt(i);
      
      if (!(((c >= '0') && (c <= '9')) || (c == '.') || (c == '/'))) {
        return false;
      }
    }
    
    return true;
  }
  
  @Override
  public void validate() throws InvalidEditorFieldException {
    parseDate(getText());
  }
  
  @Override
  protected boolean isNumeric() {
    return true;
  }
  
  /**
   * Parses the given date input.
   * @param f The input field.
   * @param s The date text.
   */
  @SuppressWarnings("deprecation")
  private void parseDate(String s) throws InvalidEditorFieldException {
    int                 day = 0;
    int                 month = 0;
    int                 year = -2;
    String[]            tokens = s.split("#|\\.|/");

    if (tokens.length == 0) {
      throw new InvalidEditorFieldException(this, "00003");
    }
    day = stringToInt(tokens[0]);
    if (tokens.length >= 2) {
      month = stringToInt(tokens[1]);
    }
    if (tokens.length >= 3) {
      year = stringToInt(tokens[2]);
    }
    if (tokens.length > 3 || day == -1 || month == -1 || year == -1) {
      throw new InvalidEditorFieldException(this, "00003");
    }

    if (month == 0) {
      Date       now = new Date();
      
      month = now.getMonth() + 1;
      year  = now.getYear() + 1900;
    } else if (year == -2) {
      Date       now = new Date();
      
      year  = now.getYear() + 1900;
    } else if (year < 50) {
      year += 2000;
    } else if (year < 100) {
      year += 1900;
    } else if (year < 1000) {
      // less than 4 digits cause an error in database while paring the 
      // sql statement
      throw new InvalidEditorFieldException(this, "00003");
    }

    if (!isDate(day, month, year)) {
      throw new InvalidEditorFieldException(this, "00003");
    }
    
    setText(format(year, month, day));
  }

  /**
   * 
   * @param in
   * @return
   */
  private static int stringToInt(String in) {
    try {
      return Integer.valueOf(in).intValue();
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Checks if the given year, month and day is a valid date
   * @param d The day
   * @param m The month
   * @param y The year
   * @return {@code true} if the given parameters corresponds to a valid date.
   */
  private static boolean isDate(int d, int m, int y) {
    if (y < 1 || m < 1 || m > 12 || d < 1) {
      return false;
    } else {
      switch (m) {
      case 2:
        return d <= (isLeapYear(y) ? 29 : 28);
      case 4:
      case 6:
      case 9:
      case 11:
        return d <= 30;
      default:
        return d <= 31;
      }
    }
  }

  /**
   * Checks if the given date is a leap year.
   * @param year The year
   * @return {@code true} if the year is leap.
   */
  private static boolean isLeapYear(int year) {
    return ((year % 4) == 0) && (((year % 100) != 0) || ((year % 400) == 0));
  }
  
  /**
   * Formats the given date to the standard pattern (dd.MM.yyyy)
   * @param year The date year.
   * @param month The date month.
   * @param day The date day.
   * @return The formatted date.
   */
  @SuppressWarnings("deprecation")
  private static String format(int year, int month, int day) {
    return DateTimeFormat.getFormat("dd.MM.yyyy").format(new Date(year - 1900, month - 1, day));
  }
}
