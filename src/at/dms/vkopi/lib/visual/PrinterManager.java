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
 * $Id: PrinterManager.java,v 1.3 2004/08/19 08:55:43 graf Exp $
 */

package at.dms.vkopi.lib.visual;

import at.dms.vkopi.lib.util.PreviewPrinter;
import at.dms.vkopi.lib.util.Printer;

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
  public Printer getPreviewPrinter() {
    try {
      String	command;

      command= ApplicationConfiguration.getConfiguration().getStringFor("print.preview.command");
      return new PreviewPrinter("PreviewPrinter", command);
    } catch (PropertyException e) {
      return null;
    }
  }

  /**
   * Returns the currently selected printer for current user
   */
  public abstract Printer getCurrentPrinter();

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

  private static PrinterManager printerManager;
}
