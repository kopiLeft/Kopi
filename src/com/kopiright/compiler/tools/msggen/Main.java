/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.compiler.tools.msggen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import com.kopiright.compiler.base.CompilerMessages;

/**
 * This class is the entry point for the Message generator.
 */
public class Main {

  // ----------------------------------------------------------------------
  // ENTRY POINT
  // ----------------------------------------------------------------------

  /**
   * Entry point
   *
   * @param	args		the command line arguments
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
   * Constructor of Main
   */
  public Main() {
  }

  /**
   * Runs a compilation session
   *
   * @param	args		the command line arguments
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    }
    if (!parseSourceFiles()) {
      return false;
    }
    if (!checkIdentifiers()) {
      return false;
    }
    if (!buildInterfaceFile()) {
      return false;
    }

    return true;
  }

  /**
   * Parse the argument list
   */
  private boolean parseArguments(String[] args) {
    options = new MsggenOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    if (options.nonOptions.length == 0) {
      System.err.println(CompilerMessages.NO_INPUT_FILE.getFormat());
      return false;
    } else if (options.nonOptions.length > 1) {
      options.usage();
      return false;
    }

    return true;
  }

  /**
   *
   */
  private boolean parseSourceFiles() {
    boolean		errorsFound = false;

    try {
      definition = DefinitionFile.read(options.nonOptions[0]);
    } catch (MsggenError e) {
      System.err.println(e.getMessage());
      errorsFound = true;
    }

    return !errorsFound;
  }


  /**
   *
   */
  private boolean checkIdentifiers() {
    Hashtable		identifiers = new Hashtable();
    boolean		errorsFound = false;

    try {
      definition.checkIdentifiers(identifiers);
    } catch (MsggenError e) {
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
    File		outputFile = new File(prefix + "Messages.java");

    try {
      PrintWriter	out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

      definition.printFile(out);

      out.flush();
      out.close();
      return true;
    } catch (IOException e) {
      System.err.println("I/O Exception on " + outputFile.getPath() + ": " + e.getMessage());
      return false;
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private MsggenOptions		options;
  private DefinitionFile	definition;
}
