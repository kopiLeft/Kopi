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

import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

/**
 * An integer field.
 */
class VIntegerField(width : Int, minval : Double, maxval : Double) : InputTextField<TextField>(TextField()) {

  init {
    internalField.pattern = "[0-9-]*"
    internalField.isPreventInvalidInput = true
    internalField.element.setProperty("min", minval)
    internalField.element.setProperty("max", maxval)
    this.width = width.toString()
    setValueChangeMode()
  }

  override fun setMaxLength(maxLength: Int) {
    internalField.maxLength = maxLength
  }

  override fun getMaxLength(): Double = internalField.maxLength.toDouble()

  override fun setValueChangeMode() {
    internalField.valueChangeMode = ValueChangeMode.ON_CHANGE
  }
}
