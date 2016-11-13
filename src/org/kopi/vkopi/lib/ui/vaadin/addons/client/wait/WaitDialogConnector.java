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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.wait;

import org.kopi.vkopi.lib.ui.vaadin.addons.WaitDialog;

import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The wait dialog connector. 
 */
@Connect(value = WaitDialog.class, loadStyle = LoadStyle.LAZY)
@SuppressWarnings("serial")
public class WaitDialogConnector extends AbstractComponentConnector {
  
  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
  }
  
  @Override
  public VWaitDialog getWidget() {
    return (VWaitDialog) super.getWidget();
  }
  
  @Override
  public WaitDialogState getState() {
    return (WaitDialogState) super.getState();
  }
  
  @OnStateChange("title")
  /*package*/ void setTitle() {
    getWidget().setTitle(getState().title);
  }
  
  @OnStateChange("message")
  /*package*/ void setMessage() {
    getWidget().setMessage(getState().message);
  }
  
  @OnStateChange("maxTime")
  /*package*/ void createProgressBar() {
    getWidget().setMaxTime(getState().maxTime);
    getWidget().startWait();
  }
  
  @Override
  public void onUnregister() {
    getWidget().hide();
    getWidget().clear();
    getWidget().removeFromParent();
    super.onUnregister();
  }
}
