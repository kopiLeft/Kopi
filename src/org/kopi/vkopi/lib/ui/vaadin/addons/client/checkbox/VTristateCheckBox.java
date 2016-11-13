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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox;

import java.util.ArrayList;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VObjectField;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public class VTristateCheckBox extends VObjectField implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the widget instance.
   */
  public VTristateCheckBox() {
    setStyleName("tristate-checkbox");
    inner = new Inner();
    listeners = new ArrayList<TristateChangeListener>();
    sinkEvents(Event.ONCLICK);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Registers a new tristate change listener. 
   * @param l The listener to be registered.
   */
  public void addTristateChangeListener(TristateChangeListener l) {
    listeners.add(l);
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    
    if (isEnabled()) {
      switch (DOM.eventGetType(event)) {
      case Event.ONCLICK:
	inner.iterateState(true);
	fireTristateChanged(inner.getState());
	break;
      case Event.ONKEYDOWN:
	handleKeyEvents(event.getKeyCode());
	break;
      default:
	// nothing to be by default
      }
    }
  }
  
  /**
   * Handles the key events.
   * @param keycode The pressed key code
   */
  protected void handleKeyEvents(int keycode) {
    switch (keycode) {
    case KeyCodes.KEY_DOWN:
      inner.iterateState(true);
      fireTristateChanged(inner.getState());
      break;
    case KeyCodes.KEY_UP:
      inner.iterateState(false);
      fireTristateChanged(inner.getState());
      break;
    default:
      // nothing to do by default
    }
  }
  
  /**
   * Returns the inner state.
   * @return The inner state.
   */
  public State getState() {
    return inner.getState();
  }
  
  /**
   * Sets the inner state.
   * @param state The state.
   */
  public void setState(String state) {
    inner.setState(State.fromString(state));
  }
  
  /**
   * Fires a tristate change event.
   * @param state The inner state.
   */
  protected void fireTristateChanged(State state) {
    for (TristateChangeListener l : listeners) {
      if (l != null) {
	l.stateChanged(state);
      }
    }
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  private final class Inner extends Widget {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates the widget instance.
     */
    public Inner() {
      setElement(Document.get().createElement("i"));
      state = NULL;
      setStyleName("icon");
    }
    
    /**
     * Iterates the inner state.
     */
    public void iterateState(boolean next) {
      State	old = state;
      
      if (next) {
	state = state.next();
      } else {
	state = state.previous();
      }
      removeFromParent();
      if (state != NULL) {
	VTristateCheckBox.this.setWidget(this);
      }
      removeStyleDependentName(old.getStyleName());
      addStyleDependentName(state.getStyleName());
    }
    
    /**
     * Returns the inner state.
     * @return The inner state.
     */
    public State getState() {
      return state;
    }
    
    /**
     * Sets the inner state.
     * @param state The inner state.
     */
    public void setState(State state) {
      this.state = state;
      removeFromParent();
      if (state != NULL) {
	VTristateCheckBox.this.setWidget(this);
      }
      addStyleDependentName(state.getStyleName());
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private State 			state;
  }
  
  public static abstract class State {
    
    /**
     * Returns the next state to be applied.
     * @return The next state to be applied.
     */
    public abstract State next();
    
    /**
     * Returns the previous state to be applied.
     * @return The previous state to be applied.
     */
    public abstract State previous();
    
    /**
     * Returns the state style name.
     * @return the state style name.
     */
    public abstract String getStyleName();
    
    /**
     * Creates the equivalent state.
     * @param state The string representation.
     * @return The inner state.
     */
    public static State fromString(String state) {
      if ("true".equals(state)) {
	return TRUE;
      } else if ("false".equals(state)) {
	return FALSE;
      } else {
	return NULL;
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean					enabled;
  private final Inner					inner;
  private final ArrayList<TristateChangeListener> 	listeners;
  
  public static final State			TRUE = new State() {
    
    @Override
    public State next() {
      return FALSE;
    }
    
    @Override
    public State previous() {
      return NULL;
    }
    
    @Override
    public String getStyleName() {
      return "true";
    }
  };
  
  public static final State			FALSE = new State() {
    
    @Override
    public State next() {
      return NULL;
    }
    
    @Override
    public State previous() {
      return TRUE;
    }
    
    @Override
    public String getStyleName() {
      return "false";
    }
  };
  
  public static final State			NULL = new State() {
    
    @Override
    public State next() {
      return TRUE;
    }
    
    @Override
    public State previous() {
      return FALSE;
    }
    
    @Override
    public String getStyleName() {
      return "";
    }
  };
}
