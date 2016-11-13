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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;

/**
 * A text area input zone.
 */
public class VInputTextArea extends VInputTextField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the text area instance.
   */
  public VInputTextArea() {
    super(DOM.createTextArea());
    setStyleName(Styles.TEXT_AREA_INPUT);
    getTextAreaElement().getStyle().setProperty("resize", "none");
    addKeyDownHandler(enterDownHandler);
    if (!browserSupportsMaxLengthAttribute()) {
      addKeyUpHandler(maxLengthHandler);
      addChangeHandler(maxLengthHandler);
      sinkEvents(Event.ONPASTE);
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONPASTE) {
      maxLengthHandler.onPaste(event);
    }
  }

  @Override
  public int getCursorPos() {
    // This is needed so that TextBoxImplIE6 is used to return the correct
    // position for old Internet Explorer versions where it has to be
    // detected in a different way.
    return getImpl().getTextAreaCursorPos(getElement());
  }

  @Override
  protected void setMaxLengthToElement(int newMaxLength) {
    // There is no maxlength property for textarea. The maximum length is
    // enforced by the KEYUP handler
  }
  
  /**
   * Sets word wrap ability.
   * @param wordwrap The word wrap ability.
   */
  @SuppressWarnings("deprecation")
  public void setWordwrap(boolean wordwrap) {
    if (wordwrap) {
      getElement().removeAttribute("wrap");
      getElement().getStyle().clearOverflow();
      getElement().getStyle().clearWhiteSpace();
    } else {
      getElement().setAttribute("wrap", "off");
      getElement().getStyle().setOverflow(Overflow.AUTO);
      getElement().getStyle().setWhiteSpace(WhiteSpace.PRE);
    }
    if (BrowserInfo.get().isOpera()
	|| (BrowserInfo.get().isWebkit() && wordwrap)) {
      // Opera fails to dynamically update the wrap attribute so we detach
      // and reattach the whole TextArea.
      // Webkit fails to properly reflow the text when enabling wrapping,
      // same workaround
      Util.detachAttach(getElement());
    }
  }
  
  /**
   * Sets if the we are using fixed new line mode.
   * @param isFixedNewLine Is fixed new line mode is used ?
   */
  protected void setFixedNewLine(boolean isFixedNewLine) {
    this.isFixedNewLine = isFixedNewLine;
  }
  
  /**
   * Returns the text area element.
   * @return The text area element.
   */
  protected TextAreaElement getTextAreaElement() {
    return super.getElement().cast();
  }

  /**
   * Sets the rows of this text area.
   * @param rows The text area total rows.
   * @param visibleRows The text area visible rows
   */
  public void setRows(int rows, int visibleRows) {
    getTextAreaElement().setRows(visibleRows);
    this.rows = rows;
  }
  
  /**
   * Sets the text size.
   * @param size The text size.
   */
  public void setSize(int size) {
    getTextAreaElement().setCols(size);
  }
  
  /**
   * Returns the rows available for this text area.
   * @return The rows available for this text area.
   */
  public int getRows() {
    return rows == -1 ? getTextAreaElement().getRows() : rows;
  }
  
  /**
   * Checks browser max length compatibility.
   * @return {@code true} if the max length attribute is supported.
   */
  protected boolean browserSupportsMaxLengthAttribute() {
    BrowserInfo info = BrowserInfo.get();
    if (info.isFirefox() && info.isBrowserVersionNewerOrEqual(4, 0)) {
      return true;
    }
    if (info.isSafari() && info.isBrowserVersionNewerOrEqual(5, 0)) {
      return true;
    }
    if (info.isIE() && info.isBrowserVersionNewerOrEqual(10, 0)) {
      return true;
    }
    if (info.isAndroid() && info.isBrowserVersionNewerOrEqual(2, 3)) {
      return true;
    }
    return false;
  }

  protected void enforceMaxLength() {
    if (getMaxLength() >= 0) {
      Scheduler.get().scheduleDeferred(new Command() {
	
        @Override
	public void execute() {
	  if (getText().length() > getMaxLength()) {
	    setText(getText().substring(0, getMaxLength()));
	  }
	}
      });
    }
  }
  
  /**
   * Returns the number of columns contained in this text area.
   * @return The number of columns contained in this text area.
   */
  protected int getCols() {
    return getTextAreaElement().getCols();
  }
  
  /**
   * Sets the number of columns in this text area.
   * @param cols The number of columns.
   */
  protected void setCols(int cols) {
    getTextAreaElement().setCols(cols);
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    // prevent new line when max lines is reached when scroll is not enabled
    if (isFixedNewLine && getNumberOfLines(event.getCharCode()) >= getRows() + 1) {
      event.stopPropagation();
      event.preventDefault();
    }
    
    super.onKeyPress(event);
  }
  
  /**
   * Returns the effective number of lines in this text area.
   * @param c the typed character
   * @return The effective number of lines in this text area.
   */
  private int getNumberOfLines(char c) {
    String              source;
    int                 lines;
    
    // first we will convert the text area entry
    // to a single line with that contains blanks
    // completing the missing columns if a line break
    // is added through.
    source = convertToSingleLine(String.valueOf(c) + getText(), getCols());
    // now, since we use the monospaced font in text inputs, the number of line
    // is exactly the number of characters in converted string divided by the
    // number of columns in text area.
    lines = source.length() / getCols();
    if (source.length() % getCols() != 0) {
      lines += 1;
    }

    return lines;
  }

  /**
   * Converts a given string to a line string.
   * @param source The source text.
   * @param col The column index.
   * @return The converted string.
   */
  private static String convertToSingleLine(String source, int col) {
    StringBuffer      target = new StringBuffer();
    int               length = source.length();
    int               start = 0;

    while (start < length) {
      int             index = source.indexOf('\n', start);

      if (index - start < col && index != -1) {
        target.append(source.substring(start, index));
        for (int j = index - start; j < col; j++) {
          target.append(' ');
        }
        start = index+1;
        if (start == length) {
          // last line ends with a "new line" -> add an empty line
          for (int j = 0; j < col; j++) {
            target.append(' ');
          }
        }
      } else {
        if (start + col >= length) {
          target.append(source.substring(start, length));
          for (int j = length; j < start+col; j++) {
            target.append(' ');
          }
          start = length;          
        } else {
          // find white space to break line
          int   i;
    
          for (i = start + col - 1; i > start; i--) {
            if (isWhitespace(source.charAt(i))) {
              break;
            }
          }
          if (i == start) {
            index = start + col;
          } else {
            index = i + 1;
          }
    
          target.append(source.substring(start, index));
          for (int j = (index - start)%col; j != 0 && j < col; j++) {
            target.append(' ');
          }
          start = index;
        }
      }
    }
    
    return target.toString();
  }
  
  /**
   * Checks if the given character is a white space.
   * The implementation is picked from java implementation
   * since GWT does not contains the implementation of {@link Character#isWhitespace(char)}
   * @param c The concerned character.
   * @return {@code true} if the character is whitespace.
   */
  private static boolean isWhitespace(char c) {
    return c == ' ' 
      || c == '\u00A0' // SPACE_SEPARATOR
      || c == '\u2007'   // LINE_SEPARATOR
      || c == '\u202F'   // PARAGRAPH_SEPARATOR
      || c == '\u0009'   // HORIZONTAL TABULATION.
      || c == '\n'       // LINE FEED.
      || c == '\u000B'   // VERTICAL TABULATION.
      || c == '\u000C'   // FORM FEED.
      || c == '\r'       // CARRIAGE RETURN.
      || c == '\u001C'   // FILE SEPARATOR.
      || c == '\u001D'   // GROUP SEPARATOR.
      || c == '\u001E'   // RECORD SEPARATOR.
      || c == '\u001F';  // UNIT SEPARATOR.
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * Max length handler.
   */
  private class MaxLengthHandler implements KeyUpHandler, ChangeHandler {

    @Override
    public void onKeyUp(KeyUpEvent event) {
      enforceMaxLength();
    }

    public void onPaste(Event event) {
      if (isFixedNewLine) {
        setText(convertToSingleLine(getText(), getCols()));
      }
      enforceMaxLength();
    }

    @Override
    public void onChange(ChangeEvent event) {
      // Opera does not support paste events so this enforces max length
      // for Opera.
      enforceMaxLength();
    }
  }

  /**
   * Enter down handler.
   */
  private class EnterDownHandler implements KeyDownHandler {

    @Override
    public void onKeyDown(KeyDownEvent event) {
      // if the key being pressed is enter, we stop
      // propagation of the KeyDownEvents if there were no modifier keys
      // also pressed. This prevents shortcuts that are bound to only the
      // enter key from being processed but allows usage of e.g.
      // shift-enter or ctrl-enter.
      if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && !event.isAnyModifierKeyDown()) {
	event.stopPropagation();
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private MaxLengthHandler 		maxLengthHandler = new MaxLengthHandler();
  private EnterDownHandler 		enterDownHandler = new EnterDownHandler();
  private boolean                       isFixedNewLine;
  private int                           rows = -1;
}
