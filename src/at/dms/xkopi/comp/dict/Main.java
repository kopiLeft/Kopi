/*
 * Copyright (C) 1990-99 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: Main.java,v 1.2 2005/01/19 11:25:21 mike Exp $
 */

package at.dms.xkopi.comp.dict;

import java.io.PrintWriter;

import at.dms.xkopi.comp.dbi.SCompilationUnit;

/**
 * This class implements the entry point of the compiler
 */
public class Main extends at.dms.xkopi.comp.dbi.Main {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Entry point
   */
  public static void main(String[] args) {
    boolean	success;

    try {
      success = new Main(null, null).run(args);
    } catch (RuntimeException e) {
      e.printStackTrace();
      success = false;
    }

    flush();
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

  public void generateDatabase(SCompilationUnit[] cunit,
			       String packageName,
			       String destination,
                               String classname,
                               boolean toUpperCase)
  {
    DBInterface.generateDatabase(this, cunit, packageName, destination, classname, toUpperCase);
  }

}
