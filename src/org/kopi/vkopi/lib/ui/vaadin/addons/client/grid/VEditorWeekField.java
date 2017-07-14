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

/**
 * The widget implementation of a week editor field. 
 */
public class VEditorWeekField extends VEditorTextField {

  @Override
  public EditorWeekFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorWeekFieldConnector.class);
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
    parseWeek(getText());
  }
  
  @Override
  protected boolean isNumeric() {
    return true;
  }
  
  /**
   * Parses the given string entry as a week.
   * @param field The concerned input zone.
   * @param s The text to be parsed
   * @throws InvalidEditorFieldException When the text is not a valid week.
   */
  @SuppressWarnings("deprecation")
  private void parseWeek(String s) throws InvalidEditorFieldException {
    int         week = 0;
    int         year = -1;
    int         bp = 0;
    int         state;
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
        throw new InvalidEditorFieldException(this, "00008");
      }
    }
    if (state == -1) {
      throw new InvalidEditorFieldException(this, "00008");
    }

    if (year == -1) {
      Date       now = new Date();

      year  = now.getYear() + 1900;
    } else if (year < 50) {
      year += 2000;
    } else if (year < 100) {
      year += 1900;
    }

    setText(toString(year, week));
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
