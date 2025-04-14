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

import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.textfield.TextArea

import org.kopi.vkopi.lib.form.ModelTransformer
import org.kopi.vkopi.lib.form.UTextField
import org.kopi.vkopi.lib.form.VConstants
import org.kopi.vkopi.lib.form.VFieldUI
import org.kopi.vkopi.lib.ui.vaadinflow.base.BackgroundThreadHandler.access
import org.kopi.vkopi.lib.ui.vaadinflow.field.TextField
import org.kopi.vkopi.lib.util.LineBreaker
import org.kopi.vkopi.lib.visual.Action
import org.kopi.vkopi.lib.visual.VException
import org.kopi.vkopi.lib.visual.VlibProperties

/**
 * The `DTextField` is the vaadin implementation
 * of the [UTextField] specifications.
 *
 * @param model The row controller.
 * @param label The field label.
 * @param align The field alignment.
 * @param options The field options.
 * @param detail Does the field belongs to the detail view ?
 */
open class DTextField(
        model: VFieldUI,
        label: DLabel?,
        align: Int,
        options: Int,
        detail: Boolean,
) : DField(model, label, align, options, detail), UTextField {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private val field: TextField // the text component
  protected var inside = false
  protected var noEdit = options and VConstants.FDO_NOEDIT != 0
  protected var scanner = options and VConstants.FDO_NOECHO != 0 && getModel().height > 1
  private var selectionAfterUpdateDisabled = false
  private var updatedFromClient = false
  protected var transformer: ModelTransformer? = null

  init {
    transformer = if (getModel().height == 1 || (!scanner && ((getModel().typeOptions and VConstants.FDO_DYNAMIC_NL) > 0))) {
      DefaultTransformer(getModel().width, getModel().height)
    } else if (!scanner) {
      NewlineTransformer(getModel().width, getModel().height)
    } else {
      ScannerTransformer(this)
    }
    field = createFieldGUI(options and VConstants.FDO_NOECHO != 0, scanner, align)

    if (field.inputField.internalField is TextArea) {
      field.inputField.internalField.addValueChangeListener { event ->
        if (event.isFromClient) {
          updatedFromClient = true
          if (!getModel().hasFocus()) {
            getModel().block!!.gotoField(getModel())
          }
          checkTextSize(event.oldValue, event.value)
          valueChanged()
          updatedFromClient = false
        }
      }
    } else {
      field.inputField.addTextValueChangeListener {
        if (it.isFromClient) {
          if (!getModel().hasFocus()) {
            getModel().block!!.gotoField(getModel())
          }
          valueChanged()
        }
      }
    }

    createContextMenu()
    setFieldContent(field)
  }

  override fun valueChanged() {
    val value = text

    if (isChanged(getModel().getText(), value)) {
      checkText(value)
    }
  }

  /**
   * Returns `true` if there is a difference between the old and the new text.
   * @param oldText The old text value.
   * @param newText The new text value.
   * @return `true` if there is a difference between the old and the new text.
   */
  protected fun isChanged(oldText: String?, newText: String?): Boolean {
    return oldText != newText
  }

  // --------------------------------------------------
  // CREATE FIELD UI
  // --------------------------------------------------
  /**
   * Creates the field UI component.
   * @param noEcho Password field ?
   * @param scanner Scanner field ?
   * @param align The field alignment.
   * @return The [TextField] object.
   */
  private fun createFieldGUI(noEcho: Boolean,
                             scanner: Boolean,
                             align: Int): TextField {

    return TextField(getModel(),
                     noEcho,
                     scanner,
                     noEdit,
                     align,
                     model.hasAutofill(),
                     this)
  }

  /**
   * Check the Gui Text size to ensure its compatibility with the model requirements
   */
  private fun checkTextSize(oldValue: String?, newValue: String?) {
    if (field.inputField.internalField is TextArea) {
      val maxModelLength = getModel().width * getModel().height
      var modelText = transformer!!.toModel(newValue.orEmpty())
      val guiText = transformer!!.toGui(modelText)
      val textLines = guiText.split("\n").size

      println("DEBUG : GUI TXT lines = $textLines, MAX LINES = ${getModel().height}")
      if (textLines > getModel().height) {
        if (oldValue.isNullOrEmpty()) {
          modelText = modelText.take(maxModelLength)
          field.inputField.internalField.value = transformer!!.toGui(modelText)
        } else {
          field.inputField.internalField.value = oldValue
        }
      }
    }
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------7
  override fun updateAccess() {
    super.updateAccess()
    label!!.update(model, getBlockView().getRecordFromDisplayLine(position))
    access(currentUI) {
      field.isEnabled = access >= VConstants.ACS_VISIT
      isEnabled = access >= VConstants.ACS_VISIT
    }
  }

  override fun updateText() {
    val newModelTxt = getModel().getText(rowController.blockView.getRecordFromDisplayLine(position))
    if (!updatedFromClient && field.value?.trimEnd() != transformer!!.toGui(newModelTxt)?.trimEnd()) {
      access(currentUI) {
        field.value = transformer!!.toGui(newModelTxt)
      }
    }
    super.updateText()
    if (modelHasFocus() && !selectionAfterUpdateDisabled) {
      selectionAfterUpdateDisabled = false
    }
  }

  override fun updateColor() {
    styleManager.createAndApplyStyle(field.inputField, getModel().align, foreground, background)
  }

  override fun updateFocus() {
    label!!.update(model, position)
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
        leaveMe()
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
      }
    }
    super.updateFocus()
  }

  override fun forceFocus() {
    enterMe()
  }

  /**
   * Gets the focus to this field.
   */
  private fun enterMe() {
    access(currentUI) {
      if (scanner) {
        field.value = transformer!!.toGui("")
      }
      field.focus()
    }
  }

  /**
   * Leaves the field.
   */
  private fun leaveMe() {
    reInstallSelectionFocusListener()
    // update GUI: for
    // scanner nescessary
    if (scanner) {
      // trick: it is now displayed on a different way
      access(currentUI) {
        field.value = transformer!!.toModel(field.value.toString())
      }
    }
  }

  /**
   * Check the given text against model definition.
   *
   * @param s The text to be verified.
   * @throws VException Errors occurs during check.
   */
  private fun checkText(s: String?) {
    val text = transformer!!.toModel(s ?: "")
    if (!transformer!!.checkFormat(text)) {
      return
    }
    getModel().onTextChange(text!!)

    if (getModel().checkText(text)) {
      // affect value directly to the model.
      getModel().getForm().performAsyncAction(object : Action("check_type") {
        override fun execute() {
          getModel().checkType(text)
        }
      })
    }
  }

  //---------------------------------------------------
  // TEXTFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getText(): String? {
    return transformer!!.toModel(field.value.orEmpty())
  }

  override fun setHasCriticalValue(b: Boolean) {
    // ignore
  }

  override fun addSelectionFocusListener() {
    // ignore
  }

  override fun removeSelectionFocusListener() {
    // ignore
  }

  /**
   * Reinstalls the focus listener.
   */
  open fun reInstallSelectionFocusListener() {
    removeSelectionFocusListener()
    addSelectionFocusListener()
  }

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {
    selectionAfterUpdateDisabled = disable
  }

  //---------------------------------------------------
  // DFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getObject(): Any? {
    return wrappedField.value
  }

  override fun setBlink(blink: Boolean) {
    access(currentUI) {
      field.setBlink(blink)
    }
  }

  /**
   * Default implementation of the [ModelTransformer]
   *
   * @param col The column index.
   * @param row The row index.
   */
  inner class DefaultTransformer(var col: Int, var row: Int) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toGui(modelTxt: String?): String? {
      return modelTxt
    }

    override fun toModel(guiTxt: String?): String? {
      return guiTxt
    }

    override fun checkFormat(guiTxt: String?): Boolean {
      return if (row == 1) true else convertToSingleLine(guiTxt, col, row).length <= row * col
    }
  }

  /**
   * A scanner model transformer.
   */
  internal class ScannerTransformer(private val field: DTextField) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toGui(modelTxt: String?): String {
      return if (modelTxt == null || "" == modelTxt) {
        VlibProperties.getString("scan-ready")
      } else if (!field.field.isReadOnly) {
        VlibProperties.getString("scan-read") + " " + modelTxt
      } else {
        VlibProperties.getString("scan-finished")
      }
    }

    override fun toModel(guiTxt: String?): String? {
      return guiTxt
    }

    override fun checkFormat(guiTxt: String?): Boolean {
      return true
    }
  }

  /**
   * New line model transformer.
   *
   * @param col The column index.
   * @param row The row index.
   */
  inner class NewlineTransformer(private val col: Int, private val row: Int) : ModelTransformer {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun toModel(guiTxt: String?): String {
      return LineBreaker.textToModel(guiTxt, col, Int.MAX_VALUE)
    }

    override fun toGui(modelTxt: String?): String {
      return modelTxt?.let { LineBreaker.modelToText(modelTxt, col) }.orEmpty()
    }

    override fun checkFormat(guiTxt: String?): Boolean = guiTxt!!.length <= row * col
  }

  /**
   * Add the field context menu.
   */
  protected fun createContextMenu() {
    if (model.hasAutofill() && getModel().getDefaultAccess() > VConstants.ACS_SKIPPED) {
      val contextMenu = ContextMenu()
      contextMenu.addItem(VlibProperties.getString("item-index")) {
        performAutoFillAction()
      }
      //.setData(VlibProperties.getString("item-index")) TODO

      contextMenu.target = field
    }
  }

  companion object {
    /**
     * Converts a given string to a line string.
     * @param source The source text.
     * @param col The column index.
     * @param row The row index.
     * @return The converted string.
     */
    private fun convertToSingleLine(source: String?, col: Int, row: Int): String =
            buildString {
              val length = source!!.length
              var start = 0
              while (start < length) {
                var index = source.indexOf('\n', start)
                if (index - start < col && index != -1) {
                  append(source.substring(start, index))
                  for (j in index - start until col) {
                    append(' ')
                  }
                  start = index + 1
                  if (start == length) {
                    // last line ends with a "new line" -> add an empty line
                    for (j in 0 until col) {
                      append(' ')
                    }
                  }
                } else {
                  if (start + col >= length) {
                    append(source.substring(start, length))
                    for (j in length until start + col) {
                      append(' ')
                    }
                    start = length
                  } else {
                    // find white space to break line
                    var i = start + col - 1
                    while (i > start) {
                      if (Character.isWhitespace(source[i])) {
                        break
                      }
                      i--
                    }
                    index = if (i == start) {
                      start + col
                    } else {
                      i + 1
                    }
                    append(source.substring(start, index))
                    var j = (index - start) % col
                    while (j != 0 && j < col) {
                      append(' ')
                      j++
                    }
                    start = index
                  }
                }
              }
            }

    /**
     * Converts a given string to a fixed line string.
     * @param source The source text.
     * @param col The column index.
     * @param row The row index.
     * @return The converted string.
     */
    private fun convertFixedTextToSingleLine(source: String?, col: Int, row: Int): String =
            buildString {
              val length = source!!.length
              var start = 0
              while (start < length) {
                var index = source.indexOf('\n', start)
                if (index - start < col && index != -1) {
                  append(source.substring(start, index))
                  for (j in index - start until col) {
                    append(' ')
                  }
                  start = index + 1
                  if (start == length) {
                    // last line ends with a "new line" -> add an empty line
                    for (j in 0 until col) {
                      append(' ')
                    }
                  }
                } else {
                  if (start + col >= length) {
                    append(source.substring(start, length))
                    for (j in length until start + col) {
                      append(' ')
                    }
                    start = length
                  } else {
                    // find white space to break line
                    var i = start + col
                    while (i > start) {
                      if (Character.isWhitespace(source[i])) {
                        break
                      }
                      i--
                    }
                    index = if (i == start) {
                      start + col
                    } else {
                      i
                    }
                    append(source.substring(start, index))
                    for (j in index - start until col) {
                      append(' ')
                    }
                    start = index + 1
                  }
                }
              }
            }
  }
}
