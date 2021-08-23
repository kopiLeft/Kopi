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
 * $Id: Utils.kt 35411 2020-05-21 14:22:39Z hfazai $
 */

package org.kopi.gradle.common

import java.io.File

/**
 * Adds the extension to the files names. if a file already contains the extension, this method returns the file's name as it is.
 *
 * @param receiver list of names of the files
 * @param extension extension of the files
 */
fun List<String>?.withExtension(extension: String): List<String> = this?.map { it.withExtension(extension)!! }.orEmpty()

/**
 * Adds the extension to the file's name. if file already contains the extension, this method returns the file's name as it is.
 *
 * @param receiver file name
 * @param extension extension of the file
 */
fun String?.withExtension(extension: String): String? = this?.also {
  if (it.isNotEmpty() && !it.endsWith(".$extension")) return "$this.$extension"
}


/**
 * returns a flat list of arguments.
 *
 * @param args program arguments
 */
fun listOfArgs(vararg args: Any?): List<String> {
  val flattenArgs = mutableListOf<String>()

  args.forEach {arg ->
    when(arg) {
      is String? -> flattenArgs.add(arg.orEmpty())
      is List<*>? -> arg.orEmpty().forEach { flattenArgs.add(it as String) }
    }
  }

  return flattenArgs
}

/**
 * Returns the lines that contains in which the regular expression in [pattern] can find at least one match in the specified [input]
 *
 * @param input input string
 * @param pattern represents the regular expression
 */
fun findPattern(input: String, pattern: String): List<String> {
  val regexPattern = pattern.toRegex()
  val result = mutableListOf<String>()
  val lines = input.lines()

  lines.forEach { line ->
    if (regexPattern.containsMatchIn(line)) {
      result.add(line)
    }
  }

  return  result
}

/**
 * resolves [folder] with this.
 */
fun String?.prefixWithFolder(folder: String) : String? = if (this == null) null else File(folder).resolve(this).absolutePath

/**
 * resolves [folder] with this.
 */
fun List<String>?.prefixWithFolder(folder: String) : List<String>? = this?.map { it.prefixWithFolder(folder)!! }

/**
 * resolves [folder] with this.
 */
fun Map<String, String>?.prefixWithFolder(folder: String) : Map<String, String>? = this?.map { property ->
  property.key.prefixWithFolder(folder)!! to property.value.prefixWithFolder(folder)!!
}?.toMap()

/**
 * returns the relative path of [receiver] to the current directory.
 */
fun File.relativeToCurrentDir(): String {
  val currentDir = File(".").absoluteFile
  val relativeFile = try {
    this.absoluteFile.relativeTo(currentDir)
  } catch (e: IllegalArgumentException) {
    this.absoluteFile
  }
  return relativeFile.toString()
}
