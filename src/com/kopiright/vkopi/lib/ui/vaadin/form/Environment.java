/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;

/**
 * VAADIN environment.
 */
public class Environment {
  
  /**
   * Adds default key.
   * @param form The form view.
   */
  public static void addDefaultFormKey(DForm form) {  
    addKey(form, KeyNavigator.KEY_EMPTY_FIELD, KeyCode.ENTER, ModifierKey.CTRL);
    addKey(form, KeyNavigator.KEY_DIAMETER, KeyCode.D, ModifierKey.CTRL);
    addKey(form, KeyNavigator.KEY_REC_DOWN, KeyCode.PAGE_DOWN);
    addKey(form, KeyNavigator.KEY_REC_DOWN, KeyCode.PAGE_DOWN, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_REC_FIRST, KeyCode.HOME, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_REC_LAST, KeyCode.END, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_REC_UP, KeyCode.PAGE_UP);
    addKey(form, KeyNavigator.KEY_REC_UP, KeyCode.PAGE_UP, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_PREV_FIELD, KeyCode.ARROW_LEFT, ModifierKey.CTRL);
    addKey(form, KeyNavigator.KEY_PREV_FIELD, KeyCode.TAB, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_PREV_FIELD, KeyCode.ARROW_UP, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_NEXT_FIELD, KeyCode.ARROW_RIGHT, ModifierKey.CTRL);
    addKey(form, KeyNavigator.KEY_NEXT_FIELD, KeyCode.TAB);
    addKey(form, KeyNavigator.KEY_NEXT_FIELD, KeyCode.ARROW_DOWN, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_PRINTFORM, PRINTSCREEN, ModifierKey.SHIFT);
    // the magnet card reader sends a CNTR-J as last character
    addKey(form, KeyNavigator.KEY_NEXT_FIELD, KeyCode.J, ModifierKey.CTRL);

    addKey(form, KeyNavigator.KEY_ESCAPE, KeyCode.ESCAPE);
    addKey(form, KeyNavigator.KEY_NEXT_VAL, KeyCode.ARROW_DOWN, ModifierKey.CTRL);
    addKey(form, KeyNavigator.KEY_PREV_VAL, KeyCode.ARROW_UP, ModifierKey.CTRL);
    // text field shortcuts
    addKey(form, KeyNavigator.KEY_NEXT_BLOCK, KeyCode.ENTER, ModifierKey.SHIFT);
    addKey(form, KeyNavigator.KEY_PREV_FIELD, KeyCode.ARROW_UP);
    addKey(form, KeyNavigator.KEY_NEXT_FIELD, KeyCode.ARROW_DOWN);
    addKey(form, KeyNavigator.KEY_NEXT_FIELD, KeyCode.ENTER);
  }

  /**
   * Registers a key.
   * @param form The form view.
   * @param code The key code.
   * @param keyCode The shortcut key code.
   * @param modfiersKeys The modifiers key.
   */
  private static void addKey(DForm form, int code, int keyCode, int... modfiersKeys) {
    form.addShortcutListener(new KeyNavigator(code, keyCode, modfiersKeys)); 
  }
		  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
		  
  private static final int		PRINTSCREEN = 44;
}
