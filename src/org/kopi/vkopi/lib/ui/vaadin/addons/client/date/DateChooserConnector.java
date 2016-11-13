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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.date;

import java.util.Date;

import org.kopi.vkopi.lib.ui.vaadin.addons.DateChooser;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.DateChooserListener;

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The date chooser connector.
 */
@SuppressWarnings("serial")
@Connect(value = DateChooser.class, loadStyle = LoadStyle.LAZY)
public class DateChooserConnector extends AbstractComponentConnector implements DateChooserListener {

  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
    getWidget().addDateChooserListener(this);
  }
  
  @Override
  public VDateChooser getWidget() {
    return (VDateChooser) super.getWidget();
  }
  
  @Override
  public DateChooserState getState() {
    return (DateChooserState) super.getState();
  }

  @Override
  public void onClose(Date selected, int offset) {
    getRpcProxy(DateChooserServerRpc.class).onClose(selected, offset);
  }
  
  @Override
  public void onUnregister() {
    getWidget().removeDateChooserListener(this);
    getWidget().clear();
    super.onUnregister();
  }
}
