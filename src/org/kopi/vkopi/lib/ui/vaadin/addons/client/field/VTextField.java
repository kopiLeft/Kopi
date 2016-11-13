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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.DefaultSuggestOracle;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.QueryListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionDisplay;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.vaadin.client.ApplicationConnection;

/**
 * A field widget. It can be either Integer, String, Fixnum, ..
 * All depends on the model that will decide what is the validation
 * strategy.
 */
public class VTextField extends SimplePanel implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the field widget.
   */
  public VTextField() {
    setStyleName(Styles.TEXT_FIELD);
    sinkEvents(Event.ONCONTEXTMENU);
    addDomHandler(new ContextMenuHandler() {
      
      @Override
      public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
      }
    }, ContextMenuEvent.getType());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the text field component.
   * @param textField The text field component.
   */
  public void setTextField(VInputTextField textField) {
    this.textField = textField;
    this.textField.setEnabled(enabled);
    add(this.textField);
  }
  
  /**
   * Sets the field focus.
   * @param focused The field focus.
   */
  public boolean setFocus(boolean focused) {
    if (textField != null) {
      return !textField.requestFocusInWindow(focused);
    }
    // try again
    return true;
  }
  
  /**
   * Sets the blink state.
   * @param blink The blink state.
   */
  public void setBlink(boolean blink) {
    if (textField != null) {
      textField.setBlink(blink);
    }
  }
  
  /**
   * Selects all of the text in the box.
   * 
   * This will only work when the widget is attached to the document and not
   * hidden.
   */
  public void selectAll() {
    if (textField != null) {
      textField.selectAll();
    }
  }

  /**
   * Sets the cursor position.
   * 
   * This will only work when the widget is attached to the document and not
   * hidden.
   * 
   * @param pos the new cursor position
   */
  public void setCursorPos(int pos) {
    if (textField != null) {
      textField.setCursorPos(pos);
    }
  }
  
  /**
   * Sets the field text.
   * @param text The field text.
   */
  public void setText(String text) {
    if (textField != null) {
      textField.updateFieldContent(text);
    }
  }
  
  /**
   * Returns the text value.
   * @return The text value.
   */
  public String getText() {
    if (textField != null) {
      return textField.getText();
    }
    
    return null;
  }
  
  /**
   * Returns the rows number of this text input.
   * @return the rows number of this text input.
   */
  public int getRows() {
    if (textField instanceof VInputTextArea) {
      return ((VInputTextArea)textField).getRows();
    }
    
    return 1;
  }
  
  /**
   * Sets the application connection.
   * @param client The application connection.
   */
  public void setConnection(ApplicationConnection client) {
    if (textField != null) {
      textField.client = client;
    }
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (textField != null) {
      textField.setEnabled(enabled);
    }
  }
  
  /**
   * Sets the auto complete ability.
   * @param hasAutocomplete the auto complete ability.
   */
  public void setHasAutocomplete(boolean hasAutocomplete) {
    if (textField != null) {
      textField.setHasAutocomplete(hasAutocomplete);
    }
  }
  
  /**
   * Sets the suggestion display.
   * @param display the suggestion display.
   */
  public void setSuggestionDisplay(SuggestionDisplay display) {
    if (textField != null) {
      textField.setDisplay(display);
    }
  }
  
  /**
   * Sets the suggestion oracle used to create suggestions.
   * @param oracle the oracle.
   */
  public void setOracle(int minimumQueryCharacters) {
    if (textField != null) {
      DefaultSuggestOracle	oracle = new DefaultSuggestOracle(textField);
      
      oracle.setMinimumQueryCharacters(minimumQueryCharacters);
      textField.setOracle(oracle);
    }
  }
  
  /**
   * Adds a selection handler.
   * @param handler The handler to be registered.
   * @return The handler registration.
   */
  public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
    if (textField != null) {
      return textField.addSelectionHandler(handler);
    } else {
      return null;
    }
  }
  
  /**
   * Sets the query listener.
   * @param queryListener the quert listener.
   */
  public void setQueryListener(QueryListener queryListener) {
    if (textField != null) {
      if (textField.getOracle() != null && textField.getOracle() instanceof DefaultSuggestOracle) {
	((DefaultSuggestOracle)textField.getOracle()).setQueryListener(queryListener);
      }
    }
  }
  
  /**
   * Sets the list of available suggestions.
   * @param suggestions The available suggestions.
   */
  public void setSuggestions(List<AutocompleteSuggestion> suggestions) {
    if (textField != null) {
      if (textField.getOracle() != null && textField.getOracle() instanceof DefaultSuggestOracle) {
	((DefaultSuggestOracle)textField.getOracle()).setSuggestions(suggestions);
      }
    }
  }
  
  
  /**
   * Hides auto complete suggestions.
   */
  /*package*/ void hideSuggestions() {
    if (textField != null) {
      textField.hideSuggestions();
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /*package*/ VInputTextField			textField;
  private boolean				enabled = true;
}
