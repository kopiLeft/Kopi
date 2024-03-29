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
package org.kopi.vkopi.lib.ui.vaadinflow.notif

import org.kopi.vkopi.lib.ui.vaadinflow.base.Styles
import org.kopi.vkopi.lib.ui.vaadinflow.base.Utils.findMainWindow
import org.kopi.vkopi.lib.ui.vaadinflow.common.Dialog
import org.kopi.vkopi.lib.ui.vaadinflow.common.VSpan
import org.kopi.vkopi.lib.ui.vaadinflow.window.Window

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * An abstract implementation of notification components such as
 * warnings, errors, confirms and information.
 *
 * @param title the notification title.
 * @param message the notification message.
 * @param locale  the notification locale
 */

@CssImport.Container(value = [
  CssImport("./styles/galite/notification.css"),
  CssImport("./styles/galite/notification.css" , themeFor = "vcf-enhanced-dialog-overlay")
])
abstract class AbstractNotification(title: String?,
                                    message: String?,
                                    protected val locale: String,
                                    val parent: Component?)
  : Dialog(), Focusable<AbstractNotification> {

  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  private var listeners = mutableListOf<NotificationListener>()
  private val icon = Icon(iconName)
  private val title = H3(title)
  private var content = HorizontalLayout()
  private var message = VSpan()
  protected var buttons = Div()
  internal var yesIsDefault = false
  val footer = Div()

  init {
    element.classList.add("notification")
    element.themeList.add("notification")
    element.setAttribute("hideFocus", true)
    element.style["outline"] = "0px"
    isDraggable = true
    isCloseOnOutsideClick = false
    isCloseOnEsc = false
    this.message.className = Styles.NOTIFICATION_MESSAGE
    this.message.style["white-space"] = "nowrap"
    buttons.className = Styles.NOTIFICATION_BUTTONS
    this.title.className = "k-notification-title"
    this.content.className = "k-notification-content"
    content.justifyContentMode = FlexComponent.JustifyContentMode.CENTER

    setHeader(this.title)
    setNotificationMessage(message)
    icon.setSize("2.8em")
    icon.className = "k-notification-icon"
    content.add(icon)
    content.add(this.message)
    setContent(content)
    footer.add(buttons)
    setButtons()
    setFooter(footer)
  }

  /**
   * Shows the notification popup.
   */
  fun show() {
    open()
  }

  /**
   * Registers a new notification listener.
   * @param l The listener to be added.
   */
  fun addNotificationListener(l: NotificationListener) {
    listeners.add(l)
  }

  /**
   * Removes a new notification listener.
   * @param l The listener to be removed.
   */
  fun removeNotificationListener(l: NotificationListener) {
    listeners.remove(l)
  }

  /**
   * Fires a close event.
   * @param action The user action.
   */
  protected fun fireOnClose(action: Boolean?) {
    val lastActiveWindow = parent?.findMainWindow()?.currentWindow as? Window

    close()

    for (l in listeners) {
      l.onClose(action)
    }

    lastActiveWindow?.goBackToLastFocusedTextField()
  }

  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------
  /**
   * Sets the notification message.
   *
   * @param text The notification message.
   */
  private fun setNotificationMessage(text: String?) {
    if (text != null) {
      message.setHtml(text.replace("\n".toRegex(), "<br>").replace("<br><br>".toRegex(), "<br>"))
    }
  }


  override fun onAttach(attachEvent: AttachEvent?) {
    super.onAttach(attachEvent)

    val focused = getDefaultButton()

    focused.focus()
    // Delay setting focus-ring attribute because initially the focus ring doesn't appear on the button.
    // This happens when the button is inside a dialog.
    focused.element.executeJs("setTimeout(function(){$0.setAttribute('focus-ring', '');},60)", focused.element)

    setNavigationListeners()
  }

  //-------------------------------------------------
  // ABSTRACT METHODS / PROPERTIES
  //-------------------------------------------------
  /**
   * Sets the notification buttons.
   */
  abstract fun setButtons()

  /**
   * The icon name to be used with this notification.
   */
  protected abstract val iconName: VaadinIcon

  internal abstract fun setNavigationListeners()

  internal abstract fun getDefaultButton(): Button

  companion object {
    const val CLICK_OK = 0
    const val CLICK_CANCEL = 1
    const val CLICK_CLOSE = 2
  }
}
