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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.LabelServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.LabelState;

import com.vaadin.ui.AbstractComponent;

/**
 * The label server component.
 */
@SuppressWarnings("serial")
public class Label extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the label instance.
   * @param text The label instance.
   */
  public Label(String caption) {
    registerRpc(rpc);
    setImmediate(true);
    setCaption(caption);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the auto fill ability. 
   * @param autofill the auto fill ability. 
   */
  public void setAutofill(boolean autofill) {
    getState().hasAutofill = autofill;
  }
  
  /**
   * Sets the label info text.
   * @param infoText The info text.
   */
  public void setInfoText(String infoText) {
    getState().infoText = infoText;
  }
  
  @Override
  protected LabelState getState() {
    return (LabelState) super.getState();
  }
  
  /**
   * Fires a label event to all registered listeners.
   */
  protected void fireClicked() {
    fireEvent(new LabelEvent(this));
  }
  
  /**
   * Adds the label listener.
   * @param listener The Listener to be added.
   */
  public void addLabelListener(LabelListener listener) {
    addListener(LabelEvent.class, listener, LabelListener.LABEL_CLICK_METHOD);
  }

  /**
   * Removes the label listener.
   * @param listener The Listener to be removed.
   */
  public void removeLabelListener(LabelListener listener) {
    removeListener(LabelEvent.class, listener, LabelListener.LABEL_CLICK_METHOD);
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
    return true;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final LabelServerRpc		rpc = new LabelServerRpc() {
    
    @Override
    public void onClick() {
      fireClicked();
    }
  };
}
