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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyHandler.HasAcceleratorKey;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.KeyboardListener;

/**
 * The accelerator key and modifiers combination.
 */
@SuppressWarnings("deprecation")
public class AcceleratorKeyCombination {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------

  /**
   * Creates a new {@code AcceleratorKeyCombination} instance.
   * @param kc The key code.
   * @param modifierMask The modifier mask.
   */
  public AcceleratorKeyCombination(char kc, int modifierMask) {
    keyCode = kc;
    modifiersMask = modifierMask;
    this.kc = kc;
  }

  /**
   * Creates a new {@code AcceleratorKeyCombination} instance.
   * @param kc  The key code.
   * @param modifiers The modifier mask.
   * @param owner The accelerator owner.
   */
  public AcceleratorKeyCombination(int kc, int[] modifiers, HasAcceleratorKey owner) {
    this.owner = owner;
    this.kc = kc;
    keyCode = (char) kc;
    modifiersMask = 0;
    
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
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Checks if the given {@link AcceleratorKeyCombination} is equals to this object.
   * @param other The other {@link AcceleratorKeyCombination}
   * @return {@code true} if its the same {@link AcceleratorKeyCombination}
   */
  public boolean equals(AcceleratorKeyCombination other) {
    if (keyCode == other.keyCode && modifiersMask == other.modifiersMask) {
      return true;
    }

    return false;
  }
  
  /**
   * Returns the accelerator key owner.
   * @return The accelerator key owner.
   */
  public HasAcceleratorKey getOwner() {
    return owner;
  }

  /**
   * Returns the string representation of this accelerator key.
   * @param locale The locale to be used.
   * @return The string representation of this accelerator key.
   */
  public String toString(String locale) {
    return getKeyModifiersText(locale) + getKeyText(locale);
  }
  
  /**
   * Returns the text of the key code of this accelerator.
   * @param locale The locale to be used.
   * @return The text of the key code.
   */
  protected String getKeyText(String locale) {
    switch (kc) {
    case KeyCodes.KEY_ENTER:
      return LocalizedProperties.getString(locale, "enter");
    case KeyCodes.KEY_PAGEUP:
      return LocalizedProperties.getString(locale, "pgup");
    case KeyCodes.KEY_PAGEDOWN:
      return LocalizedProperties.getString(locale, "pgdn");
    case KeyCodes.KEY_HOME:
      return LocalizedProperties.getString(locale, "home");
    case KeyCodes.KEY_END:
      return LocalizedProperties.getString(locale, "end");
    case KeyCodes.KEY_ESCAPE:
      return LocalizedProperties.getString(locale, "escape");
    case KeyCodes.KEY_F1:
      return "F1";
    case KeyCodes.KEY_F2:
      return "F2";
    case KeyCodes.KEY_F3:
      return "F3";
    case KeyCodes.KEY_F4:
      return "F4";
    case KeyCodes.KEY_F5:
      return "F5";
    case KeyCodes.KEY_F6:
      return "F6";
    case KeyCodes.KEY_F7:
      return "F7";
    case KeyCodes.KEY_F8:
      return "F8";
    case KeyCodes.KEY_F9:
      return "F9";
    case KeyCodes.KEY_F10:
      return "F10";
    case KeyCodes.KEY_F11:
      return "F11";
    case KeyCodes.KEY_F12:
      return "F12";
    default:
      return String.valueOf(keyCode);
    }
  }
  
  /**
   * Returns the modifier key text.
   * @param locale The locale to be used.
   * @return The modifier key text.
   */
  protected String getKeyModifiersText(String locale) {
    StringBuffer                buff;
    
    buff = new StringBuffer();
    if ((modifiersMask & KeyboardListener.MODIFIER_ALT) != 0) {
      buff.append(LocalizedProperties.getString(locale, "alt"));
      buff.append("-");
    }
    
    if ((modifiersMask & KeyboardListener.MODIFIER_CTRL) != 0) {
      buff.append(LocalizedProperties.getString(locale, "control"));
      buff.append("-");
    }
    
    if ((modifiersMask & KeyboardListener.MODIFIER_SHIFT) != 0) {
      buff.append(LocalizedProperties.getString(locale, "shift"));
      buff.append("-");
    }
    
    if ((modifiersMask & KeyboardListener.MODIFIER_META) != 0) {
      buff.append(LocalizedProperties.getString(locale, "meta"));
      buff.append("-");
    }
    
    return buff.toString();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /*package*/ char 				keyCode = 0;
  /*package*/ int 				modifiersMask;
  private final int                             kc; // original key code
  private HasAcceleratorKey			owner;
}
