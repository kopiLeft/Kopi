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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldState;

import com.vaadin.ui.AbstractSingleComponentContainer;

/**
 * The field server component. Contains one text input field or other component
 * like image field.
 */
@SuppressWarnings("serial")
public class Field extends AbstractSingleComponentContainer {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the field server component.
   * @param hasIncrement has increment button ?
   * @param hasDecrement has decrement button ?
   */
  public Field(boolean hasIncrement, boolean hasDecrement) {
    listeners = new ArrayList<FieldListener>();
    registerRpc(rpc);
    getState().hasIncrement = hasIncrement;
    getState().hasDecrement = hasIncrement;
    setImmediate(true);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Registers a field listener.
   * @param l The listener to be registered.
   */
  public void addFieldListener(FieldListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a field listener.
   * @param l The listener to be removed.
   */
  public void removeFieldListener(FieldListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires an increment action.
   */
  protected void fireIncremented() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onIncrement();
      }
    }
  }
  
  /**
   * Fires an decrement action.
   */
  protected void fireDecremented() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onDecrement();
      }
    }
  }
  
  /**
   * Fires when this field is clicked.
   */
  protected void fireClicked() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onClick();
      }
    }
  }
  
  @Override
  protected FieldState getState() {
    return (FieldState) super.getState();
  }
  
  @Override
  public void setVisible(boolean visible) {
    // use CSS styles to hide components.
    // in vaadin 7, invisible components are not sent to
    // client side. But we want that invisible components occupy
    // their place in the layout.
    if (getState().visible == visible) {
      return;
    }    
    getState().visible = visible;
  }
  
  @Override
  public boolean isVisible() {
    return true; // ensure that all fields are send to client side.
  }
  
  /**
   * Sets the field visible height.
   * @param visibleHeight The visible height.
   */
  public void setVisibleHeight(int visibleHeight) {
    getState().visibleHeight = visibleHeight;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<FieldListener>		listeners;
  private FieldServerRpc		rpc = new FieldServerRpc() {
    
    @Override
    public void onIncrement() {
      fireIncremented();
    }
    
    @Override
    public void onDecrement() {
      fireDecremented();
    }
    
    @Override
    public void onClick() {
      fireClicked();
    }
  };
}
