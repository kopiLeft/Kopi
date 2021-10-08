/*
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH
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
 */

package org.kopi.gradle.dsl

import java.io.File

import org.kopi.gradle.common.prefixWithFolder
import org.kopi.gradle.common.relativeToCurrentDir

/**
 * Set of modules declared to be built.
 */
class Modules {

  var modules = mutableListOf<Module>()

  /**
   * Returns only modules under the package [p]
   *
   * @param package selected package name.
   * @param hasPProperty whether the user has selected a package to build or not.
   * @param folder selected folder.
   * @param hasFProperty whether the user has selected a folder to build or not.
   */
  fun filter(`package`: String?, hasPProperty: Boolean, folder: String?, hasFProperty: Boolean): List<Module> {
    var modulesToBuild = modules

    if (hasPProperty) {
      val packageName = if (`package`!!.endsWith(".")) `package` else `package` + "."
      modulesToBuild = modulesToBuild.filter { it.packageName.startsWith(packageName) || it.packageName == `package` }.toMutableList()
    }

    if (hasFProperty) {
      modulesToBuild = modulesToBuild.filter {
        val _relativeSelectedFolder = File(folder!!).relativeToCurrentDir()
        val _relativeModuleFolder = File(it.folder).relativeToCurrentDir()
        val relativeSelectedFolder = if (_relativeSelectedFolder == "") "" else _relativeSelectedFolder + File.separator
        val relativeModuleFolder = if (_relativeModuleFolder == "") "" else _relativeModuleFolder + File.separator
        relativeModuleFolder.startsWith(relativeSelectedFolder)
      }.toMutableList()
    }

    return modulesToBuild
  }

  /**
   * initializes or resets modules to an empty list
   */
  fun init() {
    modules = mutableListOf()
  }
}

/**
 * Represents a module
 */
open class Module(val packageName: String) {
  var folder = topDir + File.separator + packageName.replace(rootPackage, "").replace(".", File.separator)

  val taskNamePrefix: String
    get() {
      val relativeFolder = File(folder).relativeToCurrentDir()
      return rootPackage + (if (relativeFolder != "" && rootPackage != "") "." else "") + relativeFolder.replace(File.separator, ".")
    }

  var javaFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var resources: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var targetDir: String? = null

  var properties: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var messageFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var optionFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var grm1voc: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var grm2voc: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var grm1dep: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var grm2dep: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var scanner1: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var scanner2: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var grammar: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var compilerClasses: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var syntaxTreeClasses: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var files: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var tempDir: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var kjcFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var xFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var jFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var genFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var jsFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var gifFiles: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var widgetSets: List<String>? = null
    get() = if (field == null) null else field.prefixWithFolder(folder)

  var toolsBuilt: String? = null
    get() = field ?: System.getenv("TOOLS_BUILT")

  var nonStandardBuild: Boolean = false

  var nonStandardCompiler: Boolean = false

  var copyFiles: List<String>? = null

  var copySources: Boolean = false
}

/**
 * The root package
 */
var rootPackage = ""

/**
 * Global variable of declared modules list.
 */
var modules = Modules()

var topDir = "."
