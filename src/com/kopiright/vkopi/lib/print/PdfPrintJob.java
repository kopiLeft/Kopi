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
 * $Id: PostscriptPrintJob.java 22806 2005-04-05 16:49:10Z taoufik $
 */

package com.kopiright.vkopi.lib.print;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.util.PrintJob;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * PPage/Report creates a PrintJob
 *
 * A Printer prints PrintJob 
 */
public class PdfPrintJob extends PrintJob {
  public PdfPrintJob(boolean landscape) throws IOException {
    super();
    setPrintInformation(null, landscape, (int)PageSize.A4.width(), (int)PageSize.A4.height(), 1);

    try {
      document = new Document(landscape ? new Rectangle(PageSize.A4.rotate().width(), PageSize.A4.rotate().height()) : PageSize.A4, 0, 0, 0, 0);
      writer = PdfWriter.getInstance(document, new FileOutputStream(getDataFile()));
      document.open();
    } catch (Exception e) {
      closed = true;
      throw new InconsistencyException(e);
    }
    closed = false;
  }

  public PdfPrintJob(File file) throws IOException {
    super(file, true);
    closed = true;
  }

  public PdfWriter getWriter() throws PSPrintException {
    if (closed) {
      throw new PSPrintException("Document is already closed");
    }
    return writer;
  }

  public Document getDocument() throws PSPrintException {
    if (closed) {
      throw new PSPrintException("Document is already closed");
    }
    return document;
  }

  public void close() throws PSPrintException {
    if (!closed) {
      closed = true;
      document.close();
    } else {
      throw new PSPrintException("PDF-document is already closed");
    }
  }

  public int getDataType() {
    return DAT_PDF;
  }


  public PdfPrintJob merge(String watermark) throws PSPrintException {
    try {
      PdfPrintJob         result = new PdfPrintJob(isLandscape());

      PdfReader         reader = new PdfReader(getInputStream());
      PdfReader         mergeDoc = new PdfReader(watermark);
      int               n = reader.getNumberOfPages();
      PdfStamper        stamp = new PdfStamper(reader, result.getOutputStream());

      int               i = 0;
      PdfContentByte    under;
      PdfContentByte    over;

      while (i < n) {
        i++;

        under = stamp.getUnderContent(i);
        under.addTemplate(stamp.getImportedPage(mergeDoc, 1), 1, 0, 0, 1, 0, 0);
      }
      stamp.close();
      return result;
    } catch (Exception de) {
      throw new PSPrintException("Can't merge " + watermark);
    }
  }

  public PdfPrintJob append(PdfPrintJob nextJob) throws PSPrintException {
    // to implement PdfCopy
    return null;
  }

  private Document      document;
  private PdfWriter     writer;
  private boolean       closed;
}
