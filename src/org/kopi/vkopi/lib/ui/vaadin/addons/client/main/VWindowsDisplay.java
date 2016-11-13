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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The opened windows display widget.
 * This widget will be the responsible of displaying
 * the open windows menu.
 */
public class VWindowsDisplay extends SimplePanel implements CloseHandler<PopupPanel>{

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the display widget.
   */
  public VWindowsDisplay() {
    menu = new VWindowsMenu(true);
    setWidget(menu);
    setStyleName("k-windows-menu-container");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates an item for the given window.
   * @param window The window widget.
   * @param caption The window caption.
   * @return The menu item added
   */
  public VWindowsMenuItem addWindow(Widget window, String caption) {
    VWindowsMenuItem		item;
    
    item = new VWindowsMenuItem(window, caption, false);
    menu.addItem(item);
    
    return item;
  }
  
  /**
   * Updates the menu item caption for the given window.
   * @param window The concerned window.
   * @param caption The new item caption.
   */
  public void updateCaption(Widget window, String caption) {
    VWindowsMenuItem            item;
    
    item = menu.getItemFor(window);
    if (item != null) {
      item.setText(caption);
    }
  }
  
  /**
   * Removes the given window item.
   * @param window The window widget.
   */
  public void removeWindow(Widget window) {
    MenuItem		item;
    
    item = menu.getItemFor(window);
    if (item != null) {
      menu.removeItem(item);
    }
  }
  
  /**
   * Removes the given window item.
   * @param window The window item.
   */
  public void removeWindow(MenuItem window) {
    menu.removeItem(window);
  }
  
  /**
   * Sets the given window to be the current widget.
   * @param window The new current window.
   */
  public void setCurrent(Widget window) {
    setCurrent(menu.getItemFor(window));
  }
  
  /**
   * Sets the given window to be the current.
   * @param window The window widget.
   */
  public void setCurrent(MenuItem item) {
    menu.resetCurrentWindow();
    if (item != null) {
      item.addStyleDependentName("current");
    }
  }
  
  /**
   * Shows the windows menu.
   * @param connection The application connection.
   * @param target The relative target.
   */
  public void showMenuRelativeTo(ApplicationConnection connection,
                                 HasWidgets parent,
                                 UIObject target)
  {
    popup = new VPopup(connection, true, false);
    parent.add(popup);
    popup.setAnimationEnabled(true);
    popup.setWidget(this);
    popup.addCloseHandler(this);
    popup.setAnimationType(AnimationType.ROLL_DOWN);
    popup.showRelativeTo(target);
  }
  
  /**
   * Hides the windows menu.
   */
  public void hideMenu() {
    if (popup != null) {
      popup.hide();
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    if (popup != null && event.getTarget() == popup) {
      popup.clear();
      popup.removeFromParent();
      popup = null;
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VWindowsMenu		menu;
  private VPopup			popup;
}
