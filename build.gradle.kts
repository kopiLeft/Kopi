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
import java.io.ByteArrayOutputStream

import org.kopi.gradle.common._project
import org.kopi.gradle.common.clean
import org.kopi.gradle.common.withExtension
import org.kopi.gradle.dsl.modules
import org.kopi.gradle.dsl.topDir
import org.kopi.gradle.tasks.copyProperties
import org.kopi.gradle.tasks.javac
import org.kopi.gradle.tasks.jcc
import org.kopi.gradle.tasks.jflex
import org.kopi.gradle.tasks.lexinc
import org.kopi.gradle.tasks.messageGen
import org.kopi.gradle.tasks.optionGen
import org.kopi.gradle.tasks.resources
import org.kopi.gradle.tasks.scriptExecutor
import org.kopi.gradle.tasks.tokenGen
import org.kopi.gradle.tasks.xkjc

// Passing project variable to buildSrc

_project = project

/** ------------ Importing Variables declarations ------------ */

apply(from = "declarations.gradle.kts")

/** ------------------ Global Definitions and gradle tasks ------------------ */

plugins {
  kotlin("jvm") version "1.5.30"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

sourceSets.main {
  java.srcDirs("src")
}

// KOPI VERSION
val kopiVersion = "2.3B"

// ENVIRONMENT VARIABLES
val extdirs: String? = System.getenv("EXTDIRS")
val classRoot: String? = System.getenv("CLASSROOT")
val jdk7Home: String? = System.getenv("JDK_7")
val javadocRoot: String? = System.getenv("JAVADOCROOT")

// Dependencies versions
val vaadinVersion = "21.0.9"
val enhancedDialogVersion = "21.0.0"
val apexChartVersion = "2.0.0.beta10"
val ironIconsVersion = "2.0.1"
val WYSIWYG_EJAVA = "2.0.1"

// ----------------------------------------------------------------------
// CHECK IF PATH TO JAVA 7 INSTALLATION IS SET AND VALID

fun javaExecutable(executableName: String): String {
  val executable = file("$jdk7Home/bin/$executableName")
  require(executable.exists()) { "There is no $executableName executable in ${"$jdk7Home/bin"}" }
  return executable.toString()
}

// ----------------------------------------------------------------------
// CHECK THAT CLASSROOT IS SET

//require(classRoot != null) { "No CLASSROOT defined" }

// ----------------------------------------------------------------------
// DEPENDENCIES

repositories {
  mavenCentral()
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // Jars present in extdirs folder
  extdirs?.split(":")
    ?.forEach { extdir ->
      "implementation"(fileTree(mapOf("dir" to extdir, "include" to listOf("*.jar"))))
    }
  "implementation"(files(classRoot))

  // Vaadin dependencies
  implementation("com.vaadin", "vaadin-core") {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
           "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
           "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
      .forEach { group -> exclude(group = group) }
  }
  // Vaadin addons
  // Wysiwyg-e Rich Text Editor component for Java
  implementation("org.vaadin.pekka", "wysiwyg_e-java", WYSIWYG_EJAVA)
  // EnhancedDialog
  implementation("com.vaadin.componentfactory", "enhanced-dialog", enhancedDialogVersion)
  // Apex charts
  implementation("com.github.appreciated", "apexcharts", apexChartVersion)
  // Iron Icons
  implementation("com.flowingcode.addons", "iron-icons", ironIconsVersion)
}

// ----------------------------------------------------------------------
// READ GRADLE ARGUMENTS

val `package`: String? by project // Command line argument specifying which module to build based on package name.
val folder: String? by project // Command line argument specifying which module to build based on folder.

// ----------------------------------------------------------------------
// SELECT MODULES TO BUILD

val modulesToBuild = modules.filter(`package`, project.hasProperty("package"), folder, project.hasProperty("folder"))
modules.init()

/** ------------------ Registering main gradle tasks ------------------ */

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileKotlin {
    destinationDir = file(classRoot!!)

    if(jdk7Home != null) {
      sourceCompatibility = "1.7"
      targetCompatibility = "1.7"
    }
  }

  register("grammar.jcc")
  register("grm1voc.tokenGen")
  register("grm1voc.lexinc")
  register("grm1voc.jflex")
  register("grm1voc.jcc")
  register("grm2voc.tokenGen")
  register("grm2voc.lexinc")
  register("grm2voc.jflex")
  register("grm2voc.jcc")
  register("messageGen")
  register("optionGen")
  register("javac")
  register("xkjc")
  register("copyProperties")
  register("resources")
  register("script")

  register("copyWidgetSet") {
    doLast {
      copy {
        from("./src/org/kopi/vkopi/lib/ui/vaadin/addons/KopiAddonsWidgetset.gwt.xml")
        into("$classRoot/org/kopi/vkopi/lib/ui/vaadin/addons")
      }
    }
  }

  register("run") {
    dependsOn("optionGen")
    dependsOn("messageGen")
    dependsOn("javac")
    dependsOn("compileKotlin")
    dependsOn("grm1voc.tokenGen")
    dependsOn("grm2voc.tokenGen")
    dependsOn("grm1voc.jflex")
    dependsOn("grm2voc.jflex")
    dependsOn("grammar.jcc")
    dependsOn("grm1voc.jcc")
    dependsOn("grm2voc.jcc")
    dependsOn("grm1voc.lexinc")
    dependsOn("grm2voc.lexinc")
    dependsOn("copyProperties")
    dependsOn("xkjc")
    dependsOn("resources")
    dependsOn("script")
    dependsOn("copyWidgetSet")
  }

  named("jar") {
    dependsOn("run")
    mustRunAfter("run")
  }

  named("copyWidgetSet") {
    dependsOn("javac")
    mustRunAfter("javac")
  }

  named("build") { dependsOn("run") }
  register("clean-all") { dependsOn("clean") }
  register("clean-classes") { dependsOn("clean") }

  register("galiteflow") {
    doLast {
      val out = ByteArrayOutputStream()

      val existValue = exec {
        executable = "git"
        args = listOf("status")
        standardOutput = out
      }

      val clean = out.toString().let { it.contains("la copie de travail est propre") || it.contains("nothing to commit working tree clean") }

      if(!clean) {
        error("Check your local changes before updating. Execute 'git status'")
      } else {
        val galiteBuildDir = "${buildDir}/galite"
        val galiteBuildDirVaadin = "$galiteBuildDir/galite-core/src/main/kotlin/org/kopi/galite/visual/ui/vaadin"
        val vaadinDir = file("./src/org/kopi/vkopi/lib/ui/vaadinflow")
        val frontendDir = "./src/resources/META-INF/resources/frontend"

        file(galiteBuildDir).deleteRecursively()
        vaadinDir.deleteRecursively()

        exec {
          executable = "git"
          args = listOf("clone", "https://github.com/kopiLeft/Galite.git", "-b", "master", "--single-branch", galiteBuildDir)
        }

        // Copy sources
        copy {
          from(galiteBuildDirVaadin) {
            include("**/*.kt")
            exclude("**/DAbstractFullCalendar.kt",
                    "**/DFullCalendarBlock.kt",
                    "**/DTimeGridCalendar.kt",
                    "**/pivottable/*.kt")
          }
          into(vaadinDir)
          filter { line ->
            line
              .replace("org.kopi.galite.visual.ui.vaadin", "org.kopi.vkopi.lib.ui.vaadinflow")

                    // org.kopi.galite.visual package
              .replace("org.kopi.galite.visual.Action", "org.kopi.vkopi.lib.visual.Action")
              .replace("org.kopi.galite.visual.ActionHandler", "org.kopi.vkopi.lib.visual.ActionHandler")
              .replace("org.kopi.galite.visual.Application", "org.kopi.vkopi.lib.visual.Application")
              .replace("org.kopi.galite.visual.ApplicationConfiguration", "org.kopi.vkopi.lib.visual.ApplicationConfiguration")
              .replace("org.kopi.galite.visual.ApplicationContext", "org.kopi.vkopi.lib.visual.ApplicationContext")
              .replace("org.kopi.galite.visual.Color", "org.kopi.vkopi.lib.visual.Color")
              .replace("org.kopi.galite.visual.Constants", "org.kopi.vkopi.lib.visual.Constants")
              .replace("org.kopi.galite.visual.DefaultActor", "org.kopi.vkopi.lib.visual.DefaultActor")
              .replace("org.kopi.galite.visual.DPositionPanelListener", "org.kopi.vkopi.lib.visual.DPositionPanelListener")
              .replace("org.kopi.galite.visual.Executable", "org.kopi.vkopi.lib.visual.Executable")
              .replace("org.kopi.galite.visual.FileHandler", "org.kopi.vkopi.lib.visual.FileHandler")
              .replace("org.kopi.galite.visual.FileProductionListener", "org.kopi.vkopi.lib.visual.FileProductionListener")
              .replace("org.kopi.galite.visual.ImageHandler", "org.kopi.vkopi.lib.visual.ImageHandler")
              .replace("org.kopi.galite.visual.Item", "org.kopi.vkopi.lib.visual.Item")
              .replace("org.kopi.galite.visual.ItemTreeManager", "org.kopi.vkopi.lib.visual.ItemTreeManager")
              .replace("org.kopi.galite.visual.LogoutModule", "org.kopi.vkopi.lib.visual.LogoutModule")
              .replace("org.kopi.galite.visual.Message", "org.kopi.vkopi.lib.visual.Message")
              .replace("org.kopi.galite.visual.MessageCode", "org.kopi.vkopi.lib.visual.MessageCode")
              .replace("org.kopi.galite.visual.MessageListener", "org.kopi.vkopi.lib.visual.MessageListener")
              .replace("org.kopi.galite.visual.ModelCloseListener", "org.kopi.vkopi.lib.visual.ModelCloseListener")
              .replace("org.kopi.galite.visual.Module", "org.kopi.vkopi.lib.visual.Module")
              .replace("org.kopi.galite.visual.PreviewRunner", "org.kopi.vkopi.lib.visual.PreviewRunner")
              .replace("org.kopi.galite.visual.PrinterManager", "org.kopi.vkopi.lib.visual.PrinterManager")
              .replace("org.kopi.galite.visual.ProgressDialogListener", "org.kopi.vkopi.lib.visual.ProgressDialogListener")
              .replace("org.kopi.galite.visual.PropertyException", "org.kopi.vkopi.lib.visual.PropertyException")
              .replace("org.kopi.galite.visual.Registry", "org.kopi.vkopi.lib.visual.Registry")
              .replace("org.kopi.galite.visual.RootItem", "org.kopi.vkopi.lib.visual.RootItem")
              .replace("org.kopi.galite.visual.RootMenu", "org.kopi.vkopi.lib.visual.RootMenu")
              .replace("org.kopi.galite.visual.UActor", "org.kopi.vkopi.lib.visual.UActor")
              .replace("org.kopi.galite.visual.UIFactory", "org.kopi.vkopi.lib.visual.UIFactory")
              .replace("org.kopi.galite.visual.UItemTree", "org.kopi.vkopi.lib.visual.UItemTree")
              .replace("org.kopi.galite.visual.UMenuTree", "org.kopi.vkopi.lib.visual.UMenuTree")
              .replace("org.kopi.galite.visual.UserConfiguration", "org.kopi.vkopi.lib.visual.UserConfiguration")
              .replace("org.kopi.galite.visual.UWindow", "org.kopi.vkopi.lib.visual.UWindow")
              .replace("org.kopi.galite.visual.VActionListener", "org.kopi.vkopi.lib.visual.VActionListener")
              .replace("org.kopi.galite.visual.VActor", "org.kopi.vkopi.lib.visual.VActor")
              .replace("org.kopi.galite.visual.VColor", "org.kopi.vkopi.lib.visual.VColor")
              .replace("org.kopi.galite.visual.VCommand", "org.kopi.vkopi.lib.visual.VCommand")
              .replace("org.kopi.galite.visual.VDatabaseUtils", "org.kopi.vkopi.lib.visual.VDatabaseUtils")
              .replace("org.kopi.galite.visual.VerifyConfiguration", "org.kopi.vkopi.lib.visual.VerifyConfiguration")
              .replace("org.kopi.galite.visual.VException", "org.kopi.vkopi.lib.visual.VException")
              .replace("org.kopi.galite.visual.VExecFailedException", "org.kopi.vkopi.lib.visual.VExecFailedException")
              .replace("org.kopi.galite.visual.VHelpGenerator", "org.kopi.vkopi.lib.visual.VHelpGenerator")
              .replace("org.kopi.galite.visual.VHelpViewer", "org.kopi.vkopi.lib.visual.VHelpViewer")
              .replace("org.kopi.galite.visual.VInterruptException", "org.kopi.vkopi.lib.visual.VInterruptException")
              .replace("org.kopi.galite.visual.VItemTree", "org.kopi.vkopi.lib.visual.VItemTree")
              .replace("org.kopi.galite.visual.VlibProperties", "org.kopi.vkopi.lib.visual.VlibProperties")
              .replace("org.kopi.galite.visual.VMenuTree", "org.kopi.vkopi.lib.visual.VMenuTree")
              .replace("org.kopi.galite.visual.VModel", "org.kopi.vkopi.lib.visual.VModel")
              .replace("org.kopi.galite.visual.VRuntimeException", "org.kopi.vkopi.lib.visual.VRuntimeException")
              .replace("org.kopi.galite.visual.VTrigger", "org.kopi.vkopi.lib.visual.VTrigger")
              .replace("org.kopi.galite.visual.VWindow", "org.kopi.vkopi.lib.visual.VWindow")
              .replace("org.kopi.galite.visual.WaitDialogListener", "org.kopi.vkopi.lib.visual.WaitDialogListener")
              .replace("org.kopi.galite.visual.WaitInfoListener", "org.kopi.vkopi.lib.visual.WaitInfoListener")
              .replace("org.kopi.galite.visual.WindowBuilder", "org.kopi.vkopi.lib.visual.WindowBuilder")
              .replace("org.kopi.galite.visual.WindowController", "org.kopi.vkopi.lib.visual.WindowController")

              .replace("org.kopi.galite.visual", "org.kopi.vkopi.lib")
              .replace("org.kopi.galite.type", "org.kopi.vkopi.lib.type")
              .replace("org.kopi.galite.util", "org.kopi.vkopi.lib.util")
              .replace("ApplicationContext.applicationContext.getApplication()", "ApplicationContext.getApplicationContext().getApplication()")
              .replace("block.currentRecord = rec + 1", "block.setCurrentRecord(rec + 1)")
              .replace("block.currentRecord = rec", "block.setCurrentRecord(rec)")
              .replace("fun hasDisplays(): Boolean = isDisplayInitialized", "fun hasDisplays(): Boolean = displays != null")
              .replace("dBConnection", "dbContext")
              .replace("objectName", "`object`")
              .replace("override fun dispose()", "private fun dispose()")
              .replace("isDisplayInitialized", "displays != null")
              .replace("item.isDefaultItem = true", "item.setDefault(true)")
              .replace("item.isDefaultItem = false", "item.setDefault(false)")
          }
        }

        file(frontendDir).deleteRecursively()
        // Copy resources
        copy {
          from("$galiteBuildDir/galite-core/src/main/resources/META-INF/resources/frontend")
          into(frontendDir)
        }

        // cleanup build dir
        file(galiteBuildDir).deleteRecursively()
      }
    }
  }
}

