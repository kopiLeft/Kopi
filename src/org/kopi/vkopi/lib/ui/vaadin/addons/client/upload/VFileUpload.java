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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.upload;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasName;

/**
 * File upload widget.
 */
public final class VFileUpload extends FocusWidget implements HasName, HasChangeHandlers {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VFileUpload</code> widget.
   * @param upload The uploader widget.
   */
  public VFileUpload() {
    super(Document.get().createFileInputElement());
    sinkEvents(Event.ONCLICK);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }
  
  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }

  /**
   * Gets the filename selected by the user. This property has no mutator, as
   * browser security restrictions preclude setting it.
   * 
   * @return the widget's filename
   */
  public String getFilename() {
    return getInputElement().getValue();
  }

  @Override
  public String getName() {
    return getInputElement().getName();
  }

  @Override
  public void setName(String name) {
    getInputElement().setName(name);
  }

  /**
   * Returns the encapsulated input element.
   * @return The encapsulated input element.
   */
  private InputElement getInputElement() {
    return getElement().cast();
  }
  
  /**
   * Opens the browse client dialog.
   */
  public void click() {
    getInputElement().click();
  }
  
  /**
   * Sets a mime type filter.
   * @param mimeType The mime type filter.
   */
  public void setMimeType(String mimeType) {
    getInputElement().setAccept(mimeType);
  }
  
  /**
   * Unsets the used mime type.
   */
  public void unsetMimeType() {
    getInputElement().setAccept("");
  }
}
