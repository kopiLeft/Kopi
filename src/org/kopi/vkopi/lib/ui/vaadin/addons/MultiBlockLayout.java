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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockAlignment;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.ComponentConstraint;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.MultiBlockLayoutState;

import com.vaadin.ui.Component;

/**
 * A multi block layout. A mix between chart and simple layout.
 */
@SuppressWarnings("serial")
public class MultiBlockLayout extends AbstractBlockLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new multi block layout instance.
   * @param detailCol The detail column number.
   * @param detailLine The detail line number.
   * @param chartCol The chart column number.
   * @param chartLine The chart line number.
   */
  public MultiBlockLayout(int detailCol, int detailLine, int chartCol, int chartLine) {
    layouts = new LinkedList<Component>();
    this.chartLayout = new ChartBlockLayout(chartCol, chartLine);
    this.detailLayout = new SimpleBlockLayout(detailCol, chartCol);
    addComponent(chartLayout); // attach to UI
    addComponent(detailLayout); // attach to UI
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds a component to detail block layout.
   * @param component The component to be added.
   * @param x the x position.
   * @param y The y position.
   * @param width the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll Use all available area ?
   */
  public void addToDetail(Component component,
                          int x,
                          int y,
                          int width,
                          boolean alignRight,
                          boolean useAll)
  {
    detailLayout.addComponent(component, x, y, width, alignRight, useAll);
  }
  
  @Override
  public void addComponent(Component component,
                           int x,
                           int y,
                           int width,
                           boolean alignRight,
                           boolean useAll)
  {
    chartLayout.addComponent(component, x, y, width, alignRight, useAll);
  }
  
  /**
   * Sets the alignment information for this simple layout.
   * @param ori The original block to align with.
   * @param targets The alignment targets.
   * @param isChart Is the original block chart ? 
   */
  public void setBlockAlignment(Component ori, int[] targets, boolean isChart) {
    BlockAlignment		align = new BlockAlignment();
    
    detailLayout.getState().align.isChart = isChart;
    detailLayout.getState().align.targets = targets;
    detailLayout.getState().align.ori = ori;
    detailLayout.getState().align = align;
  }
  
  @Override
  protected void addComponent(Component component, ComponentConstraint constraints) {
    // should never be used
  }
  
  @Override
  public void addComponent(Component c) {
    layouts.add(c);
    super.addComponent(c);
  }
  
  @Override
  protected MultiBlockLayoutState getState() {
    return (MultiBlockLayoutState) super.getState();
  }
  
  @Override
  public Iterator<Component> iterator() {
    return layouts.iterator();
  }
  
  @Override
  public int getComponentCount() {
    return layouts.size();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<Component>			layouts;
  private final SimpleBlockLayout		detailLayout;
  private final ChartBlockLayout		chartLayout;
}
