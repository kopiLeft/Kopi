package org.kopi.vkopi.lib.util.base

import java.util.*

class Utils {
  companion object {
    /**
     * Executes a task after some delay.
     *
     * @param delay   the delay.
     * @param task    the task to execute.
     */
    fun doAfter(delay: Long, task: () -> Unit) {
      Timer().schedule(
        object : TimerTask() {
          override fun run() {
            task()
          }
        },
        delay
      )
    }
  }
}
