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

package com.kopiright.compiler.tools.optgen;

import java.io.PrintWriter;
import java.util.Hashtable;

class OptionDefinition {

  /**
   * Constructs an option definition
   */
  public OptionDefinition(String longname,
			  String shortname,
			  String type,
			  String defaultValue,
			  String argument,
			  String help)
  {
    this.longname = longname;
    this.shortname = shortname;
    this.type = type;
    this.defaultValue = defaultValue;
    this.argument = argument;
    this.help = help;
  }

  private static final String trail(String s) {
    // strip leading and trailing quotes
    if (s == null) {
      return null;
    } else if (s.length() < 2) {
      return s;
    } else {
      return s.substring(1, s.length() - 1);
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Check for duplicate identifiers
   *
   * @param	identifiers	a table of all token identifiers
   * @param	sourceFile	the file where the token is defined
   */
  public void checkIdentifiers(Hashtable identifiers, String sourceFile)
    throws OptgenError
  {
    String		stored = (String)identifiers.get(longname);

    if (stored != null) {
      throw new OptgenError(OptgenMessages.DUPLICATE_DEFINITION,
                            new Object[] { longname, sourceFile, stored });
    }

    identifiers.put(longname, sourceFile);
  }

  /**
   * Check for duplicate shortcuts
   *
   * @param	identifiers	a table of all token identifiers
   * @param	sourceFile	the file where the token is defined
   */
  public void checkShortcuts(Hashtable shortcuts, String sourceFile)
    throws OptgenError
  {
    String		stored = (String)shortcuts.get(shortname);

    if (stored != null) {
      throw new OptgenError(OptgenMessages.DUPLICATE_SHORTCUT,
                            new Object[] { shortname, sourceFile, stored });
    }

    shortcuts.put(shortname, sourceFile);
  }

  /**
   * Prints the case statement for the parseArgument method
   *
   * @param	out		the output stream
   */
  public void printParseArgument(PrintWriter out) {
    out.print("    case \'");
    out.print(shortname);
    out.println("\':");

    out.print("      ");
    out.print(longname);
    out.print(" = ");
    if (argument == null) {
      out.print("!" + defaultValue);
      out.print(";");
    } else {
      String	 methodName;
      String	 arg = this.argument;

      if (type.equals("int")) {
	methodName = "getInt";
	if (arg == null || arg.equals("")) {
	  arg = "0";
	}
      } else {
	methodName = "getString";
	arg = "\"" + arg + "\"";
      }

      out.print(methodName + "(g, " + arg + ")");
      out.print(";");
    }
    out.println(" return true;");
  }

  /**
   * Prints the field declaration
   *
   * @param	out		the output stream
   */
  public void printFields(PrintWriter out) {
    out.print("  public ");
    out.print(type);
    out.print(" ");
    out.print(longname);
    out.print(" = ");
    if (!type.equals("String") || defaultValue.equals("null")) {
      out.print(defaultValue);
    } else {
      out.print("\"" + defaultValue + "\"");
    }
    out.println(";");
  }

  /**
   * Prints the usage message
   *
   * @param	out		the output stream
   */
  public void printUsage(PrintWriter out) {
    StringBuffer	prefix = new StringBuffer("\"  --");

    prefix.append(longname);
    prefix.append(", -");
    prefix.append(shortname);
    if (argument != null) {
      prefix.append("<" + type + ">");
    }
    prefix.append(": ");
    for (int i = prefix.length(); i < 25; i++) {
      prefix.append(" ");
    }
    out.print(prefix.toString() + help);
    if (!defaultValue.equals("null")) {
      out.print(" [");
      out.print(defaultValue);
      out.print("]");
    }
    out.print("\"");
  }

  /**
   * Prints the LongOpt instantiation
   *
   * @param	out		the output stream
   */
  public void printLongOpts(PrintWriter out) {
    out.print("    new LongOpt(\"");
    out.print(longname);
    out.print("\", ");
    if (argument == null) {
      out.print("LongOpt.NO_ARGUMENT");
    } else if (argument.equals("")) {
      out.print("LongOpt.REQUIRED_ARGUMENT");
    } else {
      out.print("LongOpt.OPTIONAL_ARGUMENT");
    }
    out.print(", null, \'");
    out.print(shortname);
    out.print("\')");
  }

  /**
   * Prints the short option
   *
   * @param	out		the output stream
   */
  public void printShortOption(PrintWriter out) {
    out.print(shortname);
    if (argument != null) {
      if (argument.equals("")) {
	out.print(":");
      } else {
	out.print("::");
      }
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final String longname;
  private final String shortname;
  private final String type;
  private final String defaultValue;
  private final String argument;
  private final String help;
}
