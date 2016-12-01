
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

package org.kopi.vkopi.lib.cross;

import java.io.IOException;

import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VDictionaryForm;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.print.PProtectedPage;
import org.kopi.vkopi.lib.util.PrintException;
import org.kopi.vkopi.lib.util.PrintJob;
import org.kopi.vkopi.lib.util.Printer;
import org.kopi.vkopi.lib.visual.PrinterManager;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.DBContextHandler;

@SuppressWarnings("serial")
public abstract class VMultiPrintSelectionForm extends VDictionaryForm {

  protected VMultiPrintSelectionForm() throws VException {
  }

  protected VMultiPrintSelectionForm(VForm caller) throws VException {
    super(caller);
  }

  protected VMultiPrintSelectionForm(DBContextHandler caller) throws VException {
    super(caller);
  }

  protected VMultiPrintSelectionForm(DBContext caller) throws VException {
    super(caller);
  }

  /**
   * Implements interface for COMMAND PRINT
   */
  public void PrintForm(VBlock b) throws VException {
    b.validate();
    int[]	copyCount = getNumberOfCopies(false);

    for (int i = 0; i < copyCount.length; i++) {
      if (copyCount[i] > 0) {
	Printer		printer = PrinterManager.getPrinterManager().getCurrentPrinter();
	PProtectedPage	report = createReport(i);

	if (report != null) {
          PrintJob      job = report.createPrintJob();

          job.setNumberOfCopies(copyCount[i]);
          try {
            printer.print(job);
          } catch (IOException ioe) {
            throw new VExecFailedException(ioe);
          } catch (PrintException pe) {
            throw new VExecFailedException(pe);
          }
	}
      } else if (copyCount[i] < 0) {
	Printer		printer = PrinterManager.getPrinterManager().getPreviewPrinter();
	PProtectedPage	report = createReport(i);

	if (report != null) {
	  report.printImmediately(printer);
	}
      }
    }
  }

  /**
   * Implements interface for COMMAND Preview
   */
  public void PreviewForm(VBlock b) throws VException {
    b.validate();
    int[]	copyCount = getNumberOfCopies(true);

    for (int i = 0; i < copyCount.length; i++) {
      if (copyCount[i] > 0) {
	Printer		printer = PrinterManager.getPrinterManager().getPreviewPrinter();
	PProtectedPage	report = createReport(i);
	if (report != null) {
	  report.printImmediately(printer);
	}
      }
    }
  }

  /**
   * get the number of copies to print
   */
  protected int[] getNumberOfCopies(boolean preview) {
    return new int[] {1};
  }

  /**
   * create a report for this form
   * @param	number		report number
   */
  protected PProtectedPage createReport(int number) {
    return null;
  }
}
