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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.AbstractNotificationState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.NotificationServerRpc;

import com.vaadin.ui.AbstractComponent;

/**
 * An abstract implementation of the notification panel.
 */
@SuppressWarnings("serial")
public abstract class AbstractNotification extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new Warning notification component.
   * @param title the warning notification title.
   * @param message the warning notification message. 
   */
  public AbstractNotification(String title, String message) {
    registerRpc(rpc);
    setImmediate(true);
    getState().title = title;
    getState().message = message;
    listeners = new ArrayList<NotificationListener>();    
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected AbstractNotificationState getState() {
    return (AbstractNotificationState) super.getState();
  }
  
  /**
   * Sets the notification locale.
   * @param locale The notification locale.
   */
  public void setLocale(String locale) {
    getState().locale = locale;
  }
  
  /**
   * Registers a new notification listener.
   * @param l The listener to be added.
   */
  public void addNotificationListener(NotificationListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a new notification listener.
   * @param l The listener to be removed.
   */
  public void removeNotificationListener(NotificationListener l) {
    listeners.remove(l);
  }

  /**
   * Fires an action when the notification is closed.
   * @param action
   */
  protected void fireOnCloseAction(boolean action) {
    for (NotificationListener l : listeners) {
      if (l != null) {
        l.onClose(action);
      }
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<NotificationListener>		listeners;

  /**
   * Notification server RPC
   */
  private NotificationServerRpc rpc= new NotificationServerRpc() {

    @Override
    public void onClose(boolean action) {
      fireOnCloseAction(action);
    }
  };
}
