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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Fax printer
 */
 class FaxPrinter extends AbstractPrinter implements CachePrinter {

  /**
   * Constructs a fax printer
   */
  private FaxPrinter(final String faxHost,
		    final String nummer,
		    final String user,
		    final String id)
  {
    super("FaxPrinter "+nummer);
    this.faxHost = faxHost;
    this.nummer = nummer;
    this.user = user;
    this.id = id;
  }

  /**
   * Gets the phone nummer
   */
  public String getNummer() {
    return nummer;
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  public String print(PrintJob printdata) throws IOException, PrintException {
    try {
      Fax.fax(faxHost, printdata.getInputStream(), user, nummer, id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return "NYI";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String		faxHost;
  private final String		nummer;
  private final String		id;
  private final String		user;
}
