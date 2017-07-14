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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;

/**
 * An integer field for grid inline edit
 */
public class VEditorIntegerField extends VEditorTextField {
  
  @Override
  public EditorIntegerFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorIntegerFieldConnector.class);
  }
  
  @Override
  protected boolean check(String text) {
    for (int i = 0; i < text.length(); i++) {
      char      c = text.charAt(i);
      
      if (!(Character.isDigit(c) || c == '.' || c == '-')) {
        return false;
      }
    }
    
    return true;
  }
  
  @Override
  public void validate() throws InvalidEditorFieldException {
    int       value;

    value = intValue();
    if (minValue != null && value < minValue) {
      throw new InvalidEditorFieldException(this, "00012", minValue);
    }
    if (maxValue != null && value > maxValue) {
      throw new InvalidEditorFieldException(this, "00009", maxValue);
    }
  }
  
  @Override
  protected boolean isNumeric() {
    return true;
  }
  
  /**
   * Sets the minimum accepted value for this integer editor field.
   * @param minValue The minimum accepted value.
   */
  public void setMinValue(Integer minValue) {
    this.minValue = minValue;
  }
  
  /**
   * Sets the maximum accepted value for this integer editor field.
   * @param maxValue The maximum accepted value.
   */
  public void setMaxValue(Integer maxValue) {
    this.maxValue = maxValue;
  }
  
  /**
   * Returns the integer value of this editor field.
   * @return The integer value of this editor field.
   */
  protected int intValue() throws InvalidEditorFieldException {
    try {
      return Integer.parseInt(getText());
    } catch (NumberFormatException e) {
      throw new InvalidEditorFieldException(this, "00006");
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Integer                       minValue;
  private Integer                       maxValue;
}
