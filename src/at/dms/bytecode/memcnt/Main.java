/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.bytecode.memcnt;

import java.io.*;

import at.dms.bytecode.classfile.ClassFileFormatException;
import at.dms.bytecode.classfile.ClassInfo;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.CompilerMessages;

/**
 * This class is the entry point for the KOPI disassembler.
 */
public class Main {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Only main can construct Main
   */
  private Main(String[] args) {
    if (!parseArguments(args)) {
      System.exit(1);
    }

    String[]	infiles = options.nonOptions;

    if (infiles.length == 0) {
      options.usage();
      System.err.println(MemcntMessages.NO_INPUT_FILE);
      System.exit(1);
    } else {
      boolean	errorsFound = false;

      for (int i = 0; i < infiles.length; i++) {
	if (options.verbose) {
	  System.err.println("Processing " + infiles[i] + ".");
	}

	try {
	  instrumentClass(infiles[i]);
	} catch (UnpositionedError e) {
	  System.err.println("Error: " + e.getMessage());
	  errorsFound = true;
	}
      }

      System.exit(errorsFound ? 1 : 0);
    }
  }

  // --------------------------------------------------------------------
  // ACTIONS
  // --------------------------------------------------------------------

  /**
   * Reads, instruments and writes a class file
   * @exception	UnpositionedError	an error occurred
   */
  private void instrumentClass(String fileName) throws UnpositionedError {
    ClassInfo		info;

    info = readClassFile(fileName);
    info = Instrumenter.instrument(info);
    if (info != null) {
      writeClassFile(info, fileName);
    }
  }

  private ClassInfo readClassFile(String fileName) throws UnpositionedError {
    try {
      DataInputStream	in;
      ClassInfo		info;

      in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName), 2048));
      info = new ClassInfo(in, false);
      in.close();

      return info;
    } catch (ClassFileFormatException e) {
      throw new UnpositionedError(MemcntMessages.SEMANTIC_ERROR, new Object[] { fileName, e.getMessage() });
    } catch (IOException e) {
      throw new UnpositionedError(MemcntMessages.IO_EXCEPTION, new Object[] { fileName, e.getMessage() });
    }
  }

  private void writeClassFile(ClassInfo info, String fileName) throws UnpositionedError {
    try {
      DataOutputStream	out;

      out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));

      info.write(out);
      out.flush();
      out.close();
    } catch (ClassFileFormatException e) {
      throw new UnpositionedError(MemcntMessages.SEMANTIC_ERROR, new Object[] { fileName, e.getMessage() });
    } catch (IOException e) {
      e.printStackTrace();
      throw new UnpositionedError(MemcntMessages.IO_EXCEPTION, new Object[] { fileName, e.getMessage() });
    }
  }

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * Entry point to the assembler
   */
  public static void main(String[] args) {
    new Main(args);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /*
   * Parse command line arguments.
   */
  private boolean parseArguments(String[] args) {
    options = new MemcntOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private MemcntOptions		options;
}
