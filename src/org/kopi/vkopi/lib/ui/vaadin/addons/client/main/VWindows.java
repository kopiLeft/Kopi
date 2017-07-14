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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VAnchorPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VULPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.dom.client.Document;
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
    windowsLink = new VULPanel();
    inner = new VLIPanel();
    anchor = new VAnchorPanel();
    label = new VSpan();
    icon = new VIcon();
    windowsLink.add(inner);
    anchor.setHref("#");
    windowsLink.setId("windows_link");
    label.setStyleName("hide");
    inner.setWidget(anchor);
    setWidget(windowsLink);
    anchor.add(label);
    anchor.add(icon);
    icon.setName("clone");
    hideLabel();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the link text.
   * @param text The link text.
   */
  public void setText(String text) {
    label.setText(text);
  }
  
  /**
   * Shows the link localized label.
   */
  public void showLabel() {
    label.setVisible(true);
  }
  
  /**
   * Hides the link localized label.
   */
  public void hideLabel() {
    label.setVisible(false);
  }
  
  /**
   * Sets this windows link to be focused.
   * @param focus The focus state.
   */
  public void setFocused(boolean focus) {
    if (focus) {
      windowsLink.addStyleName("focus");
      inner.addStyleName("active");
    } else {
      windowsLink.removeStyleName("focus");
      inner.removeStyleName("active");
    }
  }
  
  /**
   * Registers a click handler to the welcome text.
   * @param handler The handler to be registered.
   * @return The registration handler.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return anchor.addClickHandler(handler);
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
  
  /**
   * A simple panel that wraps an <li> element inside.
   */
  private static final class VLIPanel extends SimplePanel {
    
    public VLIPanel() {
      super(Document.get().createLIElement());
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VULPanel                windowsLink;
  private final VLIPanel                inner;
  private final VAnchorPanel            anchor;
  private final VSpan                   label;
  private final VIcon                   icon;
  private boolean                       enabled;
}
