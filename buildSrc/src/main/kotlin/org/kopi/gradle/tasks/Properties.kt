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
package org.kopi.gradle.tasks

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

import org.kopi.gradle.common.makeTaskDependencies

/**
 * copying properties task class
 */
open class Properties : DefaultTask() {
  @OutputDirectory
  var propertiesDest: String = ""

  @Incremental
  @InputFiles
  var properties: FileCollection = project.files()

  @TaskAction
  fun run(inputChanges: InputChanges) {

    val changedFiles = if (inputChanges.isIncremental) inputChanges.getFileChanges(properties).map { it.file.path } else properties.map { it.path }

    if (changedFiles.isNotEmpty()) {
      println("Files to be copied : $changedFiles into : $propertiesDest")
    }

    project.copy {
      from(changedFiles)
      into(propertiesDest)
    }
  }
}

fun Project.copyProperties(packageName: String, action: Action<Properties>) {
  val rootTask = "copyProperties"
  tasks.register<Properties>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
