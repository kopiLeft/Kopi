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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.ActorServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.ActorState;

import com.vaadin.shared.communication.SharedState;
import com.vaadin.ui.AbstractComponent;

/**
 * The server side of an actor.
 */
@SuppressWarnings("serial")
public class Actor extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new actor component.
   * @param caption The actor caption.
   * @param description The actor help.
   * @param menu The menu to which this actor belongs to.
   * @param icon The actor icon.
   * @param acceleratorKey The accelerator key.
   * @param modifiersKey The modifiers key.
   */
  public Actor(String caption,
               String description,
               String menu,
               String icon,
               int acceleratorKey,
               int... modifiersKey)
  {
    registerRpc(rpc);
    setImmediate(true);
    getState().icon = icon;
    getState().acceleratorKey = acceleratorKey;
    getState().modifiersKey = modifiersKey;
    getState().menu = menu;
    setCaption(caption);
    setDescription(description);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected ActorState getState() {
    return (ActorState) super.getState();
  }
  
  @Override
  protected ActorState getState(boolean markAsDirty) {
    return (ActorState) super.getState(markAsDirty);
  }
  
  /**
   * Fires an action performed event to all registered listeners.
   */
  protected void fireActionPerformed() {
    fireEvent(new ActionEvent(this));
  }
  
  /**
   * Adds the actor action listener.
   * @param listener The Listener to be added.
   */
  public void addActionListener(ActionListener listener) {
    addListener(ActionEvent.class, listener, ActionListener.ACTOR_ACTION_METHOD);
  }

  /**
   * Removes the actor action listener.
   * @param listener The Listener to be removed.
   */
  public void removeActionListener(ActionListener listener) {
    removeListener(ActionEvent.class, listener, ActionListener.ACTOR_ACTION_METHOD);
  }
  
  /**
   * Always enable this connector to allow RPC communications
   * even if the {@link SharedState#enabled} is set to {@code false}.
   * The actor ability in the client side is controlled by {@link SharedState#enabled}
   * value but this actor still always enabled to allow RPC communications when the owner field
   * is focused in the client side using the free navigation mechanism. In this case, actors are
   * not controlled by the server side but by the client side using the {@link ActorState#number}
   * value to identify the actor in the client side.
   */
  @Override
  public boolean isConnectorEnabled() {
    return true;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Actor RPC server.
   */
  private ActorServerRpc rpc = new ActorServerRpc() {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public void actionPerformed() {
      fireActionPerformed();
    }
  };
}
