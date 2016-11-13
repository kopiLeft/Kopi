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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple panel that wraps a span element.
 */
public class VSpanPanel extends ComplexPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new span panel.
   */
  @SuppressWarnings("deprecation")
  public VSpanPanel() {
    setElement(DOM.createSpan());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds a new child widget to the panel.
   * 
   * @param w the widget to be added
   */
  @SuppressWarnings("deprecation")
  @Override
  public void add(Widget w) {
    add(w, getElement());
  }

  /**
   * Inserts a widget before the specified index.
   * @param w the widget to be inserted
   * @param beforeIndex the index before which it will be inserted
   */
  public void insert(IsWidget w, int beforeIndex) {
    insert(asWidgetOrNull(w), beforeIndex);
  }

  /**
   * Inserts a widget before the specified index.
   * 
   * @param w the widget to be inserted
   * @param beforeIndex the index before which it will be inserted
   * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of
   *           range
   */
  @SuppressWarnings("deprecation")
  public void insert(Widget w, int beforeIndex) {
    insert(w, getElement(), beforeIndex, true);
  }
}
