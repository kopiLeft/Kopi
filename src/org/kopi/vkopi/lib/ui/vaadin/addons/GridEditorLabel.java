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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorLabelClickServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorLabelState;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * A label used at grid headers
 */
@SuppressWarnings("serial")
public class GridEditorLabel extends AbstractComponent {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public GridEditorLabel(String caption) {
    setCaption(caption);
    setImmediate(true);
    registerRpc(new EditorLabelClickServerRpc() {
      
      @Override
      public void clicked() {
        fireClickEvent();
      }
    });
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  protected EditorLabelState getState() {
    return (EditorLabelState) super.getState();
  }
  
  
  /**
   * Sets the field action trigger ability. 
   * @param hasAction the field action trigger ability. 
   */
  public void setHasAction(boolean hasAction) {
    getState().hasAction = hasAction;
  }
  
  /**
   * Sets the label info text.
   * @param infoText The info text.
   */
  public void setInfoText(String infoText) {
    getState().infoText = infoText;
  }
  
  /**
   * Registers a new click listener on this editor label.
   * @param listener The click listener object.
   */
  public void addClickListener(ClickListener listener) {
    addListener("handleClick", ClickEvent.class, listener, ClickEvent.CLICK_METHOD);
  }

  /**
   * Removes a click listener from this editor label.
   * @param listener The listener to remove.
   */
  public void removeClickListener(ClickListener listener) {
    removeListener("handleClick", ClickEvent.class, listener);
  }
  
  /**
   * Fires a click event on this editor label
   */
  protected void fireClickEvent() {
    fireEvent(new ClickEvent(this));
  }
  
  //---------------------------------------------------
  // LISTENERS & EVENTS
  //---------------------------------------------------
  
  /**
   * Click listener on editor labels
   */
  public interface ClickListener extends ConnectorEventListener {
    
    /**
     * Notifies registered objects that a click event is fired on the editor label.  
     * @param event The click event object.
     */
    void onClick(ClickEvent event);
  }
  
  
  /**
   * The editor label click event
   */
  public static class ClickEvent extends Event {
    
    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public ClickEvent(Component source) {
      super(source);
    }
    
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    public static final Method          CLICK_METHOD;
    
    static {
      try {
        // Set the header click method
        CLICK_METHOD = ClickListener.class.getDeclaredMethod("onClick", ClickEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }
}
