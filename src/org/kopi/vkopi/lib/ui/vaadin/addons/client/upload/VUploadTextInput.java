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
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * The upload widget text input.
 * This input will contain the selected file full path
 */
/*package*/ class VUploadTextInput extends TextBoxBase {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new upload text area widget.
   */
  public VUploadTextInput() {
    super(Document.get().createTextInputElement());
    setStyleName("k-upload-textinput");
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  /**
   * Sets the requested width of the text box (this is not an exact value, as
   * not all characters are created equal).
   *
   * @param width the requested width, in characters
   */
  public void setCharacterWidth(int width) {
    getTextInputElement().setSize(width);
  }

  /**
   * Returns the {@link TextAreaElement} element.
   * @return The native text area element.
   */
  protected InputElement getTextInputElement() {
    return getElement().cast();
  }
}
