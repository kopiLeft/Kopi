/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: Main.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.util.ipp;

import java.io.File;
import java.io.FileInputStream;

/**
 * Print files using IPP
 */
public class Main {

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * Program entry point
   */
  public static void main(String[] args) {
    try {
      IPPOptions options = new IPPOptions();

      if (!parseArguments(options, args)) {
        options.help();
        System.exit(1);
        return;
      }

      IPPClient  printer = new IPPClient(options.host,
                                         (short) options.port,
                                         options.printer,
                                         options.user);

      // the files are the non options of the command line arguments
      String[]    infiles = options.nonOptions;

      // print each file
      for (int i = 0; i < infiles.length; i++) {
        printer.print(new FileInputStream(new File(infiles[i])),
                      options.nbcopies,
                      new String[] {options.tray});
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e);
    }
  }


  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  /**
   * Parse command line arguments
   *
   * @param options the ipp options to use
   * @param args the command line arguments
   * @return true iff the command line was correctly parsed
   */
  private static boolean parseArguments(IPPOptions options, String[] args) {
    if (!options.parseCommandLine(args)) {
      return false;
    }
    if (options.host == null) {
      System.err.println("IPP Host name required!");
      return false;
    }
    if (options.printer == null) {
      System.err.println("Printer name required!");
      return false;
    }
    if (options.user == null) {
      System.err.println("User name required!");
      return false;
    }
    return true;
  }
}
