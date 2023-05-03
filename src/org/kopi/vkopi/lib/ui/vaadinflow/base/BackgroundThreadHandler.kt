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

import java.util.concurrent.ExecutionException

import com.vaadin.flow.component.UI

/**
 * Collects some utilities for background threads in a vaadin application.
 *
 *
 * Note that all performed background tasks are followed by an client UI update
 * using the push mechanism incorporated with vaadin.
 *
 */
object BackgroundThreadHandler {

  /**
   * Exclusive access to the UI from a background thread to perform some updates.
   * @param command the command which accesses the UI.
   */
  fun access(currentUI: UI? = null, command: () -> Unit) {
    if (UI.getCurrent() != null) {
      command()

      return
    }

    val currentUI = currentUI ?: locateUI()

    if (currentUI == null) {
      command()
    } else {
      currentUI.access(command)
    }
  }

  /**
   * Exclusive access to the UI from a background thread to perform some updates.
   * @param command the command which accesses the UI.
   */
  fun accessAndPush(currentUI: UI? = null, command: () -> Unit) {
    println("========== IN ACCESS METHOD ===========  ")
    if (UI.getCurrent() != null) {
      println("**** **** ***** UI.getCurrent() is not null here is the value : ${UI.getCurrent().page}")
      println(" ====== currentUI is attached ? =+>  ${UI.getCurrent().isAttached}")

      UI.getCurrent().access {
        command()
        UI.getCurrent().push()
      }

      return
    }

    val currentUI = currentUI ?: locateUI()
    if (currentUI == null) {
      command()
    } else {
      println(" ====== currentUI is attached ? =+>  ${currentUI.isAttached}")
      println(" ====== currentUI element text ? =+>  ${currentUI.element.text}")
      currentUI.access {
        try {
          command()
        } finally {
          println("before pushing the ui")
          currentUI.push()
          println( " ************* push config ************ ${currentUI.pushConfiguration.fallbackTransport}")
        }
      }
    }
  }

  /**
   * Exclusive access to the UI from a background thread to perform some updates.
   *
   * This will awaits until computation completes.
   *
   * This method is used when you are creating a Vaadin component from a background thread. This will wait until
   * initialization is finished to avoid NPE later.
   *
   *
   * @param command the command which accesses the UI.
   */
  fun accessAndAwait(currentUI: UI? = null, command: () -> Unit) {
    if (UI.getCurrent() != null) {
      command()

      return
    }

    val currentUI = currentUI ?: locateUI()

    if (currentUI == null) {
      command()
    } else {
      try {
        currentUI
          .access(command)
          .get()
      } catch (executionException: ExecutionException) {
        executionException.cause?.let {
          throw it
        }
      }
    }
  }

  /**
   * Starts a task asynchronously and blocks the current thread. The lock will be released
   * if a notify signal is send to the blocking object.
   *
   * @param lock      The lock object.
   * @param command   The command which accesses the UI.
   */
  fun startAndWait(lock: Object, currentUI: UI? = null, command: () -> Unit) {
    access(currentUI = currentUI, command = command)

    synchronized(lock) {
      try {
        lock.wait()
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Starts a task asynchronously and blocks the current thread. The lock will be released
   * if a notify signal is send to the blocking object.
   *
   * @param lock      The lock object.
   * @param command   The command which accesses the UI.
   */
  fun startAndWaitAndPush(lock: Object, currentUI: UI? = null, command: () -> Unit) {
    println("==== ==== ==== In startAndWaitAndPush() ==== ==== ==== ")
    accessAndPush(currentUI = currentUI, command = command)
    println("======= lock =====> $lock")
    synchronized(lock) {
      try {
        lock.wait()
        println("=============================== after lock.wait() ========================")
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Releases the lock based on an object.
   * @param lock The lock object.
   */
  fun releaseLock(lock: Object) {
    println("=========== ============ releaseLock ========== ==========")
    println("====== lock: =====$lock")
    synchronized(lock) {
      lock.notifyAll()
      println("notifying all => lock released #######")
    }
  }

  fun setUI(ui: UI?) {
    uiThreadLocal.set(ui)
  }

  fun updateUI(ui: UI?) {
    ui?.accessSynchronously {
      ui.push()
    }
  }

  fun locateUI(): UI? = UI.getCurrent() ?: uiThreadLocal.get()

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val uiThreadLocal = ThreadLocal<UI?>()
}
