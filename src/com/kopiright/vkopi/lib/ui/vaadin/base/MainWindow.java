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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import com.kopiright.vkopi.lib.base.UComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * The application main window based on a {@link CustomLayout}.
 * The used HTML template will divide the main window on 6 sections
 * <ul>
 *   <li>The window header</li>
 *   <li>The window title</li>
 *   <li>The window toolbar</li>
 *   <li>The window menu</li>
 *   <li>The window forms</li>
 *   <li>The window search</li>
 *   <li>The window uploader</li>
 * </ul>
 */
@SuppressWarnings("serial")
public class MainWindow extends Window implements UComponent {
	 
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>MainWindow</code> object.
   */
  public MainWindow() {
    layout = new CustomLayout("main");
    setSizeFull();
    setImmediate(true);
    layout.setImmediate(true);
    setContent(layout);
  }
	  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
	  
  public void setHeader(Component component) {
    Component	header = layout.getComponent("header");
	    
    if (header == null) {
      layout.addComponent(component, "header");
    } else {
      layout.removeComponent("header");
      layout.addComponent(component, "header");
    }
  }
	  
  public void setTitle(String title) {
    Component	label = layout.getComponent("title");
	    
    if (label == null) {
      layout.addComponent(new Label(title), "title");
    } else {
      layout.removeComponent("title");
      layout.addComponent(new Label(title), "title");
    }
  }
	  
  public void setToolbar(Component component) {
    Component	toolbar = layout.getComponent("toolbar");
	    
    if (toolbar == null) {
      layout.addComponent(component, "toolbar");
    } else {
      layout.removeComponent("toolbar");
      layout.addComponent(component, "toolbar");
    }
  }
	  
  public void setMenu(Component component) {
    Component	menuTree = layout.getComponent("menu");
	    
    if (menuTree == null) {
      layout.addComponent(component, "menu");
    } else {
      layout.removeComponent("menu");
      layout.addComponent(component, "menu");
    }
  }
	  
  public void setWindows(Component component) {
    Component	windows = layout.getComponent("windows");
	    
    if (windows == null) {
      layout.addComponent(component, "windows");
    } else {
      layout.removeComponent("windows");
      layout.addComponent(component, "windows");
    }
  }
	  
  public void removeWindows() {
    layout.removeComponent("windows");
  }
	  
  public boolean hasWindows() {
    return layout.getComponent("windows") != null;
  }
	  
  public void setSearch(Component component) {
    Component	search = layout.getComponent("search");
	    
    if (search == null) {
      layout.addComponent(component, "search");
    } else {
      layout.removeComponent("search");
      layout.addComponent(component, "search");
    }
  }
	  
  public void addUploader(Component component) {
    Component	uploader = layout.getComponent("uploader");
	    
    if (uploader == null) {
      layout.addComponent(component, "uploader");
    } else {
      layout.removeComponent("uploader");
      layout.addComponent(component, "uploader");
    }
  }
	  
  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------
	  
  private CustomLayout			layout;
}
