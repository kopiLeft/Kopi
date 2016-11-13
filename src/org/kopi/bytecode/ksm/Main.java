/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.bytecode.ksm;

import org.kopi.util.base.Message;
import org.kopi.util.base.MessageDescription;

/**
 * This class is the entry point for the KOPI assembler.
 */
public class Main {

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

    String[]	infiles = options.nonOptions;

    if (infiles.length == 0) {
      options.usage();
      inform(KsmMessages.NO_INPUT_FILE);
      return false;
    } else {
      boolean	errorsFound = false;

      for (int i = 0; i < infiles.length; i++) {
	if (options.verbose) {
	  inform(KsmMessages.PROCESSING, new String[]{ infiles[i] });
	}

	try {
	  Assembler		asm = new Assembler();

	  asm.assemble(infiles[i], options.destination, options.optimize);
	} catch (KsmError e) {
	  inform(e.getMessage());
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
    options = new KsmOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
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

  private KsmOptions		options;
}
