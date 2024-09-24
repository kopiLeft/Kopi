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
package org.kopi.vkopi.lib.ui.vaadinflow.list

import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout

import org.kopi.vkopi.lib.ui.vaadinflow.base.LocalizedProperties
import org.kopi.vkopi.lib.ui.vaadinflow.base.Styles
import org.kopi.vkopi.lib.ui.vaadinflow.common.Dialog
import org.kopi.vkopi.lib.visual.ApplicationContext
import org.kopi.vkopi.lib.visual.VlibProperties

/**
 * A list dialog
 */
@CssImport.Container(value = [
  CssImport("./styles/galite/grid.css" , themeFor = "vaadin-grid"),
  CssImport("./styles/galite/list.css" , themeFor = "vaadin-grid"),
  CssImport("./styles/galite/list.css" , themeFor = "vcf-enhanced-dialog-overlay")
])
open class GridListDialog : Dialog(), HasEnabled, HasStyle {

  protected var newForm: Boolean = false
  protected var newFormButton: Button = Button(VlibProperties.getString("new-record"))
  protected val close = Button(LocalizedProperties.getString(locale, "CLOSE"))
  private var content: VerticalLayout = VerticalLayout()
  protected var pattern: String? = null

  init {
    className = Styles.LIST_DIALOG_CONTAINER
    element.themeList.add(Styles.LIST_DIALOG_CONTAINER)
    content.className = Styles.LIST_DIALOG
    content.element.setAttribute("hideFocus", "true")
    content.element.style["outline"] = "0px"
    isResizable = true
    isCloseOnOutsideClick = false
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------


  fun showListDialog() {
    // now show the list dialog
    super.open()
  }

  override fun open() {
    showListDialog()
  }

  /**
   * Sets the table component associated with this list dialog.
   */
  protected var table: ListTable? = null
    set(table) {
      field = table
      field!!.className = Styles.LIST_DIALOG_TABLE
      field!!.addThemeName(Styles.LIST_DIALOG_TABLE)
      content.add(field) // put table inside the focus panel
      add(field!!.widthStyler, content)
      if (!newForm) {
        addToFooter(close)
      } else {
        addToFooter(newFormButton)
      }
    }

  private val locale get() = ApplicationContext.getApplicationContext().getApplication().defaultLocale.toString()
}
