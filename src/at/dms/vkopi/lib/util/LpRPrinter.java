/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import at.dms.util.lpr.LpR;
import at.dms.util.lpr.LpdException;

/**
 * Remote execution client
 */
public class LpRPrinter implements Printer {

  /**
   * Creates a printer that send file to an lpd server
   */
  public LpRPrinter(String name,
                    String serverHost,
                    int port,
                    String proxyHost,
                    String queue,
                    String user)
  {
    this.name = name;
    this.serverHost = serverHost;
    this.port = port;
    this.proxyHost = proxyHost;
    this.queue = queue;
    this.user = user;

    //    setNumberOfCopies(1);
    selectTray(1); // Standard tray (see common/MAKEDB/dbSchema)
    setPaperFormat(null);
  }

  public String getPrinterName() {
    return name;
  }

  // ----------------------------------------------------------------------
  // PRINT OPTIONS
  // ----------------------------------------------------------------------

//   /**
//    * Sets the number of copy to print
//    */
//   public void setNumberOfCopies(int number) {
//     this.numberOfCopies = number;
//   }

//   /**
//    * Gets the number of copy to print
//    */
//   public int getNumberOfCopies() {
//     return numberOfCopies;
//   }

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
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  public String print(PrintJob data) throws IOException, PrintException {
    LprImpl     lpr = new LprImpl(data);

    return lpr.print();
  }

  private class LprImpl extends LpR {

    LprImpl(PrintJob data) {
      super(serverHost, port, proxyHost, queue, user);
      setPrintBurst(false);

      this.data = data;
    }

    public String print() throws IOException, PrintException {
      try {
        if (data.getTitle() != null) {
          setTitle(data.getTitle());
        }
        print(data.getInputStream(), null);
        close();
      } catch (LpdException e) {
        throw new PrintException(e.getMessage(), PrintException.EXC_UNKNOWN);
      }
      return "not yet implemented";
    }

    protected byte[] readFully(InputStream is) throws IOException {
      StringBuffer      header = new StringBuffer("%Kopi ");

      if (data.getNumberOfCopies() > 1) {
        header.append("-c" + data.getNumberOfCopies() + " ");
      }
      if (tray > 1) {
        header.append("-t" + tray + " ");
      }
      if (paperFormat != null) {
        header.append("-p" + paperFormat);
      }
      header.append("\n");

      int         size = (int)is.available();
      byte[]      headerB = header.toString().getBytes();
      int         count = headerB.length;
      byte[]      data = new byte[size + headerB.length];
      System.arraycopy(headerB, 0, data, 0, headerB.length);
      size += count;
      while (count < size) {
        count += is.read(data, count, size-count);
      }
      is.close();
      return data;
    }

    private final PrintJob              data;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private final String          name;

  private final String          serverHost;
  private final int             port;
  private final String          proxyHost;
  private final String          queue;
  private final String          user;


  private int                   tray;
  private String                paperFormat;
  private PrintInformation      info;

  private File                  file;
  private OutputStream          ous;
}