defaultTasks("run")

/** ------------------ Configuring gradle tasks ------------------ */

if(jdk7Home != null) {
  println("JAVA 7 is used to compile this project.")

  // Compatibility with java 7
  tasks.withType<JavaExec>().configureEach {
    executable = javaExecutable("java")
  }
  tasks.withType<Javadoc>().configureEach {
    executable = javaExecutable("javadoc")
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.apply {
    // Compatibility with java 7
    if(jdk7Home != null) {
      sourceCompatibility = "1.7"
      targetCompatibility = "1.7"
      forkOptions.javaHome = file(jdk7Home)
    }
    isFork = true
    isFailOnError = true
    isDeprecation = true
    isListFiles = true
    encoding = "utf-8"
  }
}

// Adding generated files
modulesToBuild.forEach { module ->
  module.apply {
    val allJavaFiles = javaFiles.orEmpty().toMutableList()
    val allGenFiles= genFiles.orEmpty().toMutableList()
    val grammar = grammar.orEmpty()
    val grm1voc = grm1voc.orEmpty()
    val grm2voc = grm2voc.orEmpty()
    val grm1dep = grm1dep.orEmpty()
    val grm2dep = grm2dep.orEmpty()
    val scanner1 = scanner1.orEmpty()
    val scanner2 = scanner2.orEmpty()

    if (!grammar.isNullOrEmpty()) {
      allJavaFiles.addAll(grammar.map { "${it}Parser" })
      allJavaFiles.addAll(grammar.map { "${it}TokenTypes" })
      allJavaFiles.addAll(grammar.map { "${it}Lexer" })
      allJavaFiles.addAll(grammar.map { "${it}LexerTokenTypes" })

      allGenFiles.addAll(grammar.map { "${it}Parser.java" })
      allGenFiles.addAll(grammar.map { "${it}TokenTypes.java" })
      allGenFiles.addAll(grammar.map { "${it}Lexer.java" })
      allGenFiles.addAll(grammar.map { "${it}LexerTokenTypes.java" })
      allGenFiles.addAll(grammar.map { "${it}TokenTypes.txt" })
      allGenFiles.addAll(grammar.map { "${it}LexerTokenTypes.txt" })
    }

    if (!grm1voc.isNullOrEmpty()) {
      allJavaFiles.addAll(grm1voc.map { "${it}TokenTypes" })
      allGenFiles.addAll(grm1voc.map { "${it}TokenTypes.java" })
      allGenFiles.addAll(grm1voc.map { "${it}TokenTypes.txt" })

      if (!scanner1.isNullOrEmpty()) {
        allJavaFiles.addAll(scanner1.map { "${it}Scanner" })
        allGenFiles.addAll(grm1voc.map { "${it}FlexRules.txt" })
        allGenFiles.addAll(scanner1.withExtension("flex"))
        allGenFiles.addAll(scanner1.map { "${it}Scanner.java" })
      } else {
        allJavaFiles.addAll(grm1voc.map { "${it}Keywords" })
        allJavaFiles.addAll(grm1voc.map { "${it}Parser" })
        allGenFiles.addAll(grm1voc.map { "${it}Keywords.java" })
        allGenFiles.addAll(grm1voc.map { "${it}Parser.java" })
      }

      if (!grm1dep.isNullOrEmpty()) {
        allGenFiles.addAll(grm1voc.map { file(it).parentFile.path + "/expanded${file(it).name}.g" })
      }
    }

    if (!grm2voc.isNullOrEmpty()) {
      allJavaFiles.addAll(grm2voc.map { "${it}TokenTypes" })
      allGenFiles.addAll(grm2voc.map { "${it}TokenTypes.java" })
      allGenFiles.addAll(grm2voc.map { "${it}TokenTypes.txt" })

      if (!scanner2.isNullOrEmpty()) {
        allJavaFiles.addAll(scanner2.map { "${it}Scanner" })
        allGenFiles.addAll(grm2voc.map { "${it}FlexRules.txt" })
        allGenFiles.addAll(scanner2.withExtension("flex"))
        allGenFiles.addAll(scanner2.map { "${it}Scanner.java" })
      } else {
        allJavaFiles.addAll(grm2voc.map { "${it}Keywords" })
        allJavaFiles.addAll(grm2voc.map { "${it}Parser" })
        allGenFiles.addAll(grm2voc.map { "${it}Keywords.java" })
        allGenFiles.addAll(grm2voc.map { "${it}Parser.java" })
      }

      if (!grm2dep.isNullOrEmpty()) {
        allGenFiles.addAll(grm2voc.map { file(it).parentFile.path + "/expanded${file(it).name}.g" })
      }
    }

    allJavaFiles.addAll(messageFiles.orEmpty())
    allGenFiles.addAll(messageFiles.withExtension("java"))
    allJavaFiles.addAll(optionFiles.orEmpty())
    allGenFiles.addAll(optionFiles.withExtension("java"))

    javaFiles = allJavaFiles
    genFiles = allGenFiles
  }
}

// Configuring gradle tasks to each module to build
tasks {

  // JAVADOC GENERATION
  named<Javadoc>("javadoc") {
    val allJavaFiles = modulesToBuild.map { it.javaFiles.withExtension("java") }.flatten()

    options.source = "1.4"
    options.jFlags = listOf("-Xmx128m")
    options.memberLevel = JavadocMemberLevel.PACKAGE
    val javadocDocletOptions = options.windowTitle("Kopi Suite Version $kopiVersion")
    javadocDocletOptions.isUse = true
    javadocDocletOptions.isNoTimestamp = false
    setDestinationDir(file("$javadocRoot/html/"))
    this.source = fileTree(topDir)
    include(allJavaFiles.map { file(it).relativeTo(file(topDir)).toString() })
    doFirst {
      require(javadocRoot != null) { "No JAVADOCROOT defined" }
    }
  }

  modulesToBuild.forEach { module ->
    module.apply {
      val classRoot = classRoot ?: project.buildDir.absolutePath
      val javaFiles: List<String> = javaFiles.withExtension("java")
      val grammar: List<String>? = grammar
      val grm1voc: List<String>? = grm1voc
      val grm2voc: List<String>? = grm2voc
      val grm1dep: List<String>? = grm1dep
      val grm2dep: List<String>? = grm2dep
      val scanner1: List<String>? = scanner1
      val scanner2: List<String>? = scanner2
      val xFiles: List<String> = xFiles.withExtension("x")
      val jFiles: List<String> = jFiles.withExtension("java")
      val nonStandardCompiler: Boolean = nonStandardCompiler
      val nonStandardBuild: Boolean = nonStandardBuild
      val properties: List<String>? = this@apply.properties?.withExtension("properties")
      val resources: List<String>? = resources
      val gifFiles: List<String>? = gifFiles.withExtension("gif")
      val optionFiles: List<String>? = optionFiles
      val messageFiles: List<String>? = messageFiles

      // ----------------------------------------------------------------------
      // STANDARD BUILD TASKS

      if(!nonStandardBuild) {

        // ----------------------------------------------------------------------
        // GRAMMAR HANDLING (GRAMMARS WITH ANTLR TOKENIZER)

        if (!grammar.isNullOrEmpty()) {
          jcc("grm1voc.jcc", taskNamePrefix) {
            this.grammarFiles = files(grammar.withExtension("g"))
            this.currentDir = folder
          }
        }

        // ----------------------------------------------------------------------
        // GRAMMAR HANDLING (GRAMMARS WITH SEPARATE TOKENIZER)

        if (!grm1voc.isNullOrEmpty()) {
          tokenGen("grm1voc.tokenGen", taskNamePrefix) {
            val grm1vocT = grm1voc.withExtension("t")
            val grm1depT = grm1dep.withExtension("t")

            this.grmvocFiles = files(grm1depT + grm1vocT)
            this.currentDir = folder
            this.withScanner = scanner1.isNullOrEmpty()
          }

          if (!scanner1.isNullOrEmpty()) {

            /** -------- JFlex based scanner -------- */

            lexinc("grm1voc.lexinc", taskNamePrefix) {
              this.scannerFlexInFiles = files(scanner1.withExtension("flex.in"))
              this.grmvocFiles = grm1voc
              this.currentDir = folder
            }

            jflex("grm1voc.jflex", taskNamePrefix) {
              this.scannerFiles = files(scanner1.withExtension("flex"))
              this.jflexSkeleton = "$classRoot/org/kopi/compiler/skeleton.shared"
              this.currentDir = folder
            }
          }

          jcc("grm1voc.jcc", taskNamePrefix) {
            val grm1vocG = grm1voc.withExtension("g")
            val grm1depG = grm1dep.withExtension("g")

            this.grammarFiles = files(grm1depG + grm1vocG)
            this.currentDir = folder
          }
        }

        // ----------------------------------------------------------------------

        if (!grm2voc.isNullOrEmpty()) {
          tokenGen("grm2voc.tokenGen", taskNamePrefix) {
            val grm2vocT = grm2voc.withExtension("t")
            val grm2depT = grm2dep.withExtension("t")

            this.grmvocFiles = files(grm2depT + grm2vocT)
            this.currentDir = folder
            this.withScanner = scanner2.isNullOrEmpty()
          }

          if (!scanner2.isNullOrEmpty()) {

            /** -------- JFlex based scanner -------- */

            lexinc("grm2voc.lexinc", taskNamePrefix) {
              this.scannerFlexInFiles = files(scanner2.withExtension("flex.in"))
              this.grmvocFiles = grm2voc
              this.currentDir = folder
            }

            jflex("grm2voc.jflex", taskNamePrefix) {
              this.scannerFiles = files(scanner2.withExtension("flex"))
              this.jflexSkeleton = "$classRoot/org/kopi/compiler/skeleton.shared"
              this.currentDir = folder
            }
          }

          jcc("grm2voc.jcc", taskNamePrefix) {
            val grm2vocG = grm2voc.withExtension("g")
            val grm2depG = grm2dep.withExtension("g")

            this.grammarFiles = files(grm2depG + grm2vocG)
            this.currentDir = folder
          }
        }

        // ----------------------------------------------------------------------
        // MESSAGES

        if (!messageFiles.isNullOrEmpty()) {
          messageGen(taskNamePrefix) {
            this.messageFiles = files(messageFiles.withExtension("xml"))
            this.currentDir = folder
          }
        }

        // ----------------------------------------------------------------------
        // OPTIONS

        if (!optionFiles.isNullOrEmpty()) {
          optionGen(taskNamePrefix) {
            this.release = release.orEmpty()
            this.optionFiles = files(optionFiles.withExtension("xml"))
            this.currentDir = folder
          }
        }

        // ----------------------------------------------------------------------
        // BUILD CLASSFILES

        if (!nonStandardCompiler && javaFiles.isNotEmpty()) {
          javac(taskNamePrefix) {
            destinationDir = file(classRoot)
            source = files(".").asFileTree
            options.sourcepath = files(topDir)
            include(javaFiles.map { file(it).relativeTo(file(".")).path })

            doLast {
              val copyFiles = copyFiles.orEmpty()

              // Copy files into classroot/package
              if (copyFiles.isNotEmpty()) {
                val packagePath = packageName.replace(".", "/")
                println("Files to be copied : $copyFiles Into : $classRoot/$packagePath")
                copy {
                  from(copyFiles)
                  into("$classRoot/$packagePath")
                }
              }

              // Copy sources into classroot/package
              if (copySources) {
                val packagePath = packageName.replace(".", "/")
                println("Files to be copied : $javaFiles Into : $classRoot/$packagePath")
                copy {
                  from(javaFiles)
                  into("$classRoot/$packagePath")
                }
              }
            }
          }
        }

        // ----------------------------------------------------------------------
        // NON STANDARD COMPILATION FOR package org.kopi.xkopi.lib.oper

        val xkjcFiles = xFiles + jFiles
        if (nonStandardCompiler && xkjcFiles.isNotEmpty() && packageName == "org.kopi.xkopi.lib.oper") {
          xkjc(taskNamePrefix) {
            this.classRoot = classRoot
            this.verbose = true
            this.noo = true
            this.xkjcPath = "/dev/null"
            this.xkjcFiles = files(xkjcFiles)
            this.currentDir = folder
          }
        }

        // ----------------------------------------------------------------------
        // INSTALL PROPERTIES FILES

        if (properties != null) {
          copyProperties(taskNamePrefix) {
            this.properties = files(properties)
            this.propertiesDest = classRoot
          }
        }
      }

      // ----------------------------------------------------------------------
      // NON STANDARD BUILD TASKS

      if(nonStandardBuild) {

        val packagePath = packageName.replace(".", "/")

        // ----------------------------------------------------------------------
        // NON STANDARD BUILD FOR PACKAGE org.kopi.vkopi.lib.ui.vaadin.resource

        if (packageName == "org.kopi.vkopi.lib.ui.vaadin.resource") {
          if (resources != null) {
            resources(taskNamePrefix) {
              this.resources = files(resources)
              if(targetDir == null) {
                this.resourceDestDir = classRoot + File.separator + packagePath
              } else {
                this.resourceDestDir = classRoot + File.separator + targetDir
              }
            }
          }
        }

        // ----------------------------------------------------------------------
        // NON STANDARD BUILD FOR PACKAGE org.kopi.compiler.resource

        if (packageName == "org.kopi.compiler.resource") {
          if (resources != null) {
            resources(taskNamePrefix) {
              this.resources = files(resources)
              if(targetDir == null) {
                this.resourceDestDir = classRoot + File.separator + packagePath
              } else {
                this.resourceDestDir = classRoot + File.separator + targetDir
              }
            }
          }
        }

        // ----------------------------------------------------------------------
        // NON STANDARD BUILD FOR PACKAGE org.kopi.vkopi.lib.resource

        if (packageName == "org.kopi.vkopi.lib.resource") {
          if (resources != null) {
            resources(taskNamePrefix) {
              this.resources = files(resources)
              if(targetDir == null) {
                this.resourceDestDir = classRoot + File.separator + packagePath
              } else {
                this.resourceDestDir = classRoot + File.separator + targetDir
              }
            }
          }

          if (gifFiles != null) {
            scriptExecutor(taskNamePrefix) {
              this.inputFiles = gifFiles
              this.currentDir = folder
              this.script = "makegallery.sh"
              this.outputFile = "gallery.html"
            }
          }
        }
      }

      // ----------------------------------------------------------------------
      // CLEAN AND REMOVE GENERATED FILES

      named("clean-all") {
        doLast {
          if (!genFiles.isNullOrEmpty()) {
            genFiles!!.forEach {
              clean(it, null)
            }
          }
        }
      }

      // ----------------------------------------------------------------------
      // CLEAN CLASSFILES

      named("clean-classes") {
        doLast {
          val packageInClassRoot = file("$classRoot/${packageName.replace(".", "/")}")
          packageInClassRoot.listFiles { it -> it.extension == "class" }.orEmpty().forEach {
            clean(it.name, packageInClassRoot.path)
          }
        }
      }

      // ----------------------------------------------------------------------

    }
  }
}

/** ------ Global error handling message ------ */
project.gradle.buildFinished {
  if (this.failure != null) {
    println("An error has occured. See the error output logs or try with > gradle --rerun-tasks")
  }
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
  }
}
