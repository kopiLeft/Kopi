/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import java.io.File

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
 * Class of the task copying resource files to a destination directory.
 */
open class Resources : DefaultTask() {
  @OutputDirectory
  var resourceDestDir = ""

  @Incremental
  @InputFiles
  var resources: FileCollection = project.files()

  @TaskAction
  fun run(inputChanges: InputChanges) {

    val changedFiles = resources.map { it.path }

    if (changedFiles.isNotEmpty()) {
      println("Files to be copied from $changedFiles into : $resourceDestDir")
    }

    changedFiles.forEach {
      if(!File(it).exists()) {
        error("File not found : $it")
      }
    }

    project.copy {
      from(changedFiles)
      into(resourceDestDir)
    }
  }
}

fun Project.resources(packageName: String, action: Action<Resources>) {
  val rootTask = "resources"
  tasks.register<Resources>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
