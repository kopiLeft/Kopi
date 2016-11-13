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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A scatter marker that should provide its size, color and opacity. 
 */
public class ScatterMarker extends AbstractMarker {
  
  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public ScatterMarker(){
    this.color = Colors.LIGHTSKYBLUE;
    this.opacity = 0.8;
    this.size = 10;
  }
  
  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  /**
   * Sets the marker color.
   * @param color
   */
  public void setColor(Color color){
    this.color = color;
  }
  
  /**
   * Sets the marker size.
   * @param s
   */
  public void setSize(int size){
    this.size = size;
  }
  
  @Override
  protected int getSize() {
    return size;
  }


  @Override
  public Color getColor() {
    return this.color;
  }

  @Override
  public double getOpacity() {
    return opacity;
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private int                           size;
  private double                        opacity;
  private Color                         color;
}
