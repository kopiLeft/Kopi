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
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * An input label widget.
 */
public class VInputLabel extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new input label widget.
   */
  public VInputLabel() {
    super(Document.get().createLabelElement());
  }
  
  public VInputLabel(String text) {
    super(Document.get().createLabelElement());
    if (text != null) {
      setText(text);
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * This attribute links this label with another form control by id attribute.
   */
  public void setHtmlFor(String target) {
    getLabelElement().setHtmlFor(target);
  }
  
  /**
   * Sets the label text.
   * @param text The label text.
   */
  public void setText(String text) {
    getLabelElement().setInnerText(text);
  }
  
  /**
   * Returns the encapsulated label element.
   * @return The encapsulated label element.
   */
  private LabelElement getLabelElement() {
    return getElement().cast();
  }
}
