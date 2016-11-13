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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main window content widget.
 */
public class VContent extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the content widget instance.
   */
  public VContent() {
    super(Document.get().createDivElement());
    getElement().setId("content");
    table = new FlexTable();
    setWidget(table);
    table.setWidth("100%"); // full size.
    table.setHeight("100%"); // full size.
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the content of this main container.
   * @param content The main container content.
   */
  public void setContent(Widget content, boolean separate) {
    table.setWidget(0, 0, content); // we will only use the first column of the table.
    // add separators.
    if (separate) {
      DOM.appendChild(table.getCellFormatter().getElement(0, 0), Document.get().createBRElement());
      DOM.appendChild(table.getCellFormatter().getElement(0, 0), Document.get().createBRElement());
    } else {
      table.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
      table.getCellFormatter().setHeight(0, 0, "100%");
    }
    empty = false;
  }
  
  /**
   * Clears the content.
   */
  public void clearContent() {
    table.removeCell(0, 0);
    empty = true;
  }
  
  /**
   * Checks for view content.
   * @return {@code true} is content exists.
   */
  public boolean isEmpty() {
    return empty;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final FlexTable			table;
  private boolean				empty = true;
}
