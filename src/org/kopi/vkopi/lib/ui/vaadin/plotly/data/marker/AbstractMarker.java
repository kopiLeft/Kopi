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

import static org.kopi.vkopi.lib.ui.vaadin.plotly.base.Util.toJsonArrayColors;

import java.util.Collection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.line.Line;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A common implementation of data markers. 
 */
public abstract class AbstractMarker implements Marker {
  
  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  @Override
  public JsonValue getValue() {
    JsonObjectBuilder           builder;
    
    builder = Json.createObjectBuilder();
    if (getColors() != null && !getColors().isEmpty()) {
      builder.add("colors", toJsonArrayColors(getColors()));
    }
    
    if (getColor() != null) {
      builder.add("color", Colors.toRGBA(getColor()));
    }
    
    if (getSize() != 0) {
      builder.add("size", getSize());
    }
    
    if (getSizes() != null) {
      builder.add("size", getSizes());
    }
    
    if (getLine() != null) {
      builder.add("line", getLine().getValue());
    }
    
    if (getOpacity() != 0) {
      builder.add("opacity", getOpacity());
    }
    
    return builder.build();
  }
  
  @Override
  public Collection<Color> getColors() {
    return null;
  }
  
  @Override
  public Color getColor() {
    return null;
  }

  @Override
  public JsonArray getSizes() {
    return null;
  }
  
  @Override
  public double getOpacity() {
    return 0;
  }
  
  /**
   * Returns the marker line properties.
   * @return The marker line properties.
   */
  protected Line getLine() {
    return null;
  }
  
  /**
   * Returns the marker line size.
   * @return The marker line size.
   */
  protected int getSize() {
    return 0;
  }
  
  @Override
  public void setSizes(JsonArray sizes) throws DataMismatchException{
    throw new DataMismatchException("setSizes only used in case of BubbleData");
  }
}
