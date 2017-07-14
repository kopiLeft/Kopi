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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.login;

import java.io.Serializable;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Instances of this class may be used to calculate the width of any font. This
 * class works best with fixed fonts, where each character shares a common
 * width/height.
 * 
 * This class uses a hidden div along with the various font details set upon
 * this instance. Unfortunately not all browsers make the
 * offsetWidth/offsetHeight immediately available requiring the user of a
 * deferred command, after setting properties and calling {@link #calculate()}.
 * 
 */
@SuppressWarnings("serial")
public class FontMetrics implements Serializable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Needed by GWT compiler
   */
  public FontMetrics() {}

  public FontMetrics(String fontFamily, int fontSize, String text) {
    setFontFamily(fontFamily);
    setFontSize(fontSize);
    setText(text);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  /**
   * Schedules the process that will calculate the average width/height for a
   * single character for the given font.
   * 
   * @param text This text should not contain any newlines or carriage returns.
   */
  protected void calculate() {
    // needs to be calculated
    if (needsCalculating()) {
      doCalculate();
    }
  }

  protected void doCalculate() {
    setReady(CALCULATING);
    // create a span set its width /height to 1...
    final Element       element = DOM.createSpan();
    final Style         inlineStyle = element.getStyle();
    final String        text = getText();
    final Element       body = RootPanel.getBodyElement();
    
    inlineStyle.setDisplay(Display.INLINE);
    inlineStyle.setMargin(0, Unit.PX);
    inlineStyle.setBorderWidth(0, Unit.PX);
    inlineStyle.setPadding(0, Unit.PX);
    inlineStyle.setVisibility(Visibility.HIDDEN);
    inlineStyle.setPosition(Position.ABSOLUTE);
    inlineStyle.setProperty("fontFamily", getFontFamily());
    inlineStyle.setFontSize(getFontSize(), Unit.PX);
    inlineStyle.setFontWeight(FontWeight.NORMAL);
    inlineStyle.setFontStyle(FontStyle.NORMAL);
    inlineStyle.setWhiteSpace(WhiteSpace.PRE);
    element.setInnerText(text);
    DOM.appendChild(body, element);
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
        FontMetrics.this.finishCalculate(element);
      }
    });
  }

  protected void finishCalculate(final Element element) {
    if (element == null) {
      return;
    }
    
    try {
      final String      text = getText();
      final int         charsAcross = text.length();
      final int         width = element.getOffsetWidth();
      final int         height = element.getOffsetHeight();
      final int         averageWidth = width / charsAcross;
      final int         averageHeight = height;

      setWidth(averageWidth);
      setHeight(averageHeight);

      setReady(READY);
    } finally {
      // dont want element to remain regardless whether or not
      // measurements succeeded.
      element.getParentElement().removeChild(element);
    }
  }

  public int getWidth() {
    checkReady("width");
    return width;
  }

  protected void setWidth(final int width) {
    if (width > 0) {
      this.width = width;
    }
  }

  public int getHeight() {
    checkReady("height");
    return height;
  }

  protected void setHeight(final int height) {
    if (height > 0) {
      this.height = height;
    }
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public void setFontFamily(final String fontFamily) {
    if (fontFamily != null && fontFamily.length() > 0) {
      this.fontFamily = fontFamily;
      setReady(WAITING);
    }
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(final int fontSize) {
    if (fontSize != 0) {
      this.fontSize = fontSize;
      setReady(WAITING);
    }
  }

  public String getText() {
    return text;
  }

  /**
   * The best text typically has just two lines worth of about 10 characters.
   * 
   * @param text
   */
  public void setText(final String text) {
    if (text != null && text.length() > 0) {
      this.text = text;
      setReady(WAITING);
    }
  }

  public boolean isReady() {
    return READY == ready;
  }

  /**
   * If the status of this FontMetrics instance is waiting and not ready and
   * the calculate method has not been called returns false. This enables the
   * calculate method to guard against double invocations.
   * 
   * @return
   */
  protected boolean needsCalculating() {
    return WAITING == ready;
  }

  protected void setReady(final int ready) {
    this.ready = ready;
  }

  protected void checkReady(final String property) {
    if (!isReady()) {
      throw new IllegalStateException("The "
        + property
        + " cannot be immediately queried after updating any property, use a DeferredCommand to read, this: "
        + this);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * This flag indicates that the width/height values of this instance are
   * invalid because a pending calculate has not completed or begin was never
   * called
   */
  private static final int    WAITING = 0;
  private static final int    CALCULATING = 1;
  private static final int    READY = 2;

  private int                 ready;
  private String              text;
  private String              fontFamily;
  private int                 width;
  private int                 fontSize;
  private int                 height;
}
