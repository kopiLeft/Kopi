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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * The graphical control element Breadcrumbs or breadcrumb trail is a navigation aid used in user interfaces.
 * <p>
 *   It allows users to keep track of their locations within programs or documents.
 * </p>
 */
@SuppressWarnings("serial")
public class BreadCrumb extends CssLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>BreadCrumb</code> object.
   * @param root The root item.
   */
  public BreadCrumb (String root) {
    super();
    addStyleName(KopiTheme.BREAD_CRUMB_STYLE);
    rootLabel = new BreadCrumbItem(root);
    rootLabel.removeStyleName(KopiTheme.BREAD_CRUMB_NODE_STYLE);
    rootLabel.addStyleName(KopiTheme.BREAD_CRUMB_ROOT_STYLE);
    addComponent(rootLabel);
  }
  
  //--------------------------------------------------
  // IMPLEMENTATIONS
  //--------------------------------------------------
  
  /**
   * Adds an item to this BreadCrumb.
   * @param item The item to be added.
   */
  public void addItem(String item) { 
    addComponentAsFirst(new BreadCrumbSeparator());
    addComponentAsFirst(new BreadCrumbItem(item));
  }
  
  //--------------------------------------------------
  // ACCESSORS
  //--------------------------------------------------
  
  /**
   * Returns the root item.
   * @return The root item.
   */
  public String getRoot() {
    return rootLabel.getValue();
  }

  /**
   * Sets the root item.
   * @param root The root item.
   */
  public void setRoot(String root) {
    this.rootLabel.setValue(root);
  }
  
  //--------------------------------------------------
  // BREADCRUMBSEPARATOR CLASS IMPLEMENTATION
  //--------------------------------------------------

  /**
   * A BreadCrumb separator used to make a separation
   * between items.
   */
  public class BreadCrumbSeparator extends Label {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>BreadCrumbSeparator</code> object.
     */
    public BreadCrumbSeparator () {
      super("/");
      setWidth("-1");
      addStyleName(KopiTheme.BREAD_CRUMB_SEPARATOR_STYLE);
    } 
  }
  
  //--------------------------------------------------
  // BREADCRUMBITEM CLASS IMPLEMENTATION
  //--------------------------------------------------

  /**
   * A BreadCrumb item to be included in the layout.
   */
  public class BreadCrumbItem extends Label {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>BreadCrumbItem</code> object.
     * @param value The item value.
     */
    public BreadCrumbItem (String value) {
      super(value);
      setWidth("-1");
      addStyleName(KopiTheme.BREAD_CRUMB_NODE_STYLE);
    } 
  }
  
  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------

  private BreadCrumbItem			rootLabel;
}
