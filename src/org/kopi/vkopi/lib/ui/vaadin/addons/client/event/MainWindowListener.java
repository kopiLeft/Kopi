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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.event;

import java.io.Serializable;

import com.google.gwt.user.client.ui.Widget;

/**
 * Registered objects are notified about actions happening
 * in the main application window.
 */
public interface MainWindowListener extends Serializable {

  /**
   * Fired when the logout link is clicked.
   */
  public void onLogout();
  
  /**
   * Fired when the connected user link is clicked.
   */
  public void onUser();
  
  /**
   * Fired when windows link is clicked.
   */
  public void onWindows();

  /**
   * Fired when the given window is shown on the main window.
   * @param window The window the became visible.
   */
  public void onWindowVisible(Widget window);
}
