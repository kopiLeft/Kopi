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

plugins {
  kotlin("jvm") version "1.4.30"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

val vaadinVersion = "18.0.3"

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

  // Vaadin dependencies
  implementation("com.vaadin", "vaadin-core") {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
           "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
           "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
  }
}

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
  }
}