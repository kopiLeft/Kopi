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

import java.util.List;

import at.downdrown.vaadinaddons.highchartsapi.model.ChartType;
import at.downdrown.vaadinaddons.highchartsapi.model.plotoptions.ColumnChartPlotOptions;
import at.downdrown.vaadinaddons.highchartsapi.model.plotoptions.HighChartsPlotOptionsImpl;
import at.downdrown.vaadinaddons.highchartsapi.model.series.ColumnChartSeries;
import at.downdrown.vaadinaddons.highchartsapi.model.series.HighChartsSeries;

import com.kopiright.vkopi.lib.chart.VDataSeries;

@SuppressWarnings("serial")
public class DColumnChart extends DAbstractChartType {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new column chart from a given data series model.
   * @param title The chart title.
   * @param dataSeries The data series model.
   */
  public DColumnChart(String title, VDataSeries[] dataSeries) {
    super(title, dataSeries);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected HighChartsSeries createChartSeries(String name, List<Object> values) {
    return new ColumnChartSeries(name, toHighChartsDataList(values));
  }

  @Override
  protected ChartType getChartType() {
    return ChartType.COLUMN;
  }
  
  @Override
  protected HighChartsPlotOptionsImpl getPlotOptions() {
    return new ColumnChartPlotOptions();
  }
}
