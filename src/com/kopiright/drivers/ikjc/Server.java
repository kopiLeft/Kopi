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

package com.kopiright.drivers.ikjc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Vector;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.util.base.Utils;

/**
 * This class implements the server side of the Kopi client/server compiler.
 */
public class Server {

  /**
   * Launch the server
   */
  public static void main(final String[] args) {
    parseArguments(args);

    // create the socket
    try {
      if (serverSocket == null) {
	serverSocket = new ServerSocket(options.port);
      }
    } catch (IOException e) {
      System.err.println("Could not listen on port: " + options.port);
      System.exit(1);
    }

    // call our compiler with changed output
    Thread t = new Thread(new ThreadGroup("Worker"), new Runnable() {
      public void run() {
	try {
	  new Server().run();
	} catch (Exception e) {
	  e.printStackTrace();
	  try {
	    clientSocket.close();
	  } catch (Exception ef) {}
	  main(args);
	}
      }});
    t.setPriority(Thread.MIN_PRIORITY);
    t.start();
  }

  /**
   * The run process
   */
  public void run() throws Exception {
    if (options.verbose) {
      System.err.println("Server launched.");
    }

    while (true) {
      if (options.verbose) {
	System.err.println("Waiting for clients...");
      }

      try {
	clientSocket = serverSocket.accept();
      } catch (IOException e) {
	System.err.println("Accept failed.");
	continue;
      }

      processClientRequest(clientSocket);

      if (options.verbose) {
	System.err.println("Compilation processed, cleaning up ...");
      }
      clientSocket.close();
      //System.gc();
    }

    // serverSocket.close();
  }

  /*
   *
   */
  private void processClientRequest(Socket client) throws Exception {
    BufferedReader	in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    PrintWriter		out = new PrintWriter(client.getOutputStream(), true);
    
    if (options.verbose) {
      System.err.println("Processing compilation...");
    }

    try {
      // read arguments
      Vector		args = new Vector();
      String		line;
      Class		clazz;
      Constructor	constructor;
      Compiler		compiler;
      String		directory;
      String[]		arguments;
      boolean		success;

      while ((line = in.readLine()) != null && !line.equals("")) {
	args.addElement(line);
      }

      if (options.verbose) {
	System.err.print("Calling ikjc with arguments:");
	for (int i = 0; i < args.size(); i++) {
	  System.err.print(' ');
	  System.err.print(args.elementAt(i));
	}
	System.err.println();
      }

      if (args.size() <= 2) {
	System.err.println("Error: not enough arguments given.");
	return;
      }

      // get working directory
      directory = (String)args.elementAt(0);
      args.removeElementAt(0);

      // get compiler class
      clazz = Class.forName("com.kopiright." + ((String)args.elementAt(0)) + ".Main");
      args.removeElementAt(0);

      // get constructor
      constructor = clazz.getConstructor(new Class[]{ String.class, PrintWriter.class });

      // create instance
      compiler = (Compiler)constructor.newInstance(new Object[]{ directory, out });

      // get compiler arguments
      arguments = (String[])Utils.toArray(args, String.class);

      // run the compiler
      success = compiler.run(arguments);
      out.println(Constants.TERM_STRING);
      out.println(success);

      if (options.verbose) {
	System.err.println("compilation ended");
      }
    } finally {
      in.close();
      out.close();
    }
  }

  /**
   * Parses the argument list.
   */
  private static boolean parseArguments(String[] args) {
    options = new ServerOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static ServerOptions	options;

  private static ServerSocket	serverSocket;
  private static Socket		clientSocket;
}
