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

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.LineNumberReader;
import java.io.InputStreamReader;

/**
 * Remote execution client
 */

public class Rexec {

  /**
   * Creates a remote execution client with specified host and port
   */
  public Rexec(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Creates a remote execution client with specified host
   */
  public Rexec(String host) {
    this(host, STANDARD_EXEC_PORT);
  }

  /**
   *
   */
  public boolean open(String user, String pass, String command) {
    setUser(user, pass);
    return run(command);
  }

  /**
   * Sets the user
   */
  public void setUser(String user, String pass) {
    this.user = user;
    this.pass = pass;
  }

  /**
   *
   */
  public boolean run(String command) {
    try {
      socket = new Socket(host, port);
    } catch (java.net.UnknownHostException e) {
      e.printStackTrace();
      return false;	// !!! raise an exception
    } catch (java.io.IOException e) {
      e.printStackTrace();
      return false;	// !!! raise an exception
    }

    try {
      OutputStream	output = socket.getOutputStream();

      output.write("0".getBytes());	// no socket for stderr
      output.write(0);

      output.write(user.getBytes());	// !!! at most 16 chars
      output.write(0);

      output.write(pass.getBytes());	// !!! at most 16 chars
      output.write(0);

      output.write(command.getBytes());
      output.write(0);

      return socket.getInputStream().read() == 0;
    } catch (java.io.IOException e) {
      e.printStackTrace();
      return false;	// !!! raise an exception
    }
  }

  /**
   *
   */
  public void close() {
    try {
      getOutputStream().flush();
      Thread.sleep(250); // !!!!
      getOutputStream().close();
    } catch (Exception e) {
      // Already close
      e.printStackTrace();
    }
    try {
      socket.close();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      // !!! raise an exception
    } finally {
      socket = null;
    }
  }

  /**
   *
   */
  public InputStream getInputStream() throws java.io.IOException {
    return socket.getInputStream();
  }

  /**
   *
   */
  public OutputStream getOutputStream() throws java.io.IOException {
    return socket.getOutputStream();
  }

  // ----------------------------------------------------------------------
  // TEST java org.kopi.vkopi.lib.util.Rexec host user passwd cmd
  // ----------------------------------------------------------------------

  public static void main(String[] args)
    throws java.io.IOException
  {
    System.err.println("USAGE:Rexec server user pass command");

    Rexec	rexec = new Rexec(args[0]);

    if (! rexec.open(args[1], args[2], args[3])) {
      System.err.println("Error");
      System.exit(1);
    }

    LineNumberReader	in = new LineNumberReader(new InputStreamReader(rexec.getInputStream()));
    while (true) {
      String	line = in.readLine();

      if (line == null) {
	break;
      }

      System.out.println("--> " + line);
    }

    rexec.close();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	STANDARD_EXEC_PORT = 512;	// exec/tcp

  private Socket		socket;
  private String		host;
  private String		user;
  private String		pass;
  private int			port;
}
