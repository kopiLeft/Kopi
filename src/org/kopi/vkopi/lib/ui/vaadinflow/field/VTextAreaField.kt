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
package org.kopi.vkopi.lib.ui.vaadinflow.field

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.data.value.ValueChangeMode

/**
 * A text area input zone.
 */
class VTextAreaField : InputTextField<TextArea>(TextArea()) {

  init {
    className = "textarea"
    // Set value change mode to EAGER to be able dynamically calculate the field size limit
    setValueChangeMode()
  }

  var cols: Int = 0

  var visibleRows = 0

  fun setRows(visibleRows: Int) {
    internalField.element.executeJs("this.querySelector('textarea').rows = $0;", visibleRows)
    this.visibleRows = visibleRows
  }

  override fun setValueChangeMode() {
    internalField.valueChangeMode = ValueChangeMode.EAGER
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    setRows(visibleRows)
    element.executeJs("setTimeout(function(){$0._inputValue = $1},0)", element, value)
  }

  /**
   * Sets the text size.
   */
  override var size: Int
    get() = element.getProperty("cols").toInt()
    set(value) { element.setProperty("cols", value.toString()) }

  fun setWordwrap(b: Boolean) {
    // TODO
  }

  fun setFixedNewLine(b: Boolean) {
    // TODO
  }

  override fun setMaxLength(maxLength: Int) {
    internalField.maxLength = maxLength
  }

  override fun getMaxLength(): Double = internalField.maxLength.toDouble()
}
