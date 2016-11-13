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

package org.kopi.vkopi.lib.ui.vaadin.plotly.layout;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;
import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Util;

/**
 * A layout configuration with axis. 
 */
public class LayoutWithAxis extends Layout {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public LayoutWithAxis(int height, int width, String title, String xTitle, String yTitle) {
    super(height, width, title);
    xaxis = new Axis(xTitle);
    yaxis = new Axis(yTitle);
  }

  public LayoutWithAxis(int height, int width, String title) {
    super(height, width, title);
    xaxis = new Axis();
    yaxis = new Axis();
  }

  public LayoutWithAxis(String title, String xTitle, String yTitle) {
    super(title);
    xaxis = new Axis(xTitle);
    yaxis = new Axis(yTitle);
  }

  public LayoutWithAxis(String title) {
    super(title);
    xaxis = new Axis();
    yaxis = new Axis();
  }

  public LayoutWithAxis(int height, int width, String xTitle, String yTitle) {
    super(height, width);
    xaxis = new Axis(xTitle);
    yaxis = new Axis(yTitle);
  }

  public LayoutWithAxis(int height, int width) {
    super(height, width);
    xaxis = new Axis();
    yaxis = new Axis();
  }

  public LayoutWithAxis(String xTitle, String yTitle) {
    xaxis = new Axis(xTitle);
    yaxis = new Axis(yTitle);
  }

  public LayoutWithAxis() {
    xaxis = new Axis();
    yaxis = new Axis();
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  /**
   * @return The x Axis.
   */

  public Axis getXaxis() {
    return xaxis;
  }

  /**
   * @return The y Axis.
   */
  public Axis getYaxis() {
    return yaxis;
  }

  /**
   * Sets the x Axis tile.
   * @param xAxisTitle
   */
  public void setxAxisTitle(String xAxisTitle){
    this.xaxis.setTitle(xAxisTitle);
  }

  /**
   * Sets the y Axis title.
   * @param yAxisTitle
   */
  public void setyAxisTitle(String yAxisTitle){
    this.yaxis.setTitle(yAxisTitle);
  }

  /**
   *  Enables x grid.
   */
  public void enableXGrid() {
    this.xaxis.enableGrid();
  }

  /**
   * Disables x grid.
   */
  public void disableXGrid() {
    this.xaxis.disableGrid();
  }

  /**
   * Shows the x axis.
   */
  public void showXAxis() {
    this.xaxis.showAxis();
  }

  /**
   * Hides the X axis.
   */
  public void hideXAxis()  {
    this.xaxis.hideAxis();
  }

  /**
   * Enables the Y grid.
   */
  public void enableYGrid() {
    this.yaxis.enableGrid();
  }
  
  /**
   * Disables the Y grid.
   */
  public void disableYGrid() {
    this.yaxis.disableGrid();
  }

  /**
   * Shows the y axis.
   */
  public void showYAxis() {
    this.yaxis.showAxis();
  }

  /**
   * Hides the y axis.
   */
  public void hideYAxis()  {
    this.yaxis.hideAxis();
  }

  @Override
  public JsonObject getValue() {
    JsonObjectBuilder 		obj;
    
    obj = Json.createObjectBuilder();
    if (this.getHeight() != 0) {
      obj.add("height", this.getHeight());
    }
    if (this.getWidth() != 0) {
      obj.add("width", this.getWidth());
    }
    if (this.getGraphTitle()!= null) {
      obj.add("title", this.getGraphTitle());
    }
    obj.add("showlegend", this.isLegendShown());
    obj.add("barmode","group");
    obj.add("xaxis", this.xaxis.getValue());
    obj.add("yaxis", this.yaxis.getValue());
    if (this.getBackgroundColor() != null) {
      obj.add("paper_bgcolor", Colors.toHex(this.getBackgroundColor()));
      obj.add("plot_bgcolor",  Colors.toHex(this.getBackgroundColor()));
    }
    if (this.getAnnotations() != null && !this.getAnnotations().isEmpty()) {
      obj.add("annotations", Util.toJsonArrayProperties(getAnnotations()));
    }
    
    return obj.build();
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private Axis	                         xaxis;
  private Axis	                         yaxis;
}
