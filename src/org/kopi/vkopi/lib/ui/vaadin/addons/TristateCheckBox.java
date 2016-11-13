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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox.TristateCheckBoxClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox.TristateCheckBoxServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox.TristateCheckBoxState;

/**
 * The check box server side component.
 */
@SuppressWarnings("serial")
public class TristateCheckBox extends ObjectField {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public TristateCheckBox() {
    registerRpc(rpc);
    listeners = new ArrayList<TristateChangeListener>();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the check box value.
   * @param value The new value
   */
  public void setValue(String value) {
    this.value = value;
    getRpcProxy(TristateCheckBoxClientRpc.class).setState(value);
  }
  
  /**
   * Returns the check box value.
   * @return The check box value.
   */
  public String getValue() {
    return value;
  }
  
  /**
   * Registers a new trisate change listener.
   * @param l The listener to be registered.
   */
  public void addTristateChangeListener(TristateChangeListener l) {
    listeners.add(l);
  }
  
  /**
   * Fires a state change event.
   * @param state The new state
   */
  protected void fireStateChanged(String state) {
    for (TristateChangeListener l : listeners) {
      if (l != null) {
	l.stateChanged(state);
      }
    }
  }
  
  @Override
  protected TristateCheckBoxState getState() {
    return (TristateCheckBoxState) super.getState();
  }
  
  @Override
  public void beforeClientResponse(boolean initial) {
    super.beforeClientResponse(initial);
    getState().state = value;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String				value = "";
  private ArrayList<TristateChangeListener>	listeners;
  private TristateCheckBoxServerRpc		rpc = new TristateCheckBoxServerRpc() {
    
    @Override
    public void setState(String state) {
      value = state;
      fireStateChanged(state);
    }
  };
}
