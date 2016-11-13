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

import org.kopi.vkopi.lib.ui.vaadin.addons.TextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.DefaultSuggestion;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.DefaultSuggestionDisplay;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.QueryListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The field component connector
 */
@SuppressWarnings("serial")
@Connect(value = TextField.class, loadStyle = LoadStyle.DEFERRED)
public class TextFieldConnector extends AbstractFieldConnector implements QueryListener, SelectionHandler<Suggestion> {

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    registerRpc(TextFieldClientRpc.class, rpc);
  }
  
  @Override
  public VTextField getWidget() {
    return (VTextField) super.getWidget();
  }
  
  @Override
  public TextFieldState getState() {
    return (TextFieldState) super.getState();
  }
  
  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    // set the input widget.
    if (getWidget().textField == null) {
      getWidget().setTextField(createTextInput());
      getWidget().setConnection(getConnection());
    }
    if (getState().text != null && getState().text.length() > 0) {
      getWidget().setText(getState().text);
    }
    getWidget().setHasAutocomplete(getState().hasAutocomplete);
    getWidget().setSuggestionDisplay(new DefaultSuggestionDisplay(getConnection()));
    getWidget().setOracle(getState().autocompleteLength);
    // now we can set the query listener and selection listener
    getWidget().setQueryListener(this);
    getWidget().addSelectionHandler(this);
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }

  @Override
  public void onSelection(final SelectionEvent<Suggestion> event) {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
	getServerRpc().onSuggestion(((DefaultSuggestion) event.getSelectedItem()).getWrappedSuggestion());
      }
    });
  }

  @Override
  public void handleQuery(String query) {
    if (isSuggestionsQueryCanceled) {
      allowSSuggestionsQuery();
    } else {
      getServerRpc().onQuery(query);
    }
  }
  
  /**
   * Cancels the suggestions query
   */
  protected void cancelSuggestionsQuery() {
    isSuggestionsQueryCanceled = true;
  }
  
  /**
   * Allows suggestions to be fetched form
   * server side.
   */
  protected void allowSSuggestionsQuery() {
    isSuggestionsQueryCanceled = false;
  }
  
  /**
   * Creates the attached text field component.
   * @return the attached text field component.
   */
  protected VInputTextField createTextInput() {
    VInputTextField		text;
    
    text = createInputWidget();
    setValidationStrategy(text);
    setAlign(text);
    if (getState().noEdit) {
      text.setTextValidationStrategy(new NoeditValidationStrategy());
      text.setReadOnly(true);
    }
    
    return text;
  }
  
  /**
   * Creates the input widget according to field state.
   * @return the input widget
   */
  protected VInputTextField createInputWidget() {
    VInputTextField	text;
    int			col;
    
    col = getState().col;
    if (getState().noEcho && getState().rows == 1) {
      text = new VInputPasswordField();
    } else if (getState().rows > 1) {
      if (getState().scanner) {
	col = 40;
      }
      
      text = new VInputTextArea();
      ((VInputTextArea)text).setRows(getState().rows, getState().visibleRows);
      ((VInputTextArea)text).setCols(col);
      ((VInputTextArea)text).setWordwrap(true);
      // if fixed new line mode is used, we remove scroll bar from text area
      ((VInputTextArea)text).setFixedNewLine(getState().fixedNewLine);
    } else {
      text = new VInputTextField();
    }
    if (BrowserInfo.get().isChrome()) {
      // for webkit browsers, we need to substract 1 from the field size.
      text.setSize(Math.max(1, col -1));
    } else {
      text.setSize(col);
    }
    text.setMaxLength(col * getState().rows);
    // add navigation handler.
    text.addKeyDownHandler(TextFieldNavigationHandler.newInstance(this, text, getState().rows > 1));
    return text;
  }
  
  /**
   * Returns the server RPC for text field.
   * @return The server RPC for text field.
   */
  protected TextFieldServerRpc getServerRpc() {
    return getRpcProxy(TextFieldServerRpc.class);
  }
  
  /**
   * Sets the alignment of a text field.
   * @param text The text field.
   */
  protected void setAlign(VInputTextField text) {
    switch (getState().align) {
    case Styles.BOTTOM:
    case Styles.TOP:
    case Styles.LEFT:
      text.setAlignment(TextAlignment.LEFT);
      break;
    case Styles.CENTER:
      text.setAlignment(TextAlignment.CENTER);
      break;
    case Styles.RIGHT:
      text.setAlignment(TextAlignment.RIGHT);
      break;
    default:
      break;
    }
  }
  
  /**
   * Sets the validation strategy of a text field.
   * @param text The text field.
   */
  protected void setValidationStrategy(VInputTextField text) {
    switch (getState().type) {
    case STRING:
      text.setTextValidationStrategy(new AllowAllValidationStrategy());
      break;
    case INTEGER:
      text.setTextValidationStrategy(new IntegerValidationStrategy());
      break;
    case FIXNUM:
      text.setTextValidationStrategy(new FixnumValidationStrategy());
      break;
    case DATE:
      text.setTextValidationStrategy(new DateValidationStrategy());
      break;
    case TIME:
      text.setTextValidationStrategy(new TimeValidationStrategy());
      break;
    case MONTH:
      text.setTextValidationStrategy(new MonthValidationStrategy());
      break;
    case WEEK:
      text.setTextValidationStrategy(new WeekValidationStrategy());
      break;
    case TIMESTAMP:
      text.setTextValidationStrategy(new AllowAllValidationStrategy());
      break;
    case CODE:
      text.setTextValidationStrategy(new EnumValidationStrategy(getState().enumerations));
      break;
    default:
      text.setTextValidationStrategy(new AllowAllValidationStrategy());
    }
  }
  
  /**
   * Send the current text field to the server.
   */
  protected void sendTextToServer() {
    sendTextToServer(getWidget().getText());
  }
  
  /**
   * Sends the widget text to the server.
   * @param text The widget text.
   */
  protected void sendTextToServer(final String text) {
    getRpcProxy(TextChangeServerRpc.class).onTextChange(text);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean                       isSuggestionsQueryCanceled;
  private TextFieldClientRpc		rpc = new TextFieldClientRpc() {
    
    @Override
    public void setFocus(final boolean focused) {
      Scheduler.get().scheduleEntry(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setFocus(focused);
        }
      });
    }

    @Override
    public void setText(final String text) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setText(text);
        }
      });
    }

    @Override
    public void setBlink(final boolean blink) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setBlink(blink);
        }
      });
    }

    @Override
    public void setSuggestions(final List<AutocompleteSuggestion> suggestions) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {

	@Override
	public void execute() {
	  if (isSuggestionsQueryCanceled) {
	    isSuggestionsQueryCanceled = false;
	  } else {
	    getWidget().setSuggestions(suggestions);
	  }
	}
      });
    }
  };
  
  //---------------------------------------------------
  // FIELD TYPES CONSTANTS
  //---------------------------------------------------
  
  /**
   * String field
   */
  private static final int			STRING = 0;
  
  /**
   * Integer field
   */
  private static final int			INTEGER = 1;
  
  /**
   * Fixnum field.
   */
  private static final int			FIXNUM = 2;
  
  /**
   * Date field.
   */
  private static final int			DATE = 3;
  
  /**
   * Time field.
   */
  private static final int			TIME = 4;
  
  /**
   * Month field.
   */
  private static final int			MONTH = 5;
  
  /**
   * Week field
   */
  private static final int			WEEK = 6;
  
  /**
   * Timestamp field.
   */
  private static final int			TIMESTAMP = 7;
  
  /**
   * code field.
   */
  private static final int			CODE = 8;
}
