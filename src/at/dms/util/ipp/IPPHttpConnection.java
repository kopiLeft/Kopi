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

package at.dms.util.ipp;

import java.net.URL;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class IPPHttpConnection {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public IPPHttpConnection(URL printURL) throws IOException {
    this.url = printURL;
    if (IPP.DEBUG) {
      System.out.println("printURL : " + printURL);
    }
    this.connection = new Socket(url.getHost(), url.getPort());
    this.os = new BufferedOutputStream(new DataOutputStream(connection.getOutputStream()));
    this.is = new BufferedInputStream(new DataInputStream(connection.getInputStream()));
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public void sendRequest(IPP request) throws IOException {
    IPPHttp     httpRequest = new IPPHttp(url.getPath(), request);

    httpRequest.write(new IPPOutputStream(os));
    os.flush();
  }

  public IPP receiveResponse() throws IOException {
    IPPHttp     httpRequest = new IPPHttp(new IPPInputStream(is));

    return httpRequest.getIPP();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private URL           url;
  private Socket        connection;
  private OutputStream  os;
  private InputStream   is;
}
