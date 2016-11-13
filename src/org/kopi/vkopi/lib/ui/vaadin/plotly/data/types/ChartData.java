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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.types;

import java.util.Collection;
import java.util.List;

import javax.json.JsonObject;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.AbstractDataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.DataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.DataMode;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.HoverInfo;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.Orientation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.line.Line;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.Marker;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.TypeMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Annotation;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * All things that chart data configuration should provide.
 */
public interface ChartData {

  /**
   * @return the data to be added to the configuration.
   * @throws DataMismatchException 
   */
  List<JsonObject> getValue() throws DataMismatchException;
  
  /**
   * Returns the list of the layout annotations to be used.
   * @param orientation TODO
   * @return The list of the layout annotations to be used.
   */
  Collection<Annotation> getAnnotations();

  /**
   * @return the type of data: bar, scatter or pie.
   */
  DataType getType();

  /**
   * @return The data's name.
   */
  String getName();

  /**
   * @return The mode line, markers or lines+markers.
   */
  DataMode getMode();

  /**
   * @return the graph's marker.
   */
  Marker getMarker();

  /**
   * @return the graph's line.
   */
  Line getLine();

  /**
   * @return the hover info.
   */
  HoverInfo getHoverInfo();
  
  /**
   * @return the color.
   */
  Color getColor();

  /**
   * @return the pie hole in case of pie type.
   */
  double getHole();

  /**
   * @return the fill type 'tonexty' or 'tozeroy' in case of area.
   */
  String getFill();

  /**
   * @return the values to be displayed on the chart.
   */
  DataSeries getData();

  /**
   * 
   * @return a boolean value representing if it is a pie or donut or not. 
   * This is necessary to construct the data configuration. 
   */
  boolean isPieOrDonut();

  /**
   *  sets the graphs color in case of any chart except pie or donut.
   * @param color
   * @throws TypeMismatchException 
   */
  void setColor(Color color) throws TypeMismatchException;

  /**
   *  sets the data name.
   * @param title
   */
  void setName(String title);

  /**
   * 
   * @return the graph orientation
   */
  Orientation getOrientation();

  /**
   * sets the hover info of the graph.
   * @param hoverinfo
   */
  void setHoverinfo(HoverInfo hoverinfo);

  /**
   * 
   * @return a boolean value representing if a RangeArea. 
   * This is necessary to construct the data configuration. 
   */
  boolean isRangeArea();

  /**
   * 
   * @return a boolean value representing if a Bubble. 
   * This is necessary to construct the data configuration. 
   */
  boolean isBubble();
  
  /**
   * 
   * @return a boolean value representing if a BoxPlot. 
   * This is necessary to construct the data configuration. 
   */
  boolean isBoxPlot();
  
  /**
   * sets the colors in case of a pie
   * @param colors
   */
  void setColors(List<Color> colors);
  
  /**
   * Adds series to the data.
   * @param series
   * @throws Exception 
   */
  void setDataSeries(AbstractDataSeries series) throws Exception;
  
  /**
   * sets the domain of each graph in case of pie or donut type.
   * @param x1
   * @param x2
   * @param y1
   * @param y2
   * @throws TypeMismatchException
   */
  void setDomain(double x1, double x2, double y1, double y2) throws TypeMismatchException;
  
  /**
   * @return the domain table
   */
  double[] getDomain();
}
