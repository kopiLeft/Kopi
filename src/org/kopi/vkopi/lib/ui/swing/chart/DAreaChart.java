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

package org.kopi.vkopi.lib.ui.swing.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kopi.vkopi.lib.chart.VDataSeries;

@SuppressWarnings("serial")
public class DAreaChart extends DAbstractChartType {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new area chart from a given data series.
   * @param title The chart title.
   * @param series The data series
   */
  public DAreaChart(String title, VDataSeries[] series) {
    super(title, series);
  }

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  
  @Override
  protected JFreeChart createChart(String title, VDataSeries[] series) {
    CategoryDataset		dataset;
    JFreeChart			chart;
    CategoryPlot		plot;
    
    dataset = createDataset(series);
    chart = ChartFactory.createAreaChart(title,
	                                 series[0].getDimension().name,
	                                 null,
	                                 dataset,
	                                 PlotOrientation.VERTICAL,
	                                 true,
	                                 true,
	                                 false);
    plot = (CategoryPlot) chart.getPlot();
    plot.setBackgroundPaint(getPlotBackground());
    chart.setBackgroundPaint(getChartBackground());
    plot.setRangeGridlinePaint(getGridLineColor());
    
    return chart;
  }
}
