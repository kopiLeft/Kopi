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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.WindowState;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

/**
 * The server side of a window.
 */
@SuppressWarnings("serial")
public class Window extends AbstractComponentContainer  {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new window component.
   * @param caption The window caption.
   * @param description The window description.
   */
  public Window() {
    super();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds an actor to this window.
   * @param actor The actor to be added.
   */
  public void addActor(Actor actor) {
    actors.add(actor);
    addComponent(actor);
  }
  
  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
    // component replacement are not supported for a window
  }

  @Override
  public int getComponentCount() {
    return actors.size() + ((content != null) ? 1 : 0);
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the actor list.
   * @return the actor list.
   */
  public List<Actor> getActors() {
    return actors;
  }
  
  /**
   * Sets the window content.
   * @param content The window content.
   */
  public void setContent(Component content) {
    this.content = content;
    addComponent(content);
  }
  
  /**
   * Returns the window content.
   * @return The window content.
   */
  public Component getContent() {
    return content;
  }

  @Override
  public Iterator<Component> iterator() {
    LinkedList<Component>	components;
    
    components = new LinkedList<Component>(actors);
    if (content != null) {
      components.add(content);
    }
    return components.iterator();
  }
  
  @Override
  protected WindowState getState() {
    return (WindowState) super.getState();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private LinkedList<Actor>		actors = new LinkedList<Actor>();
  private Component			content;
}
