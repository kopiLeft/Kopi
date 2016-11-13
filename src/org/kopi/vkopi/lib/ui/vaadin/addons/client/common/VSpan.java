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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple span widget.
 */
public class VSpan extends Widget {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new span widget.
   */
  public VSpan() {
    setElement(Document.get().createSpanElement());
  }
  
  /**
   * Creates a span with a text.
   * @param text The span text.
   */
  public VSpan(String text) {
    this();
    setText(text);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the inner text of this span widget.
   * @param text The text to be set.
   */
  public void setText(String text) {
    getElement().setInnerText(text);
  }
  
  /**
   * Returns the span inner text.
   * @return The span inner text.
   */
  public String getText() {
    return getElement().getInnerText();
  }
  
  /**
   * Sets the inner HTML of this span widget.
   * @param html The HTML to be set.
   */
  public void setHtml(String html) {
    getElement().setInnerHTML(html);
  }
}
