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

package org.kopi.vkopi.lib.ui.vaadin.plotly.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.base.ChartProprety;
import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.HoverInfo;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.types.ChartData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.TypeMismatchException;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * The chart data configuration
 */
public class DataConfiguration implements ChartProprety {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public DataConfiguration() {
    this.dataConfiguraion = new ArrayList<>();
    colors = new ArrayList<>();
    colors.add(Colors.LIGHTGREEN);
    colors.add(Colors.LIGHTSALMON);
    colors.add(Colors.LIGHTCORAL);
    colors.add(Colors.LIGHTSTEELBLUE);
    colors.add(Colors.LIGHTGRAY);
    isPieOrDonut = false;
    isBoxPlot = false;
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Override
  public JsonArray getValue() {
    JsonArrayBuilder 	  arr;

    arr = Json.createArrayBuilder();
    if (isPieOrDonut) { 
      setDomain(this.dataConfiguraion.size());
    }
    for (ChartData data: dataConfiguraion) {
      try {
        for (JsonObject o : data.getValue()){
          arr.add(o);
        }
      } catch (DataMismatchException e) {
        throw new InconsistencyException(e);
      }
    }

    return arr.build();
  }

  /**
   * Adds data to the configuration.
   * @param data
   */
  public void addDataConfiguration(ChartData data) {
    if (dataConfiguraion.isEmpty()) {
      if ((!data.isPieOrDonut()) && data.getColor().equals(Colors.LIGHTSKYBLUE)) {
        try {
          data.setColor(colors.get(0));
        } catch (TypeMismatchException e) {
          throw new InconsistencyException(e);
        }
      }
      this.dataConfiguraion.add(data);
      isPieOrDonut = data.isPieOrDonut();
      isBoxPlot = data.isBoxPlot();
    } else {
      if (!data.isPieOrDonut() && !isPieOrDonut) {
        if ((!data.isPieOrDonut()) && data.getColor().equals(Colors.LIGHTSKYBLUE)) {
          try {
            data.setColor(colors.get(dataConfiguraion.size()));
          } catch (TypeMismatchException e) {
            throw new InconsistencyException(e);
          }
        }
        this.dataConfiguraion.add(data);
      } else if(data.isPieOrDonut()&&isPieOrDonut){
        this.dataConfiguraion.add(data);
      }
    }
  }

