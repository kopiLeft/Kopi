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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.notification;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.vaadin.client.ApplicationConnection;

/** 
 * An error message popup window containing stack trace detail
 * for an server exception.
 */
public class VErrorMessagePopup extends VPopup {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new popup error message window.
   * @param connection The application connection.
   */
  public VErrorMessagePopup(ApplicationConnection connection) {
    super(connection, false, false);
    createContent();
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Creates the window content.
   */
  protected void createContent() {
    scroller = new ScrollPanel();
    scroller.setStyleName("error-details");
    message = new VSpan();
    message.setStyleName("details-message");
    scroller.setWidget(message);
    setWidget(scroller);
  }
  
  /**
   * Sets the error message.
   * @param message The error message.
   */
  public void setMessage(String message) {
    this.message.getElement().setInnerHTML(message);
  }
  
  @Override
  public void setPixelSize(int width, int height) {
    if (scroller != null) {
      scroller.setPixelSize(width, height);
    } else {
      super.setPixelSize(width, height);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VSpan					message;
  private ScrollPanel				scroller;
}
