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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleItem;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleListMenu;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The actors menu widget that shows the total window actors
 * even those who have no icon and no accelerator key.
 */
@SuppressWarnings("deprecation")
public class VActorsMenu extends VModuleListMenu {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the actors menu widget.
   * @param vertical Show vertical menu bar ?
   */
  public VActorsMenu(ApplicationConnection connection) {
    super(connection, false);
    setStyleName("actors-menu");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds the given actor to this actors menu.
   * @param actor The actor to be added.
   */
  public void addActor(ApplicationConnection connection, ActorConnector actor) {
    //addActorItem(connection, actor.createNavigationItem());
  }
  
  /**
   * Adds the given actor item to this actors menu.
   * @param item The actor item to be added.
   */
  protected void addActorItem(ApplicationConnection connection, VActorMenuItem item) {
    VModuleItem                 parent;
    
    parent = getItem(isHelpMenu(item.getMenu()) ? "help" : item.getMenu());
    if (parent == null) {
      parent = addItem(isHelpMenu(item.getMenu()) ? "help" : item.getMenu(), item.getMenu(), false, false);
      parent.setSubMenu(new VModuleListMenu(connection, false));
      parent.setStyleName("actor-menu");
      parent.getSubMenu().setStyleName("actor-submenu");
    }
    // now add the actor as an item to the parent sub menu.
    parent.getSubMenu().addItem(item, false, false);
  }
  
  @Override
  protected void add(Widget child, Element container) {
    VModuleItem item = (VModuleItem) child;
    // adds the help item at the end
    if (!isHelpMenu(item.getCaption())) {
      VModuleItem       help;
      
      help = getItem("help");
      if (help == null) {
        // help is not yet inserted
        super.add(child, container);
      } else {
        insert(child, container, getWidgetIndex(help), true);
        getItems().remove(help);
        getItems().add(help);
      }
    } else {
      super.add(child, container);
    }
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
}
