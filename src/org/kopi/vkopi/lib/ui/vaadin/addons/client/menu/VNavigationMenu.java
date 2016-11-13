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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.vaadin.client.ApplicationConnection;

/**
 * The popup that shows the navigation panel as its content.
 */
public class VNavigationMenu extends VPopup {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new navigation menu with its content.
   * @param connection The application connection.
   * @param panel The content of the menu.
   */
  public VNavigationMenu(ApplicationConnection connection) {
    super(connection, true, false);
    addCloseHandler(this);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Sets the the navigation panel associated with this navigation menu.
   * @param panel The navigation panel.
   */
  public void setNavigationPanel(VNavigationPanel panel) {
    setWidget(panel);
  }
  
  /**
   * Opens this menu relatively to the given target.
   * @param target The reference target. If {@code null}, the
   * menu will be shown centered.
   */
  public void open(UIObject target) {
    if (target != null) {
      showRelativeTo(target);
    } else {
      center();
    }
    isOpened = true;
    if (VInputTextField.getLastFocusedTextField() != null) {
      lastFocusedWindow = VInputTextField.getLastFocusedTextField().getParentWindow();
    }
  }
  
  /**
   * Closes this navigation menu.
   */
  public void close() {
    hide();
    isOpened = false;
  }
  
  /**
   * Tells that this menu is already opened.
   * @return {@code true} is the menu is already opened.
   */
  public boolean isOpened() {
    return isOpened;
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    super.onClose(event);
    if (lastFocusedWindow != null) {
      lastFocusedWindow.goBackToLastFocusedTextBox();
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VWindow                               lastFocusedWindow;
  private boolean                               isOpened;
}
