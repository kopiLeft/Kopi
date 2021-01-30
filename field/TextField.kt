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
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

/**
 * A text field component.
 *
 * @param label the text field label, if it's null it will create a field without label.
 */
open class TextField(label: String? = null) : Div(), HasEnabled {
  val textField = TextField()

  init {
    setContent(textField)
    textField.label = label
    textField.addThemeVariants(TextFieldVariant.LUMO_SMALL, TextFieldVariant.MATERIAL_ALWAYS_FLOAT_LABEL)
  }

  private fun setContent(textField: TextField) {
    removeAll()
    add(textField)
  }

}
