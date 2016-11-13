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

import java.util.Collection;

import javax.json.JsonArray;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * bubble marker that should provide its color and opacity. 
 */
public class BubbleMarker extends AbstractMarker {
  
  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public BubbleMarker(){
    this.color = Colors.LIGHTSKYBLUE;
    this.opacity = 0.8;
  }
  
  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Deprecated
  @Override
  public Collection<Color> getColors() {
    return null;
  }

  @Override
  public JsonArray getSizes() {
    return sizes;
  }

  @Override
  public Color getColor() {
    return this.color;
  }

  @Override
  public double getOpacity() {
    return opacity;
  }
  
  /**
   * Sets the marker's color.
   * @param color
   */
  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public void setSizes(JsonArray sizes) {
    this.sizes = sizes;
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private JsonArray                     sizes;
  private double		        opacity;
  private Color                         color;
}
