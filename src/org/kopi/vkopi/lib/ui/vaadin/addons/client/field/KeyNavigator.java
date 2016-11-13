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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutAction;

/**
 * A text field input key navigator action.
 */
public class KeyNavigator extends ShortcutAction {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new key navigator action.
   * @param connector The text input connector (for action fire)
   * @param code The action code
   * @param key The key code.
   * @param modifiers The modifiers to be used
   */
  public KeyNavigator(TextFieldConnector connector,
      		      VInputTextField field,
      		      int code,
                      int key,
                      int[] modifiers) {
    super("key-navigator" + code, key, modifiers);
    this.code = code;
    this.connector = connector;
    this.field = field;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void performAction() {
    /*
     * If we are waiting for a suggestion list to be shown
     * the navigation action will be executed and the suggestions
     * list well be cancelled.
     */
    if (field.isAboutShowingSuggestions()) {
      field.cancelSuggestions();
    }
    /*
     * If a suggestions list is already shown
     * we let the user choose an entry before.
     */
    if (field.isShowingSuggestions()) {
      return;
    }
    // The client text must be send to server before
    // leaving the focused field since value change is
    // fired on blur event.
    fireTextChangeEvent();
    switch (code) {
    case KEY_NEXT_FIELD:
      connector.getServerRpc().gotoNextField();
      break;
    case KEY_PREV_FIELD:
      connector.getServerRpc().gotoPrevField();
      break;
    case KEY_NEXT_BLOCK:
      connector.getServerRpc().gotoNextBlock();
      break;
    case KEY_REC_UP:
      connector.getServerRpc().gotoPrevRecord();
      break;
    case KEY_REC_DOWN:
      connector.getServerRpc().gotoNextRecord();
      break;
    case KEY_REC_FIRST:
      connector.getServerRpc().gotoFirstRecord();
      break;
    case KEY_REC_LAST:
      connector.getServerRpc().gotoLastRecord();
      break;
    case KEY_EMPTY_FIELD:
      connector.getServerRpc().gotoNextEmptyMustfill();
      break;
    case KEY_DIAMETER:
      // FIXME. Is it important to do ? not so sure.
      break;
    case KEY_ESCAPE:
      connector.getServerRpc().closeWindow();
      break;
    case KEY_PRINTFORM:
      connector.getServerRpc().printForm();
      break;
    case KEY_PREV_VAL:
      connector.getServerRpc().previousEntry();
      break;
    case KEY_NEXT_VAL:
      connector.getServerRpc().nextEntry();
      break;
    default:
      // nothing to do by default.
    }
  }
  
  /**
   * Fire a text change event.
   * This is important for actions that causes
   * the change of the active field in the model
   * to detect if value has changed.
   */
  protected void fireTextChangeEvent() {
    field.communicateTextValueToServer();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final int			code;
  private final TextFieldConnector	connector;
  private final VInputTextField 	field;
  
  // navigation constants.
  public static final int	KEY_NEXT_FIELD          =  0;
  public static final int	KEY_PREV_FIELD		=  1;
  public static final int	KEY_REC_UP		=  2;
  public static final int	KEY_REC_DOWN		=  3;
  public static final int	KEY_REC_FIRST		=  4;
  public static final int	KEY_REC_LAST		=  5;
  public static final int	KEY_EMPTY_FIELD         =  6;
  public static final int	KEY_NEXT_BLOCK		=  7;
  public static final int	KEY_PREV_VAL		=  8;
  public static final int	KEY_NEXT_VAL		=  9;
  public static final int	KEY_DIAMETER		= 10;
  public static final int	KEY_ESCAPE		= 11;
  public static final int	KEY_PRINTFORM		= 12;
}
