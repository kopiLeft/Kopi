/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: LpRm.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.util.lpr;

import java.io.IOException;

public class LpRm {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public LpRm(String host, int port) {
    options = new LpRmOptions();
    client = new LpdClient(options);
    client.setPrintHost(host);
    client.setRemotePort(port);
  }

  /**
   * Only main can construct Main
   */
  private LpRm(String[] args) throws IOException {
    if (!parseArguments(args)) {
      System.exit(1);
    }

    client = new LpdClient(options);

    String[]	jobs = options.nonOptions;

    removeJob(jobs);
  }

  private boolean parseArguments(String[] args) {
    options = new LpRmOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  // --------------------------------------------------------------------
  // ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * Entry point to the assembler
   */
  public static void main(String[] args) throws IOException {
    new LpRm(args);
  }

  // --------------------------------------------------------------------
  // QUEUE ACCESS
  // --------------------------------------------------------------------

  public void removeJob(String[] args) throws IOException {
    client.open();

    client.removeJob(options.queue, args);

    client.close();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private LpRmOptions	options;
  private LpdClient	client;
}
