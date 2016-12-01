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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.DecimalFormatSymbols;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextFieldState.ConvertType;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.DefaultSuggestionDisplay;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionCallback;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionDisplay;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionHandler;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.WindowConnector;

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
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;

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
    // block any key when a suggestions query is launched.
    if (getConnector().isQueryingForSuggestions()) {
      cancelKey();
      return;
    }
    
    if (event.isControlKeyDown() || event.isAltKeyDown() || event.isMetaKeyDown()
	|| (String.valueOf(event.getCharCode()).trim().length() == 0
	&& event.getNativeEvent().getKeyCode() != KeyCodes.KEY_SPACE))
    {
      return;
    }
    
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_SPACE
        && getSelectedText() != null
        && getSelectedText().equals(getText()))
    {
      setText(null); // clear text
      cancelKey();
      return;
    }
    
    // if the content if the input is selected, validate only the typed character.
    // this is typically used for enumeration fields to allow the content to be overwritten
    if (getSelectedText() != null
        && getSelectedText().equals(getText())
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
	    } else {
	      // even if it is not really correct, we mark the field as dirty after
	      // a paste event. The action is delayed until the user releases the shortcut keys.
	      new Timer() {

	        @Override
	        public void run() {
	          getFieldConnector().setChanged(true);
	          getFieldConnector().markAsDirty(getRecord());
	        }
	      }.schedule(100); // enough time to release paste shortcut keys
	    }
	  }
	});
      }
    }

    super.onBrowserEvent(event);
  }
  
  @Override
  public void setText(String text) {
    // set record to synchronize view and model even field is not focused
    setRecord();
    // set only valid inputs
    if (validationStrategy instanceof NoeditValidationStrategy
        || validationStrategy.validate(text, getMaxLength()))
    {
      if (text == null) {
        text = ""; // avoid NullPointerException
      }
      if (!text.equals(getText())) {
        getFieldConnector().setChanged(true);
      }
      super.setText(text);
    }
    if (text != null) {
      valueBeforeEdit = text;
    }
  }
  
  /**
   * Sets the input text foreground and background colors.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  public void setColor(String foreground, String background) {
    String              style;
    
    // clear server color styles if necessary
    if ((foreground == null || foreground.length() == 0)
        && (background == null || background.length() == 0))
    {
      clearServerStyles();
    }
    style = "text-align : " + this.align + ";";
    if (foreground != null && foreground.length() > 0) {
      // set color directly on element style
      style += "color : " + foreground + " !important;";
    }
    
    if (background != null && background.length() > 0) {
      // set color directly on element style
      style += "background-color : " + background + " !important;";
    }
    
    getElement().setAttribute("style", style);
  }
  
  /**
   * Removes the color styles generated by the server
   */
  protected void clearServerStyles() {
    for (String style : getConnector().getWidget().getStyleName().split("\\s")) {
      if (style.endsWith("-" + getFieldConnector().getPosition())) {
        getConnector().getWidget().removeStyleName(style);
      }
    }
  }
  
  @Override
  public void setAlignment(TextAlignment align) {
    super.setAlignment(align);
    this.align = align.toString().toLowerCase();
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
    super.setText(text);
    getFieldConnector().unsetDirty();
  }
  
  /**
   * Sets the record number from the display line
   */
  protected void setRecord() {
    int                 position;

    position = getFieldConnector().getPosition();
    recordNumber = getFieldConnector().getColumnView().getRecordFromDisplayLine(position);
  }
  
  /**
   * Returns the record number of this input widget.
   * @return The record number of this input widget.
   */
  protected int getRecord() {
    if (recordNumber == -1) {
      // get the record from the display line when it is not set
      setRecord();
    }
    
    return recordNumber;
  }

  /**
   * Called when the field value might have changed and/or the field was
   * blurred. These are combined so the blur event is sent in the same batch
   * as a possible value change event (these are often connected).
   * 
   * @param blurred true if the field was blurred
   */
  public void valueChange(boolean blurred) {
    // if field is not changed give up
    // this can happen when the dirty values
    // are sent before blurring this field
    if (!getFieldConnector().isChanged()) {
      return;
    }
    
    int         rec;
    
    rec = getRecord();
    // mark the field as dirty only when really changed
    if (!getText().equals(getFieldConnector().getCachedValueAt(rec))) {
      getFieldConnector().markAsDirty(rec, getText());
    }
    // field is left and a showing suggestions are
    // displayed ==> restore the old value of the field
    if (blurred) {
      maybeRestoreOldValue();
      if (getFieldConnector().isDirty()) {
        maybeSynchronizeWithServerSide();
      }
    } else {      
      handleEnumerationFields(rec);
      maybeCheckValue(rec);
    }
  }
  
  /**
   * Restores the old value of the field when it is needed.
   * This can happen when the field is blurred and the suggestions
   * popup is showing.
   */
  protected void maybeRestoreOldValue() {
    if (isShowingSuggestions()) {
      setText(getFieldConnector().getColumnView().getValueAt(getFieldConnector().getPosition()));
    }
  }
  
  /**
   * When the field is blurred and the navigation of this
   * field is delegated to server side, the state of this component
   * should be synchronized with the server side to have all necessary
   * triggers executed
   */
  protected void maybeSynchronizeWithServerSide() {
    if (delegateNavigationToServer()
        && getConnector().needsSynchronization()
        && !isAlreadySynchronized())
    {
      sendDirtyValuesToServerSide();
    }
  }
  
  /**
   * Checks the value of this input field when it is needed.
   * @param rec The record number of the field.
   */
  protected void maybeCheckValue(final int rec) {
    Timer               lazyValueChecker;
    
    // The value check must be delayed to allow selected
    // item capture from the suggestion display if it is showing.
    // Otherwise, it can cause strange behavior.
    lazyValueChecker = new Timer() {
      
      @Override
      public void run() {
        if (!isCheckingValue && !delegateNavigationToServer()) {
          try {
            checkValue(rec);
          } catch (CheckTypeException e) {
            e.displayError();
          }
        }
      }
    };
    
    lazyValueChecker.schedule(60); //!!! experimental
  }
  
  /**
   * Special handling for enumeration fields.
   * @param rec The field record number
   */
  protected void handleEnumerationFields(int rec) {
    if (!isShowingSuggestions() && validationStrategy instanceof EnumValidationStrategy) {
      getFieldConnector().markAsDirty(rec, getText());
    }
  }
  
  /**
   * Sends all dirty values to the server side.
   * This will send all pending dirty values to be sure
   * that all necessary values are sent to the server model.
   */
  protected void sendDirtyValuesToServerSide() {
    WindowConnector             window;
    BlockConnector              block;
    
    window = ConnectorUtils.getParent(getConnector(), WindowConnector.class);
    block = ConnectorUtils.getParent(getConnector(), BlockConnector.class);
    if (block != null) {
      window.cleanDirtyValues(block);
    }
    // now the field is synchronized with server side.
    setAlreadySynchronized(true);
  }
  
  /**
   * Should the navigation be delegated to server side ?
   * @return {@code true} if the navigation is delegated to serevr side.
   */
  protected boolean delegateNavigationToServer() {
    return getFieldConnector().delegateNavigationToServer();
  }

  /**
   * Returns {@code true} if word wrap is used.
   * @return {@code true} if word wrap is used.
   */
  protected boolean isWordwrap() {
    String wrap = getElement().getAttribute("wrap");
    return !"off".equals(wrap);
  }
  
  /**
   * Sets this field to be checking it value.
   * @param isCheckingValue Are we checking the field value ?
   */
  public void setCheckingValue(boolean isCheckingValue) {
    this.isCheckingValue = isCheckingValue;
  }
  
  /**
   * Sets this field to be already synchronized with the server side.
   * @param isAlreadySynchronized The synchrinization state.
   */
  public void setAlreadySynchronized(boolean isAlreadySynchronized) {
    this.isAlreadySynchronized = isAlreadySynchronized;
  }
  
  /**
   * Returns {@code true} if the state of this field is not synchronized with server side.
   * @return {@code true} if the state of this field is not synchronized with server side.
   */
  public boolean isAlreadySynchronized() {
    return isAlreadySynchronized;
  }
  
  @Override
  public void onKeyUp(KeyUpEvent event) {
    // After every user key input, refresh the popup's suggestions.
    if (hasAutocomplete && !getConnector().isQueryingForSuggestions()) {
      switch (event.getNativeKeyCode()) {
      case KeyCodes.KEY_DOWN:
      case KeyCodes.KEY_PAGEDOWN:
      case KeyCodes.KEY_UP:
      case KeyCodes.KEY_PAGEUP:
      case KeyCodes.KEY_LEFT:
      case KeyCodes.KEY_RIGHT:
      case KeyCodes.KEY_ENTER:
      case KeyCodes.KEY_TAB:
      case KeyCodes.KEY_SHIFT:
      case KeyCodes.KEY_CTRL:
      case KeyCodes.KEY_ALT:
      case KeyCodes.KEY_ESCAPE:
	break;
      default :
        refreshSuggestions();
	break;
      }
    }
    // look if the decimal separator must be changed.
    maybeReplaceDecimalSeparator();
    // check if the field has really changed.
    if (isChanged()) {
      getFieldConnector().setChanged(true);
    }
    valueBeforeEdit = getText();
  }
  
  /**
   * Returns {@code true} when the field context is changed. 
   * @return {@code true} when the field context is changed.
   */
  protected boolean isChanged() {
    if (validationStrategy instanceof StringValidationStrategy) {
      StringValidationStrategy          strategy = (StringValidationStrategy) validationStrategy;
      
      // look to the lower and upper convert type to detect if the field value has really changed
      if (strategy.getConvertType() == ConvertType.UPPER) {
        return !getText().toUpperCase().equals(valueBeforeEdit.toUpperCase());
      } else if (strategy.getConvertType() == ConvertType.LOWER) {
        return !getText().toLowerCase().equals(valueBeforeEdit.toLowerCase());
      } else {
        return !getText().equals(valueBeforeEdit);
      }
    } else {
      return !getText().equals(valueBeforeEdit);
    }
  }
  
  /**
   * Checks if the decimal separator must be changed.
   */
  protected void maybeReplaceDecimalSeparator() {
    if (validationStrategy instanceof FixnumValidationStrategy && getText().contains(".")) {
      DecimalFormatSymbols      dfs = DecimalFormatSymbols.get(VMainWindow.getLocale());
      
      if (dfs.getDecimalSeparator() != '.') {
        super.setText(getText().replace('.', dfs.getDecimalSeparator()));
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
    if (hasAutocomplete && !getConnector().isQueryingForSuggestions()) {
      if (isShowingSuggestions()) {
        // stop the propagation to parent elements when
        // the suggestions display is showing.
        event.stopPropagation();
      }
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
	Suggestion     suggestion = display.getCurrentSelection();
	
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
  
  /**
   * Fires a goto next field event.
   */
  public void gotoNextBlockTextInput() {
    // first send dirty values to server side.
    sendDirtyValuesToServerSide();
    getFieldConnector().getColumnView().gotoNextField();
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
    lazyHideSuggestions();
    recordNumber = -1; // set this field is not related to any record
  }

  @Override
  public void onFocus(FocusEvent event) {
    if (focusedTextField == this) {
      // already got the focus. give up
      return;
    }
    focusedTextField = this;
    setRecord();
    getFieldConnector().getColumnView().disableAllBlocksActors();
    addStyleDependentName("focus");
    setLastFocusedInput();
    // cancel the fetch of suggestions list on field focus
    // when the field is not empty
    maybeCancelSuggestions();
    getFieldConnector().getColumnView().setAsActiveField(-1);
    getFieldConnector().setChanged(false);
    setAlreadySynchronized(false);
    valueBeforeEdit = getText();
    // activate all actors related to this field.
    getFieldConnector().setActorsEnabled(true);
    // ensure the selection of the field content.
    maybeSelectAll();
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
  protected TextFieldConnector getConnector() {
    return ConnectorUtils.getConnector(client, getParent(), TextFieldConnector.class);
  }
  
  /**
   * Checks the value of this text field.
   * @param rec The active record.
   * @throws CheckTypeException When field content is not valid
   */
  /*package*/ void checkValue(int rec) throws CheckTypeException {
    isCheckingValue = true; //!!! don't check twice on field blur
    if (validationStrategy != null) {
      validationStrategy.checkType(this, getText() == null ? "" : getText().trim());
      if (!getText().equals(getFieldConnector().getCachedValueAt(rec))) {
        getConnector().markAsDirty(rec, getText());
      }
    }
    isCheckingValue = false;
  }
  
  @Override
  public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
    return addDomHandler(handler, ContextMenuEvent.getType());
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
    if (text == null || text.length() == 0 || text.length() == getMaxLength()) {
      hideSuggestions();
    } else {
      currentText = text;
    }
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
      gotoNextBlockTextInput();
    }
  }
  
  /**
   * Hides auto complete suggestions.
   */
  /*package*/ void hideSuggestions() {
    if (display != null && display.isSuggestionListShowingImpl()) {
      display.hideSuggestions();
    }
  }
  
  /**
   * Hides auto complete suggestions with a delay.
   */
  /*package*/ void lazyHideSuggestions() {
    if (display.isSuggestionListShowingImpl()) {
      lazySuggestionsHider.schedule(200);
    }
  }
  
  /**
   * Returns {@code true} if we are waiting for suggestions.
   * @return {@code true} if we are waiting for suggestions.
   */
  /*package*/ boolean isAboutShowingSuggestions() {
    return display != null && display.isAboutShowingSuggestions();
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
    getConnector().allowSuggestionsQuery();
  }
  
  /**
   * Returns the parent field connector.
   * @return The parent field connector.
   */
  protected FieldConnector getFieldConnector() {
    return ConnectorUtils.getParent(getConnector(), FieldConnector.class);
  }
  
  /**
   * Checks if the content of this field is empty.
   * @return {@code true} if this field is empty.
   */
  /*package*/ boolean isNull() {
    return getText() == null || "".equals(getText());
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

  /**
   * Fires a suggestion selection event.
   * @param selectedSuggestion The selected suggestion.
   */
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
   * Sets the suggestion display modality.
   * @param modal The display modality.
   */
  public void setDisplayModality(boolean modal) {
    if (display != null && display instanceof DefaultSuggestionDisplay) {
      ((DefaultSuggestionDisplay)display).setModal(modal);
    }
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
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (!enabled && display != null) {
      display.hideSuggestions();
    }
  }
  
  /**
   * Releases the content of this input field.
   */
  public void release() {
    client = null;
    validationStrategy = null;
    currentText = null;
    oracle = null;
    display = null;
    valueBeforeEdit = null;
    align = null;
    parent = null;
    lazySuggestionsHider = null;
    callback = null;
    suggestionCallback = null;
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
  
  /**
   * Returns the last focused field connector.
   * @return the last focused text connector.
   */
  public static FieldConnector getLastFocusedConnector() {
    if (lastFocusedTextField != null) {
      return lastFocusedTextField.getFieldConnector();
    } else {
      return null;
    }
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
  private String                        valueBeforeEdit = "";
  private String                        align;
  private VWindow                       parent;
  private boolean                       isCheckingValue;
  private boolean                       isAlreadySynchronized;
  private int                           recordNumber = -1; // The record number corresponding to this text input
  private Timer                         lazySuggestionsHider = new Timer() {
    
    @Override
    public void run() {
      hideSuggestions();
    }
  };
  
  private Callback                      callback = new Callback() {
    
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
  
  private SuggestionCallback            suggestionCallback = new SuggestionCallback() {
    
    @Override
    public void onSuggestionSelected(Suggestion suggestion) {
      setNewSelection(suggestion);
    }
  };
}
