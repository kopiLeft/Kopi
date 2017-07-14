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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutAction;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ShortcutActionHandler;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.DefaultSuggestOracle;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.DefaultSuggestionDisplay;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionCallback;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionDisplay;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.SuggestionHandler;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ui.Field;

/**
 * The editor field for a grid property
 */
public class VEditorTextField extends TextBoxBase
  implements Field, ChangeHandler, FocusHandler, BlurHandler, KeyDownHandler,
  HasContextMenuHandlers, KeyPressHandler, KeyUpHandler, EditorField<String>,
  SuggestionHandler, Callback, SuggestionCallback, ValueChangeHandler<String>
{
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new editor field widget
   */
  public VEditorTextField() {
    this(DOM.createInputText());
  }
  
  /**
   * Creates a new editor field widget
   * @param node The DOM element node
   */
  public VEditorTextField(Element node) {
    super(node);
    setStyleName("editor-field");
    actionHandler = new ShortcutActionHandler();
    createNavigationActions();
    addKeyPressHandler(this);
    addFocusHandler(this);
    addBlurHandler(this);
    addChangeHandler(this);
    addValueChangeHandler(this);
    addKeyDownHandler(this);
    addKeyUpHandler(this);
    addContextMenuHandler(new ContextMenuHandler() {
      
      @Override
      public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault(); // don't show context menu on fields.
      }
    });
    getInputElement().setAttribute("autocomplete", "on");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onBrowserEvent(Event event) {
    if (event.getTypeInt() == Event.ONPASTE) {
      // should validate text content
      final String      before = getText();

      Scheduler.get().scheduleDeferred(new Command() {

        @Override
        public void execute() {
          if (check(getText())) {
            setText(before);
          }
        }
      });
    }

    super.onBrowserEvent(event);
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    // allow special keys    
    if (event.isAnyModifierKeyDown() || isSpecialKey(event)) {
      return;
    }
    // if the content if the input is selected, validate only the typed character.
    // this is typically used for enumeration fields to allow the content to be overwritten
    if (getSelectedText() != null && getSelectedText().equals(getText()) && isEnum()) {
      if (!check(String.valueOf(event.getCharCode()))) {
        cancelKey();
      }
      
      return;
    }
    // if the typed text is not valid according
    // to the field specification, cancels the input
    if (!check(getText() + event.getCharCode())) {
      cancelKey();
    } else {
      changed = true;
    }
  }
  
  @Override
  public void onKeyUp(KeyUpEvent event) {
    // After every user key input, refresh the popup's suggestions.
    if (autocompleteLength > 0) {
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
      case KeyCodes.KEY_F1:
      case KeyCodes.KEY_F2:
      case KeyCodes.KEY_F3:
      case KeyCodes.KEY_F4:
      case KeyCodes.KEY_F5:
      case KeyCodes.KEY_F6:
      case KeyCodes.KEY_F7:
      case KeyCodes.KEY_F8:
      case KeyCodes.KEY_F9:
      case KeyCodes.KEY_F10:
      case KeyCodes.KEY_F11:
      case KeyCodes.KEY_F12:
        break;
      default :
        refreshSuggestions();
        break;
      }
    }
  }
  
  /**
   * Returns true of the pressed key is a special key for the field and should not be cancelled.
   * @param event The key press event.
   * @return true if the key press event presents a special key for the field.
   */
  protected boolean isSpecialKey(KeyPressEvent event) {
    return String.valueOf(event.getCharCode()).trim().length() == 0;
  }
  
  /**
   * Returns true if it is an enumeration field.
   * @return True if it is an enumeration field.
   */
  protected boolean isEnum() {
    return false;
  }
  
  /**
   * Returns true if this field is a numeric one and does not allow blanks.
   * @return True if this field is a numeric one and does not allow blanks.
   */
  protected boolean isNumeric() {
    return false;
  }
  
  /**
   * Returns true if it is a multi line editor field.
   * @return True if it is a multi line editor field.
   */
  protected boolean isMultiLine() {
    return false;
  }
  
  /**
   * Sets this field to be an auto fill field
   */
  public void setAutofill() {
    autofillIcon = new VIcon();
    autofillIcon.setName("sort-desc");
    autofillIcon.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        getConnector().autofill();
      }
    });
  }
  
  /**
   * Sets the auto complete length for this field
   * @param length The auto complete length.
   */
  public void setAutocompleteLength(int length) {
    autocompleteLength = length;
  }
  
  @Override
  public void onKeyDown(KeyDownEvent event) {
    if (suggestionDisplay != null && suggestionDisplay.isSuggestionListShowingImpl()) {
      handleAutocompleteAction(event);
    } else {
      // clear text on blank
      maybeClearText(event);
      // forbid blanks for numeric fields
      if (isNumeric() && event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
        cancelKey();
      }

      if (BrowserInfo.get().isIE() && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        // IE does not send change events when pressing enter in a text
        // input so we handle it using a key listener instead
        valueChanged(false);
      }
      actionHandler.handleAction(event);
    }
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    if (autofillIcon != null) {
      new Timer() {

        @Override
        public void run() {
          installSuggestionsProcessor();
          VMainWindow.get().add(autofillIcon);
          DOM.appendChild(DOM.getParent(getElement()), autofillIcon.getElement());
        }
      }.schedule(50);
    }
  }
  
  /**
   * Handles an auto complete action from the given key down event.
   * @param event
   */
  private void handleAutocompleteAction(KeyDownEvent event) {
    // stop the propagation to parent elements when
    // the suggestions display is showing.
    event.stopPropagation();
    event.preventDefault();
    switch (event.getNativeKeyCode()) {
    case KeyCodes.KEY_DOWN:
      suggestionDisplay.moveSelectionDown();
      break;
    case KeyCodes.KEY_UP:
      suggestionDisplay.moveSelectionUp();
      break;
    case KeyCodes.KEY_ESCAPE:
      suggestionDisplay.hideSuggestions();
      break;
    case KeyCodes.KEY_ENTER:
    case KeyCodes.KEY_TAB:
      Suggestion     suggestion = suggestionDisplay.getCurrentSelection();
      
      if (suggestion == null) {
        suggestionDisplay.hideSuggestions();
      } else {
        onSuggestionSelected(suggestion);
      }
      break;
    }
  }
  
  /**
   * Tries to clear text content if conditions are satisfied.
   * @param event The key down event
   */
  protected void maybeClearText(KeyDownEvent event) {
    if (clearTextOnBlank() && isAllTextSelected()) {
      if (event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
        // call super to not pass by field validation
        super.setText("");
        cancelKey();
      }
    } else if (isReadOnly() && event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
      // prevent page scroll
      event.preventDefault();
    }
  }
  
  /**
   * Returns true if the selected text is cleared when blank is pressed.
   * @return true if the selected text is cleared when blank is pressed.
   */
  protected boolean clearTextOnBlank() {
    return !isReadOnly();
  }
  
  /**
   * Returns true if the hole typed text is selected for this field.
   * @return True if the hole typed text is selected for this field.
   */
  protected boolean isAllTextSelected() {
    return getSelectedText() != null && getSelectedText().equals(getText());
  }

  @Override
  public void onBlur(BlurEvent event) {
    layzyHideSuggestions();
    valueChanged(true);
  }

  @Override
  public void onFocus(FocusEvent event) {
    lastFocusedEditor = this;
    selectAll();
    getWindow().setLastFocusedTextBox(this);
    changed = false;
    lastCommunicatedValue = null;
  }

  @Override
  public void onChange(ChangeEvent event) {
    changed = true;
    valueChanged(false);
  }
  
  @Override
  public void onValueChange(ValueChangeEvent<String> event) {
    changed = true;
    valueChanged(false);
  }
  
  @Override
  public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
    return addDomHandler(handler, ContextMenuEvent.getType());
  }
  
  @Override
  public void setText(String text) {
    if (check(text)) {
      super.setText(text);
      valueBeforeEdit = text;
      changed = true;
    }
  }
  
  @Override
  public void setBlink(boolean blink) {
    if (blink) {
      addStyleDependentName("blink");
    } else {
      removeStyleDependentName("blink");
    }
  }
  
  @Override
  public void validate() throws InvalidEditorFieldException {
    // to be overridden when needed
  }
  
  @Override
  public void focus() {
    super.setFocus(true);
  }

  @Override
  public ApplicationConnection getConnection() {
    return connection;
  }
  
  @Override
  public void setReadOnly(boolean readOnly) {
    boolean     wasReadOnly = isReadOnly();

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
  
  @Override
  public void setColor(String foreground, String background) {
    if (foreground != null && foreground.length() > 0) {
      getElement().getStyle().setColor(foreground);
    } else {
      getElement().getStyle().setColor("inherit");
    }
    if (background != null && background.length() > 0) {
      getElement().getStyle().setBackgroundColor(background);
    } else {
      getElement().getStyle().setBackgroundColor("inherit");
    }
  }
  
  /**
   * Called when the field value might have changed and/or the field was
   * blurred.
   * @param blurred true if the field was blurred
   */
  public void valueChanged(boolean blurred) {
    if (isChanged() && !getText().equals(lastCommunicatedValue)) {
      EditorTextFieldConnector          connector;
      
      connector = getConnector();
      if (connector != null) {
        getConnector().sendTextToServer(getText());
        valueBeforeEdit = getText();
        lastCommunicatedValue = getText();
      }
    }
  }
  
  /**
   * Returns true if the content of the field has been changed.
   * @return True if the content of the field has been changed.
   */
  protected boolean isChanged() {
    if (changed) {
      return true;
    } else {
      String              newText;

      newText = getText();
      if (newText == null) {
        newText = "";
      }
      if (valueBeforeEdit == null) {
        valueBeforeEdit = "";
      }

      return !newText.equals(valueBeforeEdit);
    }
  }
  
  /**
   * Validates the given text according to the field type.
   * @param text The text to be validated
   * @return true if the text is valid.
   */
  protected boolean check(String text) {
    return true;
  }
  
  /**
   * Returns the connector attached with this text field editor.
   * @return The connector attached with this text field editor.
   */
  public EditorTextFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorTextFieldConnector.class);
  }
  
  /**
   * Returns the field max length.
   * @return The field max length.
   */
  protected int getMaxLength() {
    return getInputElement().getMaxLength();
  }

  /**
   * Sets the text zone max length.
   * @param maxLength The max length.
   */
  protected void setMaxLength(int maxLength) {
    updateMaxLength(maxLength);
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
   * Returns the input element.
   * @return The input element.
   */
  protected InputElement getInputElement() {
    return getElement().cast();
  }
  
  /**
   * Returns the window that contains this editor field.
   * @return The editor window.
   */
  public VWindow getWindow() {
    if (window == null) {
      window = WidgetUtils.getParent(this, VWindow.class);
    }

    return window;
  }
  
  /**
   * Returns the last focused editor.
   * @return The last focused editor.
   */
  public static VEditorTextField getLastFocusedEditor() {
    return lastFocusedEditor;
  }

  //---------------------------------------------------
  // SUGGESTIONS
  //---------------------------------------------------
  
  @Override
  public void refreshSuggestionList() {
    if (isAttached() && !isReadOnly()) {
      refreshSuggestions();
    }
  }

  @Override
  public void showSuggestionList() {
    if (isAttached() && !isReadOnly()) {
      currentText = null;
      refreshSuggestions();
    }
  }
  
  @Override
  public void onSuggestionsReady(Request request, Response response) {
    if (!isReadOnly() && suggestionDisplay != null && suggestOracle != null) {
      suggestionDisplay.setMoreSuggestions(response.hasMoreSuggestions(), response.getMoreSuggestionsCount());
      if (response instanceof org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response) {
        suggestionDisplay.showSuggestions(this,
                                         ((org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response)response).getAutocompleteSuggestion(),
                                         suggestOracle.isDisplayStringHTML(),
                                         true,
                                         this);
      }
    }
  }
  
  @Override
  public void onSuggestionSelected(Suggestion suggestion) {
    if (suggestion != null && !currentText.equals(suggestion.getReplacementString())) {
      currentText = suggestion.getReplacementString();
      setText(currentText);
      hideSuggestions();
      valueChanged(false);
      getConnector().gotoNextField();
    }
  }
  
  /**
   * Installs the suggestion processors on this editor.
   */
  protected void installSuggestionsProcessor() {
    if (autocompleteLength > 0) {
      suggestOracle = new DefaultSuggestOracle(this);
      ((DefaultSuggestOracle)suggestOracle).setMinimumQueryCharacters(autocompleteLength);
      ((DefaultSuggestOracle)suggestOracle).setQueryListener(getConnector());
      suggestionDisplay = new DefaultSuggestionDisplay(getConnection());
    }
  }
  

  /**
   * Refreshes the suggestions list.
   */
  protected void refreshSuggestions() {
    // Get the raw text.
    String      text = getText();
    
    if (text == null || text.length() == 0 || text.length() == getMaxLength()) {
      hideSuggestions();
    } else {
      currentText = text;
    }
    showSuggestions(text);
  }
  

  /**
   * Shows the suggestions beginning with the given query string.
   * @param query The searched text.
   */
  protected void showSuggestions(String query) {
    if (isReadOnly() || suggestOracle == null) {
      return;
    }
    
    if (query.length() == 0) {
      suggestOracle.requestDefaultSuggestions(new Request(null, QUERY_LIMIT), this);
    } else {
      suggestOracle.requestSuggestions(new Request(query, QUERY_LIMIT), this);
    }
  }
  
  /**
   * Delayed Hide of the suggestions display.
   * This is needed when the suggestions display
   * is closed after a suggestion choice by a click
   * on it. We should delay the close of the popup to
   * ensure click event sending and thus choose the selected value.
   */
  protected void layzyHideSuggestions() {
    new Timer() {
      
      @Override
      public void run() {
        hideSuggestions();
      }
    }.schedule(200);
  }
  
  /**
   * Hides auto complete suggestions.
   */
  protected void hideSuggestions() {
    if (suggestionDisplay != null && suggestionDisplay.isSuggestionListShowingImpl()) {
      suggestionDisplay.hideSuggestions();
    }
  }
  
  /**
   * Sets the list of suggestions to the suggestions oracle handled by this editor.
   * @param suggestions The list of suggestions
   */
  protected void setSuggestions(List<AutocompleteSuggestion> suggestions) {
    if (suggestOracle != null && suggestOracle instanceof DefaultSuggestOracle) {
      ((DefaultSuggestOracle)suggestOracle).setSuggestions(suggestions);
    }
  }

  //---------------------------------------------------
  // NAVIGATION
  //---------------------------------------------------
  
  /**
   * Creates the navigation actions.
   * @param connector The text field connector.
   */
  protected void createNavigationActions() {
    addNavigationAction(NavigationAction.KEY_EMPTY_FIELD, KeyCodes.KEY_ENTER, KeyCodes.KEY_CTRL);
    addNavigationAction(NavigationAction.KEY_NEXT_BLOCK, KeyCodes.KEY_ENTER, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_DIAMETER, KeyCodes.KEY_D, KeyCodes.KEY_CTRL);
    addNavigationAction(NavigationAction.KEY_REC_DOWN, KeyCodes.KEY_PAGEDOWN, 0);
    addNavigationAction(NavigationAction.KEY_REC_DOWN, KeyCodes.KEY_PAGEDOWN, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_REC_FIRST, KeyCodes.KEY_HOME, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_REC_LAST, KeyCodes.KEY_END, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_REC_UP, KeyCodes.KEY_PAGEUP, 0);
    addNavigationAction(NavigationAction.KEY_REC_UP, KeyCodes.KEY_PAGEUP, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_PREV_FIELD, KeyCodes.KEY_LEFT, KeyCodes.KEY_CTRL);
    addNavigationAction(NavigationAction.KEY_PREV_FIELD, KeyCodes.KEY_TAB, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_PREV_FIELD, KeyCodes.KEY_UP, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_NEXT_FIELD, KeyCodes.KEY_RIGHT, KeyCodes.KEY_CTRL);
    addNavigationAction(NavigationAction.KEY_NEXT_FIELD, KeyCodes.KEY_TAB, 0);
    addNavigationAction(NavigationAction.KEY_NEXT_FIELD, KeyCodes.KEY_DOWN, KeyCodes.KEY_SHIFT);
    addNavigationAction(NavigationAction.KEY_PRINTFORM, KeyCodes.KEY_PRINT_SCREEN, KeyCodes.KEY_SHIFT);
    // the magnet card reader sends a CNTR-J as last character
    addNavigationAction(NavigationAction.KEY_NEXT_FIELD, KeyCodes.KEY_J, KeyCodes.KEY_CTRL);
    addNavigationAction(NavigationAction.KEY_ESCAPE, KeyCodes.KEY_ESCAPE, 0);
    addNavigationAction(NavigationAction.KEY_NEXT_VAL, KeyCodes.KEY_DOWN, KeyCodes.KEY_CTRL);
    addNavigationAction(NavigationAction.KEY_PREV_VAL, KeyCodes.KEY_UP, KeyCodes.KEY_CTRL);
    if (!isMultiLine()) {
      // In multiline fields these keys are used for other stuff
      addNavigationAction(NavigationAction.KEY_PREV_FIELD, KeyCodes.KEY_UP, 0);
      addNavigationAction(NavigationAction.KEY_NEXT_FIELD, KeyCodes.KEY_DOWN, 0);
      addNavigationAction(NavigationAction.KEY_NEXT_FIELD, KeyCodes.KEY_ENTER, 0);
    }
  }
  
  /**
   * Adds the  navigation action to the actions handler.
   * @param connector The input text connector.
   * @param code The navigator code.
   * @param key The key code.
   * @param modifiers The modifiers.
   */
  protected void addNavigationAction(int code, int key, int... modifiers) {
    addAction(new NavigationAction(code, key, modifiers));
  }
  
  /**
   * Adds the given shortcut action to this editor.
   * @param action The shortcut action.
   */
  protected void addAction(ShortcutAction action) {
    actionHandler.addAction(action);
  }

  //---------------------------------------------------
  // NAVIGATION ACTION
  //---------------------------------------------------
  
  /**
   * A navigation action
   */
  protected class NavigationAction extends ShortcutAction {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    public NavigationAction(int code, int key, int[] modifiers) {
      super("navigation-" + code, key, modifiers);
      this.code = code;
    }

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    
    @Override
    public void performAction() {
      // block any navigation request if suggestions is showing
      if (suggestionDisplay != null && suggestionDisplay.isSuggestionListShowingImpl()) {
        return;
      }
      // first sends the text value to server side if changed
      valueChanged(false);
      switch (code) {
      case KEY_NEXT_FIELD:
        getConnector().gotoNextField();
        break;
      case KEY_PREV_FIELD:
        getConnector().gotoPrevField();
        break;
      case KEY_NEXT_BLOCK:
        getConnector().gotoNextBlock();
        break;
      case KEY_REC_UP:
        // done with editor handler
        //getConnector().gotoPrevRecord();
        break;
      case KEY_REC_DOWN:
        // done with editor handler
        //getConnector().gotoNextRecord();
        break;
      case KEY_REC_FIRST:
        getConnector().gotoFirstRecord();
        break;
      case KEY_REC_LAST:
        getConnector().gotoLastRecord();
        break;
      case KEY_EMPTY_FIELD:
        getConnector().gotoNextEmptyMustfill();
        break;
      case KEY_DIAMETER:
        StringBuffer    text;

        text = new StringBuffer(getText());
        text.insert(getCursorPos(), "\u00D8");
        setText(text.toString());
        break;
      case KEY_ESCAPE:
        //!!! removed and let the actor quit form operate.
        //!! we do not really need it.
        //connector.getServerRpc().closeWindow();
        break;
      case KEY_PRINTFORM:
        
        break;
      case KEY_PREV_VAL:
        
        break;
      case KEY_NEXT_VAL:
        
        break;
      default:
        // nothing to do by default.
      }
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    private final int                   code;
    
    // navigation constants.
    public static final int             KEY_NEXT_FIELD          =  0;
    public static final int             KEY_PREV_FIELD          =  1;
    public static final int             KEY_REC_UP              =  2;
    public static final int             KEY_REC_DOWN            =  3;
    public static final int             KEY_REC_FIRST           =  4;
    public static final int             KEY_REC_LAST            =  5;
    public static final int             KEY_EMPTY_FIELD         =  6;
    public static final int             KEY_NEXT_BLOCK          =  7;
    public static final int             KEY_PREV_VAL            =  8;
    public static final int             KEY_NEXT_VAL            =  9;
    public static final int             KEY_DIAMETER            = 10;
    public static final int             KEY_ESCAPE              = 11;
    public static final int             KEY_PRINTFORM           = 12;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static VEditorTextField               lastFocusedEditor;
  /*package*/ ApplicationConnection             connection;
  private String                                valueBeforeEdit;
  private String                                lastCommunicatedValue;
  private final ShortcutActionHandler           actionHandler;
  private VWindow                               window;
  private boolean                               changed;
  private VIcon                                 autofillIcon;
  private SuggestOracle                         suggestOracle;
  private SuggestionDisplay                     suggestionDisplay;
  private String                                currentText;
  private int                                   autocompleteLength;
  private static final int                      QUERY_LIMIT = 20;
}
