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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.BooleanFieldClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.BooleanFieldServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.BooleanFieldState;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.Component;

/**
 * Server side implementation of the boolean field 
 */
@SuppressWarnings("serial")
public class BooleanField extends ObjectField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new boolean field instance.
   * @param trueRepresentation The representation of the true value.
   * @param falseRepresentation The representation of the false false
   */
  public BooleanField(String trueRepresentation, String falseRepresentation) {
    setImmediate(true);
    getState().trueRepresentation = trueRepresentation;
    getState().falseRepresentation = falseRepresentation;
    registerRpc(new BooleanFieldServerRpc() {
      
      @Override
      public void valueChanged(Boolean value) {
        setValue(value);
        fireValueChangeEvent(value);
      }
    });
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  protected BooleanFieldState getState() {
    return (BooleanFieldState) super.getState();
  }
  
  /**
   * Sets the field value
   * @param value The field value
   */
  public void setValue(Boolean value) {
    getState().value = value;
    internalValue = value;
  }
  
  /**
   * Returns the field value.
   * @return The field value.
   */
  public Boolean getValue() {
    return internalValue;
  }
  
  @Override
  public void beforeClientResponse(boolean initial) {
    super.beforeClientResponse(initial);
    getState().value = getValue();
  }
  
  /**
   * Sets the field label.
   * @param label The field label.
   */
  public void setLabel(String label) {
    getState().label = label;
  }
  
  public void setMandatory(boolean mandatory) {
    getState().mandatory = mandatory;
  }
  
  /**
   * Sets the field focus.
   * @param focus The field focus
   */
  public void setFocus(boolean focus) {
    getRpcProxy(BooleanFieldClientRpc.class).setFocus(focus);
  }
  
  /**
   * Sets the field in blink state.
   * @param blink The blink state.
   */
  public void setBlink(boolean blink) {
    getRpcProxy(BooleanFieldClientRpc.class).setBlink(blink);
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
    fireEvent(new ValueChangeEvent(this, value));
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
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Boolean                       internalValue;
}
