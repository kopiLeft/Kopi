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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.shared.Connector;

/**
 * Collected utilities for connectors.
 */
public class ConnectorUtils {

  /**
   * Returns the parent connector having the mentioned class.
   * If the parent with the given class is not found, {@code null}
   * is returned as value.
   * @param child The child connector.
   * @param clazz The searched parent class.
   * @return The parent connector having the mentioned class or {@code null} if not found.
   */
  @SuppressWarnings("unchecked")
  public static final <T extends Connector> T getParent(ServerConnector child, Class<T> clazz) {
    if (child == null) {
      return null;
    }
    
    ServerConnector     parent = child.getParent();
    
    while (parent != null) {
      if (clazz == null || parent.getClass() == clazz) {
        return (T)parent;
      }
      
      parent = parent.getParent();
    }
    
    return null;
  }
  
  /**
   * Returns the connector of the given widget and having the mentioned class type.
   * @param connection The application connection instance.
   * @param widget The widget that we want to search its connector.
   * @param clazz The connector expected class.
   * @return The widget connector or {@code null} if the the connector has not the expected class.
   */
  @SuppressWarnings("unchecked")
  public static final <T extends Connector> T getConnector(ApplicationConnection connection,
                                                           Widget widget,
                                                           Class<T> clazz)
  {
    if (connection == null) {
      return null;
    }
    
    ComponentConnector          connector;
    
    connector = ConnectorMap.get(connection).getConnector(widget);
    if (connector != null && connector.getClass() == clazz) {
      return (T)connector;
    } else {
      return null;
    }
  }
}
