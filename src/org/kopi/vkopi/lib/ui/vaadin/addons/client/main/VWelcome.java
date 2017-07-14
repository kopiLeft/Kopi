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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.main;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleItem;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleList;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleListMenu;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The welcome text widget.
 * Contains also the logout button to disconnect from application.
 */
public class VWelcome extends FlowPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the welcome text widget.
   */
  public VWelcome() {
    getElement().setId("welcome");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Initializes the welcome widget.
   * @param connection The application connection.
   */
  protected void init(ApplicationConnection connection) {
    userMenu = new VModuleListMenu(connection, true);
    adminMenu = new VModuleListMenu(connection, true);
    bookmarksMenu = new VModuleListMenu(connection, true);
    workspaceContextMenu = new VModuleListMenu(connection, true);
    userItem = new VModuleItem();
    adminItem = new VModuleItem();
    bookmarksItem = new VModuleItem();
    workspaceContextItem = new VModuleItem();
    userMenu.addItem(userItem, false, true);
    adminMenu.addItem(adminItem, false, true);
    bookmarksMenu.addItem(bookmarksItem, false, true);
    workspaceContextMenu.addItem(workspaceContextItem, false, true);
    add(workspaceContextMenu);
    add(bookmarksMenu);
    add(adminMenu);
    add(userMenu);
    userMenu.setAutoOpen(false);
    adminMenu.setAutoOpen(false);
    bookmarksMenu.setAutoOpen(false);
    workspaceContextMenu.setAutoOpen(false);
    userMenu.getElement().setId("user_menu");
    adminMenu.getElement().setId("admin_menu");
    bookmarksMenu.getElement().setId("bookmarks_menu");
    workspaceContextMenu.getElement().setId("wrkcontext_menu");
  }
  
  /**
   * Sets the user menu.
   * @param menu The menu widget.
   */
  public void setUserMenu(final VModuleList menu) {
    userItem.setRoot(false);
    userItem.buildContent();
    userItem.setIcon("user");
    new Timer() {
      
      @Override
      public void run() {
        if (menu.getMenu() != null) {
          userItem.setSubMenu(menu.getMenu());
        }
      }
    }.schedule(100);
  }
  
  /**
   * Sets the connected user.
   * @param username The user name.
   */
  public void setConnectedUser(String username) {
    userItem.setCaption(username);
  }
  
  /**
   * Sets the admin menu.
   * @param menu The menu widget.
   */
  public void setAdminMenu(final VModuleList menu) {
    adminItem.setRoot(false);
    adminItem.buildContent();
    adminItem.setIcon("cog");
    new Timer() {
      
      @Override
      public void run() {
        if (menu.getMenu() != null) {
          adminItem.setSubMenu(menu.getMenu());
        }
      }
    }.schedule(100);
  }
  
  /**
   * Sets the bookmarks menu.
   * @param menu The menu widget.
   */
  public void setBookmarksMenu(final VModuleList menu) {
    bookmarksItem.setRoot(false);
    bookmarksItem.buildContent();
    bookmarksItem.setIcon("star");
    new Timer() {
      
      @Override
      public void run() {
        if (menu.getMenu() != null) {
          bookmarksItem.setSubMenu(menu.getMenu());
        }
      }
    }.schedule(100);
  }
  
  /**
   * Sets the workspace context menu.
   * @param menu The menu widget.
   */
  public void setWorkspaceContextItemMenu(Widget menu) {
    workspaceContextItem.setRoot(false);
    workspaceContextItem.buildContent();
    workspaceContextItem.setIcon("map-marker");
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  // menues
  private VModuleListMenu               userMenu;
  private VModuleListMenu               adminMenu;
  private VModuleListMenu               bookmarksMenu;
  private VModuleListMenu               workspaceContextMenu;
  
  // items
  private VModuleItem                   userItem;
  private VModuleItem                   adminItem;
  private VModuleItem                   bookmarksItem;
  private VModuleItem                   workspaceContextItem;
}
