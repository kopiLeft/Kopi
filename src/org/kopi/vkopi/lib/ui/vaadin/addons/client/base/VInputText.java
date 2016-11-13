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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;

/** 
 * An input text widget.
 */
public class VInputText extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the input text widget.
   */
  public VInputText() {
    this(Document.get().createTextInputElement());
  }
  
  /**
   * For children classes.
   * @param inputElement The inner element.
   */
  protected VInputText(InputElement inputElement) {
    super(inputElement);
    sinkEvents(Event.ONKEYPRESS);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the input element size.
   * @param size The element size.
   */
  public void setSize(int size) {
    getInputElement().setSize(size);
  }
  
  /**
   * Sets the element id.
   * @param id The element id.
   */
  public void setId(String id) {
    getInputElement().setId(id);
  }
  
  /**
   * Sets the element name.
   * @param name The element name.
   */
  public void setName(String name) {
    getInputElement().setName(name);
  }
  
  /**
   * Sets the tab index of this input element.
   * @param tabIndex The tab index.
   */
  public void setTabIndex(int tabIndex) {
    getInputElement().setTabIndex(tabIndex);
  }
  
  /**
   * Returns the input text value.
   * @return The input text value.
   */
  public String getValue() {
    return getInputElement().getValue();
  }
  
  /**
   * Returns the encapsulated input element.
   * @return The encapsulated input element.
   */
  private InputElement getInputElement() {
    return getElement().cast();
  }
  
  /**
   * Registers a key press handler on this input text.
   * @param handler The handler to register
   * @return The handler registration.
   */
  public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
    return addDomHandler(handler, KeyPressEvent.getType());
  }
}
