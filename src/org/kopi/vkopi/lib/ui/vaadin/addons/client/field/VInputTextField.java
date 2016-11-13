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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionCallback;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionDisplay;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionHandler;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ConnectorMap;

/**
 * A text field widget that can support many validation
 * strategies to restrict field input.
 */
public class VInputTextField extends TextBoxBase implements ValueChangeHandler<String>, KeyUpHandler,
  KeyPressHandler, ChangeHandler, FocusHandler, BlurHandler, KeyDownHandler, HasContextMenuHandlers,
  HasSelectionHandlers<Suggestion>, SuggestionHandler, HasValue<String>
{

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the text input instance.
   */
  public VInputTextField() {
    this(DOM.createInputText());
  }
  
  /**
   * Protected constructor to use to create other types of fields.
   * @param node The node element.
   */
  protected VInputTextField(Element node) {
    super(node);
    setStyleName(Styles.TEXT_INPUT);
    addKeyPressHandler(this);
    addKeyUpHandler(this);
    addValueChangeHandler(this);
    sinkEvents(Event.ONPASTE);
    sinkEvents(Event.ONCONTEXTMENU);
    addChangeHandler(this);
    addKeyDownHandler(this);
    addFocusHandler(this);
    addBlurHandler(this);
    addContextMenuHandler(new ContextMenuHandler() {
      
      @Override
      public void onContextMenu(ContextMenuEvent event) {
	event.preventDefault(); // don't show context menu on fields.
      }
    });
    
    if (hasAutoComplete()) {
      getInputElement().setAttribute("autocomplete", "on");
    } else {
      getInputElement().setAttribute("autocomplete", "off");
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    if (event.isAnyModifierKeyDown()
	|| (String.valueOf(event.getCharCode()).trim().length() == 0
	&& event.getNativeEvent().getCharCode() != KeyCodes.KEY_SPACE))
    {
      return;
    }
    // if the content if the input is selected, validate only the typed character.
    // this is typically used for enumeration fields to allow the content to be overwritten
    if (getSelectedText() != null && getSelectedText().equals(getText())
	&& validationStrategy != null
	&& validationStrategy instanceof EnumValidationStrategy)
    {
      if (!validationStrategy.validate(String.valueOf(event.getCharCode()), getMaxLength())) {
	cancelKey();
      }
      
      return;
    }
    // validate the whole text input.
    if (validationStrategy != null) {
      if (!validationStrategy.validate(getText() + String.valueOf(event.getCharCode()), getMaxLength())) {
	cancelKey();
      }
    }
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    if (event.getTypeInt() == Event.ONPASTE) {
      // should validate text content
      if (validationStrategy != null) {
	final String		before = getText();
	
	Scheduler.get().scheduleDeferred(new Command() {

	  @Override
	  public void execute() {
	    if (!validationStrategy.validate(getText(), getMaxLength())) {
	      setText(before);
	    }
	  }
	});
      }
    }

    super.onBrowserEvent(event);
  }
  
  @Override
  public void setText(String text) {
    // set only valid inputs
    if (!(validationStrategy instanceof NoeditValidationStrategy)
	&& validationStrategy.validate(text, getMaxLength()))
    {
      super.setText(text);
    } else {
      // for no edit strategy, no validation is performed.
      super.setText(text);
    }
  }
  
  /**
   * Returns {@code true} if the auto complete function should be used.
   * @return {@code true} if the auto complete function should be used.
   */
  protected boolean hasAutoComplete() {
    return hasAutocomplete;
  }
  
  /**
   * Sets the text validation strategy.
   * @param validationStrategy The text validation strategy.
   */
  public void setTextValidationStrategy(TextValidationStrategy validationStrategy) {
    this.validationStrategy = validationStrategy;
  }
  
  /**
   * Sets the fields width.
   * @param width The field width.
   */
  public void setWidth(int width) {
    setWidth(String.valueOf(width) + "ex");
  }
  
  /**
   * Returns the field max length.
   * @return The field max length.
   */
  protected int getMaxLength() {
    return getInputElement().getMaxLength();
  }
  
  /**
   * Returns the input element.
   * @return The input element.
   */
  protected InputElement getInputElement() {
    return getElement().cast();
  }

  /**
   * Sets the text zone max length.
   * @param maxLength The max length.
   */
  public void setMaxLength(int maxLength) {
    updateMaxLength(maxLength);
  }
  
  /**
   * Sets the text size.
   * @param size The text size.
   */
  public void setSize(int size) {
    getInputElement().setSize(size);
  }

  /**
   * This method is responsible for updating the DOM or otherwise ensuring
   * that the given max length is enforced. Called when the max length for the
   * field has changed.
   * 
   * @param maxLength The new max length
   */
  protected void updateMaxLength(int maxLength) {
    if (maxLength >= 0) {
      getElement().setPropertyInt("maxLength", maxLength);
    } else {
      getElement().removeAttribute("maxLength");

    }
    setMaxLengthToElement(maxLength);
  }

  /**
   * Sets the max length to the input element.
   * @param newMaxLength The new max length.
   */
  protected void setMaxLengthToElement(int newMaxLength) {
    if (newMaxLength >= 0) {
      getElement().setPropertyInt("maxLength", newMaxLength);
    } else {
      getElement().removeAttribute("maxLength");
    }
  }
  
  /**
   * Sets the the blink state of the field.
   * @param blink The blink state.
   */
  public void setBlink(boolean blink) {
    if (blink) {
      addStyleDependentName("blink");
    } else {
      removeStyleDependentName("blink");
    }
  }

  /**
   * Updates the field content.
   * @param text The new text field content.
   */
  public void updateFieldContent(final String text) {
    setText(text);
    lastCommunicatedValue = text;
  }

  /**
   * Called when the field value might have changed and/or the field was
   * blurred. These are combined so the blur event is sent in the same batch
   * as a possible value change event (these are often connected).
   * 
   * @param blurred true if the field was blurred
   */
  public void valueChange(boolean blurred) {
    communicateTextValueToServer();
  }

  protected boolean isWordwrap() {
    String wrap = getElement().getAttribute("wrap");
    return !"off".equals(wrap);
  }
  
  @Override
  public void onKeyUp(KeyUpEvent event) {
    // After every user key input, refresh the popup's suggestions.
    if (hasAutocomplete) {
      switch (event.getNativeKeyCode()) {
      case KeyCodes.KEY_DOWN:
      case KeyCodes.KEY_UP:
      case KeyCodes.KEY_ENTER:
      case KeyCodes.KEY_TAB:
	break;
      default :
        refreshSuggestions();
	break;
      }
    }
  }
  
  @Override
  public void onValueChange(ValueChangeEvent<String> event) {
    // FIXME : this causes the block of the UI after suggestion selection
    // delegateEvent(this, event);
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {
    if (hasAutocomplete) {
      switch (event.getNativeKeyCode()) {
      case KeyCodes.KEY_DOWN:
	display.moveSelectionDown();
	break;
      case KeyCodes.KEY_UP:
	display.moveSelectionUp();
	break;
      case KeyCodes.KEY_ESCAPE:
	display.hideSuggestions();
	break;
      case KeyCodes.KEY_ENTER:
      case KeyCodes.KEY_TAB:
	Suggestion suggestion = display.getCurrentSelection();

	if (suggestion == null) {
	  display.hideSuggestions();
	} else {
	  setNewSelection(suggestion);
	}
	break;
      }
    }
    
    if (BrowserInfo.get().isIE() && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      // IE does not send change events when pressing enter in a text
      // input so we handle it using a key listener instead
      valueChange(false);
    }
  }
  
  /**
   * Request that this field get the focus in the window.
   * @param focused The focus ability
   * @return {@code true} if the focused input is this field
   */
  public boolean requestFocusInWindow(boolean focused) {
    // we need to check here if the text is already focused
    // to not get the focus twice
    if (focusedTextField == this) {
      return true;
    }
    
    setFocus(focused);
    return false;
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        parent = WidgetUtils.getParent(VInputTextField.this, VWindow.class);
      }
    });
  }

  @Override
  public void onBlur(BlurEvent event) {
    // this is called twice on Chrome when e.g. changing tab while prompting
    // field focused - do not change settings on the second time
    if (focusedTextField != this || focusedTextField == null) {
      return;
    }
    removeStyleDependentName("focus");
    focusedTextField = null;
    valueChange(true);
    hideSuggestions();
  }

  @Override
  public void onFocus(FocusEvent event) {
    if (focusedTextField == this) {
      // already got the focus. give up
      return;
    }
    focusedTextField = this;
    addStyleDependentName("focus");
    // ensure the selection of the field content.
    maybeSelectAll();
    setLastFocusedInput();
    // cancel the fetch of suggestions list on field focus
    // when the field is not empty
    maybeCancelSuggestions();
  }
  
  @Override
  public void selectAll() {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
	if (focusedTextField == VInputTextField.this) {
	  VInputTextField.super.selectAll();
	}
      }
    });
  }

  @Override
  public void onChange(ChangeEvent event) {
    valueChange(false);
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (focusedTextField == this) {
      focusedTextField = null;
    }
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    boolean wasReadOnly = isReadOnly();

    if (readOnly) {
      setTabIndex(-1);
    } else if (wasReadOnly && !readOnly && getTabIndex() == -1) {
      /*
       * Need to manually set tab index to 0 since server will not send
       * the tab index if it is 0.
       */
      setTabIndex(0);
    }

    super.setReadOnly(readOnly);
  }
  
  //---------------------------------------------------
  // PRIVATE MEMBERS
  //---------------------------------------------------
  
  /**
   * Selects the content of this text input
   */
  private void maybeSelectAll() {
    if (getText() != null && getText().length() > 0) {
      Timer     timer = new Timer() {

        @Override
        public void run() {
          selectAll();
        }
      };

      timer.schedule(60);
    }
  }
  
  /**
   * Sets the last focused input field.
   */
  private void setLastFocusedInput() {
    if (parent != null) {
      lastFocusedTextField = this;
      parent.setLastFocusedTextBox(lastFocusedTextField);
    }
  }
  
  /**
   * Cancel suggestions query if needed.
   */
  private void maybeCancelSuggestions() {
    if (getText() == null || getText().length() == 0) {
      cancelSuggestions();
      // restore the suggestions to be fetched
      // before GWT returns control to event browser
      // loop
      Scheduler.get().scheduleFinally(new ScheduledCommand() {

        @Override
        public void execute() {
          allowSuggestions();
        }
      });
    }
  }
  
  /**
   * Returns the widget connector.
   * @return The widget connector.
   */
  private TextFieldConnector getConnector() {
    return (TextFieldConnector) ConnectorMap.get(client).getConnector(getParent());
  }

  /**
   * Communicate text changes to server side.
   */
  /*package*/ void communicateTextValueToServer() {
    String              text;
    
    text = getText();
    if (text == null) {
      // avoid null pointer exception when comparing
      text = "";
    }
    
    if (!text.equals(lastCommunicatedValue)) {
      getConnector().sendTextToServer(text);
      lastCommunicatedValue = text;
    }
  }

  /**
   * Returns the last communicated string to server side.
   * @return the last communicated string to server side.
   */
  protected String getLastCommunicatedString() {
    return lastCommunicatedValue;
  }
  
  @Override
  public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
    return addDomHandler(handler, ContextMenuEvent.getType());
  }

  //---------------------------------------------------
  // STATIC METHODS
  //---------------------------------------------------
  
  /**
   * Sent the text value of the focused text field
   * to the server.
   * This method will force a text change event.
   */
  public static void communicateCurrentTextToServer() {
    if (focusedTextField != null) {
      // a suggestion popup may be shown
      // ==> hide it before communicating text value.
      focusedTextField.hideSuggestions();
      focusedTextField.valueChange(false);
    }
  }
  
  //---------------------------------------------------
  // AUTO COMPLETION UTILS
  //---------------------------------------------------
  
  /**
   * Returns <code>true</code> if the suggestions list is showing.
   * @return <code>true</code> if the suggestions list is showing.
   */
  /*package*/ boolean isShowingSuggestions() {
    return display != null && display.isSuggestionListShowingImpl();
  }

  /**
   * Refreshes the suggestions list.
   */
  private void refreshSuggestions() {
    // Get the raw text.
    String	text = getText();

    if (text == null || text.length() == 0) {
      hideSuggestions();
    } else if (text.equals(currentText)) {
      return;
    } else {
      currentText = text;
    }
    isAboutShowingSuggestions = true;
    showSuggestions(text);
  }

  /**
   * Set the new suggestion in the text box.
   *
   * @param curSuggestion the new suggestion
   */
  private void setNewSelection(Suggestion curSuggestion) {
    if (curSuggestion != null && !currentText.equals(curSuggestion.getReplacementString())) {
      currentText = curSuggestion.getReplacementString();
      setText(currentText);
      fireSuggestionEvent(curSuggestion);
      hideSuggestions();
    }
  }
  
  /**
   * Hides auto complete suggestions.
   */
  /*package*/ void hideSuggestions() {
    isAboutShowingSuggestions = false;
    if (display != null) {
      display.hideSuggestions();
    }
  }
  
  /**
   * Returns {@code true} if we are waiting for suggestions.
   * @return {@code true} if we are waiting for suggestions.
   */
  /*package*/ boolean isAboutShowingSuggestions() {
    return isAboutShowingSuggestions;
  }
  
  /**
   * Gives up showing suggestions
   */
  /*package*/ void cancelSuggestions() {
    getConnector().cancelSuggestionsQuery();
  }
  
  /**
   * Allows to query suggestions from server side.
   */
  /*package*/ void allowSuggestions() {
    getConnector().allowSSuggestionsQuery();
  }
  
  /**
   * Returns the suggest oracle.
   * @return The suggest oracle.
   */
  public SuggestOracle getOracle() {
    return oracle;
  }

  /**
   * Sets the suggestion oracle used to create suggestions.
   *
   * @param oracle the oracle
   */
  public void setOracle(SuggestOracle oracle) {
    this.oracle = oracle;
  }
  
  /**
   * Returns whether or not the first suggestion will be automatically selected.
   * This behavior is on by default.
   *
   * @return true if the first suggestion will be automatically selected
   */
  public boolean isAutoSelectEnabled() {
    return selectsFirstItem;
  }

  private void fireSuggestionEvent(Suggestion selectedSuggestion) {
    SelectionEvent.fire(this, selectedSuggestion);
  }
  
  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

  @Override
  protected void onEnsureDebugId(String baseID) {
    super.onEnsureDebugId(baseID);
    
    if (display != null) {
      display.onEnsureDebugId(baseID);
    }
  }

  /**
   * Shows the suggestions beginning with the given query string.
   * @param query The searched text.
   */
  /*package*/ void showSuggestions(String query) {
    if (!hasAutocomplete) {
      return;
    }
    
    if (oracle == null) {
      return;
    }
    
    if (query.length() == 0) {
      oracle.requestDefaultSuggestions(new Request(null, limit), callback);
    } else {
      oracle.requestSuggestions(new Request(query, limit), callback);
    }
  }

  /**
   * Check if the {@link DefaultSuggestionDisplay} is showing. Note that this
   * method only has a meaningful return value when the
   * {@link DefaultSuggestionDisplay} is used.
   *
   * @return true if the list of suggestions is currently showing, false if not
   * @deprecated use {@link DefaultSuggestionDisplay#isSuggestionListShowing()}
   */
  @Deprecated
  public boolean isSuggestionListShowing() {
    return display != null && display.isSuggestionListShowingImpl();
  }

  /**
   * Refreshes the current list of suggestions.
   */
  @Override
  public void refreshSuggestionList() {
    if (isAttached() && hasAutocomplete) {
      refreshSuggestions();
    }
  }

  /**
   * Show the current list of suggestions.
   */
  @Override
  public void showSuggestionList() {
    if (isAttached() && hasAutocomplete) {
      currentText = null;
      refreshSuggestions();
    }
  }
  
  /**
   * Sets the suggestion display.
   * @param display The suggestion display.
   */
  public void setDisplay(SuggestionDisplay display) {
    this.display = display;
  }

  /**
   * Sets the limit to the number of suggestions the oracle should provide. It
   * is up to the oracle to enforce this limit.
   *
   * @param limit the limit to the number of suggestions provided
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

  /**
   * Sets whether this widget is enabled.
   *
   * @param enabled <code>true</code> to enable the widget, <code>false</code> to disable it
   */
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (!enabled && display != null) {
      display.hideSuggestions();
    }
  }
  
  /**
   * Enable or disable animations in the {@link DefaultSuggestionDisplay}. Note
   * that this method is a no-op unless the {@link DefaultSuggestionDisplay} is
   * used.
   *
   * @deprecated use {@link DefaultSuggestionDisplay#setAnimationEnabled(boolean)} instead
   */
  @Deprecated
  public void setAnimationEnabled(boolean enable) {
    if (display != null) {
      display.setAnimationEnabledImpl(enable);
    }
  }

  /**
   * Turns on or off the behavior that automatically selects the first suggested
   * item. This behavior is on by default.
   *
   * @param selectsFirstItem Whether or not to automatically select the first suggestion
   */
  public void setAutoSelectEnabled(boolean selectsFirstItem) {
    this.selectsFirstItem = selectsFirstItem;
  }
  
  /**
   * Sets the auto completion feature.
   * @param hasAutocomplete the auto completion feature.
   */
  public void setHasAutocomplete(boolean hasAutocomplete) {
    this.hasAutocomplete = hasAutocomplete;
  }
  
  /**
   * Returns the parent window of this text field.
   * @return The parent window of this text field.
   */
  public VWindow getParentWindow() {
    return parent;
  }
  
  /**
   * Returns the last focused text field.
   * @return the last focused text field.
   */
  public static VInputTextField getLastFocusedTextField() {
    return lastFocusedTextField;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /*package*/ static VInputTextField 	focusedTextField;
  private static VInputTextField        lastFocusedTextField;
  /*package*/ ApplicationConnection     client;
  // used while checking if FF has set input prompt as value
  private TextValidationStrategy	validationStrategy;
  private String			currentText;
  private SuggestOracle 		oracle;
  private SuggestionDisplay 		display;
  // not really used.
  private int 				limit = 20;
  private boolean 			selectsFirstItem = true;
  private boolean			hasAutocomplete;
  //flag indication that we are waiting to show suggestions
  private boolean			isAboutShowingSuggestions;
  private String                        lastCommunicatedValue;
  private VWindow                       parent;
  
  private final Callback		callback = new Callback() {
    
    @Override
    public void onSuggestionsReady(Request request, Response response) {
      // If disabled while request was in-flight, drop it
      if (!isEnabled() || !hasAutocomplete) {
        return;
      }
      if (display != null) {
	display.setMoreSuggestions(response.hasMoreSuggestions(), response.getMoreSuggestionsCount());
	if (response instanceof org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response) {
	  display.showSuggestions(VInputTextField.this,
	                          ((org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response)response).getAutocompleteSuggestion(),
	                          oracle.isDisplayStringHTML(),
	                          isAutoSelectEnabled(),
	                          suggestionCallback);
	}
      }
    }
  };
  
  private final SuggestionCallback suggestionCallback = new SuggestionCallback() {
    
    @Override
    public void onSuggestionSelected(Suggestion suggestion) {
      if (focusedTextField != VInputTextField.this) {
        VInputTextField.this.setFocus(true);
      }
      setNewSelection(suggestion);
    }
  };
}
