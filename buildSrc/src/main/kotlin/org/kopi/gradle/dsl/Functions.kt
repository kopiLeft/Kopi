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
 * $Id: Functions.kt 35384 2020-05-11 09:27:56Z hfazai $
 */

package org.kopi.gradle.dsl

/**
 * Declare files and variables related to the [packageName]
 */
fun inPackage(packageName: String, moduleFunction: Module.() -> Unit) {
  val module = Module(packageName)
  module.moduleFunction()
  modules.modules.add(module)
}

/**
 * Sets the root package name
 */
fun rootPackage(packageName: String) {
  rootPackage = packageName
}
