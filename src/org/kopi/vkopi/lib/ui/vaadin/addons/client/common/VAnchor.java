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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple anchor widget.
 */
public class VAnchor extends Widget {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new anchor widget.
   */
  public VAnchor() {
    setElement(Document.get().createAnchorElement());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the anchor's href (the url to which it links).
   * @param href the anchor's href
   */
  public void setHref(String href) {
    getAnchorElement().setHref(href);
  }
  
  /**
   * Sets the anchor panel ID.
   * @param id The panel ID.
   */
  public void setId(String id) {
    getElement().setId(id);
  }
  
  /**
   * Sets the anchor panel inner text.
   * @param text The text to be shown.
   */
  public void setText(String text) {
    getElement().setInnerText(text);
  }

  /**
   * Returns the encapsulated anchor element.
   * @return The encapsulated anchor element.
   */
  protected AnchorElement getAnchorElement() {
    return getElement().cast();
  }
  
  /**
   * Adds a click handler to this 
   * @param handler The click handler.
   * @return The handler registration.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }
}
