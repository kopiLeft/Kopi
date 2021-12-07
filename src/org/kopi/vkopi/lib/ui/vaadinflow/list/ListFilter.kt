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
package org.kopi.vkopi.lib.ui.vaadinflow.list

import java.util.Locale

import org.kopi.vkopi.lib.form.VListDialog

import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.function.SerializablePredicate

class ListFilter(private val filterFields: List<TextField>,
                 private val model: VListDialog,
                 private val ignoreCase: Boolean,
                 private val onlyMatchPrefix: Boolean) : SerializablePredicate<List<Any?>> {

  override fun test(t: List<Any?>): Boolean {
    for (i in t.indices) {
      val filterString = if(ignoreCase) filterFields[i].value.lowercase(Locale.getDefault()) else filterFields[i].value
      val value = if (ignoreCase) formatObject(t[i], i).lowercase(Locale.getDefault()) else formatObject(t[i], i)

      if (onlyMatchPrefix) {
        if (!value.startsWith(filterString)) {
          return false
        }
      } else {
        if (!value.contains(filterString)) {
          return false
        }
      }
    }

    return true
  }

  /**
   * Formats an object.
   *
   * @param o The object to be formatted.
   * @param col the column index
   * @return The formatted object.
   */
  private fun formatObject(o: Any?, col: Int): String {
    return model.columns[col]?.formatObject(o).toString()
  }
}
