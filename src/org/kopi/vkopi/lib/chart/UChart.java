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

package org.kopi.vkopi.lib.chart;

import org.kopi.vkopi.lib.visual.UWindow;

/**
 * The {@code UChart} is the view part of a chart component.
 * A chart is a separate window where we can display a chart
 * component like pie chart, bar chart, column chart and line chart.
 */
public interface UChart extends UWindow {

  /**
   * Redisplays the chart content.
   */
  public void refresh();
  
  /**
   * Builds the chart display.
   */
  public void build();
  
  /**
   * Sets the chart type representation.
   * @param type The chart type view
   */
  public void setType(UChartType type);
  
  /**
   * Returns the chart type.
   * @return The chart type.
   */
  public UChartType getType();
  
  /**
   * Fired when the chart type has been changed.
   */
  public void typeChanged();
}
