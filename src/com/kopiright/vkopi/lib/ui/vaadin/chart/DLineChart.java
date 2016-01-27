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

import org.kopi.vaadin.highchartsapi.model.ChartType;
import org.kopi.vaadin.highchartsapi.model.plotoptions.HighChartsPlotOptionsImpl;
import org.kopi.vaadin.highchartsapi.model.plotoptions.LineChartPlotOptions;
import org.kopi.vaadin.highchartsapi.model.series.HighChartsSeries;
import org.kopi.vaadin.highchartsapi.model.series.LineChartSeries;

import com.kopiright.vkopi.lib.chart.VDataSeries;

@SuppressWarnings("serial")
public class DLineChart extends DAbstractChartType {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a line area chart from a given data series model.
   * @param title The chart title.
   * @param dataSeries The data series model.
   */
  public DLineChart(String title, VDataSeries[] dataSeries) {
    super(title, dataSeries);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected HighChartsSeries createChartSeries(String name, List<Object> values) {
    return new LineChartSeries(name, toHighChartsDataList(values));
  }

  @Override
  protected ChartType getChartType() {
    return ChartType.LINE;
  }
  
  @Override
  protected HighChartsPlotOptionsImpl getPlotOptions() {
    return new LineChartPlotOptions();
  }
}
