/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.util.base;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * This class implements the entry point of the Java compiler
 */
public abstract class Options {

  /**
   * Creates a new Option object.
   *
   * @param	name		the command name to pass to getopt
   */
  public Options(String name) {
    this.name = name;
  }

  /**
   * Parses the command line and processes the arguments.
   *
   * @param	args		the command line arguments
   * @return true iff the command line is valid
   */
  public boolean parseCommandLine(String[] argv) {
    Getopt	parser = new Getopt(name, argv, getShortOptions(), getLongOptions(), true);

    for (;;) {
      int	code;

      code = parser.getopt();
      if (code == -1) {
	break;
      }
      if (!processOption(code, parser)) {
	return false;
      }
    }

    // store remaining arguments
    nonOptions = new String[argv.length - parser.getOptind()];
    System.arraycopy(argv, parser.getOptind(), nonOptions, 0, nonOptions.length);

    return true;
  }

  /**
   * @param	args		the command line arguments
   */
  public boolean processOption(int code, Getopt g) {
    switch (code) {
    case 'h':
      help();
      System.exit(0);
      break;
    case 'V':
      version();
      System.exit(0);
      break;
    default:
      return false;
    }

    return true;
  }

  public String[] getOptions() {
    return new String[] {
      "  --help, -h:           Displays the help information",
      "  --version, -V:        Prints out the version information"
    };
  }

  /**
   * Prints the available options.
   */
  public void printOptions() {
    String[]	options = getOptions();

    for (int i = options.length; --i >= 0; ) {
      for (int j = 0; j < i; j++) {
	if (options[j].compareTo(options[j + 1]) > 0) {
	  String	tmp = options[j];
	  options[j] = options[j+1];
	  options[j+1] = tmp;
	}
      }
    }

    System.out.println();
    System.out.println("Here are the available options : ");
    for (int i = 0; i < options.length; i++) {
      System.out.println(options[i]);
    }
  }

  /**
   * shows a help message.
   */
  public final void help() {
    usage();
    printOptions();
    System.err.println();
    version();
    System.err.println();
    System.err.println("This program is part of the Kopi Suite.");
    System.err.println("For more info, please see: http://www.kopiright.com/kopi");
  }

  /**
   * Shows the version number.
   */
  protected abstract void version();

  /**
   * Shows a usage message.
   */
  protected abstract void usage();

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Processes an integer argument.
   */
  protected int getInt(Getopt g, int defaultValue) {
    try {
      return g.getOptarg() != null ? Integer.parseInt(g.getOptarg()) : defaultValue;
    } catch (Exception e) {
      System.err.println("malformed option: " + g.getOptarg());
      System.exit(0);
      return 1;
    }
  }

  /**
   * Processes a string argument.
   */
  protected String getString(Getopt g, String defaultValue) {
    return g.getOptarg() != null ? g.getOptarg() : defaultValue;
  }

  protected String[] addString(String[] array, String str) {
    if (array == null) {
      return new String[] { str };
    } else {
      int size = array.length;
      String[] newArray =  new String [size + 1];

      for (int i = 0; i < size; i++) {
        newArray[i] = array[i];
      }
      newArray[size] = str;
      return newArray;
    }
  }

  protected int[] addInt(int[] array, int value) {
    if (array == null) {
      return new int[] { value };
    } else {
      int size = array.length;
      int[] newArray = new int [size + 1];

      for (int i = 0; i < size; i++) {
        newArray[i] = array[i];
      }
      newArray[size] = value;
      return newArray;
    }
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPTIONS
  // ----------------------------------------------------------------------

  /**
   * Gets short options.
   */
  public String getShortOptions() {
    return "hV";
  }

  /**
   * Gets long options.
   */
  public LongOpt[] getLongOptions() {
    return new LongOpt[] {
      new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
      new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V')
    };
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  /**
   * The array of non-option arguments.
   */
  public String[]		nonOptions;

  private final String		name;
}
