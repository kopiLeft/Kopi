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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogCloseServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogSearchServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogSelectionServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogState.SelectionTarget;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

/**
 * A list dialog based on the VAADIN grid component
 */
@SuppressWarnings("serial")
public class GridListDialog extends AbstractSingleComponentContainer {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public GridListDialog() {
    registerRpc(new GridListDialogCloseServerRpc() {

      @Override
      public void closed(boolean escaped, boolean newForm) {
        fireCloseEvent(escaped, newForm);
      }
    });
    
    registerRpc(new GridListDialogSelectionServerRpc() {
      
      @Override
      public void select(SelectionTarget target) {
        fireSelectionEvent(target);
      }
    });
    
    registerRpc(new GridListDialogSearchServerRpc() {
      
      @Override
      public void search(String pattern) {
        fireSearchEvent(pattern);
      }
    });
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Sets the grid component for this list.
   * @param table The grid component.
   */
  public void setTable(Grid table) {
    setContent(table);
  }
  
  /**
   * Show the list dialog relative to the given component.
   * @param reference The reference component.
   */
  public void showRelativeTo(Component reference) {
    getState().reference = reference;
  }
  
  /**
   * Sets the new button text.
   * @param newText The new text.
   */
  public void setNewText(String newText) {
    getState().newText = newText;
  }
  
  @Override
  protected GridListDialogState getState() {
    return (GridListDialogState) super.getState();
  }

  //---------------------------------------------------
  // LISTENERS
  //---------------------------------------------------
  
  /**
   * Fires a close event.
   * @param escaped Is the popup escaped ?
   * @param newForm Is it a new form ?
   */
  protected void fireCloseEvent(boolean escaped, boolean newForm) {
    fireEvent(new CloseEvent(this, escaped, newForm));
  }
  
  /**
   * Fires a selection event.
   * @param target The selection target.
   */
  protected void fireSelectionEvent(SelectionTarget target) {
    fireEvent(new SelectionEvent(this, target));
  }
  
  /**
   * Fires a selection event.
   * @param pattern The search pattern.
   */
  protected void fireSearchEvent(String pattern) {
    fireEvent(new SearchEvent(this, pattern));
  }
  
  /**
   * Registers a close listener on this list.
   * @param listener The text change listener object.
   */
  public void addCloseListener(CloseListener listener) {
    addListener("handleClose", CloseEvent.class, listener, CloseEvent.CLOSE_METHOD);
  }

  /**
   * Removes a close listener
   * @param listener The listener to remove.
   */
  public void removeCloseListener(CloseListener listener) {
    removeListener("handleClose", CloseEvent.class, listener);
  }
  
  /**
   * Registers a new selection listener on this list.
   * @param listener The selection listener object.
   */
  public void addSelectionListener(SelectionListener listener) {
    addListener("handleSelection", SelectionEvent.class, listener, SelectionEvent.SELECT_METHOD);
  }

  /**
   * Removes a selection listener
   * @param listener The listener to remove.
   */
  public void removeSelectionListener(SelectionListener listener) {
    removeListener("handleSelection", SelectionEvent.class, listener);
  }
  
  /**
   * Registers a new search listener on this list.
   * @param listener The search listener object.
   */
  public void addSearchListener(SearchListener listener) {
    addListener("handleSearch", SearchEvent.class, listener, SearchEvent.SEARCH_METHOD);
  }

  /**
   * Removes a search listener
   * @param listener The search to remove.
   */
  public void removeSearchListener(SearchListener listener) {
    removeListener("handleSearch", SearchEvent.class, listener);
  }
  
  /**
   * The close listener for list dialogs
   */
  public interface CloseListener extends ConnectorEventListener {
    
    /**
     * Notifies the registered objects that the dialog was closed by client side.
     * @param event The close event object.
     */
    void onClose(CloseEvent event);
  }
  
  /**
   * The selection listener for the grid based list dialogs
   */
  public interface SelectionListener extends ConnectorEventListener {
    
    /**
     * Notifies the registered objects that a selection event is fired.
     * @param event The selection event.
     */
    void onSelection(SelectionEvent event);
  }
  
  /**
   * The search listener for the grid based list dialogs
   */
  public interface SearchListener extends ConnectorEventListener {
    
    /**
     * Notifies the registered objects that a search event is fired.
     * @param event The search event.
     */
    void onSearch(SearchEvent event);
  }
  
  /**
   * The list dialog close event.
   */
  public static class CloseEvent extends Event {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public CloseEvent(Component source, boolean escaped, boolean newForm) {
      super(source);
      this.escaped = escaped;
      this.newForm = newForm;
    }

    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------
    
    /**
     * @return the escaped
     */
    public final boolean isEscaped() {
      return escaped;
    }
    /**
     * @return the newForm
     */
    public final boolean isNewForm() {
      return newForm;
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------

    private final boolean               escaped;
    private final boolean               newForm;
    public static final Method          CLOSE_METHOD;
    
    static {
      try {
        // Set the header click method
        CLOSE_METHOD = CloseListener.class.getDeclaredMethod("onClose", CloseEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }
  
  /**
   * The list dialog selection event.
   */
  public static class SelectionEvent extends Event {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public SelectionEvent(Component source, SelectionTarget target) {
      super(source);
      this.target = target;
    }

    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------
    
    /**
     * Returns the selection target.
     * @return The selection target.
     */
    public SelectionTarget getTarget() {
      return target;
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------

    private final SelectionTarget       target;
    public static final Method          SELECT_METHOD;
    
    static {
      try {
        // Set the header click method
        SELECT_METHOD = SelectionListener.class.getDeclaredMethod("onSelection", SelectionEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }
  
  /**
   * The list dialog search event.
   */
  public static class SearchEvent extends Event {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public SearchEvent(Component source, String pattern) {
      super(source);
      this.pattern = pattern;
    }

    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------
    
    /**
     * Returns the search pattern.
     * @return The search pattern.
     */
    public String getPattern() {
      return pattern;
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------

    private final String                pattern;
    public static final Method          SEARCH_METHOD;
    
    static {
      try {
        // Set the header click method
        SEARCH_METHOD = SearchListener.class.getDeclaredMethod("onSearch", SearchEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }
}
