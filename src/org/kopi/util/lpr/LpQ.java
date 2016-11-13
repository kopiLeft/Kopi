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

import java.io.IOException;
import java.util.Vector;

public class LpQ {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   *
   */
  public LpQ(String host, int port) {
    options = new LpQOptions();
    client = new LpdClient(options);

    client.setPrintHost(host);
    client.setRemotePort(port);
  }

  /**
   * Only main can construct Main
   */
  private LpQ(String[] args) throws IOException {
    if (!parseArguments(args)) {
      System.exit(1);
    }

    client = new LpdClient(options);
    getQueueState();
    printState();
  }

  private boolean parseArguments(String[] args) {
    options = new LpQOptions();
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
    new LpQ(args);
  }

  // --------------------------------------------------------------------
  // QUEUE ACCESS
  // --------------------------------------------------------------------

  public void getQueueState() throws IOException {
    client.open();

    state = client.getWaitingJob(options.queue,
				 options.longFormat,
				 options.mine ?
				 client.getUser() :
				 null);

    client.close();
  }

  public void printState() {
    for (int i = 0; i < state.size(); i++) {
      System.out.println(state.elementAt(i).toString());
    }
  }

  public Vector getState() {
    return state;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  private Vector	state = new Vector();
  private LpQOptions	options;
  private LpdClient	client;
}
