/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.downdrown.vaadinaddons.highchartsapi.model.ChartType;
import at.downdrown.vaadinaddons.highchartsapi.model.data.PieChartData;
import at.downdrown.vaadinaddons.highchartsapi.model.plotoptions.HighChartsPlotOptionsImpl;
import at.downdrown.vaadinaddons.highchartsapi.model.plotoptions.PieChartPlotOptions;
import at.downdrown.vaadinaddons.highchartsapi.model.series.HighChartsSeries;
import at.downdrown.vaadinaddons.highchartsapi.model.series.PieChartSeries;

import com.kopiright.vkopi.lib.chart.VDataSeries;
import com.kopiright.vkopi.lib.chart.VMeasureData;

@SuppressWarnings("serial")
public class DPieChart extends DAbstractChartType {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new pie chart from a given data series model.
   * @param title The chart title.
   * @param dataSeries The data series model.
   */
  public DPieChart(String title, VDataSeries[] dataSeries) {
    super(title, dataSeries);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected List<HighChartsSeries> createChartSeries(VDataSeries[] dataSeries) {
    List<HighChartsSeries>			chartsSeries;
    Map<String, List<PieChartData>>		chartsSeriesMap;

    chartsSeries = new ArrayList<HighChartsSeries>();
    chartsSeriesMap = createPieChartDataMap(dataSeries);
    for (Map.Entry<String, List<PieChartData>> entry : chartsSeriesMap.entrySet()) {
      PieChartSeries		pieChartSeries;
      
      pieChartSeries = (PieChartSeries) createChartSeries(entry.getKey(), null);
      pieChartSeries.getData().addAll(entry.getValue());
      chartsSeries.add(pieChartSeries);
    }
    
    return chartsSeries;
  }
  
  /**
   * Creates the charts series map from data series model.
   * @param dataSeries The data series model.
   * @return The charts series map.
   */
  protected Map<String, List<PieChartData>> createPieChartDataMap(VDataSeries[] dataSeries) {
    Map<String, List<PieChartData>>		pieChartDataMap;
    
    pieChartDataMap = new HashMap<String, List<PieChartData>>();
    for (VDataSeries data : dataSeries) {
      fillPieChartDataMap(pieChartDataMap, data);
    }
    
    return pieChartDataMap;
  }
  
  /**
   * Fills the chart series map from a given data series model.
   * @param pieChartDataMap The chart series map.
   * @param data The data series model.
   */
  protected void fillPieChartDataMap(Map<String, List<PieChartData>> pieChartDataMap, VDataSeries data) {    
    for (VMeasureData measure : data.getMeasures()) {
      if (!pieChartDataMap.containsKey(measure.name)) {
	pieChartDataMap.put(measure.name, new ArrayList<PieChartData>());
      }
      
      pieChartDataMap.get(measure.name).add(new PieChartData(data.getDimension().value.toString(), measure.value));
    }
  }

  @Override
  protected HighChartsSeries createChartSeries(String name, List<Object> values) {
    return new PieChartSeries(name);
  }

  @Override
  protected ChartType getChartType() {
    return ChartType.PIE;
  }
  
  @Override
  protected HighChartsPlotOptionsImpl getPlotOptions() {
    return new PieChartPlotOptions();
  }
  
  @Override
  protected boolean setColorsList() {
    return true;
  }
}
