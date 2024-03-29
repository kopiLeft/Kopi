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
package org.kopi.vkopi.lib.ui.vaadinflow.grid

import org.kopi.vkopi.lib.ui.vaadinflow.actor.Actor
import org.kopi.vkopi.lib.ui.vaadinflow.base.StyleManager
import org.kopi.vkopi.lib.ui.vaadinflow.form.DBlock
import org.kopi.vkopi.lib.ui.vaadinflow.form.DGridEditorField
import org.kopi.vkopi.lib.ui.vaadinflow.visual.VApplication
import org.kopi.vkopi.lib.ui.vaadinflow.window.Window
import org.kopi.vkopi.lib.visual.ApplicationContext
import org.kopi.vkopi.lib.visual.VColor

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.converter.Converter

/**
 * A grid editor field implementation.
 */
abstract class GridEditorField<T> protected constructor() : AbstractCompositeField<Component, GridEditorField<T>, T>(null),
  Focusable<GridEditorField<T>>,
  HasSize,
  ClickNotifier<GridEditorField<T>>, HasStyle {

  lateinit var dGridEditorField: DGridEditorField<*>

  /**
   * The navigation delegation to server mode. Default to [NavigationDelegationMode.ALWAYS].
   */
  var navigationDelegationMode = NavigationDelegationMode.ALWAYS

  /**
   * Tells that this field has a PREFLD trigger. This will tell that
   * the navigation should be delegated to server if the next target
   * field has a PREFLD trigger.
   */
  var hasPreFieldTrigger = false

  /**
   * The actors associated with this field.
   */
  var actors: MutableList<Actor> = mutableListOf()

  protected val styleManager: StyleManager by lazy {
    (ApplicationContext.getApplicationContext().getApplication() as VApplication).styleManager
  }

  /**
   * The navigation delegation to server mode.
   */
  enum class NavigationDelegationMode {
    /**
     * do not delegate navigation to server
     */
    NONE,

    /**
     * delegate navigation to server if the content of this field has changed
     */
    ONCHANGE,

    /**
     * delegate navigation to server side when the field is not empty.
     */
    ONVALUE,

    /**
     * Always delegate navigation to server.
     */
    ALWAYS
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    setWidthFull()
    //registerRpc(NavigationRpcHandler())
    //registerRpc(ClickRpcHandler())
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the color properties of this editor field.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  abstract fun setColor(align: Int, foreground: VColor?, background: VColor?)

  override fun focus() {
    parentWindow?.lasFocusedField = this
    doFocus()
  }

  abstract fun doFocus()

  abstract fun addFocusListener(focusFunction: () -> Unit)

  /**
   * Sets the blink state of this editor field.
   * @param blink The blink state.
   */
  abstract fun setBlink(blink: Boolean)

  fun setConverter(converter: Converter<T, Any?>) {
    val binder = Binder(String::class.java)

    binder.forField(this)
      .withConverter(converter)
      .bind({ it }, { _, _ -> })
  }

  /**
   * Returns the parent window of this text editor.
   * @return The parent window of this text editor.
   */
  val parentWindow: Window?
    get() = (dGridEditorField.columnView.blockView as? DBlock)?.parent

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * The grid editor field navigation listener
   */
  interface NavigationListener {
    /**
     * Fired when a goto next field event is called by the user.
     */
    fun onGotoNextField()

    /**
     * Fired when a goto previous field event is called by the user.
     */
    fun onGotoPrevField()

    /**
     * Fired when a goto next block event is called by the user.
     */
    fun onGotoNextBlock()

    /**
     * Fired when a goto previous record event is called by the user.
     */
    fun onGotoPrevRecord()

    /**
     * Fired when a goto next field event is called by the user.
     */
    fun onGotoNextRecord()

    /**
     * Fired when a goto first record event is called by the user.
     */
    fun onGotoFirstRecord()

    /**
     * Fired when a goto last record event is called by the user.
     */
    fun onGotoLastRecord()

    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     */
    fun onGotoNextEmptyMustfill()
  }

  /**
   * The click listener for grid editor fields
   */
  interface ClickListener {
    /**
     * Fired when a click event is detected on editor field.
     * @param event The click event object.
     */
    fun onClick(event: ClickEvent<*>?)
  }

  interface AutofillListener {
    /**
     * Fired when an autofill action is launched on the editor
     */
    fun onAutofill()
  }
}
