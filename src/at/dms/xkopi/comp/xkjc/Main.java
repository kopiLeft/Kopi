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

package at.dms.xkopi.comp.xkjc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import at.dms.compiler.base.CompilerMessages;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.tools.antlr.extra.InputBuffer;
import at.dms.compiler.tools.antlr.runtime.ParserException;
import at.dms.kopi.comp.kjc.CBinaryTypeContext;
import at.dms.kopi.comp.kjc.JCompilationUnit;
import at.dms.kopi.comp.kjc.KjcClassReader;
import at.dms.kopi.comp.kjc.KjcEnvironment;
import at.dms.kopi.comp.kjc.KjcOptions;
import at.dms.kopi.comp.kjc.KjcPrettyPrinter;
import at.dms.kopi.comp.kjc.TypeFactory;
import at.dms.util.base.Utils;

/**
 * This class implements the entry point of the Java compiler
 */
public class Main extends at.dms.kopi.comp.kjc.Main {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Entry point
   * @param	args		the command line arguments
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
  }

  /**
   * Second entry point
   */
  public static boolean compile(String[] args) {
    return new Main(null, null).run(args);
  }

  protected KjcEnvironment createEnvironment(KjcOptions options) {
    KjcClassReader      reader = new KjcClassReader(getWorkingDirectory(),
                                                    options.classpath,
                                                    options.extdirs,
                                                    new XKjcSignatureParser());
    return new XKjcEnvironment(reader,
                              new XKjcTypeFactory(this, reader, options.source.equals("1.5")),
                              options);
  }

  /**
   * Initialize the compiler (read classpath, check classes.zip)
   */
  protected void initialize(KjcEnvironment environment) {
    super.initialize(environment);
    XStdType.init(this, new CBinaryTypeContext(environment.getClassReader(), environment.getTypeFactory()));
    XUtils.initialize(environment, options.xkjcpath, !options.nooo, options.database);
  }

  public void setOptions(XKjcOptions options) {
    super.options = this.options = options;
  }

  /**
   * Parse the argument list
   */
  public boolean parseArguments(String[] args) {
    setOptions(new XKjcOptions());
    if (!options.parseCommandLine(args)) {
      return false;
    }
    infiles = Utils.toList(options.nonOptions);
    return true;
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
  protected JCompilationUnit parseFile(File file, KjcEnvironment environment) {
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

    XKjcParser		parser;
    JCompilationUnit	unit;
    long		lastTime = System.currentTimeMillis();

    parser = new XKjcParser(this, buffer, environment);

    try {
      unit = parser.jCompilationUnit();
    } catch (ParserException e) {
      reportTrouble(parser.beautifyParseError(e));
      unit = null;
    }

    if (verboseMode()) {
      inform(CompilerMessages.FILE_PARSED,
             file.getPath(),
             new Long(System.currentTimeMillis() - lastTime));
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

  /**
   * @return the corresponding PrettyPrinter
   */
  protected KjcPrettyPrinter getPrettyPrinter(String fileName,
                                              TypeFactory factory)
    throws IOException
  {
    return new XKjcPrettyPrinter(fileName, factory);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected XKjcOptions		options;
}
