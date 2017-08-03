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
import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorSuggestionsQueryClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorSuggestionsQueryServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorTextChangeServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorTextFieldState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorTextFieldState.ConvertType;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.Component;

/**
 * A text field used as editor
 */
@SuppressWarnings("serial")
public class GridEditorTextField extends GridEditorField<String> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public GridEditorTextField(int width) {
    getState().col = width;
    registerRpc(new EditorTextChangeServerRpc() {
      
      @Override
      public void textChanged(String newText) {
        String          oldText;

        oldText = getValue();
        // Only do the setting if the string representation of the value
        // has been updated
        // server side check for max length
        if (getMaxLength() != -1 && newText.length() > getMaxLength()) {
          newText = newText.substring(0, getMaxLength());
        }
     
        if (newText != null && oldText == null && newText.equals("")) {
          newText = null;
        }
        if (newText != oldText && (newText == null || !newText.equals(oldText))) {
          boolean     wasModified = isModified();

          setValue(newText);
          displayedValue = newText;
          fireTextChangeEvent(newText, oldText);
          // If the modified status changes, or if we have a
          // formatter, repaint is needed after all.
          if (wasModified != isModified()) {
            markAsDirty();
          }
        }
      }
    });
    // suggestions
    registerRpc(new EditorSuggestionsQueryServerRpc() {
      
      @Override
      public void querySuggestions(String query) {
        fireSuggestionsQueryEvent(query);
      }
    });
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected EditorTextFieldState getState() {
    return (EditorTextFieldState) super.getState();
  }
  
  @Override
  protected EditorTextFieldState getState(boolean markAsDirty) {
    return (EditorTextFieldState) super.getState(markAsDirty);
  }
  
  @Override
  public void beforeClientResponse(boolean initial) {
    super.beforeClientResponse(initial);
    if (getValue() == null) {
      getState().text = "";
    } else {
      getState().text = getValue();
    }
  }
  
  @Override
  public boolean isValidationVisible() {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() || getValue().length() == 0;
  }
  
  @Override
  public Class<? extends String> getType() {
    return String.class;
  }
  
  @Override
  public void clear() {
    setValue("");
  }
  
  @Override
  public void setValue(String newFieldValue) throws ReadOnlyException, ConversionException {
    internalValue = newFieldValue;
    super.setValue(newFieldValue, false, true);
    setInternalValue(newFieldValue);
  }
  
  @Override
  public String getValue() {
    return internalValue;
  }
  
  /**
   * Returns the displayed value of the text editor.
   * @return The displayed value of the text editor.
   */
  public String getDisplayedValue() {
    return displayedValue == null ? "" : displayedValue;
  }
  
  /**
   * Sets the field alignment.
   * @param align The field alignment.
   */
  public void setAlignment(int align) {
    getState().align = align;
  }
  
  /**
   * Sets the auto complete length.
   * @param autocompleteLength The auto complete length.
   */
  public void setAutocompleteLength(int autocompleteLength) {
    getState().autocompleteLength = autocompleteLength;
  }
  
  /**
   * Sets the field to has auto completion enabled.
   * @param hasAutocomplete The auto completion ability.
   */
  public void setHasAutocomplete(boolean hasAutocomplete) {
    getState().hasAutocomplete = hasAutocomplete;
  }
  
  /**
   * Sets the editor to enable or disable the autofill action.
   * @param hasAutofill Should the editor activate the autofill action ?
   */
  public void setHasAutofill(boolean hasAutofill) {
    getState().hasAutofill = hasAutofill;
  }
  
  /**
   * Sets the convert type to be applied to this text field.
   * @param convertType The convert type.
   */
  public void setConvertType(ConvertType convertType) {
    getState().convertType = convertType;
  }
  
  /**
   * Sets the field suggestions.
   * @param suggestions The displayed strings.
   * @param values The encapsulated values.
   */
  public void setSuggestions(String[][] suggestions, String query) {
    List<AutocompleteSuggestion>        newSuggestions;
    
    newSuggestions = new ArrayList<AutocompleteSuggestion>();
    for (int i = 0; i < suggestions.length; i++) {
      AutocompleteSuggestion            suggestion;
      
      suggestion = new AutocompleteSuggestion();
      suggestion.setId(i);
      suggestion.setQuery(query);
      suggestion.setDisplayStrings(suggestions[i]);
      newSuggestions.add(suggestion);
    }
    // inform client
    getRpcProxy(EditorSuggestionsQueryClientRpc.class).setSuggestions(newSuggestions);
  }
  
