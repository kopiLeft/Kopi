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

import org.kopi.vkopi.lib.ui.vaadinflow.base.LocalizedProperties
import org.kopi.vkopi.lib.ui.vaadinflow.base.Styles
import org.kopi.vkopi.lib.ui.vaadinflow.base.VInputButton
import org.kopi.vkopi.lib.ui.vaadinflow.window.Window
import org.kopi.vkopi.lib.visual.ApplicationContext

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * A list dialog
 */
@CssImport.Container(value = [
  CssImport("./styles/galite/grid.css" , themeFor = "vaadin-grid"),
  CssImport("./styles/galite/list.css" , themeFor = "vaadin-grid"),
  CssImport("./styles/galite/list.css" , themeFor = "vcf-enhanced-dialog-overlay")
])
open class GridListDialog : EnhancedDialog(), HasEnabled, KeyNotifier, HasStyle {

  private var scrollBarAdded = false
  private var windowResized = false
  protected var newForm: VInputButton? = null
  private var lastActiveWindow: Window? = null
  protected val close = Button(LocalizedProperties.getString(locale, "CLOSE"))
  private var content: VerticalLayout = VerticalLayout()
  protected var pattern: String? = null
  /**
   * This is used to display a new button under the dialog.
   * No button will be drawn when it is `null`.
   */
  private var newText: String? = null

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
    // setNewText(newText) TODO
    // now show the list dialog
    super.open()
  }

  override fun open() {
    showListDialog()
  }

  /**
   * Shows the list dialog.
   */
  open fun show() {
    /*if (VInputTextField.getLastFocusedTextField() != null) {
      lastActiveWindow = VInputTextField.getLastFocusedTextField().getParentWindow()
    }
    // it can be an editor component
    if (lastActiveWindow == null && VEditorTextField.getLastFocusedEditor() != null) {
      lastActiveWindow = VEditorTextField.getLastFocusedEditor().getWindow()
    }*/
  }


  /**
   * Initializes the list dialog widget.
   * @param connection The application connection.
   */
  open fun init() {
    /*requireNotNull(connection) { "Application connection should be provided" }
    this.connection = connection
    content = VerticalPanel()
    close = VIcon()
    close.setName("close")
    popup = object : VPopup(connection, false, true) {
      protected fun onLoad() {
        super.onLoad()
        Scheduler.get().scheduleFinally(object : ScheduledCommand() {
          fun execute() {
            calculateTableSize()
            if (reference != null) {
              popup.showRelativeTo(reference)
            } else {
              popup.setGlassEnabled(true)
              popup.setGlassStyleName(Styles.LIST_DIALOG.toString() + "-glass")
            }
            focus()
          }
        })
      }
    }
    popup.addCloseHandler(this)
    close.addClickHandler(object : ClickHandler() {
      fun onClick(event: ClickEvent?) {
        fireClosed(true, false)
      }
    })*/
  }

  /**
   * Forces the table to have scroll bars.
   */
  protected open fun forceScrollBar() {
    /* val height: Double = table.getHeightByRows() * 41
     if (!windowResized) {
       if (hasVerticalScrollBar(height)) {
         table.setWidth(table.getOffsetWidth() + 8 + "px") //add horizontal scroll bar width
         scrollBarAdded = true
       }
     } else {
       if (scrollBarAdded && !hasVerticalScrollBar(height)) {
         table.setWidth(table.getOffsetWidth() - 16.toString() + "px") //remove horizontal scroll bar width
         scrollBarAdded = false
       } else if (!scrollBarAdded && hasVerticalScrollBar(height)) {
         table.setWidth(table.getOffsetWidth() + 16 + "px") //add horizontal scroll bar width
         scrollBarAdded = true
       }
     }*/
  }

  /**
   * Returns true if the list table should have a vertical scroll bar according to a given height.
   * @param height The reference height.
   * @return true if the list table should have a vertical scroll bar.
   */
  /*protected open fun hasVerticalScrollBar(height: Double): Boolean {
    return table.getScrollHeight() > height
  }

  *//**
   * Returns true if the list table should have an horizontal scroll bar according to a given width.
   * @param width The reference width.
   * @return true if the list table should have an horizontal scroll bar.
   *//*
  protected open fun hasHorizontalScrollBar(width: Double): Boolean {
    return table.getScrollWidth() > width
  }*/

  /**
   * Sets the table component associated with this list dialog.
   */
  protected var table: ListTable? = null
    set(table) {
      field = table
      field!!.className = Styles.LIST_DIALOG_TABLE
      field!!.addThemeName(Styles.LIST_DIALOG_TABLE)
      content.add(field) // put table inside the focus panel
      if (newForm != null) {
        content.add(newForm)
      }
      add(field!!.widthStyler, content)
      addToFooter(close)
    }

  /**
   * Sets the new text widget.
   * @param newText The new text widget.
   */
  open fun setNewText(newText: String?) {
    /*if (newText != null) {
      newForm = VInputButton(newText, object : ClickHandler() {
        fun onClick(event: ClickEvent?) {
          fireClosed(false, true)
        }
      })
      newForm.getInputElement().setValue(newText)
      newForm.setStyleName("new-button")
      newForm.setWidth("100%") // occupy all available space.
    }*/
  }

  private val locale get() = ApplicationContext.getApplicationContext().getApplication().defaultLocale.toString()
}
