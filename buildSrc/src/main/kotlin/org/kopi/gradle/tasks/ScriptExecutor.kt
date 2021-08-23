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
 * $Id: ScriptExecutor.kt 35445 2020-06-06 01:47:41Z hfazai $
 */

package org.kopi.gradle.tasks

import java.util.Locale

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

import org.kopi.gradle.common.listOfArgs
import org.kopi.gradle.common.makeTaskDependencies
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * scriptExecutor task class
 */
open class ScriptExecutor : Exec() {
  @InputFiles
  var inputFiles = listOf<String>()

  @Input
  var script = ""

  @Input
  var arguments = listOf<String>()

  @OutputFile
  @Optional
  var outputFile: String? = null

  @OutputDirectory
  var currentDir = "."

  @TaskAction
  override fun exec() {
    val commandOutput = ByteArrayOutputStream()
    standardOutput = commandOutput
    workingDir = project.file(currentDir)


    if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
      commandLine = listOfArgs("cmd", "/c", script, arguments, project.files(inputFiles).map { it.name })
    } else {
      executable = "sh"
      args(listOfArgs("./$script", arguments, project.files(inputFiles).map { it.name }))
    }

    println("(scriptExecutor) command : ${commandLine.joinToString(" ")}")

    super.exec()

    if(outputFile == null) {
      println(commandOutput)
    } else {
      val fileOutputStream = project.file(currentDir + File.separator + outputFile)
      fileOutputStream.writeText(commandOutput.toString())
      println("======= generated to $currentDir${File.separator}$outputFile =======")
    }
  }
}

fun Project.scriptExecutor(packageName: String, action: Action<ScriptExecutor>) {
  val rootTask = "script"
  tasks.register<ScriptExecutor>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
