/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.30"
}

repositories {
  jcenter()
}

sourceSets {
  main {
    java {
      srcDir("src")
      exclude("**/vaadin/**")
    }
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
}

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
