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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.lang.reflect.Method;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.BooleanRendererState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.BooleanRendererValueChangeServerRpc;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.renderers.ClickableRenderer;

/**
 * A boolean field renderer that uses the boolean editor field widget to display. 
 */
@SuppressWarnings("serial")
public class BooleanRenderer extends ClickableRenderer<Boolean> {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public BooleanRenderer(String trueRepresentation, String falseRepresentation) {
    super(Boolean.class);
    getState().trueRepresentation = trueRepresentation;
    getState().falseRepresentation = falseRepresentation;
    registerRpc(new BooleanRendererValueChangeServerRpc() {
      
      @Override
      public void valueChanged(Boolean value) {
        fireValueChangeEvent(value);
      }
    });
  }

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  protected BooleanRendererState getState() {
    return (BooleanRendererState) super.getState();
  }
  
  /**
   * Registers a new value change listener on this editor label.
   * @param listener The value change listener object.
   */
  public void addValueChangeListener(ValueChangeListener listener) {
    addListener("handleValueChange", ValueChangeEvent.class, listener, ValueChangeEvent.VALUE_CHANGE);
  }

  /**
   * Removes a value change listener from this editor label.
   * @param listener The listener to remove.
   */
  public void removeValueChangeListener(ValueChangeListener listener) {
    removeListener("handleValueChange", ValueChangeEvent.class, listener);
  }
  
  /**
   * Fires a new value change event for this field.
   * @param value The new field value
   */
  protected void fireValueChangeEvent(Boolean value) {
    fireEvent(new ValueChangeEvent(getParentGrid(), value));
  }

  //---------------------------------------------------
  // VALUE CHANGE
  //---------------------------------------------------
  
  /**
   * A value change listener notifies registered objects of changes
   * in the boolean fields value. 
   */
  public interface ValueChangeListener extends ConnectorEventListener {
    
    /**
     * Fired when a value change is detected.
     * @param event The value change event
     */
    void valueChange(ValueChangeEvent event);
  }
  
  /**
   * The value change event attached with the boolean field.
   */
  public static class ValueChangeEvent extends Event {
    
    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    /**
     * Creates a new value change event instance.
     * @param source The source component.
     * @param value The field new value.
     */
    public ValueChangeEvent(Component source, Boolean value) {
      super(source);
      this.value = value;
    }
    
    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------
    
    /**
     * Returns the event value.
     * @return The event value.
     */
    public Boolean getValue() {
      return value;
    }
    
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    private final Boolean               value;
    public static final Method          VALUE_CHANGE;
    
    static {
      try {
        // Set the header click method
        VALUE_CHANGE = ValueChangeListener.class.getDeclaredMethod("valueChange", ValueChangeEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }
}
