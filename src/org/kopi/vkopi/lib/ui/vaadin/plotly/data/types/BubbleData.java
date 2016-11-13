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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.types;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.RangeOfData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.DataMode;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.BubbleMarker;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Buble data configuration.
 */
public class BubbleData extends AbstractData {
  
  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public BubbleData(String name) {
    super(name, new RangeOfData());
    type = DataType.SCATTER;
    mode = DataMode.MARKERS;
    marker = new BubbleMarker();
  }
  
  public BubbleData() {
    super(null,new RangeOfData());
    type = DataType.SCATTER;
    mode = DataMode.MARKERS;
    marker = new BubbleMarker();
  }
  
  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  @Override
  public DataType getType() {
    return type;
  }

  @Override
  public DataMode getMode() {
    return mode;
  }

  @Override
  public BubbleMarker getMarker() {
    return (BubbleMarker) marker;
  }
  
  @Override
  public boolean isBubble() {
    return true;
  }
  
  /**
   * sets the data mode.
   * @param mode
   */
  public void setMode(DataMode mode) {
    this.mode = mode;
  }
  
  /**
   * sets the marker.
   * @param marker
   */
  public void setMarker(BubbleMarker marker) {
    this.marker = marker;
  }
  
  /**
   * Sets the color of the data.
   * @param color
   */
  public void setColor(Color color) {
    ((BubbleMarker)this.marker).setColor(color);
  }
  
  @Override
  public Color getColor() {
    return marker.getColor();
  }
}
