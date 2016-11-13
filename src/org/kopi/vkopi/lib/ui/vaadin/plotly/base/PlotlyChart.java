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

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * Entry point for a plotly chart
 */
@JavaScript({"plotly-latest.min.js","connector.js"})
public class PlotlyChart extends AbstractJavaScriptComponent {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  /*package*/ PlotlyChart() {
    chartId = nextChartId();
    setId(getDomId());
    getState().domId = getDomId();
    this.addStyleName("animation");
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  /**
   * Returns the next chart id for its attribution in the layout.
   * @return The next chart id for its attribution in the layout.
   */
  public static int nextChartId() {
    return ++currChartId;
  }
  
  @Override
  protected PlotlyChartState getState() {
    return (PlotlyChartState) super.getState();
  }

  /**
   * Returns the chart id for its attribution in the layout.
   * @return The chart id for its attribution in the layout.
   */
  public String getDomId() {
    return "plotly_" + chartId;
  }
  
  /**
   * Sets the chart data and layout.
   * @param data represents the data configuration.
   * @param layout represents the layout configuration.
   */
  public void setChartConfiguration(DataConfiguration data, LayoutConfiguration layout) {
    getState().data = "var data = " + data.toString();
    getState().layout = "var layout = " + layout.toString();
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  public static int                     currChartId;
  protected int                         chartId;
  private static final long             serialVersionUID = 6578878015922250803L;
}
