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
 * $Id: Main.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.sqlc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.CompilerMessages;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.tools.antlr.extra.InputBuffer;
import at.dms.compiler.tools.antlr.runtime.ParserException;
import at.dms.util.base.Utils;

/**
 * This class implements the entry point of the Java compiler
 */
public class Main extends Compiler {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Entry point
   */
  public static void main(String[] args) {
    boolean	success;

    success = new Main(null, null).run(args);

    System.exit(success ? 0 : 1);
  }

  /**
   * Creates a new compiler instance.
   *
   * @param	workingDirectory	the working directory
   * @param	diagnosticOutput	the diagnostic output stream
   */
  public Main(String workingDirectory, PrintWriter diagnosticOutput) {
    super(workingDirectory, diagnosticOutput);

    infiles = new Vector();
  }

  // --------------------------------------------------------------------
  // Language
  // --------------------------------------------------------------------

  /**
   * Returns the version of the source code
   *
   * @return     version of the code
   */
  public int getSourceVersion() {
    return  1;
  }

  // ----------------------------------------------------------------------
  // RUN FROM COMMAND LINE
  // ----------------------------------------------------------------------

  /**
   * Runs the compiler
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    }

    initialize();

    if (infiles.isEmpty()) {
      options.usage();
      inform(SqlcMessages.NO_INPUT_FILE);
      return false;
    }

    if (verboseMode()) {
      inform(CompilerMessages.COMPILATION_STARTED, new Integer(infiles.size()));
    }

    try {
      infiles = verifyFiles(infiles);
    } catch (UnpositionedError e) {
      reportTrouble(e);
      return false;
    }

    //!!!NOT USED destination = checkDestination(destination);

    SCompilationUnit[]	tree = new SCompilationUnit[infiles.size()];

    for (int count = 0; count < tree.length; count++) {
      tree[count] = parseFile((File)infiles.get(count));
    }

    if (errorFound) {
      return false;
    }

    for (int count = 0; count < tree.length; count++) {
      tree[count].check();
    }

    if (errorFound) {
      return false;
    }

    try {
      for (int count = 0; count < tree.length; count++) {
	tree[count].genSql();
      }
    } catch (PositionedError e) {
      e.printStackTrace();
    }

    return true;
  }

  /**
   * parse the argument list and set flags
   * @return parsing error occur
   */
  protected boolean parseArguments(String[] args) {
    options = new SqlcOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    infiles = Utils.toList(options.nonOptions);
    return true;
  }

  /**
   * Initialize the compiler (read classpath, check classes.zip)
   */
  protected void initialize() {
    XUtils.setChecker(new DBChecker() {
	/**
	 * Checks if table exists
	 */
	public boolean tableExists(String table) {
	  return true;
	}

	/**
	 * Checks if a column exists in a table
	 */
	public boolean columnExists(String table, String column) {
	  return true;
	}});
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return true if compilation in verbose mode
   */
  public boolean verboseMode() {
    return false;
  }

  // --------------------------------------------------------------------
  // COMPILER
  // --------------------------------------------------------------------

  /**
   * Reports a trouble (error or warning).
   *
   * @param	trouble		a description of the trouble to report.
   */
  public void reportTrouble(PositionedError trouble) {
    if (trouble instanceof CWarning) {
      if (options.warning) {
	inform(trouble);
      }
    } else {
      if (trouble.getTokenReference() != TokenReference.NO_REF) {
	inform(trouble);
	errorFound = true;
      }
    }
  }

  /**
   * Reports a trouble.
   *
   * @param	trouble		a description of the trouble to report.
   */
  public void reportTrouble(Throwable trouble) {
    inform(CompilerMessages.FORMATTED_ERROR, trouble.getMessage());
    errorFound = true;
  }

  /**
   * Returns true iff comments should be parsed (false if to be skipped)
   */
  public boolean parseComments() {
    return false;
  }

  // ----------------------------------------------------------------------
  // PROTECTED METHODS
  // ----------------------------------------------------------------------

  /**
   * parse the givven file and return a compilation unit
   * side effect: increment error number
   * @param	file		the name of the file (assert exists)
   * @return	the compilation unit defined by this file
   */
  protected SCompilationUnit parseFile(File file) {
    InputBuffer		buffer;

    try {
      buffer = new InputBuffer(file, options.encoding);
    } catch (UnsupportedEncodingException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.UNSUPPORTED_ENCODING,
					  options.encoding));
      return null;
    } catch (IOException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.IO_EXCEPTION,
					  file.getPath(),
					  e.getMessage()));
      return null;
    }

    SqlcParser		parser;
    SCompilationUnit	unit;
    long		lastTime = System.currentTimeMillis();

    parser = new SqlcParser(this, buffer);

    try {
      unit = parser.sCompilationUnit();
    } catch (ParserException e) {
      reportTrouble(parser.beautifyParseError(e));
      unit = null;
    } catch (Exception e) {
      e.printStackTrace();
      //err.println("{" + file.getPath() + ":" + scanner.getLine() + "} " + e.getMessage());
      errorFound = true;
      unit = null;
    }

    if (verboseMode()) {
      inform(CompilerMessages.FILE_PARSED, file.getPath(), new Long(System.currentTimeMillis() - lastTime));
    }

    try {
      buffer.close();
    } catch (IOException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.IO_EXCEPTION,
					  file.getPath(),
					  e.getMessage()));
    }

    return unit;
  }

  // ----------------------------------------------------------------------
  // PROTECTED DATA MEMBERS
  // ----------------------------------------------------------------------

  protected List			infiles;
  protected boolean			errorFound;

  public SqlcOptions			options;
}
