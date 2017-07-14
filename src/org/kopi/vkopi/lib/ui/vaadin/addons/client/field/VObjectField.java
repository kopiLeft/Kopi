/*
 * Copyright (c) 2013-2017 kopiLeft Development Services
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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutAction;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutActionHandler;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.SimpleFocusablePanel;

/**
 * An object field widget used to represent objects as a kopi field.
 * An object field can be an image field.
 */
public abstract class VObjectField extends SimpleFocusablePanel implements HasEnabled, FocusHandler, BlurHandler {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VObjectField</code> instance.
   */
  protected VObjectField() {
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    addKeyDownHandler(new NavigationHandler());
    addFocusHandler(this);
    addBlurHandler(this);
    sinkEvents(Event.ONKEYDOWN);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (getWidget() instanceof HasEnabled) {
      ((HasEnabled)getWidget()).setEnabled(enabled);
    }
    if (!enabled) {
      addStyleName("v-disabled");
    } else {
      removeStyleName("v-disabled");
    }
  }
  
  @Override
  public void onFocus(FocusEvent event) {
    getFieldConnector().getColumnView().setAsActiveField();;
  }
  
  @Override
  public void onBlur(BlurEvent event) {
    getFieldConnector().getColumnView().unsetAsActiveField();
  }
  
  @Override
  public void clear() {
    super.clear();
    client = null;
  }
  
  /**
   * Returns the field getConnector().
   * @return The field getConnector().
   */
  protected ObjectFieldConnector getConnector() {
    return ConnectorUtils.getConnector(client, this, ObjectFieldConnector.class);
  }
  
  /**
   * Returns the parent field connector.
   * @return The parent field connector.
   */
  protected FieldConnector getFieldConnector() {
    return (FieldConnector) getConnector().getParent();
  }
  
  /**
   * Returns {@code true} if this object field is {@code null}.
   * @return {@code true} if this object field is {@code null}.
   */
  protected abstract boolean isNull();
  
  /**
   * Sets the object field value.
   * @param o The new field value.
   */
  protected abstract void setValue(Object o);
  
  /**
   * Sets the object field color properties.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  protected abstract void setColor(String foreground, String background);
  
  /**
   * Returns the object field value.
   * @return The object field value.
   */
  protected abstract Object getValue();
  
  /**
   * Checks the internal value of this field.
   * @param rec The active record.
   */
  protected abstract void checkValue(int rec);
  
  /**
   * Sets the component visibility from the parent field.
   * @param visible The visibility state
   */
  protected abstract void setParentVisibility(boolean visible);
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The object field key navigator.
   */
  private class KeyNavigator extends ShortcutAction {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    public KeyNavigator(int code, int key, int[] modifiers) {
      super("key-navigator " + code, key, modifiers);
      this.code = code;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public void performAction() {
      if (getFieldConnector() == null) {
        return;
      }
      
      getConnector().sendValueToServer();
      switch (code) {
      case KEY_TAB:
        getFieldConnector().getColumnView().gotoNextField();
	break;
      case KEY_STAB:
        getFieldConnector().getColumnView().gotoPrevField();
	break;
      case KEY_REC_UP:
        getFieldConnector().getColumnView().gotoPrevRecord();
	break;
      case KEY_REC_DOWN:
        getFieldConnector().getColumnView().gotoNextRecord();
	break;
      case KEY_REC_FIRST:
        getFieldConnector().getColumnView().gotoFirstRecord();
	break;
      case KEY_REC_LAST:
        getFieldConnector().getColumnView().gotoLastRecord();
	break;
      case KEY_BLOCK:
	getConnector().getServerRpc().gotoNextBlock();
	break;
      default:
	// nothing to do
	break;
      }
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final int			code;
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
      createNavigatorKeys();
    }
    
    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    
    /**
     * Creates the navigation actions.
     * @param fieldConnector The text field getConnector().
     */
    protected void createNavigatorKeys() {
      addKeyNavigator(KEY_TAB, KeyCodes.KEY_ENTER, 0);
      addKeyNavigator(KEY_TAB, KeyCodes.KEY_TAB, 0);
      addKeyNavigator(KEY_STAB, KeyCodes.KEY_TAB, KeyCodes.KEY_SHIFT);
      addKeyNavigator(KEY_BLOCK, KeyCodes.KEY_ENTER, KeyCodes.KEY_SHIFT);
      addKeyNavigator(KEY_REC_UP, KeyCodes.KEY_PAGEUP, 0);
      addKeyNavigator(KEY_REC_DOWN, KeyCodes.KEY_PAGEDOWN, 0);
      addKeyNavigator(KEY_REC_FIRST, KeyCodes.KEY_HOME, 0);
      addKeyNavigator(KEY_REC_LAST, KeyCodes.KEY_END, 0);
      addKeyNavigator(KEY_STAB, KeyCodes.KEY_LEFT, KeyCodes.KEY_CTRL);
      addKeyNavigator(KEY_TAB, KeyCodes.KEY_RIGHT, KeyCodes.KEY_CTRL);
      addKeyNavigator(KEY_REC_UP, KeyCodes.KEY_UP, KeyCodes.KEY_CTRL);
      addKeyNavigator(KEY_REC_DOWN, KeyCodes.KEY_DOWN, KeyCodes.KEY_CTRL);
    }
    
    /**
     * Adds a key navigator action to this handler.
     * @param fieldConnector The object field connector
     * @param code The navigator code.
     * @param key The key code.
     * @param modifiers The modifiers.
     */
    protected void addKeyNavigator(int code, int key, int... modifiers) {
      addAction(new KeyNavigator(code, key, modifiers));
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean			enabled;
  /*package*/ ApplicationConnection	client;
  private static final int		KEY_TAB		= 0;
  private static final int		KEY_STAB	= 1;
  private static final int		KEY_REC_UP	= 2;
  private static final int		KEY_REC_DOWN	= 3;
  private static final int		KEY_REC_FIRST	= 4;
  private static final int		KEY_REC_LAST	= 5;
  private static final int		KEY_BLOCK	= 6;
}
