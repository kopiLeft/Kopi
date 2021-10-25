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

import org.gradle.api.Action
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.work.InputChanges

import org.kopi.gradle.common.makeTaskDependencies

/**
 * Javac compiler class
 */
open class Javac : JavaCompile() {

  init {
    classpath = project.files()
  }

  @TaskAction
  override fun compile(inputs: InputChanges?) {
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
    super.compile(inputs)
  }
}

fun Project.javac() =
        tasks.register<Javac>("javac")

val Project.javac: TaskProvider<Javac>
  get() = tasks.named<Javac>("javac")

fun Project.javac(packageName: String, action: Action<Javac>) {
  val rootTask = "javac"
  tasks.register<Javac>("$packageName.$rootTask") {
    action.execute(this)
  }
  makeTaskDependencies(packageName, rootTask)
}
