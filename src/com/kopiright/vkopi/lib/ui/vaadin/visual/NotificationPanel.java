/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * A notification panel based on a {@link CssLayout}
 * for performance issues.
 */
public class NotificationPanel extends CssLayout {
  
  //--------------------------------------------------
  // CONSTRUCTOR
  //--------------------------------------------------
  
  /**
   * Creates a new <code>NotificationPanel</code> instance.
   * @param title The notification panel title.
   * @param message The notification message.
   */
  public NotificationPanel(String title, String message) {
    addStyleName(KopiTheme.NOTIFICATION_STYLE);
    titleLabel = new Label(title);
    titleLabel.setSizeUndefined();
    titleLabel.addStyleName(KopiTheme.NOTIFICATION_TITLE_STYLE);
    messageLabel = new Label(message);
    messageLabel.setSizeUndefined();
    messageLabel.addStyleName(KopiTheme.NOTIFICATION_MESSAGE_STYLE);
    addComponent(titleLabel);
    addComponent(messageLabel);
    setVisible(false);
  }
  
  //--------------------------------------------------
  // ACCESSORS
  //--------------------------------------------------
  
  /**
   * Returns the notification panel title.
   * @return The notification panel title.
   */
  public String getTitle() {
    return titleLabel.getValue();
  }
  
  /**
   * Sets the notification panel title.
   * @param title The notification panel title.
   */
  public void setTitle(String title) {
    if (!title.trim().equals("")) {
      this.titleLabel.setValue(title);
      this.titleLabel.setVisible(true);
    } else {
      this.titleLabel.setVisible(false);
    }
  }

  /**
   * Returns the notification message.
   * @return The notification message.
   */
  public String getMessage() {
    return messageLabel.getValue();
  }

  /**
   * Sets the notification message.
   * @param message The notification message.
   */
  public void setMessage(String message) {
    if (!message.trim().equals("")) {
      this.messageLabel.setValue(message);
      this.messageLabel.setVisible(true);
    } else {
      this.messageLabel.setVisible(false);
    }
  }  
  
  //--------------------------------------------------
  // UTILS
  //--------------------------------------------------
  
  /**
   * Displays the notification panel.
   * @param title The notification panel title.
   * @param message The notification message.
   */
  public void displayNotification(final String title, final String message) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        setTitle(title);
        setMessage(message);
        setVisible(true);
      }
    });
  }
  
  /**
   * Hides this notification panel.
   */
  public void hide() {
    if (isVisible()) {
      BackgroundThreadHandler.start(new Runnable() {
	
        @Override
        public void run() {
	  setTitle("");
	  setMessage("");
	  setVisible(false);
        }
      });
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------

  private Label				titleLabel;
  private Label				messageLabel;
  private static final long 		serialVersionUID = -296788139044346704L;
}
