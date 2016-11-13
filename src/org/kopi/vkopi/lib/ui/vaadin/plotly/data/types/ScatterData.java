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
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.ScatterMarker;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Scatter data configuration. 
 */
public class ScatterData extends AbstractData {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public ScatterData(String name, AbstractDataSeries series){
    super(name, series);
    type = DataType.SCATTER;
    mode = DataMode.MARKERS;
    marker = new ScatterMarker();
  }

  public ScatterData(AbstractDataSeries series){
    super(series);
    type = DataType.SCATTER;
    mode = DataMode.MARKERS;
    marker = new ScatterMarker();
  }
  
  public ScatterData(String name){
    super(name, new CoupleOfData());
    type = DataType.SCATTER;
    mode = DataMode.MARKERS;
    marker = new ScatterMarker();
  }

  public ScatterData(){
    super(new CoupleOfData());
    type = DataType.SCATTER;
    mode = DataMode.MARKERS;
    marker = new ScatterMarker();
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
  public ScatterMarker getMarker() {
    return (ScatterMarker) marker;
  }

  /**
   * Sets the mode of the data.
   * @param mode
   */
  public void setMode(DataMode mode) {
    this.mode = mode;
  }

  /**
   * Sets the marker of the data.
   * @param marker
   */
  public void setMarker(ScatterMarker marker) {
    this.marker = marker;
  }

  @Override
  public void setColor(Color color) {
    ((ScatterMarker) this.marker).setColor(color);
  }
  
  @Override
  public Color getColor() {
    return marker.getColor();
  }
}
