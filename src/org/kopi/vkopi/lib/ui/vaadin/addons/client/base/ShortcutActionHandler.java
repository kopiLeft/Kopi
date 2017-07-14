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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.KeyboardListener;

/**
 * A shortcut action handler based on key down event.
 */
@SuppressWarnings("deprecation")
public class ShortcutActionHandler implements KeyDownHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new {@code ShortcutActionHandler} instance.
   */
  public ShortcutActionHandler() {
    this.actions = new HashMap<String, ShortcutAction>();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Handles the action of a given key down event.
   * @param event The key down event.
   */
  public void handleAction(KeyDownEvent event) {
    ShortcutAction              action;

    action = actions.get(ShortcutAction.createKey(event.getNativeKeyCode(), getKeyboardModifiers(event)));
    if (action != null) {
      event.preventDefault(); // prevent default action.
      action.performAction();
    }    
  }
  
  @Override
  public void onKeyDown(KeyDownEvent event) {
    handleAction(event);
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Adds an action to this handler.
   * @param action the action.
   */
  public void addAction(ShortcutAction action) {
    actions.put(action.getKey(), action);
  }
  
  /**
   * Gets the keyboard modifiers associated with a DOMEvent.
   * 
   * @param event the event.
   * @return the modifiers as defined in {@link KeyboardListener}.
   */
  public static int getKeyboardModifiers(KeyCodeEvent<?> event) {
    return (event.isShiftKeyDown() ? KeyboardListener.MODIFIER_SHIFT : 0)
     | (event.isMetaKeyDown() ? KeyboardListener.MODIFIER_META : 0)
     | (event.isControlKeyDown() ? KeyboardListener.MODIFIER_CTRL : 0)
     | (event.isAltKeyDown() ? KeyboardListener.MODIFIER_ALT : 0);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
 
  private final Map<String, ShortcutAction> 	actions;
}
