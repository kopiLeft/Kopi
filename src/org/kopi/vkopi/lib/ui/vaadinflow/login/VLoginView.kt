/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.vkopi.lib.ui.vaadinflow.login

import org.kopi.vkopi.lib.ui.vaadinflow.common.VSimpleTable

import com.vaadin.flow.component.Component

/**
 * The login view containing the login panel box.
 */
class VLoginView : VSimpleTable() {

  /**
   * Creates the login view component.
   */
  init {
    width = "100%"
    setBorderWidth(0)
    setCellPadding(0)
    setCellSpacing(0)
    element.setProperty("align", "center")
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the login box window.
   * @param loginWindow The login box window.
   */
  fun setLoginWindow(loginWindow: Component?) {
    add(loginWindow!!)
    lastTd?.setProperty("align", "center")
    lastTd?.style?.set("vertical-align", "middle")
  }
}
