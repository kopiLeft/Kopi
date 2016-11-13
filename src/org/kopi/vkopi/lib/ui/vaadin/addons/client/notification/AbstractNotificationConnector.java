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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.NotificationListener;

import com.vaadin.client.ui.AbstractComponentConnector;

/**
 * An abstract notification component connector.
 */
@SuppressWarnings("serial")
public class AbstractNotificationConnector extends AbstractComponentConnector implements NotificationListener {
  
  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
    getWidget().addNotificationListener(this);
  }
  
  @Override
  public VAbstractNotification getWidget() {
    return (VAbstractNotification) super.getWidget();
  }
  
  @Override
  public AbstractNotificationState getState() {
    return (AbstractNotificationState) super.getState();
  }
  
  @Override
  public void onClose(boolean action) {
    getRpcProxy(NotificationServerRpc.class).onClose(action);     
  }
  
  @Override
  public void onUnregister() {
    getWidget().removeNotificationListener(this);
    getWidget().clear();
    getWidget().removeFromParent();
    super.onUnregister();
  }
}
