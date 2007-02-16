/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.util;

import java.io.File;
import java.io.IOException;

/**
 * Local printer
 */
public class PreviewPrinter extends AbstractPrinter implements CachePrinter {

  /**
   *
   */
  public PreviewPrinter(String command) {
    this("PreviewPrinter", command);
  }

  public PreviewPrinter(String name, String command) {
    super(name);
    setCommand(command);
  }

  /**
   *
   */
  public PreviewPrinter(String command, File previewFile) {
    this("PreviewPrinter", command);
  }

  /**
   * Sets the command
   */
  public void setCommand(String command) {
    this.command = command;
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

 /**
   * Print a file and return the output of the command
   */
  public String print(PrintJob printData) throws IOException, PrintException {
      // execute in separte process
      File              dataFile = File.createTempFile("kopiprinter", "ps");
      
      // file is used with an external program (and cache printer),
      // do not delete it
      printData.writeDataToFile(dataFile);
      Runtime.getRuntime().exec(command + " " + dataFile);

      return "NYI";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		command;
}
