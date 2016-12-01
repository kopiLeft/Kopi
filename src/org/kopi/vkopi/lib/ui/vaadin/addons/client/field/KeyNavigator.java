/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import com.google.gwt.user.client.Timer;

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
                      int[] modifiers)
  {
    super("key-navigator" + code, key, modifiers);
    this.code = code;
    this.connector = connector;
    this.field = field;
    this.lazyActionExecutor = new Timer() {
      
      @Override
      public void run() {
        internalPerformAction();
      }
    };
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void performAction() {
    /*
     * The field change state is detected on a key up event because of
     * a validation strategies. It means that a typed character cannot
     * be accepted by the field and in this case we cannot consider that
     * the field has been changed. That's why the field change state is set
     * on the key up event.
     * The navigation actions are fired on a key down. So if we type fast
     * in the field only one character and the directly make a navigation
     * action, the field change state will not be seen at this stage and
     * dirty values will not be communicated to the server side.
     * Here we specially treat this case and delay the navigation action to
     * finally see the field change state. 
     */
    if (field.getText() != null && field.getText().length() == 1) {
      lazyActionExecutor.schedule(80); //!!! experimental
    } else {
      internalPerformAction();
    }
  }
  
  /**
   * Checks if a suggestions query has been performed for a field.
   */
  protected void maybeCancelSuggestionsQuery() {
    /*
     * If we are waiting for a suggestion list to be shown
     * the navigation action will be executed and the suggestions
     * list well be cancelled.
     */
    if (field.isAboutShowingSuggestions()) {
      field.cancelSuggestions();
    }
  }
  
  /**
   * Checks if the dirty values should be sent before performing
   * the accelerator action.
   */
  protected void maybeSendDirtyValues() {
    /*
     * When the navigation is delegated to the server side,
     * all pending values must be sent to server side to be sure
     * that the client state and the server state are synchronized
     * before executing any server trigger.
     */
    if (field.getFieldConnector().delegateNavigationToServer()) {
      field.sendDirtyValuesToServerSide();
    }
  }
  
  /**
   * Internally performs the navigation action.
   */
  protected void internalPerformAction() {
    /*
     * If a suggestions list is already shown
     * we let the user choose an entry before.
     */
    if (field.isShowingSuggestions()) {
      return;
    }
    // check if suggestions should be cancelled.
    maybeCancelSuggestionsQuery();
    // check if dirty values should be communicated
    // to server side.
    maybeSendDirtyValues();
    // perform the navigation action.
    doNavigatorAction();
  }
  
  /**
   * Executes the navigation action according to its code.
   */
  protected void doNavigatorAction() {
    switch (code) {
    case KEY_NEXT_FIELD:
      field.getFieldConnector().getColumnView().gotoNextField();
      break;
    case KEY_PREV_FIELD:
      field.getFieldConnector().getColumnView().gotoPrevField();
      break;
    case KEY_NEXT_BLOCK:
      connector.getServerRpc().gotoNextBlock();
      break;
    case KEY_REC_UP:
      field.getFieldConnector().getColumnView().gotoPrevRecord();
      break;
    case KEY_REC_DOWN:
      field.getFieldConnector().getColumnView().gotoNextRecord();
      break;
    case KEY_REC_FIRST:
      field.getFieldConnector().getColumnView().gotoFirstRecord();
      break;
    case KEY_REC_LAST:
      field.getFieldConnector().getColumnView().gotoLastRecord();
      break;
    case KEY_EMPTY_FIELD:
      field.getFieldConnector().getColumnView().gotoNextEmptyMustfill();
      break;
    case KEY_DIAMETER:
      StringBuffer      text;
      
      text = new StringBuffer(field.getText());
      text.insert(field.getCursorPos(), "\u00D8");
      field.setText(text.toString());
      break;
    case KEY_ESCAPE:
      //!!! removed and let the actor quit form operate.
      //!! we do not really need it.
      //connector.getServerRpc().closeWindow();
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

  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final int			code;
  private final TextFieldConnector	connector;
  private final VInputTextField 	field;
  private final Timer                   lazyActionExecutor;
  
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
