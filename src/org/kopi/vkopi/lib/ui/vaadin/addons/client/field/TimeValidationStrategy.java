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

/**
 * Time validation strategy.
 */
public class TimeValidationStrategy extends AllowAllValidationStrategy {

  @Override
  public boolean validate(char c) {
    return (c >= '0' && c <= '9') || c == ':';
  }
  
  @Override
  public void checkType(VInputTextField field, String text) throws CheckTypeException {
    if ("".equals(text)) {
      field.setText(null);
    } else {
      int       hours = -1;
      int       minutes = 0;
      String    buffer = text + '\0';
      int       bp = 0;
      int       state;

      for (state = 1; state > 0; bp += 1) {
        switch (state) {
        case 1: /* The first hours' digit */
          if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
            hours = buffer.charAt(bp) - '0';
            state = 2;
          } else if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
          break;

        case 2: /* The second hours' digit */
          if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
            hours = 10*hours + (buffer.charAt(bp) - '0');
            state = 3;
          } else if (buffer.charAt(bp) == ':') {
            state = 4;
          } else if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
          break;

        case 3: /* The point between hours and minutes */
          if (buffer.charAt(bp) == ':') {
            state = 4;
          } else if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
          break;

        case 4: /* The first minutes' digit */
          if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
            minutes = buffer.charAt(bp) - '0';
            state = 5;
          } else if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
          break;

        case 5: /* The second minutes' digit */
          if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
            minutes = 10*minutes + (buffer.charAt(bp) - '0');
            state = 6;
          } else {
            state = -1;
          }
          break;

        case 6: /* The end */
          if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
        }
      }

      if (state == -1) {
        throw new CheckTypeException(field, "00007");
      }

      if (hours == -1) {
        field.setText(null);
      } else {
        if (! isTime(hours, minutes)) {
          throw new CheckTypeException(field, "00007");
        }

        field.setText(toString(hours, minutes));
      }
    }
  }

  /**
   * Checks if the given time is valid.
   * @param h The hours.
   * @param m The minutes.
   * @return {@code true} if the given time is valid.
   */
  private boolean isTime(int h, int m) {
    return h >= 0 && h < 24 && m >= 0 && m < 60;
  }

  /**
   * Returns the string representation of the given time.
   * @param hours The time hours.
   * @param minutes The time minutes.
   * @return The string representation of the given time.
   */
  private String toString(int hours, int minutes) {
    StringBuffer        buffer = new StringBuffer();

    buffer.append(hours / 10);
    buffer.append(hours % 10);
    buffer.append(':');
    buffer.append(minutes / 10);
    buffer.append(minutes % 10);

    return buffer.toString();
  }
}
