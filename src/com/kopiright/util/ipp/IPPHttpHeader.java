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

package com.kopiright.util.ipp;

import java.io.IOException;

public class IPPHttpHeader {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public IPPHttpHeader(String printerName, int contentLength) {
    this.name = printerName;
    this.size = contentLength;
  }

  public IPPHttpHeader(IPPInputStream is) throws IOException {
    String      line;

    line = is.readLine();
    if (line != null) {
      String[] lineFields = line.split(" ");
      if (lineFields.length < 2 ||
          (new Integer(lineFields[1])).intValue() != HTTP_OK) {
        throw new IOException("Http error");
      }
    } else {
      throw new IOException("Http error");
    }

    while (line != null && line.length() != 0) {
      line = is.readLine();
      if (IPP.DEBUG) {
        System.out.println(line);
      }
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public void write(IPPOutputStream os) throws IOException {
    String      tmp = "POST " + name + " HTTP/1.0\r\n";

    os.writeString(tmp);
    tmp = "Content-type: application/ipp\r\n";
    os.writeString(tmp);
    tmp = "Content-length: " + size + "\r\n\r\n";
    os.writeString(tmp);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static final int      HTTP_OK = 200;

  private String                name;
  private int                   size;
}
