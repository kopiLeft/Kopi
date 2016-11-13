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
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.RangeOfData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.DataMode;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.line.Line;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Area range data configuration 
 */
public class AreaRangeData extends AbstractData {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------
  
  public AreaRangeData(String name) { 
    super(name, new RangeOfData());
    type = DataType.SCATTER;
    mode = DataMode.LINESANDMARKERS;
    line = new Line();
    fill = "tonexty";
  }
  
  public AreaRangeData() { 
    super(new RangeOfData());
    type = DataType.SCATTER;
    mode = DataMode.LINESANDMARKERS;
    line = new Line();
    fill = "tonexty";
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
  public Line getLine() {
    return line;
  }


  @Override
  public String getFill() {
    return fill;
  }

  /**
   * sets the mode of the data
   * @param mode
   */
  public void setMode(DataMode mode) {
    this.mode = mode;
  }

  /**
   * Sets the line of the data.
   * @param line
   */
  public void setLine(Line line) {
    this.line = line;
  }

  @Override
  public void setColor(Color color) {
    this.line.setColor(color);
  }
  
  @Override
  public Color getColor() {
    return line.getColor();
  }

  @Override
  public boolean isRangeArea() {
    return true;
  }
  
  @Override
  public void setDataSeries(AbstractDataSeries series){}
}
