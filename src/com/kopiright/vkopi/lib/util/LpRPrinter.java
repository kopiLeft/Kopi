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

import java.io.IOException;
import java.io.InputStream;
import com.kopiright.util.lpr.LpR;
import com.kopiright.util.lpr.LpdException;

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
      int         size = (int)is.available();
      byte[]      data = new byte[size];
      int         count;

      count = 0;
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


  @SuppressWarnings("unused")
  private int                   tray;
  @SuppressWarnings("unused")
  private String                paperFormat;

}
