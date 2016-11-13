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
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.SingleData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.BoxPlotMarker;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.PlotlyException;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Box data configuration
 */
public class BoxPlotData extends AbstractData {  
  
  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------
   
  public BoxPlotData(String name) {     
    super(name,new SingleData());
    type = DataType.BOX;
    marker = new BoxPlotMarker();
  }

  public BoxPlotData(){
    super(null, new SingleData());
    type = DataType.BOX;
    marker = new BoxPlotMarker();
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Override
  public DataType getType() {
    return type;
  }
  

  public boolean isBoxPlot() {
    return true;
  }
  
  @Override
  public void setColor(Color color) {
    ((BoxPlotMarker)this.marker).setColor(color);
  }
  
  @Override
  public BoxPlotMarker getMarker() {
    return (BoxPlotMarker) this.marker;
  }
  
  @Override
  public Color getColor() {
    return marker.getColor();
  }
  
  @Override
  public void setDataSeries(AbstractDataSeries series)
    throws PlotlyException
  {
    throw new PlotlyException("not allowed");
  }
}
