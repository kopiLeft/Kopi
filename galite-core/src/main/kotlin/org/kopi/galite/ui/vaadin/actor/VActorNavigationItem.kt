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
package org.kopi.galite.ui.vaadin.actor

import org.kopi.galite.ui.vaadin.menu.VNavigationMenu

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.flowingcode.vaadin.addons.ironicons.IronIcons

/**
 * Constructs a new actor navigation menu item that fires a command when it is selected.
 *
 * @param text the item's text
 * @param menu The parent menu name of this menu item.
 * @param acceleratorKey The accelerator key description.
 * @param icon The menu item icon.
 */
class VActorNavigationItem(text: String,
                           val menu: String?,
                           acceleratorKey: Key?,
                           keyModifier: KeyModifier?,
                           icon: Any?,
                           val navigationmenu : VNavigationMenu,
                           val action: () -> Unit) : Button() {

  init {
    if (icon is VaadinIcon) {
      setIcon(Icon(icon))
    } else if (icon is IronIcons) {
      setIcon(icon.create())
    }

    addClickListener {
      action()
      navigationmenu.close()
    }

    if (acceleratorKey != null && acceleratorKey != Key.UNIDENTIFIED) {
      val modifier = keyModifier?.keys?.get(0)

      setText(text + if(modifier != null) modifier + "-" + acceleratorKey.keys[0] else " " + acceleratorKey.keys[0])
    } else {
      setText(text)
    }

    className = getClassname()
  }

  protected fun getClassname(): String {
    return "actor-navigationItem"
  }
}
