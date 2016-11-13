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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple panel that wraps an anchor element.
 */
@SuppressWarnings("deprecation")
public class VAnchorPanel extends ComplexPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the anchor panel instance.
   */
  public VAnchorPanel() {
    setElement(DOM.createAnchor());
    sinkEvents(Event.ONCLICK);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void add(Widget child) {
    add(child, getElement());
  }
  
  /**
   * Frame to render the resource in.
   * @param target The target frame.
   */
  public void setTarget(String target) {
    getAnchorElement().setTarget(target);
  }

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
   * Returns the wrapped anchor element.
   * @return The wrapped anchor element.
   */
  private AnchorElement getAnchorElement() {
    return AnchorElement.as(getElement());
  }
  
  /**
   * Adds a click handler to this anchor panel.
   * @param clickHandler The click handler.
   * @return The handler registration.
   */
  public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
    return addDomHandler(clickHandler, ClickEvent.getType());
  }
}
