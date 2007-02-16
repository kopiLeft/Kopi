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

package com.kopiright.xkopi.comp.dbi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.CompilerMessages;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
import com.kopiright.compiler.tools.antlr.runtime.ParserException;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.Utils;
import com.kopiright.xkopi.comp.sqlc.SqlPhylum;
import com.kopiright.xkopi.lib.base.DBContext;

/**
 * This class implements the entry point of the compiler
 */
public class Main extends Compiler implements Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

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

  /**
   * Entry point
   */
  public static void main(String[] args) {
    boolean	success;

    try {
      success = new Main(null, null).run(args);
    } catch (RuntimeException re) {
      System.err.println("Failed: " + re.getMessage());
      success = false;
    }

    flush();
    System.exit(success ? 0 : 1);
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
    
    if (infiles.isEmpty() && (!options.stdin)) {
      options.usage();
      inform(DbiMessages.NO_INPUT_FILE);
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

    destination = checkDestination(destination);

    SCompilationUnit[]	tree = new SCompilationUnit[infiles.isEmpty() ? 1 : infiles.size()];

    if (!infiles.isEmpty()) {
      for (int count = 0; count < tree.length; count++) {
	tree[count] = parseFile((File)infiles.get(count));
      }
    } else {
      tree[0] = parseFile(createFileFromInputStream());
    }

    if (errorFound) {
      return false;
    }
    try {
      if (options.interfaceGen != null) {
	generateDatabase(tree, options.interfaceGen, options.destination, options.iClassName, options.sqlCase != null && options.sqlCase.equals("upper"));
      } else if (options.beautify) {
        genSQLSource(tree);
      } else if (options.stdout) {
        genSQLCode(options.syntax, tree);
      } else {
	if (context != null) {
	  for (int count = 0; count < tree.length; count++) {
	    tree[count].execute(context,
                                options.simulate,
                                options.syntax,
                                getDBAccess(context),
                                out);
	  }
	}
	if (options.commit != null) {
	  for (int count = 0; count < tree.length; count++) {
	    tree[count].addScript(dictContext,
                                  options.simulate,
                                  options.syntax,
                                  getDBAccess(dictContext),
                                  out);
	  }
	}
      }
    } catch (PositionedError e) {
      reportTrouble(e);
    } 

    out.flush();

    return true;
  }

  public void generateDatabase(SCompilationUnit[] cunit,
			       String packageName,
			       String destination,
                               String className,
                               boolean toUpperCase)
  {
    //inherit the current method to have the className information.
    generateDatabase(cunit, packageName, destination);
  }

  public void generateDatabase(SCompilationUnit[] cunit,
			       String packageName,
			       String destination)
  {
    throw new InconsistencyException("Should provide a DBInterface");
  }

  /**
   * Parse the argument list
   */
  public boolean parseArguments(String[] args) {
    options = new DbiOptions();

    if (!options.parseCommandLine(args)) {
      return false;
    } else {
      infiles = Utils.toList(options.nonOptions);
      return true;
    }
  }

  /**
   * Initialize the compiler (read classpath, check classes.zip)
   */
  protected boolean initialize() {
    try {
      bundle = ResourceBundle.getBundle("dbi");
    } catch (MissingResourceException e) {
      //!!! signal error
    }

    if (options.driver == null) {
      if(getProperty("driver")!= null){
        options.driver = getProperty("driver");
      }
      else{
      return false;
      }
    }

    if (options.url == null) {
      if (options.commit == null
          && !options.stdout
          && !options.beautify
          && !(options.interfaceGen != null))
        {
          inform(DbiMessages.NO_CONNECTION);
          return false;
        }
    } else if (options.url.indexOf("//") < 0) {
       options.url = getProperty("url") + options.url;
    }

    if (options.commit != null) {
      if (options.commit.indexOf("//") < 0) {
	options.commit = getProperty("url") + options.commit;
      }
    }

    if (options.login == null) {
      options.login = getProperty("login");
    }
    if (options.passwd == null) {
      options.passwd = getProperty("passwd");
    }

    try {
      com.kopiright.xkopi.lib.base.DBContext.registerDriver(options.driver);
      if (options.url != null) {
	context = new com.kopiright.xkopi.lib.base.DBContext();
	context.setDefaultConnection(context.createConnection(options.url, options.login, options.passwd));
      }
      if (options.commit != null) {
	dictContext = new com.kopiright.xkopi.lib.base.DBContext();
	dictContext.setDefaultConnection(dictContext.createConnection(options.commit, options.login, options.passwd));
      }
    } catch (Exception e) {
      e.printStackTrace();
      inform(DbiMessages.CONNECTION_FAILED, e.getMessage());
      return false;
    }
    return true;
  }

  protected static void exit() {
    flush();
    throw new InconsistencyException("THE END");
  }

  protected static void flush() {
    out.flush();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  protected DBAccess getDBAccess(DBContext context) {
    return null;
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
    return true;
  }

  /**
   * Returns true iff compilation runs in verbose mode.
   */
  public boolean verboseMode() {
    return false;
  }

  private static String getProperty(String prop) {
    try {
      return bundle.getString(prop);
    } catch (MissingResourceException e) {
      // try next one
    }

    return null;
  }

  // ----------------------------------------------------------------------
  // PROTECTED METHODS
  // ----------------------------------------------------------------------

  /**
   * parse the given file and return a compilation unit
   * side effect: increment error number
   * @param	file		the name of the file (assert exists)
   * @return	the compilation unit defined by this file
   */
  public SCompilationUnit parseFile(File file) {
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

    DbiParser		parser;
    SCompilationUnit	unit;
    long		lastTime = System.currentTimeMillis();

    parser = new DbiParser(this, buffer);

    try {
      unit = parser.makeDB();
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

  private File createFileFromInputStream() {
    final String        filename = "dbi.tmp";

    try {
      InputStream	source = System.in;
      OutputStream	target = new FileOutputStream(filename);
      byte[]		buffer = new byte[128];
      int		count;

      for (;;) {
	count = source.read(buffer);
	if (count == -1) {
	  break;
	}
	target.write(buffer, 0, count);
      }
      target.close();

      return new File(filename);
    } catch (IOException e) {
      e.printStackTrace();

      return null;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates SQL code.
   */
  protected void genSQLCode(final String syntax,
                            final SCompilationUnit[] tree)
    throws PositionedError
  {
    for (int count = 0; count < tree.length; count++) {
      genSQLCode(options.syntax, tree[count]);
    }
  }

  /**
   * Generates SQL code.
   */
  protected final void genSQLCode(final String syntax,
                                  final SCompilationUnit unit)
    throws PositionedError
  {
    genSQLCode(DbiChecker.create(syntax), unit);
  }


  /**
   * Generates SQL code.
   */
  protected final void genSQLCode(final DbiChecker checker,
                                  final SCompilationUnit unit)
    throws PositionedError
  {
    boolean	hasHead = unit.checkHead();
    List        elems = unit.getElems();

    for (int i = hasHead ? 1 : 0; i < elems.size(); i++) {
      ((SqlPhylum)elems.get(i)).accept(checker);

      handleStatment(checker.getStatementText());
    }
  }

  protected void handleStatment(String str) {
    out.println(str);
  }

  /**
   * Generates SQL code.
   */
  public void genSQLSource(final SCompilationUnit[] tree) {
    for (int count = 0; count < tree.length; count++) {
      genSQLSource(tree[count]);
    }
  }

  /**
   * Generates SQL code.
   */
  public void genSQLSource(final SCompilationUnit unit) {
    final String        filename = unit.getTokenReference().getFile();
    DbiPrettyPrinter    spp = null;

    try {
      spp = new DbiPrettyPrinter(filename);
      unit.accept(spp);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.err.println("cannot write : " + filename);
    } catch (PositionedError e) {
      e.printStackTrace();
    } finally {
      if (spp != null) {
        spp.close();
      }
    }
  }

  // ----------------------------------------------------------------------
  // PROTECTED DATA MEMBERS
  // ----------------------------------------------------------------------

  protected static PrintWriter	out = new PrintWriter(System.out);
  protected String		destination = "";

  protected List		infiles;

  private static DbiOptions	options;
  private static ResourceBundle bundle;

  private static DBContext	context;
  private static DBContext	dictContext;

  private boolean		errorFound;
}
