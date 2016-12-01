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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextChangeServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextFieldClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextFieldServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextFieldState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextFieldState.ConvertType;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;

import com.vaadin.ui.AbstractField;

/**
 * The server side field component.
 */
@SuppressWarnings("serial")
public class TextField extends AbstractField<String> implements com.vaadin.ui.Component.Focusable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the text field server component.
   * @param col The column number.
   * @param rows The rows number.
   * @param visibleRows The visible rows number.
   * @param fixedNewLine Are we using default line transformer ?
   * @param noEcho Is it a password field ?
   * @param scanner Is it a scanner field ?
   * @param noEdit Is it no edit field ?
   * @param align The field align.
   */
  public TextField(int col,
      		   int rows,
      		   int visibleRows,
      		   boolean fixedNewLine,
      		   boolean noEcho,
      		   boolean scanner,
      		   boolean noEdit,
      		   int align)
  {
    registerRpc(new TextFieldServerRpcHandler());
    registerRpc(textChange);
    listeners = new ArrayList<TextFieldListener>();
    textChangelisteners = new ArrayList<TextValueChangeListener>();
    setImmediate(true);
    getState().col = col;
    getState().rows = rows;
    getState().visibleRows = visibleRows;
    getState().fixedNewLine = fixedNewLine;
    getState().noEcho = noEcho;
    getState().scanner = scanner;
    getState().noEdit = noEdit;
    getState().align = align;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the text field type.
   * @param type The text field type.
   */
  public void setType(Type type) {
    getState().type = type.ordinal();
  }
  
  /**
   * Sets the enumeration for code fields.
   * @param enumerations The field enumerations.
   */
  public void setEnumerations(String[] enumerations) {
    getState().enumerations = enumerations;
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
   * Sets the convert type to be applied to this text field.
   * @param convertType The convert type.
   */
  public void setConvertType(ConvertType convertType) {
    getState().convertType = convertType;
  }
  
  /**
   * Sets the minimum value to be accepted by this field.
   * @param minval The minimum value.
   */
  public void setMinValue(Double minval) {
    getState().minval = minval;
  }
  
  /**
   * Sets the maximum value to be accepted by this field.
   * @param maxval The maximum value.
   */
  public void setMaxValue(Double maxval) {
    getState().maxval = maxval;
  }
  
  /**
   * Sets the max scale to be used with this field.
   * @param maxScale The max Scale to be used with this field.
   */
  public void setMaxScale(int maxScale) {
    getState().maxScale = maxScale;
  }
  
  /**
   * Sets this field to be a fraction one.
   * @param fraction Is it a fraction field ?
   */
  public void setFraction(boolean fraction) {
    getState().fraction = fraction;
  }
  
  /**
   * Sets the field suggestions.
   * @param suggestions The displayed strings.
   * @param values The encapsulated values.
   */
  public void setSuggestions(String[][] suggestions, String query) {
    List<AutocompleteSuggestion>	newSuggestions;
    
    newSuggestions = new ArrayList<AutocompleteSuggestion>();
    for (int i = 0; i < suggestions.length; i++) {
      AutocompleteSuggestion		suggestion;
      
      suggestion = new AutocompleteSuggestion();
      suggestion.setId(i);
      suggestion.setQuery(query);
      suggestion.setDisplayStrings(suggestions[i]);
      newSuggestions.add(suggestion);
    }
    // inform client
    getRpcProxy(TextFieldClientRpc.class).setSuggestions(newSuggestions);
  }
  
  /**
   * Sets the field focus.
   * @param focused The focus state.
   */
  public void setFocus(boolean focused) {
    getRpcProxy(TextFieldClientRpc.class).setFocus(focused);
  }
  
  /**
   * Sets the blink state of this text field.
   * @param blink Should the text get the blink state.
   */
  public void setBlink(boolean blink) {
    getRpcProxy(TextFieldClientRpc.class).setBlink(blink);
  }
  
  /**
   * Sets the text value of this field.
   * @param text The field value.
   */
  public void setText(String text) {
    // save in internal value. This is not really true cause
    // the client value may differ from server value especially
    // when value is not valid.
    if ((isEmpty(getValue()) && !isEmpty(text)) || !isEmpty(getValue()) && !getValue().equals(text)) {
      //getRpcProxy(TextFieldClientRpc.class).setText(text);
      getState().text = text;
      setValue(text);
    }
  }
  
  /**
   * Returns the inner text value. Can be different from displayed value.
   * @return The inner text value.
   */
  public String getText() {
    return getValue();
  }
  
  @Override
  protected TextFieldState getState() {
    return (TextFieldState) super.getState();
  }
  
  @Override
  protected TextFieldState getState(boolean markAsDirty) {
    return (TextFieldState) super.getState(markAsDirty);
  }
  
  @Override
  public Class<? extends String> getType() {
    return String.class;
  }

  @Override
  public void beforeClientResponse(boolean initial) {
    String 		value;
    
    super.beforeClientResponse(initial);
    value = getValue();
    if (value == null) {
      value = ""; // avoiding null pointer exceptions
    }
    
    getState().text = value;
  }
  
  @Override
  public boolean isEmpty() {
    return super.isEmpty() || getValue().length() == 0;
  }

  /**
   * Checks if the given text is empty.
   * @param text The text to check.
   * @return {@code true} if the text is empty.
   */
  private static boolean isEmpty(String text) {
    return text == null || text.trim().length() == 0;
  }

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Registers a text field exchange listener
   * @param l The text field listener.
   */
  public void addTextFieldListener(TextFieldListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a registered text field exchange listener
   * @param l The text field listener to be removed.
   */
  public void removeTextFieldListener(TextFieldListener l) {
    listeners.remove(l);
  }
  
  /**
   * Registers a text change listener
   * @param l The text change listener.
   */
  public void addTextValueChangeListener(TextValueChangeListener l) {
    textChangelisteners.add(l);
  }
  
  /**
   * Removes a text change listener
   * @param l The text change listener.
   */
  public void removeTextValueChangeListener(TextValueChangeListener l) {
    textChangelisteners.remove(l);
  }
  
  /**
   * Fires a print form event on this text field.
   */
  protected void firePrintForm() {
    for (TextFieldListener l : listeners) {
      l.printForm();
    }
  }
  
  /**
   * Fires a previous entry event on this text field.
   */
  protected void firePreviousEntry() {
    for (TextFieldListener l : listeners) {
      l.previousEntry();
    }
  }
  
  /**
   * Fires a next entry event on this text field.
   */
  protected void fireNextEntry() {
    for (TextFieldListener l : listeners) {
      l.previousEntry();
    }
  }
  
  /**
   * Fires a goto previous record event on this text field.
   */
  protected void fireGotoPrevRecord() {
    for (TextFieldListener l : listeners) {
      l.gotoPrevRecord();
    }
  }
  
  /**
   * Fires a goto previous field event on this text field.
   */
  protected void fireGotoPrevField() {
    for (TextFieldListener l : listeners) {
      l.gotoPrevField();
    }
  }
  
  /**
   * Fires a goto next record event on this text field.
   */
  protected void fireGotoNextRecord() {
    for (TextFieldListener l : listeners) {
      l.gotoNextRecord();
    }
  }
  
  /**
   * Fires a goto next field event on this text field.
   */
  protected void fireGotoNextField() {
    for (TextFieldListener l : listeners) {
      l.gotoNextField();
    }
  }
  
  /**
   * Fires a goto next empty must fill field event on this text field.
   */
  protected void fireGotoNextEmptyMustfill() {
    for (TextFieldListener l : listeners) {
      l.gotoNextEmptyMustfill();
    }
  }
  
  /**
   * Fires a goto next block event on this text field.
   */
  protected void fireGotoNextBlock() {
    for (TextFieldListener l : listeners) {
      l.gotoNextBlock();
    }
  }
  
  /**
   * Fires a goto last record event on this text field.
   */
  protected void fireGotoLastRecord() {
    for (TextFieldListener l : listeners) {
      l.gotoLastRecord();
    }
  }
  
  /**
   *  Fires a goto first record event on this text field.
   */
  protected void fireGotoFirstRecord() {
    for (TextFieldListener l : listeners) {
      l.gotoFirstRecord();
    }
  }
  
  /**
   * Fires a close window event on this text field.
   */
  protected void fireCloseWindow() {
    for (TextFieldListener l : listeners) {
      l.closeWindow();
    }
  }
  
  /**
   * Fires a text change event on this text field.
   */
  protected void fireTextChanged(String oldText, String newText) {
    for (TextValueChangeListener l : textChangelisteners) {
      if (l != null) {
	l.onTextChange(oldText, newText);
      }
    }
  }
  
  /**
   * Fires a text change event on this text field for the given record.
   * @param rec The concerned record.
   * @param text The text value.
   */
  protected void fireTextChanged(int rec, String text) {
    for (TextValueChangeListener l : textChangelisteners) {
      if (l != null) {
        l.onTextChange(rec, text);
      }
    }
  }
  
  /**
   * Fires a query start event.
   */
  protected void fireOnQuery(String query) {
    for (TextFieldListener l : listeners) {
      l.onQuery(query);
    }
  }
  
  /**
   * Fires suggestion selection event.
   * @param suggestion The selected suggestion.
   */
  protected void fireOnSuggestion(AutocompleteSuggestion suggestion) {
    for (TextFieldListener l : listeners) {
      if (l != null) {
	l.onSuggestion(suggestion);
      }
    }
  }

  //---------------------------------------------------
  // FIELD TYPE
  //---------------------------------------------------
  
  /**
   * Text field types.
   */
  public static enum Type {
    
    /**
     * String field
     */
    
    STRING,
    
    /**
     * Integer field
     */
    INTEGER,
    
    /**
     * Fixnum field.
     */
    FIXNUM,
    
    /**
     * Date field.
     */
    DATE,
    
    /**
     * Time field.
     */
    TIME,
    
    /**
     * Month field.
     */
    MONTH,
    
    /**
     * Week field
     */
    WEEK,
    
    /**
     * Timestamp field.
     */
    TIMESTAMP,
    
    /**
     * Code field
     */
    CODE;
  }

  //---------------------------------------------------
  // RPC SERVER IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Implementation of the server RPC contract.
   */
  private class TextFieldServerRpcHandler implements TextFieldServerRpc {
    
    //-------------------------------------------------
    // IMPLEMENTATIONS
    //-------------------------------------------------
    
    @Override
    public void printForm() {
      firePrintForm();
    }
    
    @Override
    public void previousEntry() {
      firePreviousEntry();
    }
    
    @Override
    public void nextEntry() {
      fireNextEntry();
    }
    
    @Override
    public void gotoPrevRecord() {
      fireGotoPrevRecord();
    }
    
    @Override
    public void gotoPrevField() {
      fireGotoPrevField();
    }
    
    @Override
    public void gotoNextRecord() {
      fireGotoNextRecord();
    }
    
    @Override
    public void gotoNextField() {
      fireGotoNextField();
    }
    
    @Override
    public void gotoNextEmptyMustfill() {
      fireGotoNextEmptyMustfill();
    }
    
    @Override
    public void gotoNextBlock() {
      fireGotoNextBlock();
    }
    
    @Override
    public void gotoLastRecord() {
      fireGotoLastRecord();
    }
    
    @Override
    public void gotoFirstRecord() {
      fireGotoFirstRecord();
    }
    
    @Override
    public void closeWindow() {
      fireCloseWindow();
    }

    @Override
    public void onQuery(String query) {
      fireOnQuery(query);
    }

    @Override
    public void onSuggestion(AutocompleteSuggestion suggestion) {
      fireOnSuggestion(suggestion);
    }
  }
  
  /**
   * Text change rpc implementation.
   */
  private TextChangeServerRpc		textChange = new TextChangeServerRpc() {
    
    @Override
    public void onTextChange(String text) {
      // Only do the setting if the string representation of the value
      // has been updated
      String 		newValue = text;
      final String	oldValue = getValue();
      
      if (newValue == null) {
	newValue = "";
      }
      
      if (!newValue.equals(oldValue)) {
	boolean		wasModified = isModified();

	setValue(newValue, true);
	fireTextChanged(oldValue, newValue);
	// If the modified status changes, or if we have a
	// formatter, repaint is needed after all.
	if (wasModified != isModified()) {
	  markAsDirty();
	}
      }
    }

    @Override
    public void onDirtyValues(Map<Integer, String> values) {
      for (Map.Entry<Integer, String> value : values.entrySet()) {
        fireTextChanged(value.getKey(), value.getValue());
      }
    }
  };
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<TextFieldListener>		listeners;
  private final List<TextValueChangeListener>	textChangelisteners;
}
