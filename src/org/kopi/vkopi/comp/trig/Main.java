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

package org.kopi.vkopi.comp.trig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.CompilerMessages;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.tools.antlr.extra.InputBuffer;
import org.kopi.compiler.tools.antlr.runtime.ParserException;
import org.kopi.kopi.comp.kjc.CBinaryTypeContext;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.KjcPrettyPrinter;
import org.kopi.kopi.comp.kjc.KjcEnvironment;
import org.kopi.kopi.comp.kjc.TypeFactory;

/**
 * This class implements the entry point of the Java compiler
 */
public class Main extends org.kopi.xkopi.comp.xkjc.Main {

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
  }

  // ----------------------------------------------------------------------
  // INITIALIZATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void initialize(KjcEnvironment environment) {
    super.initialize(environment);
    setSourceVersion(environment.getSourceVersion());
    GStdType.init(new CBinaryTypeContext(environment.getClassReader(),
                                         environment.getTypeFactory()),
                  this);
  }

  /**
   * Access files
   */
  public List getFiles() {
    return infiles;
  }

  /**
   * Access files
   */
  public void setFiles(List infiles) {
    this.infiles = infiles;
  }

  /**
   * parse the argument list and set flags
   * @return parsing error occur
   */
  public boolean parseFiles(KjcEnvironment environment) {
    tree = new JCompilationUnit[infiles.size()];

    for (int count = 0; count < tree.length; count++) {
      tree[count] = parseFile((File)infiles.get(count), environment);
    }

    return !errorFound;
  }

  /**
   * check interface of forms and java files
   * @return parsing error occur
   */
  public boolean join(JCompilationUnit[] units) {
    if (units.length + tree.length == 0) {
      reportTrouble(new PositionedError(TokenReference.NO_REF, GKjcMessages.NO_FILES_GIVEN));
    }

    // adopt forms
    JCompilationUnit[] tmp = new JCompilationUnit[tree.length + units.length];

    for (int i = 0; i < tree.length; i++) {
      tmp[i] = tree[i];
    }
    for (int i = tree.length; i < tree.length + units.length; i++) {
      tmp[i] = units[i - tree.length];
    }
    //    tree = tmp;

    for (int count = 0; count < tmp.length; count++) {
      join(tmp[count]);
    }

    return !errorFound;
  }

  /**
   * check interface of forms and java files
   * @return parsing error occur
   */
  public boolean checkInterface(JCompilationUnit[] units) {
    if (units.length + tree.length == 0) {
      reportTrouble(new PositionedError(TokenReference.NO_REF, GKjcMessages.NO_FILES_GIVEN));
    }

    // adopt forms
    JCompilationUnit[] tmp = new JCompilationUnit[tree.length + units.length];
    for (int i = 0; i < tree.length; i++) {
      tmp[i] = tree[i];
    }
    for (int i = tree.length; i < tree.length + units.length; i++) {
      tmp[i] = units[i - tree.length];
    }
    tree = tmp;

    for (int count = 0; count < tree.length; count++) {
      checkInterface(tree[count]);
    }

    return !errorFound;
  }

  /**
   * prepare initializers of forms and java files
   * 
   * @return	parsing error occur
   * @see	#checkInitializers()
   */
  public boolean prepareInitializers() {
    for (int count = 0; count < tree.length; count++) {
      prepareInitializers(tree[count]);
    }
    
    return !errorFound;
  }

  /**
   * check initializers of forms and java files
   * 
   * @return	parsing error occur
   */
  public boolean checkInitializers() {
    for (int count = 0; count < tree.length; count++) {
      checkInitializers(tree[count]);
    }
    
    return !errorFound;
  }

  /**
   * check the body of forms and java files
   * @return parsing error occur
   */
  public boolean checkBody() {
    for (int count = 0; count < tree.length; count++) {
      checkBody(tree[count]);
      if (!options.java && !options.beautify) {
        tree[count] = null;
      }
    }

    return !errorFound;
  }

  /**
   * generate code
   */
  public void genCode(TypeFactory factory) {
    if (!options.nowrite) {
      // Write Output
      if (options.java || options.beautify) {
        for (int count = 0; count < tree.length; count++) {
          generateJavaCode(tree[count], factory);
        }
      } else {
	super.genCode(factory);
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  protected boolean filterWarning(CWarning warning) {
    if (warning.hasDescription(org.kopi.kopi.comp.kjc.KjcMessages.METHOD_UNTHROWN_EXCEPTION)) {
      return false;
    } else if (warning.hasDescription(org.kopi.kopi.comp.kjc.KjcMessages.FIELD_RENAME_SUPER)
	       && warning.getTokenReference().getFile().endsWith(".vp")) {
      return false;
    } else {
      return super.filterWarning(warning);
    }
  }

  // ----------------------------------------------------------------------
  // PARSING METHODS
  // ----------------------------------------------------------------------

  /**
   * parse the givven file and return a compilation unit
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

    GKjcParser		parser;
    JCompilationUnit	unit;
    long		lastTime = System.currentTimeMillis();

    parser = new GKjcParser(this, buffer, environment);

    try {
      unit = parser.jCompilationUnit();
    } catch (ParserException e) {
      reportTrouble(parser.beautifyParseError(e));
      errorFound = true;
      unit = null;
    } catch (Exception e) {
      e.printStackTrace();
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

  /**
   * @return the corresponding PrettyPrinter
   */
  protected KjcPrettyPrinter getPrettyPrinter(String fileName,
                                              TypeFactory factory)
    throws IOException
  {
    return new GKjcPrettyPrinter(fileName, factory);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JCompilationUnit[]	tree;
}
