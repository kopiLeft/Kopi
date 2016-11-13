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

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.chart.ChartTypeFactory;
import org.kopi.vkopi.lib.chart.UChartType;
import org.kopi.vkopi.lib.chart.VChartType;
import org.kopi.vkopi.lib.chart.CConstants;

/**
 * Swing implementation of the chart type factory.
 */
public class JChartTypeFactory extends ChartTypeFactory {

  /**
   * @Override
   */
  public UChartType createTypeView(String title, VChartType model) {
    DAbstractChartType		view;
    
    switch (model.getOrdinal()) {
    case CConstants.TYPE_PIE:
      view = new DPieChart(title, model.getDataSeries());
      break;
    case CConstants.TYPE_COLUMN:
      view = new DColumnChart(title, model.getDataSeries());
      break;
    case CConstants.TYPE_BAR:
      view = new DBarChart(title, model.getDataSeries());
      break;
    case CConstants.TYPE_LINE:
      view = new DLineChart(title, model.getDataSeries());
      break;
    case CConstants.TYPE_AREA:
      view = new DAreaChart(title, model.getDataSeries());
      break;
    default:
      throw new InconsistencyException("NO UI IMPLEMENTATION FOR CHART TYPE " + model.getName());
    }
    model.setDisplay(view);
    
    return view;
  }
}
