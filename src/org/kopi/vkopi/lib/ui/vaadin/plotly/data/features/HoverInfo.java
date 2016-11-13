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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.features;

/**
 * An enumeration of a hover info possible values 
 */
public enum HoverInfo {
  
  PIE_LABEL("label"),
  PIE_LABEL_NAME("label+name"),
  PIE_VALUE("values"),
  PIE_VALUE_NAME("value+name"),
  PIE_LABEL_VALUE("label+value"),
  PIE_VALUE_LABEL_NAME("label+value+name"),
  PIE_PERCENT_LABEL_NAME("label+percent+name"),
  PIE_PERCENT_LABEL("label+percent"),
  PIE_PERCENT_NAME("percent+name"),
  X_Y_NAME("x+y+name"),
  Y_NAME("y+name"),
  X_NAME("x+name"),
  X_Y("x+y"),
  X("x"),
  Y("y");

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------
  
  HoverInfo(String value) {
    this.value = value;
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  /**
   * Returns the data mode value.
   * @return The data mode value.
   */
  public String getValue() {
    return value;
  }

  //---------------------------------------------------------
  // DATA MEMEBERS
  //---------------------------------------------------------
  
  private String                value;
}
