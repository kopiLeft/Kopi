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

import java.util.Collection;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.ChartProperty;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * The chart layout configuration
 */
public abstract class Layout implements ChartProperty {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public Layout(int height, int width, String title) {
    this.height = height;
    this.width = width;
    this.showlegend = true;
    this.title = title;
  }

  public Layout(String title) {
    this.showlegend = true;
    this.title = title;
  }

  public Layout(int height, int width) {
    this.height = height;
    this.width = width;
    this.showlegend = true;
  }

  public Layout() {
    this.showlegend = true;
  }

  //---------------------------------------------------------
  // IMPLEMNTATIONS
  //---------------------------------------------------------


  public void disableLegend() {
    this.showlegend = false;
  }

  public void enableLegend(){
    this.showlegend = true;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setLayoutTitle(String title) {
    this.title = title;
  }

  public void setBackgroundColor(Color color) {
    this.bgcolor = color;
  }
  
  public void setAnnotations(Collection<Annotation> annotations) {
    this.annotations = annotations;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean isLegendShown() {
    return showlegend;
  }

  public String getGraphTitle() {
    return title;
  }

  public Color getBackgroundColor() {
    return this.bgcolor;
  }
  
  public Collection<Annotation> getAnnotations() {
    return annotations;
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private int                           height;
  private int                           width;
  private boolean                       showlegend;
  private Color                         bgcolor;
  private String                        title;
  private Collection<Annotation>        annotations;
}
