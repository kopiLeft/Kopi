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

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.AbstractDataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.CoupleOfData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.DataMode;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.Orientation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.BarMarker;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Bar data configuration 
 */
public class BarData extends AbstractData {
  
  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public BarData(String name, AbstractDataSeries series) {
    super(name, series);
    type = DataType.BAR;
    mode = DataMode.LINESANDMARKERS;
    marker = new BarMarker();
    orientation = Orientation.HORIZONTAL;
  }
  
  public BarData(AbstractDataSeries series) {
    super(series);
    type = DataType.BAR;
    mode = DataMode.LINESANDMARKERS;
    marker = new BarMarker();
    orientation = Orientation.HORIZONTAL;
  }
  
  public BarData(String name) {
    super(name, new CoupleOfData());
    type = DataType.BAR;
    mode = DataMode.LINESANDMARKERS;
    marker = new BarMarker();
    orientation = Orientation.HORIZONTAL;
  }
  
  public BarData() {
    super(new CoupleOfData());
    type = DataType.BAR;
    mode = DataMode.LINESANDMARKERS;
    marker = new BarMarker();
    orientation = Orientation.HORIZONTAL;
  }
  
  //---------------------------------------------------------
  // IMPLEMNTATIONS
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
  public BarMarker getMarker() {
    return (BarMarker) marker;
  }
  
  /**
   * Sets the mode. 
   * @param mode
   */

  public void setMode(DataMode mode) {
    this.mode = mode;
  }
  
  /**
   * Sets the data color.
   * @param color.
   */
  public void setColor(Color color) {
    ((BarMarker) this.marker).setColor(color);
    ((BarMarker) this.marker).getLine().setColor(color);
  }
  
  /**
   * Sets the bars orientation
   * @param orientation
   */
  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }
  
  @Override
  public Orientation getOrientation() {
    return orientation;
  }
  
  @Override
  public Color getColor() {
    return marker.getColor();
  }
}
