/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.cross;

import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.VDictionaryForm;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.print.DefaultPrintManager;
import com.kopiright.vkopi.lib.print.PProtectedPage;
import com.kopiright.vkopi.lib.print.Printable;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.util.Printer;
import com.kopiright.vkopi.lib.visual.PrinterManager;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;

public abstract class VPrintSelectionForm extends VDictionaryForm implements Printable {

  protected VPrintSelectionForm() throws VException {
  }

  protected VPrintSelectionForm(VForm caller) throws VException {
    super(caller);
  }

  protected VPrintSelectionForm(DBContextHandler context) throws VException {
    super(context);
  }

  protected VPrintSelectionForm(DBContext context) throws VException {
    super(context);
  }

  /**
   * Implements interface for COMMAND PRINT
   */
  public void printForm(VBlock b) throws VException {
    b.validate();

    printable = null;
    getPrintManager().print(this,
			    this,
			    getNumberOfCopies(),
			    getDefaultPrinter(),
			    getDefaultFaxNumber(),
			    getDefaultMailAddress());
    if (!b.isMulti()) {
      b.setRecordChanged(0, false);
    }
  }

  /**
   * Implements interface for COMMAND PREVIEW
   */
  public void previewForm(VBlock b) throws VException {
    b.validate();

    printable = null;
    createReport(this).printImmediately(PrinterManager.getPrinterManager().getPreviewPrinter());

    b.setRecordChanged(0, false);
  }

  /**
   * Creates a report for this form
   */
  protected abstract PProtectedPage createReport(DBContextHandler handler) throws VException;

  /**
   * Gets the PrintManager that will handle the report's printing.
   */
  protected com.kopiright.vkopi.lib.print.PrintManager getPrintManager() throws VException {
    return new DefaultPrintManager();
  }

  /**
   * Gets the default number of page
   */
  protected int getNumberOfCopies() {
    return 1;
  }

//   /**
//    * Returns the Document (Angebot, Lieferschein, Auftrag)
//    */
//   public int getDocumentType() throws VException {
//     if (printable == null) {
//       printable = createReport(this);
//     }
//     return printable.getDocumentType();
//   }

  /**
   * Gets the default printer
   */
  protected Printer getDefaultPrinter() {
    return PrinterManager.getPrinterManager().getCurrentPrinter();
  }

  /**
   * Gets the default fax number
   */
  protected String getDefaultFaxNumber() {
    return null;
  }

  /**
   * Gets the default printer
   */
  protected String getDefaultMailAddress() {
    return null;
  }

  /**
   * Print the object into the provided printer
   */
  public PrintJob createPrintJob() throws PrintException, VException {
    if (printable == null) {
      printable = createReport(this);
    }

    return printable.createPrintJob();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  //  private String        media;
  private Printable     printable;
}
