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
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

import org.kopi.gradle.common.makeTaskDependencies

/**
 * messageGen task class
 */
open class MessageGen : JavaExec() {
  @Incremental
  @InputFiles
  var messageFiles: FileCollection = project.files()

  @OutputDirectory
  var currentDir = ""

  init {
    main = "org.kopi.compiler.tools.msggen.Main"
  }
  override fun exec() {}

  @TaskAction
  fun exec(inputChanges: InputChanges) {
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
    workingDir = project.file(currentDir)

    val changedFiles = if (inputChanges.isIncremental) inputChanges.getFileChanges(messageFiles).map { it.file.path } else messageFiles.map { it.path }

    changedFiles.forEach {
      args = listOf(it)

      if (changedFiles.isNotEmpty()) {
        println("(messageGen) Message file to be compiled : $it")
      }

      super.exec()
    }
  }
}

fun Project.messageGen(packageName: String, action: Action<MessageGen>) {
  val rootTask = "messageGen"
  tasks.register<MessageGen>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
