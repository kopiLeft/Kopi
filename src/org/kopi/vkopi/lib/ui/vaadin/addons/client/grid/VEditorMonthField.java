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
 * The widget implementation of a month editor field.
 */
public class VEditorMonthField extends VEditorTextField {
  
  @Override
  public EditorMonthFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorMonthFieldConnector.class);
  }
  
  @Override
  protected boolean check(String text) {
    for (int i = 0; i < text.length(); i++) {
      char      c = text.charAt(i);
      
      if (!(((c >= '0') && (c <= '9')) || (c == '.'))) {
        return false;
      }
    }
    
    return true;
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void validate() throws InvalidEditorFieldException {
    String      text = getText();
    
    if (text.indexOf(".") != -1 && text.indexOf(".") == text.lastIndexOf(".")) {
      // one "." and only one
      try {
        int           month = Integer.parseInt(text.substring(0, text.indexOf(".")));
        int           year  = Integer.parseInt(text.substring(text.indexOf(".") + 1));

        if (year < 50) {
          year += 2000;
        } else if (year < 100) {
          year += 1900;
        }

        if (isMonth(month, year)) {
          setText(toString(year, month));
        } else {
          throw new InvalidEditorFieldException(this, "00005");
        }
      } catch (Exception e) {
        throw new InvalidEditorFieldException(this, "00005");
      }
    } else if (text.indexOf(".") == -1) {
      // just the month, complete
      try {
        int           month = Integer.parseInt(text);
        int           year = new Date().getYear() + 1900;

        if (isMonth(month, year)) {
          setText(toString(year, month));
        } else {
          throw new InvalidEditorFieldException(this, "00005");
        }
      } catch (Exception e) {
        throw new InvalidEditorFieldException(this, "00005");
      }
    } else {
      throw new InvalidEditorFieldException(this, "00005");
    }
  
  }
  
  @Override
  protected boolean isNumeric() {
    return true;
  }

  /**
   * Returns the string representation of the given month.
   * @param year The month year.
   * @param month The month value.
   * @return The string representation of the given month.
   */
  public String toString(int year, int month) {
    StringBuffer        buffer = new StringBuffer();

    buffer.append(month / 10);
    buffer.append(month % 10);
    buffer.append('.');
    buffer.append(year);

    return buffer.toString();
  }

  /**
   * Checks if the given month is valid.
   * @param m The month.
   * @param y The year.
   * @return The {@code true} if the month is valid.
   */
  private boolean isMonth(int m, int y) {
    if (y < 1 || m < 1 || m > 12) {
      return false;
    }

    return true;
  }
}
