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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.menu;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The navigation column that represents a section in the navigation panel.
 * The navigation column can handle multiple navigation items.
 */
public class VNavigationColumn extends FlowPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new navigation column widget.
   */
  public VNavigationColumn(String ident) {
    super(UListElement.TAG);
    this.ident = ident;
    items = new LinkedList<VNavigationItem>();
    clickableItems = new LinkedList<VClickableNavigationItem>();
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Returns the column identifier.
   * @return The column identifier.
   */
  public String getIdent() {
    return ident;
  }
  
  /**
   * Sets the header item of this navigation column.
   * @param header The header item.
   */
  public void setHeader(VHeaderNavigationItem header) {
    this.header = header;
    addItem(header);
  }
  
  /**
   * Adds the given clickable item to this navigation column.
   * @param item The clickable item to be added.
   */
  public void addClickableItem(VClickableNavigationItem item) {
    clickableItems.add(item);
    addItem(item);
  }
  
  /**
   * Appends the given navigation item to this navigation section.
   * @param item The item to be added.
   */
  protected void addItem(VNavigationItem item) {
    items.add(item);
    add(item);
  }
  
  /**
   * Removes the given item from the navigation column.
   * @param item The item to be removed.
   */
  protected void removeItem(VNavigationItem item) {
    items.remove(item);
    remove(item);
  }
  
  /**
   * Returns the item having the given caption.
   * @param caption The searched caption.
   * @return The item having the given caption.
   */
  public VNavigationItem getItem(String caption) {
    for (VNavigationItem item : items) {
      if (item.getCaption() != null && item.getCaption().equals(caption)) {
        return item;
      }
    }
    
    return null;
  }
  
  /**
   * Returns the header item of this navigation column.
   * @return The header item of this navigation column.
   */
  public VHeaderNavigationItem getHeader() {
    return header;
  }
  
  /**
   * Returns the clickable items of this navigation column. 
   * @return The clickable items of this navigation column.
   */
  public VClickableNavigationItem[] getClickableItems() {
    return clickableItems.toArray(new VClickableNavigationItem[clickableItems.size()]);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final String                          ident;
  private final List<VNavigationItem>           items;
  private VHeaderNavigationItem                 header;
  private final List<VClickableNavigationItem>  clickableItems;
}
