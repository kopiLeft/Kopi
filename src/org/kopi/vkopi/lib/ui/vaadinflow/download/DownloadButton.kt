/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.vkopi.lib.ui.vaadinflow.base.LocalizedProperties

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * A download button that allows a user to download a file produced in the server.
 *
 * @param file the file to download.
 * @param name the file name
 */
class DownloadButton(file: File, name: String, locale: String): DownloadAnchor(file, name) {
  private val downloadButton = Button()

  init {
    downloadButton.text = LocalizedProperties.getString(locale, "downloadLabel")
    downloadButton.icon = Icon(VaadinIcon.DOWNLOAD_ALT)
    downloadButton.isDisableOnClick = true
    add(downloadButton)
  }
}
