// ----------------------------------------------------------------------
// Copyright (c) 2013-2023 kopiLeft Services SARL, Tunisie
// Copyright (c) 2018-2023 ProGmag SAS, France
// ----------------------------------------------------------------------
// All rights reserved - tous droits réservés.
// ----------------------------------------------------------------------

package org.kopi.gradle.tasks

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the

/**
 * copying properties task class
 */
open class CopyResources : DefaultTask() {
  @OutputDirectory
  var dest: String = ""

  /**
   * Find List of resources to be copied
   */
  fun findResources(name: String, isDirectory: Boolean) : List<String>? {
    val resources: List<String>?
    val currentFolder = project.the<SourceSetContainer>()["main"].allJava.srcDirs.first { it.path.endsWith("java") }.path

    Files.walk(Paths.get(currentFolder)).use {
      resources = it
        .filter { p -> Files.isDirectory(p) == isDirectory }
        .map { p -> p.toString() }
        .filter {f -> f.endsWith(name)}
        .toList()
    }

    return resources
  }

  /**
   * Copy all files in the directory "resources"
   */
  fun copyResources() {
    val name = "resources"
    var resources: MutableList<String> = mutableListOf()

    findResources(name, true)?.forEach {
      Files.walk(Paths.get(it)).use { p ->
        resources.addAll(p
          .filter { f  -> !Files.isDirectory(f) } // Une répertoire
          .map { f -> f.toString() } // convertir path en string
          .toList()) // collecte toutes les correspondances
      }
    }

    resources.forEach {
      println("Files to be copied : Copy $it into $dest/META-INF")
      project.copy {
        from(it)
        into("$dest/META-INF")
      }
    }
  }

  @TaskAction
  fun run() {
    copyResources()
  }
}

fun Project.copyResources(action: Action<CopyResources>) {
  val rootTask = "copyResources"

  tasks.register<CopyResources>("$rootTask") {
    action.execute(this)
  }
}
