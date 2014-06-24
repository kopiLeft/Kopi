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

package com.kopiright.vkopi.lib.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.kopiright.vkopi.lib.base.Utils;

/**
 * DefaultPrinter
 */
public abstract class AbstractPrinter implements Printer {

  protected AbstractPrinter(String name) {
    this.name = name;

    numberCopy = 1;
    tray = 1;
  }


  public String getPrinterName() {
    return name;
  }

  /**
   * Sets the number of copy to print
   */
  public void setNumberOfCopies(int number) {
    this.numberCopy = number;
  }

  /**
   * Gets the number of copy to print
   */
  public int getNumberOfCopies() {
    return numberCopy;
  }

  /**
   * Sets the tray to use
   */
  public void selectTray(int tray) {
    this.tray = tray;
  }

  /**
   * Sets the paper format
   */
  public void setPaperFormat(String paperFormat) {
    this.paperFormat = paperFormat;
  }
  // ----------------------------------------------------------------------
  // DATA CONSTANTS
  // ----------------------------------------------------------------------

  public static PrintJob convertToGhostscript(PrintJob printdata) throws IOException {
    //    file = null;
    File                tempfile = Utils.getTempFile("kopigsconv", "PS"); 
    PrintJob            gsJob = printdata.createFromThis(tempfile, true);
    BufferedWriter      ous = new BufferedWriter(new FileWriter(tempfile));
    
    // READ HEADER
    BufferedReader      reader = new BufferedReader(new InputStreamReader(printdata.getInputStream()));
    String              line;
    int                 currentPage = -1;
    
    while ((line = reader.readLine()) != null) {
      if (line.equals(TOPRINTER_TRUE)) {
        ous.write(TOPRINTER_FALSE);
      } else if (printdata.getNumberOfPages() == -1 && line.startsWith("%%Page: ")) {
        currentPage = readCurrentPageNumber(line);
        ous.write(line);
      } else {
        ous.write(line);
      }
      ous.write("\n");
    }
    ous.close();
    
    if (gsJob.getNumberOfPages() == -1 && currentPage != -1) {
      gsJob.setNumberOfPages(currentPage);
    }
    
    return gsJob;
  }

  private static int readCurrentPageNumber(String line) {
    StringBuffer    buffer = new StringBuffer();
    
    // skip "%%Page: "
    for (int i = 8; i < line.length() && Character.isDigit(line.charAt(i)); i++) {
      buffer.append(line.charAt(i));
    }
    if (buffer.length() == 0) {
      return -1;
    } else {
      try {
        return Integer.parseInt(buffer.toString());
      } catch (NumberFormatException e) {
        return -1;
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA CONSTANTS
  // ----------------------------------------------------------------------

  protected static final String TOPRINTER_TRUE = "/toprinter {true} def";
  protected static final String TOPRINTER_FALSE = "/toprinter {false} def";

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected String              name;

  protected int                 numberCopy;
  protected int                 tray;
  protected String              paperFormat;
}
