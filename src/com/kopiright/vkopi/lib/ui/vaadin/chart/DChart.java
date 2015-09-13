/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.chart;

import com.kopiright.vkopi.lib.chart.ChartTypeFactory;
import com.kopiright.vkopi.lib.chart.UChart;
import com.kopiright.vkopi.lib.chart.UChartType;
import com.kopiright.vkopi.lib.chart.VChart;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DWindow;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.CssLayout;

@SuppressWarnings("serial")
public class DChart extends DWindow implements UChart, Focusable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new chart view from its model.
   * @param model The chart model.
   */
  public DChart(VWindow model) {
    super(model);
    this.content = new CssLayout();
    this.content.addStyleName("dchart-content");
    this.tabIndex = 0;
    setContent(content);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void run() throws VException {
    ((VChart)getModel()).initChart();
    ((VChart)getModel()).setMenu();
    focus();
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
  public void build() {}

  @Override
  public void setType(final UChartType newType) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
	if (type != null && newType != null) {
	  content.removeComponent((Component) type);
	}
	if (newType != null) {
	  type = newType;
	  newType.build();
	  content.addComponent((Component) newType);
	}
      }
    });
  }

  @Override
  public UChartType getType() {
    return type;
  }

  @Override
  public void typeChanged() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	content.markAsDirty();
	((VChart)getModel()).setMenu();
      }
    });
  }

  @Override
  public int getTabIndex() {
    return tabIndex;
  }

  @Override
  public void setTabIndex(int tabIndex) {
    this.tabIndex = tabIndex;
  }
  
  @Override
  public void focus() {
    super.focus();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private UChartType				type;
  private int					tabIndex;
  private final CssLayout			content;
  
  static {
    ChartTypeFactory.setChartTypeFactory(new VChartTypeFactory());
  }
}
