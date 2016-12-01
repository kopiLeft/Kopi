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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import com.vaadin.client.ui.AbstractComponentConnector;

/**
 * The Object field component connector.
 */
@SuppressWarnings("serial")
public abstract class ObjectFieldConnector extends AbstractComponentConnector {
  
  @Override
  protected void init() {
    super.init();
    getWidget().client = getConnection();
  }
  
  @Override
  public VObjectField getWidget() {
    return (VObjectField) super.getWidget();
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
    super.onUnregister();
  }
  
  /**
   * Returns the server RPC for object field.
   * @return The server RPC for object field.
   */
  protected ObjectFieldServerRpc getServerRpc() {
    return getRpcProxy(ObjectFieldServerRpc.class);
  }
  
  /**
   * Sends the field value to the server.
   */
  protected void sendValueToServer() {
    // TO DO
  }
}
