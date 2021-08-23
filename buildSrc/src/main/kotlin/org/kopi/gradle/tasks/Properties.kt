// ----------------------------------------------------------------------
// Copyright (c) 1990-2020 kopiLeft Services SARL, Tunisie
// Copyright (c) 1964-2020 SAS Service Recherche Développement, France
// ----------------------------------------------------------------------
// All rights reserved - tous droits réservés.
// ----------------------------------------------------------------------
// $Id: Properties.kt 35442 2020-06-04 17:25:51Z hfazai $
// ----------------------------------------------------------------------

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
