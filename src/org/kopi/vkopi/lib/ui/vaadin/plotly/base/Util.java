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

package org.kopi.vkopi.lib.ui.vaadin.plotly.base;

import java.util.Collection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Collected utilities 
 */
public final class Util {

  /**
   * Converts a list of colors to a JSON array.
   * @param colors The list of colors.
   * @return The resulting JSON array.
   */
  public static JsonArray toJsonArrayColors(Collection<Color> colors) {
    JsonArrayBuilder      builder;

    builder = Json.createArrayBuilder();
    for (Color color : colors) {
      builder.add(color.getCSS());
    }
    
    return builder.build();
  }

  /**
   * Converts a list of chart properties to a JSON array.
   * @param properties The list of the chart properties.
   * @return The resulting JSON array.
   */
  public static <T extends ChartProprety> JsonArray toJsonArrayProperties(Collection<T> properties) {
    JsonArrayBuilder      builder;

    builder = Json.createArrayBuilder();
    for (ChartProprety property : properties) {
      builder.add(property.getValue());
    }
    
    return builder.build();
  }
}
