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
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that handles a h4 HTML element.
 *
 */
public class VH4 extends Widget {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new VH4 widget.
   * @param text The widget text.
   */
  public VH4(String text) {
    setElement(Document.get().createElement("h4"));
    setText(text);
  }
  
  /**
   * Creates a new VH4 widget.
   * @param text The widget text.
   */
  public VH4() {
    this(null);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the widget text.
   * @param text The widget text.
   */
  public void setText(String text) {
    if (text != null) {
      getElement().setInnerText(text);
    }
  }
  
  @Override
  public void setVisible(boolean visible) {
    getElement().getStyle().setVisibility(visible ? Visibility.VISIBLE : Visibility.HIDDEN);
  }
}
