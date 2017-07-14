/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.wait;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.client.ApplicationConnection;

/**
 * A Wait panel widget.
 */
public class VWaitWindow extends VerticalPanel implements CloseHandler<PopupPanel>{
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VWaitPanel</code> widget
   */
  public VWaitWindow() {
    image = new VIcon();
    image.addStyleName(Styles.WAIT_WINDOW_IMAGE);
    text = new VSpan();
    text.setStyleName(Styles.WAIT_WINDOW_TEXT);
    add(image);
    add(text);
    setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
    setCellHorizontalAlignment(text, HasHorizontalAlignment.ALIGN_LEFT);
    setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_MIDDLE);
    setSpacing(0);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Initializes the widget.
   * @param connection The application connection.
   */
  public void init(ApplicationConnection connection) {
    image.setName("spinner");
    image.addStyleName("fa-spin");
    popup = new VPopup(connection, false, true);
    popup.addCloseHandler(this);
    popup.setGlassEnabled(true);
    popup.setGlassStyleName(Styles.WAIT_WINDOW + "-glass");
  }
  
  /**
   * Sets the wait panel text.
   * @param text The wait text.
   */
  public void setText(String text) {
    if (text != null) {
      this.text.setText(text);
    }
  }
  
  /**
   * Shows the wait window.
   * @param parent the parent widget.
   */
  public void show(HasWidgets parent) {
    if (popup != null) {
      popup.setWidget(this);
      parent.add(popup);
      popup.center();
      popup.setWaiting();
    }
  }
  
  /**
   * Closes the wait panel.
   */
  public void close() {
    if (popup != null) {
      popup.unsetWaiting();
      popup.hide();
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
  }
  
  @Override
  public void clear() {
    super.clear();
    image = null;
    text = null;
    popup = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VIcon                         image;
  private VSpan                         text;
  private VPopup                        popup;
}
