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
 * $Id: PLabelForm.java,v 1.3 2004/12/17 18:44:15 lackner Exp $
 */

package	at.dms.vkopi.lib.print;

import java.io.IOException;

import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VExecFailedException;
import at.dms.vkopi.lib.util.LabelPrinter;
import at.dms.vkopi.lib.util.Printer;
import at.dms.vkopi.lib.util.PrintException;
import at.dms.vkopi.lib.util.PrintJob;

/**
 * Form (like payment bill) with a specific label printer
 */
public class PLabelForm implements Printable {

  public PLabelForm(LabelPrinter printer, LabelPrintable document, int anzahl) {
    this.labelPrinter = printer;
    this.document = document;
    this.anzahl = anzahl;
  }

  /**
   * Starts a print session with a printer
   */
  public PrintJob createPrintJob() throws PrintException, VException  {
   try {
      for (int i = anzahl; i >= 1; i --) {
        document.print(labelPrinter);
      }

      return labelPrinter.createPrintJob();
    } catch (IOException e) {
      throw new VExecFailedException(e);
    }
  }

  private LabelPrinter          labelPrinter;
  private LabelPrintable        document;
  private int                   anzahl;
  private String                media;
}
