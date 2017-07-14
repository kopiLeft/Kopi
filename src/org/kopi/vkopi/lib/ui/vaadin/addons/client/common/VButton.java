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
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ButtonBase;

/**
 * A HTML button widget.
 */
public class VButton extends ButtonBase {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new button widget.
   */
  public VButton() {
    super(Document.get().createPushButtonElement());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the button ID.
   * @param id The button ID
   */
  public void setId(String id) {
    getElement().setId(id);
  }
  
  /**
   * Sets the button icon
   * @param name The button icon name
   */
  public void setIcon(String name) {
    if (icon == null) {
      icon = new VIcon();
      icon.setName(name);
      DOM.appendChild(getElement(), icon.getElement());
    } else {
      icon.setName(name);
    }
  }
  
  /**
   * Simulates a click on this button.
   */
  public void click() {
    click(getElement());
  }
  
  /**
   * Fires a click from the given element.
   * @param el The  element
   */
  private native void click(Element el) /*-{
    el.click();
  }-*/;

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VIcon                         icon;
}
