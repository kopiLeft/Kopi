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

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A reprsentation of a chart axis.
 */
public class Axis {
  
  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------
  
  /**
   * Creates a chart axis with a title.
   * @param title The axis title.
   */
  public Axis(String title) {
    this.gridcolor = Colors.DEF_AXIS_GRID_COLOR;
    this.gridwidth = 1;
    this.linecolor = Colors.DEF_AXIS_LINE_COLOR;
    this.linewidth = 1;
    this.mirror = false;
    this.showgrid = true;
    this.showline = true;
    this.showticklabels = true;
    this.tickwidth = 1;
    this.title = title;
  }

  /**
   * Creates a new empty chart axis.
   */
  public Axis() {
    this.gridcolor = Colors.DEF_AXIS_GRID_COLOR;
    this.gridwidth = 1;
    this.linecolor = Colors.DEF_AXIS_LINE_COLOR;
    this.linewidth = 1;
    this.mirror = true;
    this.showgrid = true;
    this.showline = true;
    this.showticklabels = true;
    this.tickwidth = 1;
  }
  
  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  /**
   * 
   * @return the grid color.
   */
  
  public String getGridcolor() {
    return Colors.toRGBA(gridcolor);
  }
  
  /**
   * 
   * @return the grid's width.
   */
  
  public int getGridwidth() {
    return gridwidth;
  }
  
  /**
   * 
   * @return the Tickwidth.
   */
  
  public int getTickwidth() {
    return tickwidth;
  }
  
  /**
   * 
   * @return the line color.
   */
  
  public String getLinecolor() {
    return Colors.toRGBA(linecolor);
  }
  
  /**
   * 
   * @return the axis line width.
   */
  
  public int getLinewidth() {
    return linewidth;
  }
  
  /**
   * 
   * @return if the mirror is enabled or not.
   */
  
  public boolean isMirror() {
    return mirror;
  }
  
  /**
   * 
   * @return if the grid is showen or not.
   */
  
  public boolean isShowgrid() {
    return showgrid;
  }
  
  /**
   * 
   * @return if the axis line is shown or not.
   */
  
  public boolean isShowline() {
    return showline;
  }
  
  /**
   * 
   * @return if the tick labels are shown or not.
   */
  
  public boolean isShowticklabels() {
    return showticklabels;
  }
  
  /**
   * 
   * @return the title.
   */
  
  public String getTitle() {
    return title;
  }
  
  /**
   * Sets the grid color.
   * @param gridcolor
   */
  
  public void setGridcolor(Color gridcolor) {
    this.gridcolor = gridcolor;
  }
  
  /**
   * sets the grids width.
   * @param gridwidth
   */
  
  public void setGridwidth(int gridwidth) {
    this.gridwidth = gridwidth;
  }
  
  /**
   * Sets the axis line color.
   * @param linecolor
   */
  
  public void setLinecolor(Color linecolor) {
    this.gridcolor = linecolor;
  }
  
  /**
   * sets the axis line width.
   * @param linewidth
   */
  
  public void setLinewidth(int linewidth) {
    this.linewidth = linewidth;
  }
  
  /**
   * Disables the Mirror.
   */
  
  public void disableMirror() {
    this.mirror = false;
  }
  
  /**
   * Enables the mirror.
   */
  
  public void enableMirror() {
    this.mirror = true;
  }
  
  /**
   * enables the grid
   */
  
  public void enableGrid() {
    this.showgrid = true;
  }
  
  /**
   * Disables the grid.
   */
  
  public void disableGrid() {
    this.showgrid = false;
  }
  
  /**
   * SHowing the axis.
   */
  
  public void showAxis() {
    this.showline = true;
  }
  
  /**
   * Hiding the axis.
   */
  
  public void hideAxis()  {
    this.showline = false;
  }
  
  /**
   * Hiding the tick labels.
   */

  public void hideTicklabels() {
    this.showticklabels = false;
  }
  
  /**
   * Showing the the labels.
   */
  
  public void ShowTicklabels() {
    this.showticklabels = true;
  }
  
  /**
   * setting the title
   * @param title
   */
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public JsonObject getValue() {
    JsonObjectBuilder		obj;

    obj = Json.createObjectBuilder();
    obj.add("gridcolor", this.getGridcolor());
    obj.add("gridwidth", this.getGridwidth()); 
    obj.add("linecolor", this.getLinecolor()); 
    obj.add("linewidth", this.getLinewidth()); 
    obj.add("mirror", this.isMirror());
    obj.add("showticklabels", this.isShowticklabels());
    obj.add("showgrid", this.isShowgrid());
    obj.add("showline", this.isShowline());
    obj.add("tickwidth", this.getTickwidth()); 
    if (this.getTitle()!= null) {
      obj.add("title", this.getTitle());
    }

    return obj.build();
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------
  
  private Color                 gridcolor;
  private int                   gridwidth; 
  private Color 		linecolor; 
  private int                   linewidth;
  private boolean		mirror; 
  private boolean		showgrid;
  private boolean               showline;
  private boolean		showticklabels; 
  private int                   tickwidth;
  private String		title;
}
