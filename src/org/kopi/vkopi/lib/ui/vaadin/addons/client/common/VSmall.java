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
 * a widget that holds a small HTML tag inside. 
 */
public class VSmall extends Widget {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new small widget.
   */
  public VSmall() {
    setElement(Document.get().createElement("small"));
  }
  
  /**
   * Creates a small with a text.
   * @param text The small text.
   */
  public VSmall(String text) {
    this();
    setText(text);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the inner text of this small widget.
   * @param text The text to be set.
   */
  public void setText(String text) {
    getElement().setInnerText(text);
  }
  
  /**
   * Returns the small inner text.
   * @return The small inner text.
   */
  public String getText() {
    return getElement().getInnerText();
  }
  
  /**
   * Sets the inner HTML of this small widget.
   * @param html The HTML to be set.
   */
  public void setHtml(String html) {
    getElement().setInnerHTML(html);
  }
}
