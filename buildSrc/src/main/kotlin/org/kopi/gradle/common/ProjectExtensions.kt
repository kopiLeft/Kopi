/*
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: ProjectExtensions.kt 35445 2020-06-06 01:47:41Z hfazai $
 */

package org.kopi.gradle.common

import org.gradle.api.Project
import java.io.File
import java.nio.file.Files

/**
 * removes the file with name [fileName] existing in [folder]
 */
fun Project.clean(fileName: String, folder: String?) {
  var success: Boolean? = null
  val fileToClean = if(folder != null) {
    file(folder + File.separator + fileName)
  } else {
    file(fileName)
  }
  when {
    fileToClean.isDirectory -> success = fileToClean.deleteRecursively()
    fileToClean.isFile -> success = fileToClean.delete()
    Files.isSymbolicLink(fileToClean.toPath()) -> success = fileToClean.delete()
    folder != null -> {
      val files = fileTree(folder)
      files.include("**/$fileName")
      files.forEach {
        success = it.deleteRecursively()
      }
    }
  }
  if (success != null && success!!) {
    println("${fileToClean.path} is deleted")
  } else if (success != null) {
    println("Unable to delete ${fileToClean.path}")
  }
}

/**
 * Makes dependencies between tasks to build modules in the right order.
 */
fun Project.makeTaskDependencies(packageName: String, rootTaskName: String) {
  val rootTask = tasks.named(rootTaskName).get()
  val currentTaskName = "$packageName.$rootTaskName"
  val currentTask = tasks.named(currentTaskName).get()
  val lastTask = tasks.find { it.name == lastTaskName }
  val lastTaskWithSameRoot = tasks.find { it.name == taskMappings.getOrPut(rootTaskName, { "" }) }

  rootTask.dependsOn(currentTaskName)
  if(currentTask != lastTask && lastTask != null) {
    currentTask.mustRunAfter(lastTask)
  }
  lastTaskName = currentTaskName

  if(currentTask != lastTaskWithSameRoot && lastTaskWithSameRoot != null) {
    currentTask.mustRunAfter(lastTaskWithSameRoot)
  }
  taskMappings.replace(rootTaskName, currentTaskName)
}

private var lastTaskName = ""
private var taskMappings = mutableMapOf<String, String>()
