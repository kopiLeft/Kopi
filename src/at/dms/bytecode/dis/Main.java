/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: Main.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.bytecode.dis;

import at.dms.bytecode.classfile.ClassPath;
import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.Message;
import at.dms.util.base.MessageDescription;

/**
 * This class is the entry point for the KOPI disassembler.
 */
public class Main implements Constants {

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * Entry point.
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
   * Only main can construct Main
   */
  private Main() {
  }

  /**
   * Runs a disassembler session
   *
   * @param	args		the command line arguments
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    }

    ClassPath classpath = new ClassPath(null, options.classpath, options.extdirs);

    String[]	infiles = options.nonOptions;

    if (infiles.length == 0) {
      options.usage();
      inform(DisMessages.NO_INPUT_FILE);
      return false;
    } else {
      boolean	errorsFound = false;

      for (int i = 0; i < infiles.length; i++) {
	if (options.verbose) {
	  inform(DisMessages.PROCESSING, new String[]{ infiles[i] });
	}

	try {
	  disassembleClass(classpath, infiles[i]);
	} catch (UnpositionedError e) {
	  inform("Error: " + e.getMessage());
	  errorsFound = true;
	}
      }

      return !errorsFound;
    }
  }

  // --------------------------------------------------------------------
  // ACTIONS
  // --------------------------------------------------------------------

  /*
   * Parse command line arguments.
   */
  private boolean parseArguments(String[] args) {
    options = new DisOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  /**
   * Reads, and disassembles a class file
   * @exception	UnpositionedError	an error occurred
   */
  private void disassembleClass(ClassPath classpath, String fileName) throws UnpositionedError {
    Disassembler.disassemble(classpath, fileName, options.destination, options);
  }

  // --------------------------------------------------------------------
  // DIAGNOSTICS
  // --------------------------------------------------------------------

  /**
   * Write a message to the diagnostic output.
   * @param	message		the formatted message
   */
  public void inform(Message message) {
    inform(message.getMessage());
  }

  /**
   * Write a message to the diagnostic output.
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public void inform(MessageDescription description, Object[] parameters) {
    inform(new Message(description, parameters));
  }

  /**
   * Write a message to the diagnostic output.
   * @param	description	the message description
   */
  public void inform(MessageDescription description) {
    inform(description, null);
  }

  /**
   * Write a text to the diagnostic output.
   * @param	message		the message text
   */
  private void inform(String message) {
    System.err.println(message);
    System.err.flush();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private DisOptions		options;
}
