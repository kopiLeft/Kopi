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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.login;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * The login view containing the login panel box.
 */
public class VLoginView extends FlexTable {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates the login view widget.
   */
  public VLoginView() {
    setWidth("100%");
    setBorderWidth(0);
    setCellPadding(0);
    setCellSpacing(0);
    getElement().setPropertyString("align", "center");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the login box window.
   * @param loginWindow The login box window.
   */
  public void setLoginWindow(Widget loginWindow) {
    setWidget(0, 0, loginWindow);
    getColumnFormatter().getElement(0).setPropertyString("align", "center");
    getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
  }
}
