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
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.JavaExec
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
 * lexinc task class
 */
open class Lexinc : JavaExec() {
  @Incremental
  @InputFiles
  var scannerFlexInFiles: FileCollection = project.files()

  @InputFiles
  var grmvocFiles: List<String> = listOf()
  get() = field.map { "${it}FlexRules.txt" }

  @OutputFiles
  fun getScannerFlexFiles() = scannerFlexInFiles.map { it.nameWithoutExtension }

  @OutputDirectory
  var currentDir = "."

  init {
    main = "org.kopi.compiler.tools.include.Main"
  }

  override fun exec() {}

  @TaskAction
  fun exec(inputChanges: InputChanges) {
    workingDir = project.file(currentDir)
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath

    val changedFlexInFiles = if (inputChanges.isIncremental) inputChanges.getFileChanges(scannerFlexInFiles).map { it.file.path } else scannerFlexInFiles.map { it.path }

    args(listOfArgs(changedFlexInFiles ,"-o" , getScannerFlexFiles()))

    if (changedFlexInFiles.isNotEmpty()) {
      println("(lexinc) Files to be compiled : $changedFlexInFiles")
    }

    println(args)
    super.exec()
  }
}

fun Project.lexinc(rootTask: String, packageName: String, action: Action<Lexinc>) {
  tasks.register<Lexinc>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
