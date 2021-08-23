// ----------------------------------------------------------------------
// Copyright (c) 1990-2020 kopiLeft Services SARL, Tunisie
// Copyright (c) 1964-2020 SAS Service Recherche Développement, France
// ----------------------------------------------------------------------
// All rights reserved - tous droits réservés.
// ----------------------------------------------------------------------
// $Id: Resources.kt 35442 2020-06-04 17:25:51Z hfazai $
// ----------------------------------------------------------------------

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
 * copying resources task class
 */
open class Resources : DefaultTask() {
  @OutputDirectory
  var resourceDestDir = ""

  @Incremental
  @InputFiles
  var resources: FileCollection = project.files()

  @TaskAction
  fun run(inputChanges: InputChanges) {

    val changedFiles = if (inputChanges.isIncremental) inputChanges.getFileChanges(resources).map { it.file.path } else resources.map { it.path }

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
