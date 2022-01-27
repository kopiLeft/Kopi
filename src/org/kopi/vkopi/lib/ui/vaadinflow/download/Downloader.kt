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
package org.kopi.vkopi.lib.ui.vaadinflow.download

import java.io.File

import com.vaadin.flow.component.HasComponents

/**
 * A helper class containing a download anchor component that allows a user to download a file produced in the server.
 *
 * @param file the file to download.
 * @param name the file name.
 * @param parent component that will trigger this download.
 */
class Downloader(file: File, name: String, val parent: HasComponents) {
  private val anchor = DownloadAnchor(file, name)

  /**
   * Downloads the file attached to this downloader.
   */
  fun download() {
    parent.add(anchor)
    anchor.element.callJsFunction("click")
  }
}
