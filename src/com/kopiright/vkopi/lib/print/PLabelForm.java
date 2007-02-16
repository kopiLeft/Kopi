/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.print;

import java.io.IOException;

import com.kopiright.vkopi.lib.util.LabelPrinter;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;

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
}
