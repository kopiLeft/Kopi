package org.kopi.vkopi.lib.ui.vaadinflow.base

import java.net.InetAddress
import java.net.UnknownHostException

import com.vaadin.flow.server.VaadinRequest


object ClientRequest {

  init {
    println("ClientRequest initialized: ${this.hashCode()}")
  }
  /**
   * Retrieves the client's IP address using Vaadin's request.
   *
   * @return The client IP address or null if unavailable.
   */
  @JvmStatic
  fun getClientIp(): String? {
    println("Request in getClientIp(): $request")
    request?.let {
      println("Remote address: ${it.remoteAddr}")
      println("X-Forwarded-For header: ${it.getHeader("X-Forwarded-For")}")
    }
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
   * Retrieves the client's Hostname using Vaadin's request.
   *
   * @return The client Hostname or getClientIp() if unavailable.
   */
  @JvmStatic
  fun getClientHostname(): String? {
    println("ipAdress: $ipAdress")
    println("getClientIp(): ${getClientIp()}")
    val clientIp = if (ipAdress.isNullOrEmpty()) getClientIp() else ipAdress
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

  @JvmStatic
  fun hasRequest(): Boolean {
    return request != null
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  @JvmStatic
  var request: VaadinRequest? = null
    set(value) {
      println("Request is being set: $value")
      field = value
    }
  @JvmStatic
  var ipAdress : String? = null
}
