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

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.ChartProprety;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A marker should provide its color and size.
 */
public interface Marker extends ChartProprety {
  
  /**
   * @return the series of colors in case of pie or donut.
   */
  Collection<Color> getColors();
  
  /**
   * 
   * @return The sizes of the marker in case or bubble.
   */
  JsonArray getSizes();
  
  /**
   * 
   * @return the color code in form of a string. 
   */
  Color getColor();
  
  /**
   * @return the opacity of the marker. 
   */
  double getOpacity();
  
  /**
   * Sets the sizes of a marker.
   * @param sizes
   * @throws DataMismatchException 
   */
  
  void setSizes(JsonArray sizes) throws DataMismatchException;
}
