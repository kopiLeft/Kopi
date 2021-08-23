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
 * $Id: XkjcCompiler.kt 35442 2020-06-04 17:25:51Z hfazai $
 */

package org.kopi.gradle.tasks

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
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

import org.kopi.gradle.common.listOfArgs
import org.kopi.gradle.common.makeTaskDependencies

/**
 * xkjc task class
 */
open class XkjcCompiler : JavaExec() {
  @Incremental
  @InputFiles
  var xkjcFiles: FileCollection = project.files()

  @Input
  var verbose = false

  @Input
  var noo = false

  @Input
  var xkjcPath = ""

  @OutputDirectory
  var classRoot = ""

  @Input
  var currentDir = "."

  init {
    main = "org.kopi.xkopi.comp.xkjc.Main"
  }

  override fun exec() {}

  @TaskAction
  fun exec(inputChanges: InputChanges) {
    workingDir = project.file(currentDir)
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath

    val changedFiles = if (inputChanges.isIncremental) inputChanges.getFileChanges(xkjcFiles).map { it.file.path } else xkjcFiles.map { it.path }

    if(verbose) {
      args("--verbose")
    }

    if(noo) {
      args("--nooo")
    }

    if(xkjcPath.isNotEmpty()) {
      args(listOf("--xkjcpath", xkjcPath))
    }

    args(listOfArgs("-d", classRoot, changedFiles))

    if (changedFiles.isNotEmpty()) {
      println("(xkjc) Files to be compiled : $changedFiles")
    }

    super.exec()
  }
}

fun Project.xkjc(packageName: String, action: Action<XkjcCompiler>) {
  val rootTask = "xkjc"
  tasks.register<XkjcCompiler>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
