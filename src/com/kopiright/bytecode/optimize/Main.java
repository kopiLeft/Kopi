/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.bytecode.optimize;

import java.io.*;

import com.kopiright.bytecode.classfile.ClassFileFormatException;
import com.kopiright.bytecode.classfile.ClassInfo;
import com.kopiright.bytecode.classfile.CodeInfo;
import com.kopiright.bytecode.classfile.MethodInfo;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class is the entry point for the simple optimizer.
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
      System.err.println(OptimizeMessages.NO_INPUT_FILE);
      System.exit(1);
    } else {
      boolean	errorsFound = false;

      for (int i = 0; i < infiles.length; i++) {
	if (options.verbose) {
	  System.err.println("Processing " + infiles[i] + ".");
	}

	try {
	  optimizeClass(infiles[i]);
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
   * Reads, optimizes and writes a class file
   * @exception	UnpositionedError	an error occurred
   */
  private void optimizeClass(String fileName) throws UnpositionedError {
    ClassInfo		info;

    info = readClassFile(fileName);
    optimizeClass(info, options.optimize, options.verbose);
    writeClassFile(info, options.destination == null ? fileName : options.destination + File.separatorChar + com.kopiright.util.base.Utils.splitQualifiedName(info.getName())[1] + ".class");
  }

  /**
   * Reads, optimizes and writes a class file
   * @exception	UnpositionedError	an error occurred
   */
  public static void optimizeClass(ClassInfo info, int level, boolean verbose) throws UnpositionedError {
    MethodInfo[]	methods;
    int			length = 0;
    int			totalUnoptimized = 0;
    int			totalOptimized = 0;

    methods = info.getMethods();
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].getCodeInfo() != null) {
	if (verbose) {
	  length = methods[i].getCodeInfo().getInstructions().length;
	}

	optimizeMethod(methods[i], level, verbose);

	if (verbose) {
	  CodeInfo	code = methods[i].getCodeInfo();

          printRatio(methods[i].getName(), length, code.getInstructions().length);
	  totalUnoptimized += length;
	  totalOptimized += code.getInstructions().length;
	}
      }
    }

    if (verbose) {
      printRatio("Total", totalUnoptimized, totalOptimized);
    }
  }

  private static void printRatio(String name, int before, int after) {
    System.err.println(name + ": " + before + " -> " + after + " = " + ((int)(after * 10000. / before)) / 100.);
  }

  private static void optimizeMethod(MethodInfo method, int level, boolean verbose) {
    CodeInfo		code;

    code = method.getCodeInfo();
    if (code != null) {
      code = Optimizer.optimize(code, level);
      method.setCodeInfo(code);
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
      throw new UnpositionedError(OptimizeMessages.SEMANTIC_ERROR, new Object[] { fileName, e.getMessage() });
    } catch (IOException e) {
      throw new UnpositionedError(OptimizeMessages.IO_EXCEPTION, new Object[] { fileName, e.getMessage() });
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
      throw new UnpositionedError(OptimizeMessages.SEMANTIC_ERROR, new Object[] { fileName, e.getMessage() });
    } catch (IOException e) {
      throw new UnpositionedError(OptimizeMessages.IO_EXCEPTION, new Object[] { fileName, e.getMessage() });
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
    options = new OptimizeOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  protected OptimizeOptions	options;
}
