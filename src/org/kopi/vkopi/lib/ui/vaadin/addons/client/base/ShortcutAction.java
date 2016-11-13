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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.KeyboardListener;

/**
 * A shortcut action represented by its key code and modifiers.
 */
@SuppressWarnings("deprecation")
public abstract class ShortcutAction {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new {@code ShortcutAction} object.
   * @param caption The action caption.
   * @param key The action key code.
   * @param modifiers The action modifiers key.
   */
  public ShortcutAction(String caption, int key, int... modifiers) {
    this.caption = caption;
    this.key = key;
    this.modifierMask = createModifierMask(modifiers);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Performs the action handled by this shortcut.
   */
  public abstract void performAction();
  
  /**
   * Creates the action key.
   * @return The action key.
   */
  public String getKey() {
    // key is based on key & modifiers
    return createKey(key , modifierMask);
  }
  
  /**
   * Creates a unique key for a key code and a modifier mask.
   * @param keyCode The key code.
   * @param modifierMask The modifier mask
   * @return The unique key.
   */
  public static String createKey(int keyCode, int modifierMask) {
    return keyCode + "-" + modifierMask;
  }
  
  /**
   * Creates the modifier mask of this shortcut action.
   * @param modifiers The modifiers key.
   * @return The modifier mask to be used.
   */
  protected final int createModifierMask(int[] modifiers) {
    int			modifiersMask = 0;
    
    if (modifiers != null) {
      for (int i = 0; i < modifiers.length; i++) {
	switch (modifiers[i]) {
	case KeyCodes.KEY_ALT:
	  modifiersMask = modifiersMask | KeyboardListener.MODIFIER_ALT;
	  break;
	case KeyCodes.KEY_CTRL:
	  modifiersMask = modifiersMask | KeyboardListener.MODIFIER_CTRL;
	  break;
	case KeyCodes.KEY_SHIFT:
	  modifiersMask = modifiersMask | KeyboardListener.MODIFIER_SHIFT;
	  break;
	case KeyCodes.KEY_WIN_KEY_LEFT_META:
	  modifiersMask = modifiersMask | KeyboardListener.MODIFIER_META;
	  break;
	default:
	  break;
	}
      }
    }
    
    return modifiersMask;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  protected final String			caption;
  protected final int				key;
  protected final int				modifierMask;
}
