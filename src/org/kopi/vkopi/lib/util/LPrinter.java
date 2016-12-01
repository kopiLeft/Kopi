/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Local printer
 */
public class LPrinter extends AbstractPrinter {

  /**
   *
   */
  public LPrinter(String name, String command) {
    super(name);
    setCommand(command);
  }

  /**
   * Print a file and return the output of the command
   */
  public void setCommand(String command) {
    this.command = command;
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  public String print(PrintJob printData) throws IOException, PrintException {
    Process           process = Runtime.getRuntime().exec(command);
    InputStream       data = printData.getInputStream(); 
    byte[]            buffer = new byte[1024];
    OutputStream      output; 
    int               length;
    
    output = process.getOutputStream();
    
    while ((length = data.read(buffer)) != -1) {
      output.write(buffer, 0, length);
    }
    output.close();
    
    return "NYI";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		command;
}