  private void setDomain(int size) {
    if (isPieOrDonut){
      if (size > 9) {
        size = 9;
      }
      
      if (size > 1) {
        for (ChartData data: this.dataConfiguraion){
          data.setHoverinfo(HoverInfo.PIE_PERCENT_LABEL_NAME);
        }
      }

      try {
        if (size == 2) {
          this.dataConfiguraion.get(0).setDomain(0, 0.48, 0, 1);
          this.dataConfiguraion.get(1).setDomain(0.52, 1, 0, 1);
        } else if (size == 3) {
          this.dataConfiguraion.get(2).setDomain(0, 0.48, 0, 0.48);
          this.dataConfiguraion.get(1).setDomain(0.52, 1, 0.52, 1);
          this.dataConfiguraion.get(0).setDomain(0,0.48,0.52,1);
        } else if (size == 4) {
          this.dataConfiguraion.get(0).setDomain(0, 0.48, 0, 0.48);
          this.dataConfiguraion.get(1).setDomain(0.52, 1, 0, 0.48);
          this.dataConfiguraion.get(2).setDomain(0, 0.48, 0.52, 1);
          this.dataConfiguraion.get(3).setDomain(0.52, 1, 0.52, 1);
        } else if (size == 5) {
          this.dataConfiguraion.get(0).setDomain(0, 0.32, 0, 0.48);
          this.dataConfiguraion.get(1).setDomain(0.34, 0.65, 0, 0.48);
          this.dataConfiguraion.get(2).setDomain(0.67, 1, 0.52, 1);
          this.dataConfiguraion.get(3).setDomain(0, 0.32, 0.52, 1);
          this.dataConfiguraion.get(4).setDomain(0.34, 0.65, 0.52, 1);
        } else if (size == 6) {
          this.dataConfiguraion.get(0).setDomain(0, 0.32, 0, 0.48);
          this.dataConfiguraion.get(1).setDomain(0.34, 0.65, 0, 0.48);
          this.dataConfiguraion.get(2).setDomain(0.67, 1, 0, 0.48);
          this.dataConfiguraion.get(3).setDomain(0, 0.32, 0.52, 1);
          this.dataConfiguraion.get(4).setDomain(0.34, 0.65, 0.52, 1);
          this.dataConfiguraion.get(5).setDomain(0.67, 1, 0.52, 1);
        } else if (size == 7) {
          this.dataConfiguraion.get(0).setDomain(0, 0.32, 0, 0.32);
          this.dataConfiguraion.get(1).setDomain(0.34, 0.65, 0.67, 1);
          this.dataConfiguraion.get(2).setDomain(0.67, 1, 0.67, 1);
          this.dataConfiguraion.get(3).setDomain(0, 0.32, 0.34, 0.65);
          this.dataConfiguraion.get(4).setDomain(0.34, 0.65, 0.34, 0.65);
          this.dataConfiguraion.get(5).setDomain(0.67, 1, 0.34, 0.65);
          this.dataConfiguraion.get(6).setDomain(0, 0.32, 0.67, 1);
        } else if (size == 8) {
          this.dataConfiguraion.get(0).setDomain(0, 0.32, 0, 0.32);
          this.dataConfiguraion.get(1).setDomain(0.34, 0.65, 0, 0.32);
          this.dataConfiguraion.get(2).setDomain(0.67, 1, 0.67, 1);
          this.dataConfiguraion.get(3).setDomain(0, 0.32, 0.34, 0.65);
          this.dataConfiguraion.get(4).setDomain(0.34, 0.65, 0.34, 0.65);
          this.dataConfiguraion.get(5).setDomain(0.67, 1, 0.34, 0.65);
          this.dataConfiguraion.get(6).setDomain(0, 0.32, 0.67, 1);
          this.dataConfiguraion.get(7).setDomain(0.34, 0.65, 0.67, 1);
        } else if (size == 9) {
          this.dataConfiguraion.get(0).setDomain(0, 0.32, 0, 0.32);
          this.dataConfiguraion.get(1).setDomain(0.34, 0.65, 0, 0.32);
          this.dataConfiguraion.get(2).setDomain(0.67, 1, 0, 0.32);
          this.dataConfiguraion.get(3).setDomain(0, 0.32, 0.34, 0.65);
          this.dataConfiguraion.get(4).setDomain(0.34, 0.65, 0.34, 0.65);
          this.dataConfiguraion.get(5).setDomain(0.67, 1, 0.34, 0.65);
          this.dataConfiguraion.get(6).setDomain(0, 0.32, 0.67, 1);
          this.dataConfiguraion.get(7).setDomain(0.34, 0.65, 0.67, 1);
          this.dataConfiguraion.get(8).setDomain(0.67, 1, 0.67, 1);
        }
      } catch (TypeMismatchException e) {
        throw new InconsistencyException(e);
      }
    }
  }

  /**
   * returns if the configuration is set to be a pie or donut set. 
   * @return boolean value of the configuration type.
   */
  public boolean isPieOrDonut(){
    return isPieOrDonut;
  }

  /**
   * returns if the configuration is set to be a boxplot set. 
   * @return boolean value of the configuration type.
   */
  public boolean isBoxPlot(){
    return isBoxPlot;
  }

  /**
   * sets the colors of the different charts on the graph of the configuration
   * @param colors
   */

  public void setColors(List<Color> colors){
    this.colors = colors;
    if (isPieOrDonut){
      dataConfiguraion.get(0).setColors(colors);
    }
  }

  @Override
  public String toString() {
    return getValue().toString();
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private List<ChartData>		dataConfiguraion;
  private List<Color>		colors;
  private boolean               isPieOrDonut;
  private boolean               isBoxPlot;
}

