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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.visual.VModel;

/**
 * Supported chart types.
 */
@SuppressWarnings("serial")
public class VChartType implements Serializable, CConstants, VModel {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  
  /**
   * Creates a new chart type.
   * @param ordinal The type ordinal.
   * @param name The type name.
   */
  protected VChartType(int ordinal, String name) {
    this.ordinal = ordinal;
    this.name = name;
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  /**
   * @Override
   */
  public void setDisplay(UComponent display) {
    assert display instanceof UChartType : "The display should be a chart type view";
    
    this.display = (UChartType) display;
  }

  /**
   * @Override
   */
  public UComponent getDisplay() {
    return display;
  }
  
  /**
   * Creates the data series objects from the chart model.
   * @param chart The chart model.
   */
  protected void createDataSeries(VChart chart) {
    if (dataSeries == null) {
      dataSeries = new LinkedList<VDataSeries>();
    }
    
    dataSeries.clear();
    for (VRow row : chart.getRows()) {
      VDataSeries		data;
      
      data = new VDataSeries(createDimensionData(chart, row));
      // fill the measures data for the dimension data.
      fillMeasures(data, chart, row);
      dataSeries.add(data);
    }
  }
  
  /**
   * Creates the dimension data for a given data row.
   * This will fill all the measures for the first dimension existing in the data row.
   * It is like parsing a table row, the first column is the dimension and the other
   * columns are its measures.
   * @param chart The chart model.
   * @param row The data row.
   * @return The created dimension data.
   */
  protected VDimensionData createDimensionData(VChart chart, VRow row) {
    return new VDimensionData(chart.getDimension(0).getLabel(), chart.getDimension(0).format(row.getDimensionAt(0)));
  }
  
  /**
   * Fills the measure data for the given data series.
   * @param dataSeries The data series.
   * @param chart The chart model.
   * @param row The data row.
   */
  protected void fillMeasures(VDataSeries dataSeries, VChart chart, VRow row) {
    for (int i = 0; i < row.getMeasuresCount(); i++) {
      dataSeries.measures.add(new VMeasureData(chart.getMeasure(i).getLabel(), chart.getMeasure(i).toNumber(row.getMeasureAt(i))));
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  
  /**
   * Returns the type ordinal.
   * @return the type ordinal.
   */
  public int getOrdinal() {
    return ordinal;
  }
  
  /**
   * Returns the type name.
   * @return The type name.
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns the data series.
   * @return the data series.
   */
  public VDataSeries[] getDataSeries() {
    return dataSeries == null ? null : dataSeries.toArray(new VDataSeries[dataSeries.size()]);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  
  private final int					ordinal;
  private final String					name;
  protected List<VDataSeries>				dataSeries;
  private UChartType					display;
  
  // constants
  public static final VChartType			PIE = new VChartType(TYPE_PIE, "Pie");
  public static final VChartType			COLUMN = new VChartType(TYPE_COLUMN, "Column");
  public static final VChartType			BAR = new VChartType(TYPE_BAR, "Bar");
  public static final VChartType			LINE = new VChartType(TYPE_LINE, "Line");
  public static final VChartType			AREA = new VChartType(TYPE_AREA, "Area");
  public static final VChartType			DEFAULT = COLUMN;
}
