// ----------------------------------------------------------------------
// Copyright (c) 1990-2020 kopiLeft Services SARL, Tunisie
// Copyright (c) 1964-2020 SAS Service Recherche Développement, France
// ----------------------------------------------------------------------
// All rights reserved - tous droits réservés.
// ----------------------------------------------------------------------
// $Id: Lexinc.kt 35444 2020-06-05 17:41:54Z hfazai $
// ----------------------------------------------------------------------

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
