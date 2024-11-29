package org.kopi.vkopi.lib.ui.vaadinflow.base

import java.net.InetAddress
import java.net.UnknownHostException

import com.vaadin.flow.server.VaadinRequest


object ClientRequest {

  /**
   * Retrieves the client's Hostname using Vaadin's request.
   *
   * @return The client Hostname or null if unavailable.
   */
  @JvmStatic
  fun getClientHostname(): String? {
    val clientIp = if (hostnameOrIp.isNullOrEmpty()) getClientIp() else hostnameOrIp
    return if (clientIp != null) {
      try {
        val inetAddress = InetAddress.getByName(clientIp)
        inetAddress.hostName
      } catch (e: UnknownHostException) {
        clientIp // Return clientIp if DNS resolution fails
      }
    } else {
      null
    }
  }

  /**
   * Retrieves the client's IP address using Vaadin's request.
   *
   * @return The client IP address or null if unavailable.
   */
  @JvmStatic
  fun getClientIp(): String? {
    return request?.let {
      val forwardedFor = it.getHeader("X-Forwarded-For")
      if (!forwardedFor.isNullOrEmpty()) {
        forwardedFor.split(",").first().trim()
      } else {
        it.remoteAddr
      }
    }
  }

  /**
   * Retrieves request Status.
   *
   * @return true if request is detected else false.
   */
  @JvmStatic
  fun hasRequest(): Boolean {
    return request != null
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  @JvmStatic
  var request: VaadinRequest? = null
  @JvmStatic
  var hostnameOrIp : String? = null
}
