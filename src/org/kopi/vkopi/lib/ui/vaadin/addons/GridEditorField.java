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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFieldClickServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFieldClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFieldNavigationServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFieldState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFieldState.NavigationDelegationMode;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;

/**
 * A grid editor field server side implementation.
 */
@SuppressWarnings("serial")
public abstract class GridEditorField<T> extends AbstractField<T> implements Focusable {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  protected GridEditorField() {
    setImmediate(true);
    eventListenerList = new EventListenerList();
    registerRpc(new NavigationRpcHandler());
    registerRpc(new ClickRpcHandler());
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the navigation delegation mode of this editor field.
   * @param navigationDelegationMode The navigation delegation mode.
   */
  public void setNavigationDelegationMode(NavigationDelegationMode navigationDelegationMode) {
    getState().navigationDelegationMode = navigationDelegationMode;
  }
  
  /**
   * Adds the given actors list to the editor fields actors.
   * @param actors The list of field actors.
   */
  public void addActors(Collection<Actor> actors) {
    getState().actors.addAll(actors);
  }
  
  /**
   * Sets this editor field to has a PREFLD trigger.
   * @param hasPreFieldTrigger The PREFDL trigger flag.
   */
  public void setHasPreFieldTrigger(boolean hasPreFieldTrigger) {
    getState().hasPreFieldTrigger = hasPreFieldTrigger;
  }
  
  @Override
  protected EditorFieldState getState() {
    return (EditorFieldState) super.getState();
  }
  
  /**
   * Sets the color properties of this editor field.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  public void setColor(String foreground, String background) {
    getState().foreground = foreground;
    getState().background = background;
  }
  
  /**
   * Sets the field in blink state.
   * @param blink The blink state.
   */
  public void setBlink(boolean blink) {
    getRpcProxy(EditorFieldClientRpc.class).setBlink(blink);
  }
  
  @Override
  public void focus() {
    getRpcProxy(EditorFieldClientRpc.class).focus();
  }
  
  @Override
  public boolean isBuffered() {
    return true;
  }

  /**
   * Registers the given field navigation listener.
   * @param listener The navigation listener.
   */
  public void addNavigationListener(NavigationListener listener) {
    addListener(listener);
  }
  
  /**
   * Removes the given navigation listeners from the event listener list.
   * @param listener The listener to be removed.
   */
  public void removeNavigationListener(NavigationListener listener) {
    removeListener(listener);
  }
  
  /**
   * Fires a click event object.
   */
  protected void fireClickEvent() {
    fireEvent(new ClickEvent(this));
  }
  
  /**
   * Fires an autofill event object.
   */
  protected void fireAutofillEvent() {
    fireEvent(new AutofillEvent(this));
  }
  
  /**
   * Registers a new click listener on this editor.
   * @param listener The click listener object.
   */
  public void addClickListener(ClickListener listener) {
    addListener("handleClick", ClickEvent.class, listener, ClickEvent.CLICK_METHOD);
  }

  /**
   * Removes a click listener from this editor
   * @param listener The listener to remove.
   */
  public void removeClickListener(ClickListener listener) {
    removeListener("handleClick", ClickEvent.class, listener);
  }
  
  /**
   * Registers a autofill listener on this editor.
   * @param listener The autofill listener object.
   */
  public void addAutofillListener(AutofillListener listener) {
    addListener("handleAutofill", AutofillEvent.class, listener, AutofillEvent.AUTOFILL_METHOD);
  }

  /**
   * Removes a autofill from this editor
   * @param listener The listener to remove.
   */
  public void removeAutofillListener(AutofillListener listener) {
    removeListener("handleAutofill", AutofillEvent.class, listener);
  }
  
  /**
   * Adds the given connector listener to this editor field.
   * @param listener The connector event listener.
   */
  protected void addListener(ConnectorEventListener listener) {
    eventListenerList.addListener(listener);
  }
  
  /**
   * Removes the given connector event listener from the event listener list.
   * @param listener The listener to be removed.
   */
  protected void removeListener(ConnectorEventListener listener) {
    eventListenerList.removeListener(listener);
  }
  
