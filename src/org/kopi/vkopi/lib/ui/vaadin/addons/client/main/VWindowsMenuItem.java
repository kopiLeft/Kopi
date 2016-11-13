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

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class for menu items in the already opened windows menu.
 */
public class VWindowsMenuItem extends MenuItem {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new already opened window item.
   * @param caption The item caption.
   * @param asHTML Treat caption as HTML ?
   */
  public VWindowsMenuItem(Widget window, String caption, boolean asHTML) {
    super(caption, asHTML, (MenuBar)null);
    getElement().getStyle().setProperty("whiteSpace", "nowrap");
    setStyleName(STYLENAME_DEFAULT);
    setWindow(window);
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the window widget.
   * @return The window widget..
   */
  public Widget getWindow() {
    return window;
  }

  /**
   * Sets the window widget.
   * @param window The window widget.
   */
  public void setWindow(Widget window) {
    this.window = window;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static final String 		STYLENAME_DEFAULT = "item";
  private Widget 			window;
}
