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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.line;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.ChartProprety;
import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A line properties.
 */
public class Line implements ChartProprety {
  
  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------
  
  public Line() {
    width = 1.5;
    color = Colors.LIGHTSKYBLUE;
    style = LineStyle.SOLID;
    shape = LineShape.NONE;
  }
  
  //---------------------------------------------------------
  // IMPLEMENTIONS
  //---------------------------------------------------------

  @Override
  public JsonObject getValue() {
    JsonObjectBuilder   builder = Json.createObjectBuilder();
    
    builder.add("color", Colors.toHex(getColor()));
    builder.add("width", getWidth());
    builder.add("dash", getStyle().name().toLowerCase());
    builder.add("shape", getShape().name().toLowerCase());
    
    return builder.build();
  }
  
  /**
   * @return The line color.
   */
  public Color getColor() {
    return color;
  }
  
  /**
   * Sets the line color.
   * @param color 
   */
  public void setColor(Color color) {
    this.color = color;
  }
  
  /**
   * @return The line width.
   */
  public double getWidth() {
    return width;
  }
  
  /**
   *  Sets the line width
   * @param width
   */
  public void setWidth(int width) {
    this.width = width;
  }
  
  /**
   * @return The line style DASH, DOT, SOLID or DASHDOT;
   */
  public LineStyle getStyle() {
    return style;
  }
  
  /**
   * Sets the line style.
   * @param style
   */
  public void setStyle(LineStyle style) {
    this.style = style;
  }
  
  /**
   * @return The line shape.
   */
  public LineShape getShape() {
    return shape;
  }
  
  /**
   * Sets the line shape.
   * @param shape
   */
  public void setShape(LineShape shape) {
    this.shape = shape;
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------


  private Color                 color;
  private double                width;
  private LineStyle             style;
  private LineShape             shape;
}
