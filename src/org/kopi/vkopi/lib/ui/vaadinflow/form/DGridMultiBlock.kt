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
package org.kopi.vkopi.lib.ui.vaadinflow.form

import org.kopi.vkopi.lib.base.UComponent
import org.kopi.vkopi.lib.form.Alignment
import org.kopi.vkopi.lib.form.UMultiBlock
import org.kopi.vkopi.lib.form.VBlock
import org.kopi.vkopi.lib.form.VField
import org.kopi.vkopi.lib.ui.vaadinflow.block.SimpleBlockLayout
import org.kopi.vkopi.lib.visual.VException
import org.kopi.vkopi.lib.visual.VRuntimeException

import com.vaadin.flow.component.Component
import com.vaadin.flow.data.provider.ListDataProvider

/**
 * A based Grid multi block implementation
 */
class DGridMultiBlock(parent: DForm,
                      model: VBlock)
  : DGridBlock(parent, model), UMultiBlock /*, DetailsGenerator TODO */ {

  private var detail: SimpleBlockLayout? = null
  private var itemHasDetailVisible: GridBlockItem? = null

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun switchView(row: Int) {
    if (model.form.getActiveBlock() != model) {
      if (!model.isAccessible) {
        return
      }
      try {
        model.form.gotoBlock(model)
      } catch (ex: Exception) {
        (getFormView() as DForm).reportError(VRuntimeException(ex.message, ex))
        return
      }
    }
    if (row >= 0) {
      model.gotoRecord(getRecordFromDisplayLine(row))
    } else if (getDisplayLine() >= 0) {
      model.gotoRecord(getRecordFromDisplayLine(getDisplayLine()))
    }
    model.isDetailMode = !inDetailMode()
    if (editor.item != null) {
      itemHasDetailVisible = editor.item
      grid.setDetailsVisible(editor.item, inDetailMode())
      if (editor.isOpen) {
        editor.closeEditor()
      }
    }
  }

  override fun getRecordFromDisplayLine(line: Int): Int {
    return if (inDetailMode() && itemHasDetailVisible != null) {
      itemHasDetailVisible!!.record
    } else if (itemToBeEdited != null) {
      itemToBeEdited!!.record
    } else if (isEditorInitialized && editor.item != null) {
      editor.item.record
    } else {
      super.getRecordFromDisplayLine(line)
    }
  }

  override fun addToDetail(comp: UComponent?, constraint: Alignment) {
    if (detail == null) {
      detail = SimpleBlockLayout(2 * maxColumnPos, maxRowPos, this)
      setContent(detail!!)
      // detail.addStyleName("grid-detail") TODO
    }

    detail!!.addComponent(comp as Component,
                          constraint.x,
                          constraint.y,
                          constraint.width,
                          constraint.height,
                          constraint.alignRight,
                          constraint.useAll)
  }

  override fun inDetailMode(): Boolean = model.isDetailMode

  override fun blockViewModeLeaved(block: VBlock, activeField: VField?) {
    try {
      // take care that value of current field
      // is visible in the other mode
      // Not field.updateText(); because the field is
      // maybe not visible in the Detail Mode
      activeField?.leave(true)
    } catch (ex: VException) {
      ex.printStackTrace()
      model.form.error(ex.message)
    }
  }

  override fun blockViewModeEntered(block: VBlock, activeField: VField?) {
    if (inDetailMode()) {
      // update detail view access and texts
      updateDetailDisplay()
      try {
        // Show detail view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          //     model.gotoFirstField();
        } else {
          if (!activeField.noDetail()) {
            // field is visible in chartView
            activeField.enter()
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.activeField = activeField
            model.gotoNextField()
          }
        }
      } catch (ex: VException) {
        ex.printStackTrace()
        model.form.error(ex.message)
      }
    } else {
      try {
        // show chart view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          // model.gotoFirstField();
        } else {
          if (!activeField.noChart()) {
            // field is visible in chartView
            activeField.enter()
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.activeField = activeField
            model.gotoNextField()
          }
        }
      } catch (ex: VException) {
        ex.printStackTrace()
        model.form.error(ex.message)
      }
    }
  }

  override fun blockChanged() {
    super.blockChanged()
    if (model.activeRecord != -1 && itemHasDetailVisible != null && model.activeRecord != itemHasDetailVisible!!.record) {
      enterRecord(model.activeRecord)
    }
  }

  override fun enterRecord(recno: Int) {
    if (inDetailMode() && itemHasDetailVisible != null) {
      grid.setDetailsVisible(itemHasDetailVisible, false)
      model.isDetailMode = false
      if (recno != itemHasDetailVisible!!.record) {
        itemHasDetailVisible = (grid.dataProvider as ListDataProvider).items.single { it.record == recno }
        model.isDetailMode = true
        grid.setDetailsVisible(itemHasDetailVisible, true)
      }
    }
    super.enterRecord(recno)
  }

  /**
   * Updates the state of the detail display
   */
  internal fun updateDetailDisplay() {
    for (columnView in columnViews) {
      if (columnView?.detailDisplay != null) {
        columnView.detailDisplay!!.updateAccess()
        columnView.detailDisplay!!.updateText()
        columnView.detailDisplay!!.updateColor()
      }
    }
  }
}
