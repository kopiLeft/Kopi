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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Remote execution client
 */

public class RPrinter extends AbstractPrinter implements CachePrinter {

  /**
   * Creates a remote print client with specified host and port
   */
  public RPrinter(String name, String host, int port) {
    super(name);
    this.host = host;
    this.port = port;
  }

  /**
   * Creates a remote print client with specified host
   */
  public RPrinter(String name, String host) {
    super(name);
    this.host = host;
  }

  /**
   * Print a file and return the output of the command
   */
  public void setCommand(String user, String pass, String command) {
    this.user = user;
    this.pass = pass;
    this.command = command;
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  public String print(PrintJob printdata) throws IOException, PrintException {
    Rexec             exec = (port == -1) ? new Rexec(host) : new Rexec(host, port);
    InputStream       data = printdata.getInputStream();
    byte[]            buffer = new byte[1024];
    OutputStream      output;
    int               length;

    exec.open(user, pass, command+ " " + tray);
    output = exec.getOutputStream();
    
    while ((length = data.read(buffer)) != -1) {
      output.write(buffer, 0, length);
    }
    
    exec.close();
    return "NYI";
  }

  // ----------------------------------------------------------------------
  // TEST java com.kopiright.vkopi.lib.util.RPrinter host user passwd "lpr " afile
  // ----------------------------------------------------------------------

  public static void main(String[] args)
    throws IOException, PrintException
  {
    RPrinter	rexec = new RPrinter("RPrinter", args[0]);

    rexec.setCommand(args[1], args[2], args[3]);

    rexec.print(new PrintJob(new File(args[4]), false, PrintJob.FORMAT_A4));
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		user;
  private String		pass;
  private String		command;
  private String		host;
  private int                   port = -1;
}
