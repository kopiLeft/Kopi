/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.ui.vaadin.visual

import java.io.IOException

import org.kopi.galite.ui.vaadin.window.Window
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.VHelpViewer

import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The `DHelpViewer` is used to display help information.
 *
 *
 * The help view is used by the UI factory to create vaadin view version
 * of the the [VHelpViewer] model.
 *
 */
class DHelpViewer(model: VHelpViewer) : DWindow(model), Focusable<Window> {

  private val html = Html(model.url!!.openStream())

  init {
    model.setDisplay(this)

    try {
      val pane = Div()
      val layout = VerticalLayout(html)
      pane.width = "600px"
      pane.height = "500px"
      pane.add(layout)
      layout.isMargin = true
      setContent(pane)
    } catch (e: IOException) {
      throw InconsistencyException(e)
    }
  }
  override fun run() {
    focus()
    getModel()!!.setActorEnabled(VHelpViewer.CMD_QUIT, true)
  }
}
