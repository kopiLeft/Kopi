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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Widget that contains the opened windows
 * in the current application session.
 * By clicking on the link, a popup will be shown that contains
 * the opened windows and then the user can switch between them.
 */
public class VWindows extends SimplePanel implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the opened windows handler widget.
   */
  public VWindows() {
    getElement().setId("windows");
    windowsLink = new VAnchor();
    windowsLink.setHref("#");
    windowsLink.setId("windows_link");
    setWidget(windowsLink);
    setEnabled(false);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the link text.
   * @param text The link text.
   */
  public void setText(String text) {
    windowsLink.setText(text);
  }
  
  /**
   * Registers a click handler to the welcome text.
   * @param handler The handler to be registered.
   * @return The registration handler.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return windowsLink.addClickHandler(handler);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (enabled) {
      removeStyleName("empty");
    } else {
      setStyleName("empty");
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAnchor			windowsLink;
  private boolean			enabled;
}
