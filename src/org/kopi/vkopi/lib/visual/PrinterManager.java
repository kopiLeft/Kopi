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

package org.kopi.vkopi.lib.visual;

import org.kopi.vkopi.lib.util.Printer;

/**
 *
 */
public abstract class PrinterManager {

  public static PrinterManager getPrinterManager() {
    return printerManager;
  }

  public static void setPrinterManager(PrinterManager manager) {
    printerManager = manager;
  }

  /**
   * Returns a print for previewing document
   */
  public abstract Printer getPreviewPrinter();

  /**
   * Property print.server
   * Returns the name of the print server to use. for printing
   */
  public abstract String getPrintServer() throws PropertyException;

  /**
   * Returns the preferred printer for the STANDARD_MEDIA
   */
  public abstract Printer getCurrentPrinter();

  /**
   * Returns the preferredPrinter for the specified media.
   */
  public abstract Printer getPreferredPrinter(String media);

  /**
   * Returns the currently selected printer for current user
   */
  public abstract Printer getPrinterByName(String name);

  /**
   * Returns a printer that support this kind of media or the default one if none
   */
  public abstract Printer getPrinterByMedia(String media);

  /**
   * Returns the default Printer for a kind of document
   */
  public abstract Printer getPrinterByDocumentType(String documentType);

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static PrinterManager         printerManager;
}
