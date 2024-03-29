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
package org.kopi.vkopi.lib.ui.vaadinflow.main

import java.io.Serializable

/**
 * Registered objects are notified about actions happening
 * in the main application window.
 */
interface MainWindowListener : Serializable {
  /**
   * Fired when the administration link is clicked.
   */
  fun onAdmin()

  /**
   * Fired when the support link is clicked.
   */
  fun onSupport()

  /**
   * Fired when the help link is clicked.
   */
  fun onHelp()

  /**
   * Fired when the logout link is clicked.
   */
  fun onLogout()

  /**
   * Fired when the connected user link is clicked.
   */
  fun onUser()
}
