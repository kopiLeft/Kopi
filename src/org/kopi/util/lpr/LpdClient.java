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

package org.kopi.util.lpr;

// too many classes to specify explicitly
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * This class encapsulates the communication with a LPD host
 */
/*package*/ class LpdClient {

  // --------------------------------------------------------------------
  // CONSTRUCTS
  // --------------------------------------------------------------------

  /**
   * Constructs an LPD client
   */
  /*package*/ LpdClient(LpdOptions options) {
    this.options = options;
  }

  // --------------------------------------------------------------------
  // CONNECTION
  // --------------------------------------------------------------------

  /**
   * Connects to the LPD server
   */
  public void open() throws IOException {
    if (options.localHost.equals("localhost")) {
      try {
	options.localHost	= InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
	System.err.println("can't resolve local host name");
	options.localHost	= "localhost";
      }
    }
    if (options.printHost == null) {
      options.printHost	= options.localHost;
    }

    if (options.user == null) {
      options.user		= System.getProperty("user.name");
    }

    if (options.remotePort == -1) {
      options.remotePort = options.proxyHost != null ? PROXY_PORT : REMOTE_PORT;
    }

    InetAddress	remoteHost = InetAddress.getByName(options.proxyHost != null ? options.proxyHost : options.printHost);

    if (options.bindSourcePort) {
      for (int port = SOURCE_PORT_LOW; connection == null && port <= SOURCE_PORT_HIGH; port++) {
	try {
	  connection = new Socket(remoteHost,
				  options.remotePort,
				  InetAddress.getLocalHost(),
				  port);
	  options.sourcePort = port;
	} catch (IOException e) {
	  // !!! try next one, but check error first
	  if (port == SOURCE_PORT_HIGH) {
	    throw e;
	  }
	}
      }
    } else {
      connection = new Socket(options.printHost, options.remotePort);
      options.sourcePort = connection.getLocalPort();
    }

    connection.setSoTimeout(options.timeout);
    in = new DataInputStream(connection.getInputStream());
    out = new DataOutputStream(connection.getOutputStream());

    if (options.proxyHost != null) {
      out.writeBytes(options.printHost + "\n");
    }
  }

  /**
   * Disconnects from the LPD server
   */
  public void close() {
    try {
      in.close();
      out.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error while closing connection port");
      System.out.println(e.getMessage());
    }
    connection = null;
  }

  /**
   * Returns true iff the connection to the LPD server is established.
   */
  public boolean isConnected() {
    return connection != null;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the options
   */
  public void setOptions(LpdOptions options) {
    this.options = options;
  }

  /**
   * Sets the local hostname.
   */
  public void setLocalHost(String localHost) {
    options.localHost = localHost;
  }

  /**
   * Gets the local hostname.
   */
  public String getLocalHost() {
    return options.localHost;
  }

  /**
   * Sets the proxy hostname.
   */
  public void setProxyHost(String proxyHost) {
    options.proxyHost = proxyHost;
  }

  /**
   * Sets the print hostname.
   */
  public void setPrintHost(String printHost) {
    options.printHost = printHost;
  }

  /**
   * Gets the print hostname.
   */
  public String getPrintHost() {
    return options.printHost;
  }

  /**
   * Sets the remote port.
   */
  public void setRemotePort(int remotePort) {
    options.remotePort = remotePort;
  }

  /**
   * Set if source port should be bound to value in RFC 1179.
   */
  public void setBindSourcePort(boolean flag) {
    options.bindSourcePort = flag;
  }

  /**
   * Sets the user name.
   */
  public void setUser(String user) {
    options.user = user;
  }

  /**
   * Gets the user name.
   */
  public String getUser() {
    return options.user;
  }

  /**
   * Sets the timeout.
   */
  public void setTimeout(int timeout) {
    options.timeout = timeout;
  }

  // --------------------------------------------------------------------
  // LPD REQUEST
  // --------------------------------------------------------------------

  /**
   * Prints all job
   */
  public void startWaitingJob() throws IOException {
    DataOutputStream		out = getOutputStream();

    out.write(01);
    out.writeBytes("\n");
    out.flush();
  }

  /**
   * Request the queue state
   */
  public Vector getWaitingJob(String queue,
			      boolean longOutput,
			      String userOrJobID) throws IOException {
    DataOutputStream	out = getOutputStream();

    out.write(longOutput ? 04 : 03);
    out.writeBytes(queue);

    if (userOrJobID != null) {
      out.writeBytes(" " + userOrJobID);
    }
    out.writeBytes("\n");

    DataInputStream	in = getInputStream();
    LineNumberReader	lnr = new LineNumberReader(new InputStreamReader(in));
    String		line;
    Vector		state = new Vector();
    while ((line = lnr.readLine()) != null && (line.length() > 1)) {
      state.addElement(line);
    }
    return state;
  }

  /**
   * Remove jobs
   */
  public void removeJob(String queue, String[] args) throws IOException {
    DataOutputStream	out = getOutputStream();

    out.write(05);
    out.writeBytes(queue + " " + getUser());

    if (args != null) {
      for (int i = 0; i < args.length; i++) {
	out.writeBytes(" " + args[i]);
      }
    }

    out.writeBytes("\n");
  }

  /**
   *
   */
  public void sendPrinterJob(String queue,
			     boolean controlFirst,
			     String control,
			     String jobID,
			     byte[] data)
    throws IOException, LpdException
  {
    sendPrinterJob(queue,
                   controlFirst,
                   control,
                   jobID,
                   new ByteArrayInputStream(data));
  }

  /**
   *
   */
  public void sendPrinterJob(String queue,
			     boolean controlFirst,
			     String control,
			     String jobID,
			     InputStream stream)
    throws IOException, LpdException
  {
    DataOutputStream		out = getOutputStream();

    out.write(02);
    out.writeBytes(queue + "\n");
    checkAcknowledge("Error while start printing on queue " + queue);
    if (controlFirst) {
      sendControl(jobID, control);
      sendData(jobID, stream);
    } else {
      sendData(jobID, stream);
      sendControl(jobID, control);
    }
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  /*
   *
   */
  private void sendControl(String jobID,
			   String control)
    throws IOException, LpdException
  {
    DataOutputStream		out = getOutputStream();

    out.write(02);
    out.writeBytes("" + control.length());
    out.writeBytes(" ");
    out.writeBytes("cfA" + jobID + getPrintHost() + "\n");

    checkAcknowledge("Error while start sending control file");

    out.writeBytes(control);
    out.writeByte(0);

    checkAcknowledge("Error while sending control file");
  }

  /*
   *
   */
  private void sendData(String jobID, InputStream in)
    throws IOException, LpdException
  {
    DataOutputStream		out;
    int                         size;

    size = in.available();

    out = getOutputStream();
    out.write(03);
    out.writeBytes("" + size);
    out.writeBytes(" ");
    out.writeBytes("dfA" + jobID + getPrintHost() + "\n");
    checkAcknowledge("Error while start sending data file");

    for (int i = 0; i < size; i++) {
      out.write(in.read());
    }
    out.writeByte(0);
    out.flush();
    in.close();

    checkAcknowledge("Error while sending data file");
  }

  /*
   * Gets the input stream
   */
  private DataInputStream getInputStream() {
    return in;
  }

  /*
   * Gets the output stream
   */
  private DataOutputStream getOutputStream() {
    return out;
  }

  private void checkAcknowledge(String errorMessage)
    throws IOException, LpdException
  {
    int		returnCode = getInputStream().readByte();
    if (returnCode != 0) {
      throw new LpdException(returnCode, errorMessage);
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  // Constants are defined in RFC 1179
  private static final int	SOURCE_PORT_LOW = 721;
  private static final int	SOURCE_PORT_HIGH = 731;
  private static final int	REMOTE_PORT = 515;

  private static final int	PROXY_PORT = 7290;

  private Socket		connection;

  private LpdOptions		options;

  private DataInputStream	in;
  private DataOutputStream	out;
}
