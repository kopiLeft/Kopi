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

package com.kopiright.vkopi.lib.ui.swing.chart;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.TableOrder;

import com.kopiright.vkopi.lib.chart.VDataSeries;
import com.kopiright.vkopi.lib.chart.VMeasureData;
import com.kopiright.vkopi.lib.visual.ApplicationContext;

@SuppressWarnings("serial")
public class DPieChart extends DAbstractChartType {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new pie chart from a given series.
   * @param title The chart title.
   * @param series The data series.
   */
  public DPieChart(String title, VDataSeries[] series) {
    super(title, series);
  }

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  
  /**
   * @Override
   */
  protected JFreeChart createChart(String title, VDataSeries[] series) {
    JFreeChart			chart;
    
    if (series.length == 1) {
      PieDataset 		dataset;
      PiePlot			plot;

      dataset = createPieDataset(series[0]);
      chart = ChartFactory.createPieChart(title,
	                                  dataset,
	                                  true,
	                                  true,
	                                  ApplicationContext.getDefaultLocale());
      plot = (PiePlot) chart.getPlot();
      plot.setIgnoreNullValues(true);
      plot.setIgnoreZeroValues(true);
      plot.setBackgroundPaint(getPlotBackground());
    } else {
      CategoryDataset		dataset;
      MultiplePiePlot		plot;
      
      dataset = createDataset(series);
      chart = ChartFactory.createMultiplePieChart(title,
	                                          dataset,
	                                          TableOrder.BY_ROW,
	                                          true,
	                                          true,
	                                          false);
      plot = (MultiplePiePlot) chart.getPlot();
      plot.setAggregatedItemsPaint(new Color(239, 235, 222));
      plot.setBackgroundPaint(getPlotBackground());
    }
    
    chart.setBackgroundPaint(getChartBackground());
    
    return chart;
  }
  
  /**
   * Creates the Pie data set from the given chart series.
   * @param series The chart series.
   * @return The Pie data set.
   */
  protected PieDataset createPieDataset(VDataSeries series) {
    DefaultPieDataset			dataset;
    VMeasureData[]			measures;

    dataset = new DefaultPieDataset();
    measures = series.getMeasures();
    for (VMeasureData measure : measures) {
      dataset.setValue(measure.name, measure.value);
    }
    
    return dataset;
  }
}
