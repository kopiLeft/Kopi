// ----------------------------------------------------------------------
// Copyright (c) 1990-2020 kopiLeft Services SARL, Tunisie
// Copyright (c) 1964-2020 SAS Service Recherche Développement, France
// ----------------------------------------------------------------------
// All rights reserved - tous droits réservés.
// ----------------------------------------------------------------------
// $Id: Jcc.kt 35442 2020-06-04 17:25:51Z hfazai $
// ----------------------------------------------------------------------

package org.kopi.gradle.tasks

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.kopi.gradle.common.makeTaskDependencies

/**
 * jcc task class
 */
open class Jcc : JavaExec() {
  @Incremental
  @InputFiles
  var grammarFiles: FileCollection = project.files()

  @OutputDirectory
  var currentDir = "."

  init {
    main = "org.kopi.compiler.tools.antlr.compiler.Main"
    jvmArgs("-mx128m")
  }

  override fun exec() {}

  @TaskAction
  fun exec(inputChanges: InputChanges) {
    workingDir = project.file(currentDir)
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath

    val changedFiles = if (inputChanges.isIncremental) inputChanges.getFileChanges(grammarFiles).map { it.file.path } else grammarFiles.map { it.path }

    args(changedFiles)

    if (changedFiles.isNotEmpty()) {
      println("(jcc) Files to be compiled : $changedFiles")
    }

    super.exec()
  }
}

fun Project.jcc(rootTask: String, packageName: String, action: Action<Jcc>) {
  tasks.register<Jcc>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
