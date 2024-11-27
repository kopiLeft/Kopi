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
package org.kopi.vkopi.lib.ui.vaadinflow.base

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.html.Input
import com.vaadin.flow.server.VaadinService
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * An input element type button that cannot handle icons.
 */
open class VInputButton(caption: String? = null) : Input(), ClickNotifier<VInputButton> {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------

  /**
   * Creates a new input button.
   */
  init {
    type = "button"
    if (caption != null) {
      value = caption
    }
    className = Styles.INPUT_BUTTON
    element.setAttribute("hideFocus", "true")
    element.style["outline"] = "0px"

    addClickListener { event -> handleClientIpRetrieval(event) }
  }

  /**
   * Creates a new button component.
   *
   * @param caption The button caption.
   * @param clickListener The click listener.
   */
  constructor(caption: String?, clickListener: ComponentEventListener<ClickEvent<VInputButton?>?>?) : this(caption) {
    addClickListener(clickListener)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Fires a click for this button.
   */
  fun click() {
    fireEvent(ClickEvent<VInputButton>(this))
  }

  /**
   * The button caption
   */
  var caption: String?
    get() = value
    set(caption) {
      value = caption
    }

  /**
   * sets the name of the component.
   *
   * @return The name.
   */
  fun setName(name: String?) {
    element.setAttribute("name", name)
  }

  /**
   * Handles client IP retrieval during a button click.
   */
  private fun handleClientIpRetrieval(event: ClickEvent<VInputButton>) {
    println("Button clicked: ${event.source.caption}")
    val clientHostname = getClientHostname()
    if (clientHostname != null) {
      println("Client Hostname/IP: $clientHostname")
    } else {
      println("Failed to retrieve client Hostname/IP.")
    }
  }

  /**
   * Retrieves the client's IP address using Vaadin's request.
   *
   * @return The client IP address or null if unavailable.
   */
  private fun getClientIp(): String? {
    val request = VaadinService.getCurrentRequest()
    return request?.let {
      val forwardedFor = it.getHeader("X-Forwarded-For")
      if (!forwardedFor.isNullOrEmpty()) {
        forwardedFor.split(",").first().trim()
      } else {
        it.remoteAddr
      }
    }
  }

  private fun getClientHostname(): String? {
    val clientIp = getClientIp() // Reuse the getClientIp() function to get the IP
    return if (clientIp != null) {
      try {
        // Use InetAddress to resolve the IP to a hostname
        val inetAddress = InetAddress.getByName(clientIp)
        inetAddress.hostName
      } catch (e: UnknownHostException) {
        clientIp // Return clientIp if DNS resolution fails
      }
    } else {
      null
    }
  }
}
