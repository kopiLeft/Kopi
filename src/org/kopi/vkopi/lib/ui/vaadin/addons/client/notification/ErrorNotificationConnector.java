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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.notification;

import org.kopi.vkopi.lib.ui.vaadin.addons.ErrorNotification;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The error notification connector.
 */
@SuppressWarnings("serial")
@Connect(value = ErrorNotification.class, loadStyle = LoadStyle.EAGER)
public class ErrorNotificationConnector extends AbstractNotificationConnector {
  
  @Override
  protected void init() {
    super.init();
    getWidget().client = getConnection();
  }
  
  @Override
  public VErrorNotification getWidget() {
    return (VErrorNotification) super.getWidget();
  }
}
