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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.form;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputLabel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.SimpleFocusablePanel;

/**
 * A Position requester to select a record number.
 */
public class VPosRequester extends SimpleFocusablePanel {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VPosRequester</code> widget.
   * @param first The first record number.
   * @param last The last record number.
   */
  public VPosRequester(int first, int last) {
    table = new FlexTable();
    setStyleName("k-posreq");
    table.setStyleName("k-posreq-table");
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    this.first = first;
    this.last = last;
    input = new VIntegerTextInput();
    ok = new VInputButton();
    cancel = new VInputButton();
    add(table);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * The widget initialization.
   * @param connection The application connection.
   * @param locale The application locale.
   */
  public void initWidget(ApplicationConnection connection, String locale) {
    initIcon(connection);
    initInput(locale);
    initButtons(locale);
    createTitle(locale);
  }
  
  /**
   * Shows the dialog
   * @param connection The application connection.
   */
  public void show(ApplicationConnection connection, final VPositionPanel parent) {
    final VPopup		dialog;
    
    dialog = new VPopup(connection, false, true);
    dialog.setWidget(this);
    parent.add(dialog);
    ok.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	dialog.hide();
	parent.fireGotoPosition(getPosition());
	dialog.clear();
	dialog.removeFromParent();
      }
    });
    cancel.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	dialog.hide();
	dialog.clear();
	dialog.removeFromParent();
      }
    });
    // try to put the popup on to of the position panel.
    dialog.setPopupPositionAndShow(new PositionCallback() {
      
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
	position(dialog, parent, offsetWidth, offsetHeight);
      }
    });
    focus();
  }

  /**
   * Positions the popup, called after the offset width and height of the popup
   * are known.
   *
   * @param relativeObject the ui object to position relative to
   * @param offsetWidth the drop down's offset width
   * @param offsetHeight the drop down's offset height
   */
  protected void position(final VPopup dialog, final UIObject relativeObject, int offsetWidth, int offsetHeight) {
    int 		left;
    int 		top;
    int 		textBoxOffsetWidth;
    int			offsetWidthDiff;
    
    textBoxOffsetWidth = relativeObject.getOffsetWidth();
    // Compute the difference between the popup's width and the
    // textbox's width
    offsetWidthDiff = offsetWidth - textBoxOffsetWidth;
    // Left-align the popup.
    left = relativeObject.getAbsoluteLeft() - offsetWidthDiff - 25;
    // Calculate top position for the popup
    top = relativeObject.getAbsoluteTop() - offsetHeight - 15;
    dialog.setPopupPosition(left, top);
  }
  
  /**
   * The icon initialization.
   * @param connection The application connection.
   */
  protected void initIcon(ApplicationConnection connection) {
    VImage		image;
    
    image = new VImage();
    image.setStyleName("k-posreq-image");
    image.setSrc(ResourcesUtil.getImageURL(connection, Icons.QUESTION));
    table.setWidget(1, 0, image);
  }
  
  /**
   * The input initialization.
   * @param locale The application locale.
   */
  protected void initInput(String locale) {
    setLabel(locale);
    table.setWidget(1, 1, input);
  }
  
  /**
   * The buttons initialization.
   * @param locale The application locale.
   */
  protected void initButtons(String locale) {
    VSpanPanel		pane;
    
    pane = new VSpanPanel();
    pane.setStyleName("buttons");
    setButtonsCaption(locale);
    pane.add(cancel);
    pane.add(ok);
    table.setWidget(2, 0, pane);
    table.getFlexCellFormatter().setColSpan(2, 0, 2);
    table.getCellFormatter().setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
  }
  
  /**
   * Sets the buttons caption.
   * @param locale The application locale.
   */
  private void setButtonsCaption(String locale) {
    ok.getInputElement().setValue(LocalizedProperties.getString(locale, "OK"));
    cancel.getInputElement().setValue(LocalizedProperties.getString(locale, "CANCEL"));
  }
  
  /**
   * Returns the position.
   * @return The position.
   */
  protected int getPosition() {
    try {
      return Integer.parseInt(input.getValue());
    } catch (NumberFormatException e) {
      return first;
    }
  }
  
  /**
   * Sets the input label.
   * @param locale The application locale
   */
  private void setLabel(String locale) {
    input.setLabel(LocalizedProperties.getString(locale, "position-number"));
  }
  
  /**
   * Creates the requester title.
   * @param locale The application locale.
   * @return The requester title.
   */
  protected void createTitle(String locale) {  
    table.setWidget(0, 0, new VSpan(getTitle(locale)));
    table.getFlexCellFormatter().setColSpan(0, 0, 2);
    table.getCellFormatter().getElement(0, 0).setClassName("k-posreq-title");
  }
  
  /**
   * Creates the requester title.
   * @param locale The application locale.
   * @return The requester title.
   */
  private String getTitle(String locale) {
    return first + " " +  LocalizedProperties.getString(locale, "TO") + " " + last;
  }
  
  /**
   * Focus on text input.
   */
  public void focus() {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {

      @Override
      public void execute() {
        if (input != null) {
          input.focus();
        }
      }
    });
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * An integer text input
   */
  private class VIntegerTextInput extends VerticalPanel implements KeyPressHandler, KeyDownHandler {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates the input widget.
     */
    public VIntegerTextInput() {
      setStyleName("integer-input");
      label = new VInputLabel();
      label.setStyleName("integer-label");
      text = new TextBox();
      text.setStyleName("integer-text");
      text.setVisibleLength(15);
      add(label);
      add(text);
      setSpacing(8);
      text.addKeyPressHandler(this);
      text.addKeyDownHandler(this);
    }

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    /**
     * Returns the input value.
     * @return The input value.
     */
    public String getValue() {
      return text.getValue();
    }
    
    /**
     * Focus on text input.
     */
    public void focus() {
      text.setFocus(true);
    }
    
    /**
     * Sets the input label.
     * @param label The input label.
     */
    public void setLabel(String label) {
      this.label.setText(label);
    }
    
    @Override
    public void onKeyDown(KeyDownEvent event) {
      validate(event);
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
      validate(event);
    }
    
    /**
     * Validates a key code event.
     * @param event a key code event.
     */
    protected void validate(KeyEvent<?> event) {
      if (event.isAnyModifierKeyDown()
	  || (String.valueOf(getCharCode(event)).trim().length() == 0
	  && event.getNativeEvent().getCharCode() != KeyCodes.KEY_SPACE))
      {
	return;
      }
      
      if (!validate(getCharCode(event))) {
	event.preventDefault();
	event.stopPropagation();
      }
    }
    
    /**
     * Checks the character input.
     * @param c The character to check.
     * @return {@code true} if it is a valid charcater.
     */
    protected boolean validate(char c) {
      return Character.isDigit(c) || c == '.' || c == '-';
    }

    /**
     * Gets the char code for this event.
     * 
     * @return the char code
     */
    protected char getCharCode(KeyEvent<?> event) {
      return (char) getUnicodeCharCode(event);
    }

    /**
     * Gets the Unicode char code (code point) for this event.
     * 
     * @return the Unicode char code
     */
    protected int getUnicodeCharCode(KeyEvent<?> event) {
      return event.getNativeEvent().getCharCode();
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private VInputLabel			label;
    private TextBox			text;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final int				first;
  private final int				last;
  private final VIntegerTextInput		input;
  private final VInputButton			ok;
  private final VInputButton			cancel;
  private final FlexTable			table;
}
