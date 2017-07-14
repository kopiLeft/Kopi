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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutAction;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutActionHandler;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.SimpleFocusablePanel;

/**
 * An editor field that can handle navigation since it has a focus ability 
 */
public abstract class VFocusableEditorField<T> extends SimpleFocusablePanel implements EditorField<T>, FocusHandler, BlurHandler {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  protected VFocusableEditorField() {
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    addKeyDownHandler(new NavigationHandler());
    addFocusHandler(this);
    addBlurHandler(this);
    sinkEvents(Event.ONKEYDOWN);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Performs a widget initialization.
   * @param connection The application connection.
   */
  protected void init(ApplicationConnection connection) {
    this.connection = connection;
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      if (enabled) {
        removeStyleName("v-disabled");
      } else {
        addStyleName("v-disabled");
      }
    }
  }

  @Override
  public ApplicationConnection getConnection() {
    return connection;
  }
  
  @Override
  public void onFocus(FocusEvent event) {}
  
  @Override
  public void onBlur(BlurEvent event) {}

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  
  /**
   * Returns the editor connector attached to this widget.
   * @return The editor connector attached to this widget.
   */
  protected abstract EditorFieldConnector getConnector();

  //---------------------------------------------------
  // NAVIGATION ACTION
  //---------------------------------------------------
  
  /**
   * A navigation action
   */
  protected class NavigationAction extends ShortcutAction {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public NavigationAction(int code, int key, int[] modifiers) {
      super("navigation-" + code, key, modifiers);
      this.code = code;
    }

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    
    @Override
    public void performAction() {
      // first sends the text value to server side
      getConnector().sendValueToServer();
      switch (code) {
      case KEY_TAB:
        getConnector().gotoNextField();
        break;
      case KEY_STAB:
        getConnector().gotoPrevField();
        break;
      case KEY_REC_UP:
        getConnector().gotoPrevRecord();
        break;
      case KEY_REC_DOWN:
        getConnector().gotoNextRecord();
        break;
      case KEY_REC_FIRST:
        getConnector().gotoFirstRecord();
        break;
      case KEY_REC_LAST:
        getConnector().gotoLastRecord();
        break;
      case KEY_BLOCK:
        getConnector().gotoNextBlock();
        break;
      default:
        // nothing to do
        break;
      }
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    private final int                   code;
  }
  
  /**
   * The object field navigation handler.
   */
  private class NavigationHandler extends ShortcutActionHandler {
    
    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    /**
     * Creates the shortcuts.
     */
    public NavigationHandler() {
      createNavigationActions();
    }
    
    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    
    /**
     * Creates the navigation actions.
     * @param fieldConnector The text field getConnector().
     */
    protected void createNavigationActions() {
      addKeyNavigationAction(KEY_TAB, KeyCodes.KEY_ENTER, 0);
      addKeyNavigationAction(KEY_TAB, KeyCodes.KEY_TAB, 0);
      addKeyNavigationAction(KEY_STAB, KeyCodes.KEY_TAB, KeyCodes.KEY_SHIFT);
      addKeyNavigationAction(KEY_BLOCK, KeyCodes.KEY_ENTER, KeyCodes.KEY_SHIFT);
      addKeyNavigationAction(KEY_REC_UP, KeyCodes.KEY_PAGEUP, 0);
      addKeyNavigationAction(KEY_REC_DOWN, KeyCodes.KEY_PAGEUP, 0);
      addKeyNavigationAction(KEY_REC_FIRST, KeyCodes.KEY_HOME, 0);
      addKeyNavigationAction(KEY_REC_LAST, KeyCodes.KEY_END, 0);
      addKeyNavigationAction(KEY_STAB, KeyCodes.KEY_LEFT, KeyCodes.KEY_CTRL);
      addKeyNavigationAction(KEY_TAB, KeyCodes.KEY_RIGHT, KeyCodes.KEY_CTRL);
      addKeyNavigationAction(KEY_REC_UP, KeyCodes.KEY_UP, KeyCodes.KEY_CTRL);
      addKeyNavigationAction(KEY_REC_DOWN, KeyCodes.KEY_DOWN, KeyCodes.KEY_CTRL);
    }
    
    /**
     * Adds a key navigator action to this handler.
     * @param fieldConnector The object field connector
     * @param code The navigator code.
     * @param key The key code.
     * @param modifiers The modifiers.
     */
    protected void addKeyNavigationAction(int code, int key, int... modifiers) {
      addAction(new NavigationAction(code, key, modifiers));
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean                       enabled;
  private ApplicationConnection         connection;
  
  
  // navigation constants.
  private static final int              KEY_TAB         = 0;
  private static final int              KEY_STAB        = 1;
  private static final int              KEY_REC_UP      = 2;
  private static final int              KEY_REC_DOWN    = 3;
  private static final int              KEY_REC_FIRST   = 4;
  private static final int              KEY_REC_LAST    = 5;
  private static final int              KEY_BLOCK       = 6;
}
