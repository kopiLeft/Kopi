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


import com.kopiright.compiler.base.CompilerMessages;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.tools.antlr.runtime.ParserException;
import com.kopiright.util.base.InconsistencyException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/*package*/ class DefinitionFile {

  /**
   * Constructs a  definition file
   */
  public DefinitionFile(String sourceFile,
			String fileHeader,
			String packageName,
 			String parent,
 			String prefix,
 			String version,
 			String usage,
			OptionDefinition[] definitions)
  {
    this.sourceFile	= sourceFile;
    this.fileHeader	= fileHeader;
    this.packageName	= packageName;
    this.parent		= parent;
    this.version	= version;
    this.usage		= usage;
    this.prefix		= prefix;
    this.definitions	= definitions;
  }

  /**
   * Reads and parses an token definition file
   *
   * @param	sourceFile		the name of the source file
   * @return	a class info structure holding the information from the source
   *
   */
  public static DefinitionFile read(String sourceFile) throws OptgenError {

    Document            document;
    SAXBuilder          builder = new SAXBuilder();
    Element             root;

    try {
      document = builder.build(new File(sourceFile));
    } 
    catch (Exception e) {
      throw new InconsistencyException("Cannot load file " + sourceFile + ": " + e.getMessage());
    }

    root = document.getRootElement();	
    
    return new DefinitionFile(sourceFile,
                              root.getAttributeValue("fileHeader"),
                              root.getAttributeValue("package"),
                              root.getAttributeValue("parent"),
                              root.getAttributeValue("prefix"),
                              root.getAttributeValue("version"),
                              root.getAttributeValue("usage"),
                              getOptions(root));
  }


  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the version. Overrides the version supplied in the definitions file.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Checks for duplicate identifiers.
   */
  public void checkIdentifiers() throws OptgenError {
    Hashtable		identifiers = new Hashtable();

    for (int i = 0; i < definitions.length; i++) {
      definitions[i].checkIdentifiers(identifiers, sourceFile);
    }
  }

  /**
   * Checks for duplicate shortcuts.
   *
   */
  public void checkShortcuts() throws OptgenError {
    Hashtable		identifiers = new Hashtable();

    for (int i = 0; i < definitions.length; i++) {
      definitions[i].checkShortcuts(identifiers, sourceFile);
    }
  }

  /**
   * Generates the option parser.
   *
   * @param	out		the output stream
   */
  public void printFile(PrintWriter out) {
    if (fileHeader != null) {
      out.println(fileHeader);
    }
    out.print("// Generated by optgen from " + sourceFile);
    out.println();
    out.println("package " + packageName + ";");
    out.println();
    out.println("import gnu.getopt.Getopt;");
    out.println("import gnu.getopt.LongOpt;");
    out.println();
    out.print("public class " + prefix + "Options");
    out.print(parent == null ? "" : " extends " + parent);
    out.println(" {");

    // CONSTRUCTORS
    out.println();
    out.println("  public " + prefix + "Options(String name) {");
    out.println("    super(name);");
    out.println("  }");
    out.println();
    out.println("  public " + prefix + "Options() {");
    out.println("    this(\"" + prefix + "\");");
    out.println("  }");
    out.println();

    // FIELDS
    for (int i = 0; i < definitions.length; i++) {
      definitions[i].printFields(out);
    }

    // PROCESSOPTION
    out.println();
    out.println("  public boolean processOption(int code, Getopt g) {");
    out.println("    switch (code) {");
    for (int i = 0; i < definitions.length; i++) {
      definitions[i].printParseArgument(out);
    }
    out.println("    default:");
    out.println("      return super.processOption(code, g);");
    out.println("    }");
    out.println("  }");


    // GETOPTIONS
    out.println();
    out.println("  public String[] getOptions() {");
    out.println("    String[]	parent = super.getOptions();");
    out.println("    String[]	total = new String[parent.length + " + definitions.length+ "];");
    out.println("    System.arraycopy(parent, 0, total, 0, parent.length);");
    for (int i = 0; i < definitions.length; i++) {
      out.print("    total[parent.length + " + i + "] = ");
      definitions[i].printUsage(out);
      out.println(";");
    }
    out.println("    ");
    out.println("    return total;");
    out.println("  }");

    // GETSHORTOPTIONS
    out.println("\n");
    out.println("  public String getShortOptions() {");
    out.print("    return \"");
    for (int i = 0; i < definitions.length; i++) {
      definitions[i].printShortOption(out);
    }
    out.println("\" + super.getShortOptions();");
    out.println("  }");

    // VERSION
    out.println("\n");
    out.println("  public void version() {");
    out.print("    System.out.println(");
    out.print(version == null ? "" : "\"" + version + "\"");
    out.println(");");
    out.println("  }");

    // USAGE
    out.println("\n");
    out.println("  public void usage() {");
    if (usage != null) {
      out.print("    System.err.println(");
      out.print("\"" + usage + "\"");
      out.println(");");
    }
    out.println("  }");

    // GETLONGOPTIONS
    out.println();
    out.println("  public LongOpt[] getLongOptions() {");
    out.println("    LongOpt[]	parent = super.getLongOptions();");
    out.println("    LongOpt[]	total = new LongOpt[parent.length + LONGOPTS.length];");
    out.println("    ");
    out.println("    System.arraycopy(parent, 0, total, 0, parent.length);");
    out.println("    System.arraycopy(LONGOPTS, 0, total, parent.length, LONGOPTS.length);");
    out.println("    ");
    out.println("    return total;");
    out.println("  }");

    // LONGOPTS
    out.println();
    out.println("  private static final LongOpt[] LONGOPTS = {");
    for (int i = 0; i < definitions.length; i++) {
      if (i != 0) {
	out.println(",");
      }
      definitions[i].printLongOpts(out);
    }
    out.println();
    out.println("  };");

    out.println("}");
  }

  /**
   * Returns the package name
   */
  public String getClassName() {
    return packageName + "." + prefix + "Options";
  }

  /**
   * Returns the package name
   */
  public String getPackageName() {
    return packageName;
  }

  /**
   * Returns the literal prefix
   */
  public String getPrefix() {
    return prefix;
  }


  /**
   * Reads options from the xml definition file
   *
   * @param     element      the xml root element
   * @return    a class info structure holding the information from the source
   */
  public static OptionDefinition[] getOptions(Element element) {

    List                params;
    OptionDefinition[]  options;
    Iterator            iter;

    params = element.getChildren("param");
    options = new OptionDefinition[params.size()];
    iter = params.iterator();

    for (int i = 0; iter.hasNext(); i++) {
      Element   current = (Element)iter.next();
      String    type;
      String    arg;
      boolean   isMultiple = false;
 
      type = current.getAttributeValue("type");
      isMultiple = current.getAttributeValue("multiple") != null;
      arg = current.getAttributeValue("optionalDefault");
      if (arg == null && !type.equals("boolean")) {
        arg = "";
      }
      options[i] = new OptionDefinition(current.getAttributeValue("longname"),
                                        current.getAttributeValue("shortname"),
                                        type,
                                        isMultiple,
                                        current.getAttributeValue("default"),
                                        arg,
                                        current.getAttributeValue("help"));
    }
    return options;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final String			sourceFile;
  private final String			fileHeader;
  private final String			packageName;
  private final String			parent;
  private final String			prefix;
  private String			version;
  private final String			usage;
  private final OptionDefinition[]	definitions;
}
