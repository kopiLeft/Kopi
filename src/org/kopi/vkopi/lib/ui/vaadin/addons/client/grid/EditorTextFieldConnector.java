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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.QueryListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextTransform;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Grid text editor connector.
 */
@SuppressWarnings("serial")
@Connect(value = GridEditorTextField.class, loadStyle = LoadStyle.DEFERRED)
public class EditorTextFieldConnector extends EditorFieldConnector implements ClickHandler, QueryListener {
  
  @Override
  protected void init() {
    super.init();
    getWidget().connection = getConnection();
    handlerRegistration = getWidget().addClickHandler(this);
    registerRpc(EditorSuggestionsQueryClientRpc.class, new EditorSuggestionsQueryClientRpc() {
      
      @Override
      public void setSuggestions(final List<AutocompleteSuggestion> suggestions) {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
          
          @Override
          public void execute() {
            getWidget().setSuggestions(suggestions); 
          }
        });
      }
    });
  }
  
  @Override
  public VEditorTextField getWidget() {
    return (VEditorTextField) super.getWidget();
  }
  
  @Override
  public EditorTextFieldState getState() {
    return (EditorTextFieldState) super.getState();
  }
  
  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    getWidget().setReadOnly(isReadOnly());
  }
  
  /**
   * Sets the max length of the text field.
   */
  @OnStateChange("col")
  /*package*/ void setMaxLength() {
    getWidget().setMaxLength(getState().col);
  }
  
  /**
   * Sets the max length of the text field.
   */
  @OnStateChange("hasAutofill")
  /*package*/ void setAutofill() {
    if (getState().hasAutofill) {
      getWidget().setAutofill();
    }
  }
  
  /**
   * Sets the max length of the text field.
   */
  @OnStateChange({"hasAutocomplete", "autocompleteLength"})
  /*package*/ void setAutocompleteLength() {
    if (getState().hasAutocomplete) {
      getWidget().setAutocompleteLength(getState().autocompleteLength);
    }
  }
  
  
  /**
   * Sets the alignment of the text field.
   */
  @OnStateChange("align")
  /*package*/ void setAlignement() {
    switch (getState().align) {
    case Styles.BOTTOM:
    case Styles.TOP:
    case Styles.LEFT:
      getWidget().setAlignment(TextAlignment.LEFT);
      break;
    case Styles.CENTER:
      getWidget().setAlignment(TextAlignment.CENTER);
      break;
    case Styles.RIGHT:
      getWidget().setAlignment(TextAlignment.RIGHT);
      break;
    default:
      getWidget().setAlignment(TextAlignment.LEFT);
    }
  }
  
  /**
   * Sets the alignment of the text field.
   */
  @OnStateChange("convertType")
  /*package*/ void setTextTransform() {
    switch (getState().convertType) {
    case UPPER:
      getWidget().getElement().getStyle().setTextTransform(TextTransform.UPPERCASE);
      break;
    case LOWER:
      getWidget().getElement().getStyle().setTextTransform(TextTransform.LOWERCASE);
      break;
    case NAME:
      getWidget().getElement().getStyle().setTextTransform(TextTransform.CAPITALIZE);
    case NONE:
      getWidget().getElement().getStyle().setTextTransform(TextTransform.NONE);
    default:
      // nothing to do
    }
  }
  
  @Override
  public void flush() {
    getWidget().valueChanged(false);
  }
  
  @Override
  public void onUnregister() {
    super.onUnregister();
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
    }
  }

  @Override
  public void onClick(ClickEvent event) {
    getRpcProxy(EditorFieldClickServerRpc.class).clicked();
  }

  @Override
  public void handleQuery(String query) {
    getRpcProxy(EditorSuggestionsQueryServerRpc.class).querySuggestions(query);
  }
  
  /**
   * Fires an autofill event on the server side.
   */
  protected void autofill() {
    getRpcProxy(EditorFieldClickServerRpc.class).autofill();
  }
  
  /**
   * Sends The given text value to the server side.
   * @param text The text to be sent to server side.
   */
  protected void sendTextToServer(String text) {
    getRpcProxy(EditorTextChangeServerRpc.class).textChanged(text);
  }
  
  /**
   * Stored to be removed when the connector is unregistered.
   */
  private HandlerRegistration           handlerRegistration;
}
