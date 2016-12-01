/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.Date;

/**
 * Week validation strategy.
 */
public class WeekValidationStrategy extends AllowAllValidationStrategy {
  
  @Override
  public boolean validate(char c) {
    return  ((c >= '0') && (c <= '9')) || (c == '.') || (c == '/');
  }
  
  @Override
  public void checkType(VInputTextField field, String text) throws CheckTypeException {
    if ("".equals(text)) {
      field.setText(null);
    } else {
      parseWeek(field, text);
    }
  }

  /**
   * Parses the given string entry as a week.
   * @param field The concerned input zone.
   * @param s The text to be parsed
   * @throws CheckTypeException When the text is not a valid week.
   */
  @SuppressWarnings("deprecation")
  private void parseWeek(VInputTextField field, String s) throws CheckTypeException {
    int week = 0;
    int year = -1;
    int bp = 0;
    int state;
    String      buffer = s + '\0';

    for (state = 1; state > 0; bp += 1) {
      switch (state) {
      case 1: /* The first week's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          week = buffer.charAt(bp) - '0';
          state = 2;
        } else {
          state = -1;
        }
        break;

      case 2: /* The second week's digit  */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          week = 10*week + (buffer.charAt(bp) - '0');
          state = 3;
          } else if (buffer.charAt(bp) == '.' || buffer.charAt(bp) == '/') {
            state = 4;
          } else if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
        break;

      case 3: /* The first point : between week and year */
        if (buffer.charAt(bp) == '.' || buffer.charAt(bp) == '/') {
          state = 4;
        } else if (buffer.charAt(bp) == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;

      case 4: /* The first year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = buffer.charAt(bp) - '0';
          state = 5;
        } else if (buffer.charAt(bp) == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;

      case 5: /* The second year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = 10*year + (buffer.charAt(bp) - '0');
          state = 6;
        } else {
          state = -1;
        }
        break;

      case 6: /* The third year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = 10*year + (buffer.charAt(bp) - '0');
          state = 7;
        } else if (buffer.charAt(bp) == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;

      case 7:   /* The fourth year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = 10*year + (buffer.charAt(bp) - '0');
          state = 8;
        } else {
          state = -1;
        }
        break;

      case 8:   /* The end */
        if (buffer.charAt(bp)  == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;
      default:
        throw new CheckTypeException(field, "00008");
      }
    }
    if (state == -1) {
      throw new CheckTypeException(field, "00008");
    }

    if (year == -1) {
      Date       now = new Date();

      year  = now.getYear() + 1900;
    } else if (year < 50) {
      year += 2000;
    } else if (year < 100) {
      year += 1900;
    }

    field.setText(toString(year, week));
  }
  
  /**
   * Converts the given week to its string representation.
   * @param year The week year.
   * @param week The week number.
   * @return The string representation of the week.
   */
  private String toString(int year, int week) {
    return (week < 10 ? "0" + week : "" + week) + "." + year;
  }
}
