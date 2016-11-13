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


/**
 * The chart type view factory.
 */
public abstract class ChartTypeFactory {

  //---------------------------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------------------------

  /**
   * Returns the {@code ChartTypeFactory} instance.
   * @return The ChartTypeFactory instance.
   */
  public static ChartTypeFactory getChartTypeFactory() {
    return chartTypeFactory;
  }

  /**
   * Sets the {@code ChartTypeFactory} instance.
   * @param factory The ChartTypeFactory instance.
   */
  public static void setChartTypeFactory(ChartTypeFactory factory) {
    assert factory != null : "ChartTypeFactory cannot be null";

    chartTypeFactory = factory;
  }
  
  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  
  /**
   * Creates the chart type view according to the given model.
   * @param model The chart type model.
   * @return The corresponding chart type view.
   */
  public abstract UChartType createTypeView(String title, VChartType model);
  
  //---------------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------------
  
  private static ChartTypeFactory			chartTypeFactory;
}
