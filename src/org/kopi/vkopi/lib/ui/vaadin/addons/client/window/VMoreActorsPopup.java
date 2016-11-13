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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;

import com.vaadin.client.ApplicationConnection;

/**
 * The more actors popup window.
 */
public class VMoreActorsPopup extends VPopup {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new more actors popup menu.
   * @param connection The application connection.
   */
  public VMoreActorsPopup(ApplicationConnection connection) {
    super(connection, true, false);
    setPreviewingAllNativeEvents(true);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the animation type to roll down.
   */
  public native void setRollDownAnimation() /*-{
    this.@com.google.gwt.user.client.ui.PopupPanel::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
  }-*/;
}
