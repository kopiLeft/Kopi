/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

public class IPPHttp {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public IPPHttp(String printerName, IPP request) {
    this.ippHeader = new IPPHttpHeader(printerName, request.getSize());
    this.ipp = request;
  }

  public IPPHttp(IPPInputStream is) throws IOException {
    this.ippHeader = new IPPHttpHeader(is);
    this.ipp = new IPP(is);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public void write(IPPOutputStream os) throws IOException {
    ippHeader.write(os);
    ipp.write(os);
  }

  public IPP getIPP() {
    return ipp;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private IPPHttpHeader ippHeader;
  private IPP           ipp;
}
