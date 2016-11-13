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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.chart.UChartType;
import org.kopi.vkopi.lib.chart.VDataSeries;
import org.kopi.vkopi.lib.chart.VDimensionData;
import org.kopi.vkopi.lib.chart.VMeasureData;
import org.kopi.vkopi.lib.chart.VPrintOptions;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.plotly.base.PlotlyChart;
import org.kopi.vkopi.lib.ui.vaadin.plotly.base.PlotlyChartFactory;
import org.kopi.vkopi.lib.ui.vaadin.plotly.configuration.DataConfiguration;
import org.kopi.vkopi.lib.ui.vaadin.plotly.configuration.LayoutConfiguration;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.types.ChartData;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Annotation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Layout;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.LayoutWithAxis;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.LayoutWithoutAxis;
import org.kopi.xkopi.lib.type.Fixed;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.Panel;

@SuppressWarnings("serial")
public abstract class DAbstractChartType extends Panel implements UChartType {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new abstract chart type from a chart title and a data series array.
   * @param title The chart title.
   * @param dataSeries The data series.
   */
  protected DAbstractChartType(String title, VDataSeries[] dataSeries) {
    this.title = title;
    this.dataSeries = dataSeries;
    this.keyedValues = new ChartKeyedValues();
    setSizeFull();
    addStyleName("chart-container");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public void build() {
    try {
      PlotlyChart               chart;
      DataConfiguration         dataconfiguration;
      LayoutConfiguration       layoutconfiguration;
      Layout                    layout;
      List<ChartData>           series;
      Set<Annotation>           annotations;        
      
      annotations = new HashSet<Annotation>();
      series = createChartSeries(dataSeries);
      if (series.get(0).isPieOrDonut()) {
        layout = new LayoutWithoutAxis(title);
      } else {
        layout = new LayoutWithAxis(title, dataSeries[0].getDimension().name, null);
      }
      layout.enableLegend();
      layout.setBackgroundColor(getBackgroundColor());
      layoutconfiguration = new LayoutConfiguration(layout);
      dataconfiguration = new DataConfiguration();

      for (ChartData data : series) {
        dataconfiguration.addDataConfiguration(data);
        annotations.addAll(data.getAnnotations());
      }
      layout.setAnnotations(annotations);
      if (setColorsList()) {
        dataconfiguration.setColors(Arrays.asList(createColorArray()));
      }

      chart = PlotlyChartFactory.renderChart(dataconfiguration, layoutconfiguration);
      chart.addStyleName("kopi-chart");
      chart.setSizeFull();
      setContent(chart);
    } catch (DataMismatchException e) {
      throw new InconsistencyException(e);
    }
  }

  @Override
  public void refresh() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        markAsDirtyRecursive();
      }
    });
  }

  @Override
  public void exportToPDF(OutputStream destination, VPrintOptions options)
    throws IOException
  {
    // DONE IN CLIENT SIDE
  }

  @Override
  public void exportToPNG(OutputStream destination, int width, int height)
    throws IOException
  {
    // DONE IN CLIENT SIDE
  }

  @Override
  public void exportToJPEG(OutputStream destination, int width, int height)
    throws IOException
  {
    // DONE IN CLIENT SIDE
  }

  /**
   * Returns the chart background color.
   * @return The chart background color.
   */
  protected Color getBackgroundColor() {
    return Color.WHITE;
  }

  /**
   * Returns {@code true} if the color list should be created.
   * @return {@code true} if the color list should be created.
   */
  protected boolean setColorsList() {
    return false;
  }

  /**
   * Creates the charts series map.
   * @param dataSeries The data series model
   * @return The charts series map.
   */
  protected Map<String, DefaultChartsSeries> createDefaultChartsSerie(VDataSeries[] dataSeries) {
    Map<String, DefaultChartsSeries>		chartsSeries;

    chartsSeries = new HashMap<String, DefaultChartsSeries>();
    for (VDataSeries data : dataSeries) {
      fillChartsSeriesMap(chartsSeries, data);
    }

    return chartsSeries;
  }

  /**
   * Fills the charts series map with data from a given series model.
   * @param chartsSeries The charts series map.
   * @param data The data series model.
   */
  protected void fillChartsSeriesMap(Map<String, DefaultChartsSeries> chartsSeries, VDataSeries data) {
    for (VMeasureData measure : data.getMeasures()) {
      if (!chartsSeries.containsKey(measure.name)) {
	chartsSeries.put(measure.name, new DefaultChartsSeries(measure.name));
      }
      
      chartsSeries.get(measure.name).getValues().add(measure.value);
    }
  }

  /**
   * Returns the chart series to be appended to chart data.
   * @param dataSeries The data series model.
   * @return the chart series to be appended to chart data.
   */
  protected List<ChartData> createChartSeries(VDataSeries[] dataSeries)
    throws DataMismatchException
  {
    for (VDataSeries serie : dataSeries) {
      VDimensionData            dimension;
      VMeasureData[]            measures;
      
      dimension = serie.getDimension();
      measures = serie.getMeasures();
      for (VMeasureData measure : measures) {
        keyedValues.addValue(dimension.value,  measure.name, measure.value);
      }
    }
    
    return keyedValues.createChartsSeries(this);
  }

  /**
   * Creates a new charts series from a given name and values.
   * @param name The chart series name.
   * @param labels The list of X axis labels
   * @param values The chart series values.
   * @return The charts series object.
   */
  protected ChartData createChartSeries(List<Comparable<?>> labels, String name, List<Object> values)
    throws DataMismatchException
  {
    ChartData           chartData;
    int                 index;

    chartData = createChartData(name);
    index = 0 ; 
    for (Object value : values) {
      if (value != null) {
        if (value instanceof Fixed) {
          chartData.getData().addData(labels.get(index), ((Fixed)value).doubleValue());
        } else if (value instanceof Integer) {
          chartData.getData().addData(labels.get(index), ((Integer)value).intValue());
        }
      }
      index++;
    }

    return chartData;
  }

  /**
   * Creates the color list.
   * @return The color list.
   */
  protected Color[] createColorArray() {
    Set<Color> 	colors = new HashSet<Color>(Arrays.asList(BASIC_COLORS));

    // add some random colors
    for (int i = 0; i < 20; i++) {
      colors.add(randomAdditionalColor());
    }
    
    return colors.toArray(new Color[colors.size()]);
  }

  /**
   * Random an additional color.
   * @return The selected color.
   */
  protected Color randomAdditionalColor() {
    Random 		rand; 
    int 		index;
    
    rand = new Random();
    index = rand.nextInt((ADDITIONAL_COLORS.length - 1) + 1);
    
    return ADDITIONAL_COLORS[index];
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  
  /**
   * Creates the chart data to be used for the chart type.
   * @param name The chart data name.
   * @return The chart data to be used.
   */
  protected abstract ChartData createChartData(String name);

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------

  /**
   * A default charts series used for all chart types except the
   * Pie chart which will be drawn differently.
   */
  /*package*/ static class DefaultChartsSeries implements Serializable {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------

    /**
     * Creates a new Charts series instance.
     * @param name The series name.
     */
    public DefaultChartsSeries(Comparable<?> name) {
      this.name = name.toString();
      this.values = new ArrayList<Object>();
    }

    //---------------------------------------
    // ACCESSORS
    //---------------------------------------

    /**
     * Returns the series name.
     * @return The series name.
     */
    public String getName() {
      return name;
    }

    /**
     * Returns the series values.
     * @return The series values.
     */
    public List<Object> getValues() {
      return values;
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    private final String		name;
    private final List<Object>		values;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final String 				title;
  private final VDataSeries[]			dataSeries;
  private final ChartKeyedValues                keyedValues;
  
  // colors
  private static final Color[]			BASIC_COLORS = new Color[] {
    new Color(255,0,0),
    new Color(0,255,0),
    new Color(0,0,255),
    new Color(255,255,0),
    new Color(0,255,255),
    new Color(255,0,255),
    new Color(128,0,0),
    new Color(128,128,0),
    new Color(0,128,0),
    new Color(128,0,128),
    new Color(0,128,128),
    new Color(0,0,128)
  };
  
  private static final Color[]			ADDITIONAL_COLORS = new Color[] {
    new Color(128,0,0),
    new Color(139,0,0),
    new Color(165,42,42),
    new Color(178,34,34),
    new Color(220,20,60),
    new Color(255,0,0),
    new Color(255,99,71),
    new Color(255,127,80),
    new Color(205,92,92),
    new Color(240,128,128),
    new Color(233,150,122),
    new Color(250,128,114),
    new Color(255,160,122),
    new Color(255,69,0),
    new Color(255,140,0),
    new Color(255,165,0),
    new Color(255,215,0),
    new Color(184,134,11),
    new Color(218,165,32),
    new Color(238,232,170),
    new Color(189,183,107),
    new Color(240,230,140),
    new Color(128,128,0),
    new Color(255,255,0),
    new Color(154,205,50),
    new Color(85,107,47),
    new Color(107,142,35),
    new Color(124,252,0),
    new Color(127,255,0),
    new Color(173,255,47),
    new Color(0,100,0),
    new Color(0,128,0),
    new Color(34,139,34),
    new Color(0,255,0),
    new Color(50,205,50),
    new Color(144,238,144),
    new Color(143,188,143),
    new Color(0,250,154),
    new Color(0,255,127),
    new Color(46,139,87),
    new Color(102,205,170),
    new Color(60,179,113),
    new Color(32,178,170),
    new Color(47,79,79),
    new Color(0,128,128),
    new Color(0,139,139),
    new Color(0,255,255),
    new Color(0,255,255),
    new Color(224,255,255),
    new Color(0,206,209),
    new Color(64,224,208),
    new Color(72,209,204),
    new Color(175,238,238),
    new Color(127,255,212),
    new Color(176,224,230),
    new Color(95,158,160),
    new Color(70,130,180),
    new Color(100,149,237),
    new Color(0,191,255),
    new Color(30,144,255),
    new Color(173,216,230),
    new Color(135,206,235),
    new Color(135,206,250),
    new Color(25,25,112),
    new Color(0,0,128),
    new Color(0,0,139),
    new Color(0,0,205),
    new Color(0,0,255),
    new Color(65,105,225),
    new Color(138,43,226),
    new Color(75,0,130),
    new Color(72,61,139),
    new Color(106,90,205),
    new Color(123,104,238),
    new Color(147,112,219),
    new Color(139,0,139),
    new Color(148,0,211),
    new Color(153,50,204),
    new Color(186,85,211),
    new Color(128,0,128),
    new Color(216,191,216),
    new Color(221,160,221),
    new Color(238,130,238),
    new Color(255,0,255),
    new Color(218,112,214),
    new Color(199,21,133),
    new Color(219,112,147),
    new Color(255,20,147),
    new Color(255,105,180),
    new Color(255,182,193),
    new Color(255,192,203),
    new Color(250,235,215),
    new Color(245,245,220),
    new Color(255,228,196),
    new Color(255,235,205),
    new Color(245,222,179),
    new Color(255,248,220),
    new Color(255,250,205),
    new Color(250,250,210),
    new Color(255,255,224),
    new Color(139,69,19),
    new Color(160,82,45),
    new Color(210,105,30),
    new Color(205,133,63),
    new Color(244,164,96),
    new Color(222,184,135),
    new Color(210,180,140),
    new Color(188,143,143),
    new Color(255,228,181),
    new Color(255,222,173),
    new Color(255,218,185),
    new Color(255,228,225),
    new Color(255,240,245),
    new Color(250,240,230),
    new Color(253,245,230),
    new Color(255,239,213),
    new Color(255,245,238),
    new Color(245,255,250),
    new Color(112,128,144),
    new Color(119,136,153),
    new Color(176,196,222),
    new Color(230,230,250),
    new Color(255,250,240),
    new Color(240,248,255),
    new Color(248,248,255),
    new Color(240,255,240),
    new Color(255,255,240),
    new Color(240,255,255),
    new Color(255,250,250),
    new Color(105,105,105),
    new Color(128,128,128),
    new Color(169,169,169),
    new Color(192,192,192),
    new Color(211,211,211),
    new Color(220,220,220),
    new Color(245,245,245)
  };
}
