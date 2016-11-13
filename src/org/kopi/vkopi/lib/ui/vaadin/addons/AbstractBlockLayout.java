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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.AbstractBlockLayoutState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.ComponentConstraint;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

/**
 * An abstract implementation of the block layout.
 */
@SuppressWarnings("serial")
public abstract class AbstractBlockLayout extends AbstractComponentContainer implements BlockLayout {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new AbstractBlockLayout instance.
   * @param col The number of columns.
   * @param line The number of lines.
   */
  public AbstractBlockLayout(int col, int line) {
    components = new LinkedList<Component>();
    getState().columns = col;
    getState().rows = line;
  }
  
  /**
   * Creates a new AbstractBlockLayout instance.
   * This is a special hidden for use in multiple block layout.
   */
  protected AbstractBlockLayout() {
    components = new LinkedList<Component>();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public Iterator<Component> iterator() {
    return components.iterator();
  }

  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
    // no component replacement
  }

  @Override
  public int getComponentCount() {
    return components.size();
  }
  
  @Override
  protected AbstractBlockLayoutState getState() {
    return (AbstractBlockLayoutState) super.getState();
  }

  @Override
  public void addComponent(Component component,
                           int x,
                           int y,
                           int width,
                           boolean alignRight,
                           boolean useAll)
  {
    ComponentConstraint		constraints;
    
    constraints = new ComponentConstraint(x, y, width, alignRight, useAll);
    addComponent(component, constraints);
    // really attach to the connector hierarchy
    components.add(component);
    super.addComponent(component);
  }

  /**
   * Adds a component with its constraints.
   * @param component The component to be added.
   * @param constraints The component constraints.
   */
  protected void addComponent(Component component, ComponentConstraint constraints) {
    getState().constrains.put(component, constraints);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private LinkedList<Component>		components;
}
