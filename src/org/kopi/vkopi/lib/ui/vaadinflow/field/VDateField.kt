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
package org.kopi.vkopi.lib.ui.vaadinflow.field

import java.time.LocalDate

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.CssImport

/**
 * An Date field.
 */
@CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-picker-text-field")
class VDateField : InputTextField<DatePicker>(DatePicker()), KeyNotifier {

  init {
    internalField.isClearButtonVisible = true
    internalField.isAutoOpen = false
    element.themeList.add("galite-date")

    // Workaround for autoselection on focus
    element.executeJs(
      """
              this.addEventListener("focus", event => {
                    this.$.input.inputElement.select()
              })
              """
    )
    addFocusListener {
      element.executeJs("this.$.input.inputElement.select()")
    }
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    val date = TimestampValidator.parseDate(newPresentationValue.orEmpty())

    content.value = if(date != null) {
      LocalDate.of(date.year, date.month, date.day)
    } else {
      null
    }
  }
}
