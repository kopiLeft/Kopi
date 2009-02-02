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

/**
 * An interface that defines printer devices
 *
 * Such printers may be remote printers, local printers, print-to-file, fax
 * mailer or whatever
 */
public interface Printer {

  /**
   * Unique name of the printer in the database which is chosen by the user
   */
  String getPrinterName();
  
  /**
   * Prints a Printjob
   */
  String print(PrintJob data) throws IOException, PrintException;

  // ----------------------------------------------------------------------
  // PRINT OPTIONS
  // ----------------------------------------------------------------------

  /**
   * Sets the tray to use
   */
  void selectTray(int tray);

  /**
   * Sets the paper format
   */
  void setPaperFormat(String paperFormat);
}
