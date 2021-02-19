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
package org.kopi.galite.ui.vaadin.notif

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.common.VSpan

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.componentfactory.theme.EnhancedDialogVariant
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * An abstract implementation of notification components such as
 * warnings, errors, confirms and information.
 *
 * @param title the notification title.
 * @param message the notification message.
 * @param locale  the notification locale
 */
abstract class AbstractNotification(title: String?,
                                    message: String?,
                                    protected val locale: String)
  : EnhancedDialog(), Focusable<AbstractNotification> {

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
  protected fun fireOnClose(action: Boolean) {
    for (l in listeners) {
      l.onClose(action)
    }
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

  /**
   * Creates the notification content.
   */
  fun createContent() {
    content.addComponentAsFirst(icon)
    content.add(message)
    super.setContent(content)
  }

  /**
   * Should we go back to the last focused field when the notification is closed ?
   * @return `true` if we should go back to the last focused field when the notification is closed.
   */
  protected fun goBackToLastFocusedWindow(): Boolean {
    return true
  }

  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------
  /**
   * Sets the notification buttons.
   * @param locale The notification locale.
   */
  abstract fun setButtons()

  /**
   * The icon name to be used with this notification.
   */
  protected abstract val iconName: VaadinIcon

  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  private var listeners = mutableListOf<NotificationListener>()
  private val icon = Icon(iconName)
  private val title = H3(title)
  private var content = Div()
  private var message = VSpan()
  protected var buttons = Div()
  internal var yesIsDefault = false
  val footer = Div()

  init {
    element.setAttribute("hideFocus", true)
    element.style["outline"] = "0px"
    super.addThemeVariants(EnhancedDialogVariant.SIZE_SMALL)
    isDraggable = true
    this.message.className = Styles.NOTIFICATION_MESSAGE
    buttons.className = Styles.NOTIFICATION_BUTTONS

    super.setHeader(this.title)
    content.add(icon)
    setNotificationMessage(message)
    content.add(this.message)
    content.add(buttons)
    super.setContent(content)
    footer.add(buttons)
    this.setButtons()
    super.setFooter(footer)
  }
}
