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

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.AbstractDataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.CoupleOfData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.PieMarker;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Pie data configuration
 */
public class PieData extends AbstractData {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public PieData(String name, AbstractDataSeries series) {
    super(name, series);
    type = DataType.PIE;
    marker = new PieMarker();
    hole = 0;
  }

  public PieData(AbstractDataSeries series) {
    super(series);
    type = DataType.PIE;
    marker = new PieMarker();
    hole = 0;
  }
  
  public PieData() {
    super(new CoupleOfData());
    type = DataType.PIE;
    marker = new PieMarker();
    hole = 0;
  }
  
  public PieData(String name) {
    super(name, new CoupleOfData());
    type = DataType.PIE;
    marker = new PieMarker();
    hole = 0;
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------


  public void setHole(double hole){
    this.hole=hole;
  }

  @Override
  public DataType getType() {
    return type;
  }

  @Override
  public PieMarker getMarker() {
    return (PieMarker) marker;
  }

  @Override
  public double getHole() {
    return hole;
  }

  /**
   * sets the marker of the data.
   * @param marker
   */
  public void setMarker(PieMarker marker) {
    this.marker = marker;
  }

  @Override
  public void setColors(List<Color> colors) {
    ((PieMarker)this.marker).setColors(colors);
  }

  @Override
  public boolean isPieOrDonut() {
    return true;
  }
  
  @Override
  public void setDomain(double x1, double x2, double y1, double y2){   
    domain = new double[4];
    this.domain[0] = x1;
    this.domain[1] = x2;
    this.domain[2] = y1;
    this.domain[3] = y2;
  }
  
  @Override
  public double[] getDomain() {
    return domain;
  }
 
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private double                        hole;
  private double[]                      domain;
}
