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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.kopi.util.ipp.IPPClient;

/**
 * IPP printer
 */

@SuppressWarnings("deprecation")
public class IPPPrinter extends AbstractPrinter  implements CachePrinter {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  /**
   * Construct an IPP Printer
   *
   * @param host the IPP server host
   * @param port the IPP server port
   * @param printer the name of the IPP printer
   * @param user the name of the printer user
   * @param attributesForMedia a list of String[2] with the correpondance
   * between media and IPP attributes for this printer.
   */
  public IPPPrinter(String name,
                    String host,
                    int port,
                    String printer,
                    String user,
                    List<?> attributesForMedia)
  {
    super(name);
    this.host = host;
    this.port = port;
    this.printer = printer;
    this.user = user;
    this.attributesForMedia = attributesForMedia;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public List<?> getMediaTypes() throws IOException {
    IPPClient   client = new IPPClient(host, (short)port, printer, user);

    return client.getMediaTypes();
  }

  /**
   * Set a given media for the printer.
   * Choose de attributes associated with this attribute for this printer.
   *
   * @return true iff the attribute is supported by this printer.
   */
  private String[] getAttributes(String media) {
    if (media == null) {
      return null;
    } else {
      Iterator<?>  it = attributesForMedia.iterator();

      while (it.hasNext()) {
        String[]        att = (String[])it.next();

        if (att.length == 2 && att[0].equals(media)) {
          return att[1] == null ? null : att[1].split(" ");
        }
      }
      return null;
    }
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  public String print(PrintJob printData) throws IOException, PrintException {
    IPPClient             ippClient = new IPPClient(host, (short)port, printer, user);

    ippClient.print(printData.getInputStream(),
                    printData.getNumberOfCopies(),
                    printData.getMedia() == null ? null : getAttributes(printData.getMedia()));
    return "IPP Print";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String          		user;
  private final String          		host;
  private final String          		printer;
  private final int             		port;
  private final List<?>            		attributesForMedia;
}
