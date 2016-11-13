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

package org.kopi.vkopi.lib.ui.vaadin.plotly.base;

import org.kopi.vkopi.lib.ui.vaadin.plotly.configuration.DataConfiguration;
import org.kopi.vkopi.lib.ui.vaadin.plotly.configuration.LayoutConfiguration;

/**
 * The plotly charts factory.
 */
public class PlotlyChartFactory {

  /**
   * Creates a new plotly chart from a data and layout configuration.
   * @param data represents the data configuration.
   * @param layout represents the layout configuration.
   * @return The plotly chart object.
   */
  public static PlotlyChart renderChart(DataConfiguration data, LayoutConfiguration layout) {
    PlotlyChart         chart;
    
    chart = new PlotlyChart();
    chart.setChartConfiguration(data, layout);
    
    return chart;
  }
}