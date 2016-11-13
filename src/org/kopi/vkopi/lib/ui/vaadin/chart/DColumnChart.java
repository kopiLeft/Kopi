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

package org.kopi.vkopi.lib.ui.vaadin.chart;

import org.kopi.vkopi.lib.chart.VDataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.types.ChartData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.types.ColumnData;

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
  protected ChartData createChartData(String name) {
    return new ColumnData(name);
  }
}
