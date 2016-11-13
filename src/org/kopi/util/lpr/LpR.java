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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

public class LpR {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public LpR(String host, int port) {
    this(host, port, null, null, null);
  }

  public LpR(String host,
             int port,
             String proxyHost,
             String queue,
             String user)
  {
    options = new LpROptions();

    client = new LpdClient(options);
    client.setPrintHost(host);
    client.setRemotePort(port);
    setQueue(queue);
    setUser(user);
    if (proxyHost != null) {
      client.setProxyHost(proxyHost);
    }
  }

  /**
   * Only main can construct Main
   * @exception	IOException	such as file not found
   * @exception	org.kopi.util.lpr.LpdException	if some problems occurs during
   *				connection to lpd
   */
  private LpR(String[] args) throws IOException, LpdException {
    if (!parseArguments(args)) {
      System.exit(1);
    }

    client = new LpdClient(options);

    String[]	infiles = options.nonOptions;

    try {
      if (infiles.length == 0) {
	print(System.in, "standard input");
      } else {
	for (int i = 0; i < infiles.length; i++) {
	  print(new File(infiles[i]), infiles[i]);
	}
      }
    } finally {
      close();
    }
  }

  private boolean parseArguments(String[] args) {
    options = new LpROptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * Program entry point
   * @exception	org.kopi.util.lpr.LpdException	problem during communication with lpd
   * @exception	java.io.IOException	io problem
   */
  public static void main(String[] args)
    throws IOException, LpdException
  {
    new LpR(args);
  }

  // --------------------------------------------------------------------
  // PRINT METHODS
  // --------------------------------------------------------------------

  /**
   * Prints the specified file
   * @exception	org.kopi.util.lpr.LpdException	problem during communication with lpd
   * @exception	java.io.IOException	io problem
   */
  public void print(File file, String document)
    throws IOException, LpdException
  {
    FileInputStream	is = new FileInputStream(file);

    print(is, document);
    if (options.remove) {
      // !!! should remove file
    }
  }

  /**
   * Prints the specified file
   * @exception	org.kopi.util.lpr.LpdException	problem during communication with lpd
   * @exception	java.io.IOException	io problem
   */
  public void print(InputStream is, String document)
    throws IOException, LpdException
  {
    byte[]      data;

    data = readFully(is);
    print(data, document);
  }

  /**
   * Prints the specified file
   * @exception	org.kopi.util.lpr.LpdException	problem during communication with lpd
   * @exception	java.io.IOException	io problem
   */
  public void print(byte[] data, String document)
    throws IOException, LpdException
  {
    if (!client.isConnected()) {
      client.open();
    }

    options.job = getJobID();

    initControl();

    for (int i = 0; i < options.copies; i++) {
      addControl(options.filetype.charAt(0), "dfA" + options.job + client.getPrintHost());
    }

    // unlink file after printing
    addControl('U', "dfA" + options.job + client.getPrintHost());
    addControl('N', (document == null ? options.title : document));

    client.sendPrinterJob(options.queue,
			  !options.dataFirst,
			  control.toString(),
			  options.job,
			  data);

    if (options.windows) {
      client.close();
    }
  }

  /**
   * PrintWaitingJobs
   */
  public void printWaitingJob() throws IOException {
    client.startWaitingJob();
  }

  public void close() {
    client.close();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the user
   */
  public void setUser(String user) {
    client.setUser(user);
  }

  /**
   * Sets the file type
   */
  public void setFileType(char filetype) {
    options.filetype = "" + filetype;
  }

  /**
   * Sets the number of copies
   */
  public void setNumberOfCopies(int nb) {
    options.copies = nb;
  }

  /**
   * Gets the number of copy to print
   */
  public int getNumberOfCopies() {
    return options.copies;
  }

  /**
   * Sets if the burst page is printed
   */
  public void setPrintBurst(boolean printBurst) {
    options.burst = printBurst;
  }

  /**
   * Sets if a mail is send after printing
   */
  public void setMailAfter(boolean mailAfter) {
    options.mail = mailAfter;
  }

  /**
   * Sets if the file is removed after printing
   */
  public void setRemoveAfter(boolean removeAfter) {
    options.remove = removeAfter;
  }

  /**
   * Sets if the lpd (deamon) is hosted on a windows host
   */
  public void setWindowsLpd(boolean windowsLpd) {
    options.windows = windowsLpd;
  }

  /**
   * Sets the print class
   */
  public void setPrintClass(String printClass) {
    //options.printClass = printClass;
  }

  /**
   * Sets the queue
   */
  public void setQueue(String queue) {
    options.queue = queue;
  }

  /**
   * Sets the job
   */
  public void setJob(String job) {
    options.job = job;
  }

  /**
   * Sets the title
   */
  public void setTitle(String title) {
    options.title = title;
  }

  /**
   * Sets the tmp directory
   */
  public void setTmpDir(String tmpDir) {
    options.tmpdir = tmpDir;
  }

  /**
   * Sets the control first flag
   */
  public void setControlFirst(boolean controlFirst) {
    options.dataFirst = !controlFirst;
  }

  /**
   * Sets the width
   */
  public void setWidth(int width) {
    options.width = width;
  }

  /**
   * Sets the indentation
   */
  public void setIndentation(int indentation) {
    options.indent = indentation;
  }

  // --------------------------------------------------------------------
  // UTILITIES METHODS
  // --------------------------------------------------------------------

  protected byte[] readFully(InputStream is) throws IOException {
    int		size = is.available();
    byte[]	data = new byte[size];
    int		count = 0;

    while (count < size) {
      count += is.read(data, count, size-count);
    }
    is.close();
    return data;
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  /*
   *
   */
  private void initControl() {
    control = new StringBuffer();

    addControl('H', client.getPrintHost());
    addControl('P', client.getUser());
    if (options.indent != -1) {
      addControl('I', "" + options.indent);
    }
    addControl('T', options.title);

    if (options.mail) {
      addControl('M', client.getUser());
    }

    if (options.burst) {
      addControl('J', (options.job == null ? "XXX" : options.job));
      addControl('C', options.printClass == null ? client.getLocalHost() : options.printClass);
      addControl('L', client.getUser());
    }

    if (options.filetype != null
	&& (options.filetype.charAt(0) == 'c'
	    || options.filetype.charAt(0) == 'l'
	    || options.filetype.charAt(0) == 'p')) {
      addControl('W', "" + options.width);
    }
  }

  /**
   * Adds a line to the control file
   */
  private void addControl(char tag, Object value) {
    control.append(tag);
    control.append(value);
    control.append('\n');
  }

  private String getJobID() {
    String	id;

    jobID += 1;

    if (jobID < 10) {
      id = "00" + jobID;
    } else if (jobID < 100) {
      id = "0" + jobID;
    } else {
      id = "" + jobID;
    }

    return id;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static int		jobID;

  private StringBuffer		control;

  private LpROptions		options;
  private LpdClient		client;
}
