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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.actor;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VHeaderNavigationItem;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VNavigationColumn;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VNavigationPanel;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * The actors navigation panel that shows all defined actors
 * for a window.
 */
@SuppressWarnings("deprecation")
public class VActorsNavigationPanel extends VNavigationPanel {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the actors navigation panel widget.
   */
  public VActorsNavigationPanel() {
    setStyleName("actors-navigationPanel");
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds the given actor to this navigation panel.
   * @param actor The actor to be added.
   */
  public void addActor(ActorConnector actor) {
    VActorNavigationItem        item;
    VNavigationColumn           column;
    
    item = actor.createNavigationItem();
    column = getColumn(isHelpMenu(item.getMenu()) ? "help" : item.getMenu());
    if (column == null) {
      // The header item is not created
      // we will create the navigation column and the header item
      VHeaderNavigationItem     header;
      
      column = new VNavigationColumn(isHelpMenu(item.getMenu()) ? "help" : item.getMenu());
      column.setStyleName("actor-navigationColumn");
      header = new VHeaderNavigationItem();
      header.setCaption(item.getMenu());
      column.setHeader(header);
    }
    // now we can add the actor item.
    column.addClickableItem(item);
    addColumn(column);
  }
  
  /**
   * Returns the navigation column having the given identifier.
   * @param ident The column identifier.
   * @return The navigation column.
   */
  protected VNavigationColumn getColumn(String ident) {
    for (VNavigationColumn column : getColumns()) {
      if (column.getIdent() != null && column.getIdent().equals(ident)) {
        return column; 
      }
    }
    
    return null;
  }
  
  /**
   * Returns {@code true} if it is the help menu.
   * @param caption The menu caption.
   * @return {@code true} if it is the help menu.
   */
  protected boolean isHelpMenu(String caption) {
    return caption != null && (caption.equalsIgnoreCase("help")
      || caption.equalsIgnoreCase("aide")
      || caption.equalsIgnoreCase("hilfe"));
  }
  
  @Override
  protected void add(Widget child, Element container) {
    VNavigationColumn   column = (VNavigationColumn) child;
    // adds the help item at the end
    if (!"help".equals(column.getIdent())) {
      VNavigationColumn         help;
      
      help = getColumn("help");
      if (help == null) {
        // help is not yet inserted
        super.add(child, container);
      } else {
        insert(child, container, getWidgetIndex(help), true);
        getColumns().remove(help);
        getColumns().add(help);
      }
    } else {
      super.add(child, container);
    }
  }
}
