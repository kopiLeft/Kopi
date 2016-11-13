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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutActionHandler;

import com.google.gwt.event.dom.client.KeyCodes;

/**
 * A key navigation handler for a text input.
 */
public class TextFieldNavigationHandler extends ShortcutActionHandler {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the {@code TextFieldNavigationHandler} instance.
   * @param isMulti is it a multiple line text field ?
   */
  protected TextFieldNavigationHandler(boolean isMulti) {
    this.isMulti = isMulti;
  }
  
  //---------------------------------------------------
  // STATIC UTILS
  //---------------------------------------------------
  
  /**
   * Statically create a new navigation handler for text fields.
   * @param connector The text field connector.
   * @param isMulti Is it a multiple line field ?
   * @param hasAutocomplete if the field has the auto complete feature. 
   * @return The navigation handler instance.
   */
  public static TextFieldNavigationHandler newInstance(TextFieldConnector connector,
                                                       VInputTextField field,
                                                       boolean isMulti)
  {
    TextFieldNavigationHandler			handler;
    
    handler = new TextFieldNavigationHandler(isMulti);
    handler.createNavigatorKeys(connector, field);
    return handler;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates the navigation actions.
   * @param connector The text field connector.
   */
  protected void createNavigatorKeys(TextFieldConnector connector, VInputTextField field) {
    addKeyNavigator(connector, field, KeyNavigator.KEY_EMPTY_FIELD, KeyCodes.KEY_ENTER, KeyCodes.KEY_CTRL);
    addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_BLOCK, KeyCodes.KEY_ENTER, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_DIAMETER, KeyCodes.KEY_D, KeyCodes.KEY_CTRL);
    addKeyNavigator(connector, field, KeyNavigator.KEY_REC_DOWN, KeyCodes.KEY_PAGEDOWN, 0);
    addKeyNavigator(connector, field, KeyNavigator.KEY_REC_DOWN, KeyCodes.KEY_PAGEDOWN, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_REC_FIRST, KeyCodes.KEY_HOME, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_REC_LAST, KeyCodes.KEY_END, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_REC_UP, KeyCodes.KEY_PAGEUP, 0);
    addKeyNavigator(connector, field, KeyNavigator.KEY_REC_UP, KeyCodes.KEY_PAGEUP, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_PREV_FIELD, KeyCodes.KEY_LEFT, KeyCodes.KEY_CTRL);
    addKeyNavigator(connector, field, KeyNavigator.KEY_PREV_FIELD, KeyCodes.KEY_TAB, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_PREV_FIELD, KeyCodes.KEY_UP, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_FIELD, KeyCodes.KEY_RIGHT, KeyCodes.KEY_CTRL);
    addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_FIELD, KeyCodes.KEY_TAB, 0);
    addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_FIELD, KeyCodes.KEY_DOWN, KeyCodes.KEY_SHIFT);
    addKeyNavigator(connector, field, KeyNavigator.KEY_PRINTFORM, KeyCodes.KEY_PRINT_SCREEN, KeyCodes.KEY_SHIFT);
    // the magnet card reader sends a CNTR-J as last character
    addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_FIELD, KeyCodes.KEY_J, KeyCodes.KEY_CTRL);

    addKeyNavigator(connector, field, KeyNavigator.KEY_ESCAPE, KeyCodes.KEY_ESCAPE, 0);
    addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_VAL, KeyCodes.KEY_DOWN, KeyCodes.KEY_CTRL);
    addKeyNavigator(connector, field, KeyNavigator.KEY_PREV_VAL, KeyCodes.KEY_UP, KeyCodes.KEY_CTRL);

    if (!isMulti) {
      // In multiline fields these keys are used for other stuff
      addKeyNavigator(connector, field, KeyNavigator.KEY_PREV_FIELD, KeyCodes.KEY_UP, 0);
      addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_FIELD, KeyCodes.KEY_DOWN, 0);
      addKeyNavigator(connector, field, KeyNavigator.KEY_NEXT_FIELD, KeyCodes.KEY_ENTER, 0);
    }
  }
  
  /**
   * Adds a key navigator action to this handler.
   * @param connector The input text connector.
   * @param code The navigator code.
   * @param key The key code.
   * @param modifiers The modifiers.
   */
  protected void addKeyNavigator(TextFieldConnector connector, VInputTextField field, int code, int key, int... modifiers) {
    addAction(new KeyNavigator(connector, field, code, key, modifiers));
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final boolean			isMulti;
}