  /**
   * Returns the max length permitted for this field.
   * @return The max length permitted for this field.
   */
  protected int getMaxLength() {
    return getState(false).col;
  }

  //---------------------------------------------------
  // SUGGESTIONS
  //---------------------------------------------------
  
  /**
   * Fires a suggestions query event on this text editor.
   * @param query The query string.
   */
  protected void fireSuggestionsQueryEvent(String query) {
    fireEvent(new SuggestionsQueryEvent(this, query));
  }
  
  /**
   * Registers a suggestions query listener on this text editor.
   * @param listener The suggestions query listener object.
   */
  public void addSuggestionsQueryListener(SuggestionsQueryListener listener) {
    addListener("handleSuggestions", SuggestionsQueryEvent.class, listener, SuggestionsQueryEvent.QUERY_METHOD);
  }

  /**
   * Removes a suggestions query listener
   * @param listener The listener to remove.
   */
  public void removeSuggestionsQueryListener(SuggestionsQueryListener listener) {
    removeListener("handleSuggestions", SuggestionsQueryEvent.class, listener);
  }
  
  /**
   * The text change listener.
   */
  public interface SuggestionsQueryListener extends ConnectorEventListener {
    
    /**
     * Notifies registered objects that should query suggestions .
     * @param event The suggestions query event providing the query string.
     */
    void onSuggestionsQuery(SuggestionsQueryEvent event);
  }
  
  /**
   * The suggestions query event object.
   */
  public static class SuggestionsQueryEvent extends Event {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    /**
     * Creates a new text change event instance.
     * @param source The source component.
     * @param query The suggestions query
     */
    public SuggestionsQueryEvent(Component source, String query) {
      super(source);
      this.query = query;
    }

    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------
    
    /**
     * Returns the query string
     * @return The query string.
     */
    public String getQuery() {
      return query;
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    private final String                query;
    public static final Method          QUERY_METHOD;
    
    static {
      try {
        // Set the header click method
        QUERY_METHOD = SuggestionsQueryListener.class.getDeclaredMethod("onSuggestionsQuery", SuggestionsQueryEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }

  //---------------------------------------------------
  // TEXT CHANGE
  //---------------------------------------------------
  
  /**
   * Fires a text change event object.
   * @param newText The new text value.
   * @param oldText The old text value.
   */
  protected void fireTextChangeEvent(String newText, String oldText) {
    fireEvent(new TextChangeEvent(this, newText, oldText));
  }
  
  /**
   * Registers a new text change listener on this text editor.
   * @param listener The text change listener object.
   */
  public void addTextChangeListener(TextChangeListener listener) {
    addListener("handleTextChange", TextChangeEvent.class, listener, TextChangeEvent.TEXT_CHANGE);
  }

  /**
   * Removes a text change listener
   * @param listener The listener to remove.
   */
  public void removeTextChangeListener(TextChangeListener listener) {
    removeListener("handleTextChange", TextChangeEvent.class, listener);
  }
  
  /**
   * The text change listener.
   */
  public interface TextChangeListener extends ConnectorEventListener {
    
    /**
     * Notifies registered objects that text has change.
     * @param event The text change event providing new and old texts.
     */
    void onTextChange(TextChangeEvent event);
  }
  
  /**
   * The text change event object.
   */
  public static class TextChangeEvent extends Event {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    /**
     * Creates a new text change event instance.
     * @param source The source component.
     * @param newText The new text value.
     * @param oldText The old text value.
     */
    public TextChangeEvent(Component source, String newText, String oldText) {
      super(source);
      this.newText = newText;
      this.oldText = oldText;
    }

    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------
    
    /**
     * Returns the new text value.
     * @return The new text value.
     */
    public String getNewText() {
      return newText;
    }
    
    /**
     * Returns the old text value.
     * @return The old text value.
     */
    public String getOldText() {
      return oldText;
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    private final String                newText;
    private final String                oldText;
    public static final Method          TEXT_CHANGE;
    
    static {
      try {
        // Set the header click method
        TEXT_CHANGE = TextChangeListener.class.getDeclaredMethod("onTextChange", TextChangeEvent.class);
      } catch (final NoSuchMethodException e) {
        // This should never happen
        throw new RuntimeException(e);
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  // used to store an internal state of this field
  private String                        internalValue;
  private String                        displayedValue;
}
