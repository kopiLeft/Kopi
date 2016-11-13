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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox;

import org.kopi.vkopi.lib.ui.vaadin.addons.TristateCheckBox;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox.VTristateCheckBox.State;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.ObjectFieldConnector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Tri state check box component connector.
 */
@Connect(value = TristateCheckBox.class, loadStyle = LoadStyle.LAZY)
@SuppressWarnings("serial")
public class TristateCheckBoxConnector extends ObjectFieldConnector implements TristateChangeListener {

  @Override
  protected void init() {
    super.init();
    getWidget().addTristateChangeListener(this);
    registerRpc(TristateCheckBoxClientRpc.class, rpc);
  }
  
  @Override
  public VTristateCheckBox getWidget() {
    return (VTristateCheckBox) super.getWidget();
  }
  
  @Override
  public TristateCheckBoxState getState() {
    return (TristateCheckBoxState) super.getState();
  }

  @Override
  public void stateChanged(State state) {
    getRpcProxy(TristateCheckBoxServerRpc.class).setState(state.getStyleName());
  }
  
  @Override
  protected void sendValueToServer() {
    stateChanged(getWidget().getState());
  }
  
  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    if (getState().state != null) {
      getWidget().setState(getState().state);
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private TristateCheckBoxClientRpc		rpc = new TristateCheckBoxClientRpc() {
    
    @Override
    public void setState(final String state) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setState(state);
        }
      });
    }
  };
}
