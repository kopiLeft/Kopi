/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.compiler.tools.optgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.kopiright.compiler.base.CompilerMessages;

/**
 * This class is the entry point for the Message generator.
 */
public class Main {

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * Program entry point.
   */
  public static void main(String[] args) {
    boolean	success;

    success = new Main().run(args);

    System.exit(success ? 0 : 1);
  }

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Only main can construct Main.
   */
  public Main() {
  }

  /**
   * Runs a compilation session.
   *
   * @param	args		the command line arguments
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    }

    boolean		errorsFound = false;

    for (int i = 0; i < options.nonOptions.length; i++) {
      errorsFound = !processFile(options.nonOptions[i]);
    }

    return !errorsFound;
  }

  /*
   * Parse command line arguments.
   */
  private boolean parseArguments(String[] args) {
    options = new OptgenOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    if (options.nonOptions.length == 0) {
      System.err.println(CompilerMessages.NO_INPUT_FILE.getFormat());
      options.usage();
      return false;
    }
    return true;
  }

  private boolean processFile(String sourceFile) {
    if (! parseSource(sourceFile)) {
      return false;
    }
    if (options.release != null) {
      definition.setVersion(options.release);
    }
    if (! checkIdentifiers()) {
      return false;
    }
    if (! checkShortcuts()) {
      return false;
    }
    if (! buildInterfaceFile()) {
      return false;
    }
    return true;
  }

  /**
   *
   */
  private boolean parseSource(String sourceFile) {
    boolean		errorsFound = false;

    try {
      definition = DefinitionFile.read(sourceFile);
    } catch (OptgenError e) {
      System.err.println(e.getMessage());
      errorsFound = true;
    }

    return !errorsFound;
  }


  /**
   *
   */
  private boolean checkIdentifiers() {
    boolean		errorsFound = false;

    try {
      definition.checkIdentifiers();
    } catch (OptgenError e) {
      System.err.println(e.getMessage());
      errorsFound = true;
    }

    return !errorsFound;
  }

  /**
   *
   */
  private boolean checkShortcuts() {
    boolean		errorsFound = false;

    try {
      definition.checkShortcuts();
    } catch (OptgenError e) {
      System.err.println(e.getMessage());
      errorsFound = true;
    }

    return !errorsFound;
  }

  /**
   *
   */
  private boolean buildInterfaceFile() {
    String		prefix = definition.getPrefix();
    File		outputFile = new File(prefix + "Options.java");
    boolean		errorsFound = false;

    try {
      PrintWriter	out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

      definition.printFile(out);

      out.flush();
      out.close();
    } catch (java.io.IOException e) {
      System.err.println("I/O Exception on " + outputFile.getPath() + ": " + e.getMessage());
      errorsFound = true;
    }

    return !errorsFound;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private OptgenOptions		options;
  private DefinitionFile	definition;
}