  /**
   * Fires a goto next field event.
   */
  protected void fireGotoNextFieldEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoNextField(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto previous field event.
   */
  protected void fireGotoPrevFieldEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoPrevField(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto next block event.
   */
  protected void fireGotoNextBlockEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoNextBlock(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto previous record event.
   */
  protected void fireGotoPrevRecordEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoPrevRecord(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto next record event.
   */
  protected void fireGotoNextRecordEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoNextRecord(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto first record event.
   */
  protected void fireGotoFirstRecordEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoFirstRecord(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto last record event.
   */
  protected void fireGotoLastRecordEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoLastRecord(new NavigationEvent(this));
    }
  }
  
  /**
   * Fires a goto first empty mandatory field event.
   */
  protected void fireGotoNextEmptyMustfillEvent() {
    NavigationListener[]      listeners = eventListenerList.getListeners(NavigationListener.class);
    
    for (NavigationListener l : listeners) {
      l.onGotoNextRecord(new NavigationEvent(this));
    }
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The grid editor field navigation listener
   */
  public interface NavigationListener extends ConnectorEventListener {
    
    /**
     * Fired when a goto next field event is called by the user.
     * @param event The navigation event object
     */
    void onGotoNextField(NavigationEvent event);
    
    /**
     * Fired when a goto previous field event is called by the user.
     * @param event The navigation event object
     */
    void onGotoPrevField(NavigationEvent event);
    
    /**
     * Fired when a goto next block event is called by the user.
     * @param event The navigation event object
     */
    void onGotoNextBlock(NavigationEvent event);
    
    /**
     * Fired when a goto previous record event is called by the user.
     * @param event The navigation event object
     */
    void onGotoPrevRecord(NavigationEvent event);
    
    /**
     * Fired when a goto next field event is called by the user.
     * @param event The navigation event object
     */
    void onGotoNextRecord(NavigationEvent event);
    
    /**
     * Fired when a goto first record event is called by the user.
     * @param event The navigation event object
     */
    void onGotoFirstRecord(NavigationEvent event);
    
    /**
     * Fired when a goto last record event is called by the user.
     * @param event The navigation event object
     */
    void onGotoLastRecord(NavigationEvent event);
    
    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     * @param event The navigation event object
     */
    void onGotoNextEmptyMustfill(NavigationEvent event);
  }
  
  /**
   * The click listener for grid editor fields
   */
  public interface ClickListener extends ConnectorEventListener {
    
    /**
     * Fired when a click event is detected on editor field.
     * @param event The click event object.
     */
    void onClick(ClickEvent event);
  }
  
  public interface AutofillListener  extends ConnectorEventListener {
    
    /**
     * Fired when an autofill action is launched on the editor
     * @param event The autofill event.
     */
    void onAutofill(AutofillEvent event);
  }
  
  /**
   * The editor field navigation event 
   */
  public static class NavigationEvent extends Event {
    
    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public NavigationEvent(Component source) {
      super(source);
    }
  }
  
  /**
   * The editor field click event
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
  
  /**
   * The editor field click event
   */
  public static class AutofillEvent extends Event {
    
    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public AutofillEvent(Component source) {
      super(source);
    }
    
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    public static final Method          AUTOFILL_METHOD;
    
    static {
      try {
        // Set the header click method
        AUTOFILL_METHOD = AutofillListener.class.getDeclaredMethod("onAutofill", AutofillEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }
  
  /**
   * The event listener list for the editor field that has the ability to
   * handle listeners with multiple invocation inside.
   */
  private static class EventListenerList implements Serializable {
    
    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public EventListenerList() {
      listenerList = new LinkedHashSet<ConnectorEventListener>();
    }
    
    //---------------------------------------------------
    // IMPLEMENTATION
    //---------------------------------------------------
    
    /**
     * Registers a new connector event listener.
     * @param listener The listener to be registered.
     */
    public void addListener(ConnectorEventListener listener) {
      listenerList.add(listener);
    }
    
    /**
     * Removes a connector event listener.
     * @param listener The listener to be removed.
     */
    public void removeListener(ConnectorEventListener listener) {
      listenerList.remove(listener);
    }
    
    /**
     * Returns the listeners contained in this event router and that has the given type.
     * @param type The listener type.
     * @return The listener array.
     */
    @SuppressWarnings("unchecked")
    public <T extends ConnectorEventListener> T[] getListeners(Class<T> type) {
      Object[]  lList = listenerList.toArray();
      int       n = getListenerCount(lList, type);
      T[]       result = (T[])Array.newInstance(type, n);
      int       j = 0;
      
      for (int i = 0; i < lList.length; i++) {
        if (type.isAssignableFrom(lList[i].getClass())) {
          result[j++] = (T)lList[i];
        }
      }
      
      return result;
    }

    private <T extends ConnectorEventListener> int getListenerCount(Object[] list, Class<T> type) {
      int count = 0;
      
      for (int i = 0; i < list.length; i++) {
        if (type.isAssignableFrom(list[i].getClass())) {
          count++;
        }
      }
      
      return count;
    }
    
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    /**
     * List of registered listeners.
     */
    private LinkedHashSet<ConnectorEventListener>       listenerList;
  }
  
  /**
   * The editor field server RPC handler
   */
  private class NavigationRpcHandler implements EditorFieldNavigationServerRpc {

    @Override
    public void gotoNextField() {
      fireGotoNextFieldEvent();
    }

    @Override
    public void gotoPrevField() {
      fireGotoPrevFieldEvent();
    }

    @Override
    public void gotoNextBlock() {
      fireGotoNextBlockEvent();
    }

    @Override
    public void gotoPrevRecord() {
      fireGotoPrevRecordEvent();
    }

    @Override
    public void gotoNextRecord() {
      fireGotoNextRecordEvent();
    }

    @Override
    public void gotoFirstRecord() {
      fireGotoFirstRecordEvent();
    }

    @Override
    public void gotoLastRecord() {
      fireGotoLastRecordEvent();
    }

    @Override
    public void gotoNextEmptyMustfill() {
      fireGotoNextEmptyMustfillEvent();
    }
  }
  
  private class ClickRpcHandler implements EditorFieldClickServerRpc {

    @Override
    public void clicked() {
      fireClickEvent();
    }
    
    @Override
    public void autofill() {
      fireAutofillEvent();
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final EventListenerList                     eventListenerList;
}
