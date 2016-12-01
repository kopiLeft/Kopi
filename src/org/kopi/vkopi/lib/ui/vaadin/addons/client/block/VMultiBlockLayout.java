/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The multiple block layout widget composed of a simple
 * block layout and a chart block layout.
 */
public class VMultiBlockLayout extends VAbstractBlockLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new multi block layout.
   */
  public VMultiBlockLayout() {
    setWidget(0, 0, pane = new DeckPanel());
    addStyleDependentName("multi");
    chartLayout = new VChartBlockLayout();
    detailLayout = new VSimpleBlockLayout();
    pane.add(chartLayout.cast());
    pane.add(detailLayout.cast());
    pane.showWidget(0); // show chart layout by default
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void initSize(int columns, int rows) {
    // no widgets in this layout
    // all will be handled by sub layouts.
  }
  
  /**
   * Initializes the chart layout size.
   * @param columns The number of columns.
   * @param rows The number of rows.
   */
  public void initChartSize(int columns, int rows) {
    chartLayout.initSize(columns, rows);
  }
  
  /**
   * Initializes the detail layout size.
   * @param columns The number of columns.
   * @param rows The number of rows.
   */
  public void initDetailSize(int columns, int rows) {
    detailLayout.initSize(columns, rows);
  }
  
  @Override
  public void add(Widget widget, ComponentConstraint constraints) {
    chartLayout.add(widget, constraints);
  }
  
  /**
   * Adds a widget to detail layout.
   * @param widget The widget to be added.
   * @param constraints The widget constraint.
   */
  public void addToDetail(Widget widget, ComponentConstraint constraints) {
    detailLayout.add(widget, constraints);
  }

  @Override
  public void layout() {
    chartLayout.layout();
    detailLayout.layout();
  }
  
  @Override
  public void addAlignedWidget(Widget widget, ComponentConstraint constraint) {
    detailLayout.addAlignedWidget(widget, constraint);
  }
  
  @Override
  public void layoutAlignedWidgets() {
    detailLayout.layoutAlignedWidgets();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Widget> T cast() {
    return (T) this;
  }
  
  @Override
  public void updateScroll(int pageSize, int maxValue, boolean enable, int value) {
    chartLayout.updateScroll(pageSize, maxValue, enable, value);
  }
  
  @Override
  public void clear() {
    super.clear();
    pane.clear();
    pane = null;
    detailLayout.clear();
    chartLayout.clear();
    detailLayout = null;
    chartLayout = null;
  }
  
  /**
   * Switch from the chart view to the detail view and vis versa.
   * Switch is only performed when it is a multi block.
   * @param detail Should we switch to detail view ?
   */
  public void switchView(boolean detail) {
    if (detail) {
      pane.showWidget(1); // show detail view
    } else {
      pane.showWidget(0); // show chart view
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private DeckPanel		        pane;
  private VSimpleBlockLayout		detailLayout;
  private VChartBlockLayout		chartLayout;
}
