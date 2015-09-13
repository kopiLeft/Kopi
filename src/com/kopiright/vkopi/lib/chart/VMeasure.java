/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.chart;

import com.kopiright.vkopi.lib.visual.VColor;

/**
 * A chart measure represented by its label and its value.
 */
@SuppressWarnings("serial")
public abstract class VMeasure extends VColumn {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  protected VMeasure(String ident, VColor color) {
    super(ident);
    this.color = color;
  }

  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  
  /**
   * Returns the number representation of the given value.
   * @param value
   * @return
   */
  protected abstract Number toNumber(Object value);

  //---------------------------------------------------------------------
  // ACCESSSORS
  //---------------------------------------------------------------------
  
  /**
   * Returns the color to be used by the measure.
   * @return The color to be used by the measure.
   */
  public VColor getColor() {
    return color;
  }
  
  /**
   * Sets the color to be used by the measure.
   * @param color The color to be used.
   */
  public void setColor(VColor color) {
    this.color = color;
  }

  //---------------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------------
  
  private VColor				color;
}
