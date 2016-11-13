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

package org.kopi.vkopi.lib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

import org.kopi.vkopi.lib.base.Utils;


/**
 * PPage/Report creates a PrintJob
 *
 * A Printer creates a PrintTask from a PrintJob
 */
public class PrintJob {

  public PrintJob(Rectangle format) throws IOException {
    this(Utils.getTempFile("kopi", "pdf"), true, format);
  }

  public PrintJob(byte[] data, Rectangle format) throws IOException {
    this(writeToFile(new ByteArrayInputStream(data)), true, format);
  }

  public PrintJob(InputStream dataStream, Rectangle format) throws IOException {
    this(writeToFile(dataStream), true, format);
  }

  public PrintJob(File datafile, boolean delete, Rectangle format) {
    this.datafile = datafile;
    this.delete = delete;
    this.format = format;
    this.numberCopy = 1;
    this.numberOfPages = -1;
    this.dataType = DAT_PS;
    // if the jvm is stopped before the objects are
    // finalized the file must be deleted!
    if (delete) {
      datafile.deleteOnExit();
    }
  }

  private static File writeToFile(InputStream dataStream) throws IOException {
    File              tempFile = Utils.getTempFile("kopi", "pdf");

    writeToFile(dataStream, tempFile);
    return tempFile;
  }

  private static void writeToFile(InputStream dataStream, File outputfile) throws IOException {
    byte[]            buffer = new byte[1024];
    OutputStream      output;
    int               length;

    output = new FileOutputStream(outputfile);
    while ((length = dataStream.read(buffer)) != -1) {
      output.write(buffer, 0, length);
    }
    output.flush();
    output.close();
  }

  protected void finalize() throws Throwable {
    if (delete && datafile != null) {
      datafile.delete();
    }
    super.finalize();
  }

  public String toString() {
    return "PrintJob ("+delete+") "+datafile+"  "+super.toString();
  }

  /**
   * getOutputStream has to be closed before calling getInputStream
   * use with care, know waht you do!
   */
  public OutputStream getOutputStream() throws IOException {
    return new FileOutputStream(datafile);
  }
  /**
   * use with care, do onyl read from the file, not not manipulate
   */
  public File getDataFile() throws IOException {
    return datafile;
  }

  /**
   * getOutputStream has to be closed before calling getInputStream
   */
  public InputStream getInputStream() throws IOException {
    return new FileInputStream(datafile);
  }

  public byte[] getBytes() throws IOException {
    byte[]                    buffer = new byte[1024];
    InputStream               data;
    ByteArrayOutputStream     output;
    int                       length;

    // use getInputStream because in creates
    // the stream if necessary
    data = getInputStream();
    output = new ByteArrayOutputStream();

    while ((length = data.read(buffer)) != -1) {
      output.write(buffer, 0, length);
    }
    return output.toByteArray();
  }

  public void writeDataToFile(File file) throws IOException {
    writeToFile(getInputStream(), file);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setPrintInformation(String title, Rectangle format, int numberOfPages) {
    this.title = title;
    this.format = format;
    this.numberOfPages = numberOfPages;
  }

  public Rectangle getFormat() {
    return format;
  }

  @SuppressWarnings("deprecation")
  public int getWidth() {
    return (int)format.width();
  }

  @SuppressWarnings("deprecation")
  public int getHeight() {
    return (int)format.height();
  }

  public String getTitle() {
    return title;
  }

  public int getNumberOfCopies() {
    return numberCopy;
  }

  public void setNumberOfCopies(int numberCopy) {
    this.numberCopy = numberCopy;
  }

  public int getNumberOfPages() {
    return numberOfPages;
  }

  public void setNumberOfPages(int numberOfPages) {
    this.numberOfPages = numberOfPages;
  }

  public PrintJob createFromThis(File file, boolean delete) {
    return new PrintJob(file, delete, this.format);
  }

  /**
   * Set the media for this document
   */
  public void setMedia(String media) {
    this.media = media;
  }

  /**
   * Get the printing attribute for this object
   *
   * @return the printer attribute.
   */
  public String getMedia() {
    return media;
  }

  /**
   * Kind of document to print (Proposal, Bill, ...)
   *
   * @return A number representing the document type.
   */
  public int getDocumentType() {
    return documentType;
  }

  public void setDocumentType(int documentType) {
    this.documentType = documentType;
  }

  public void setDataType(int dataType) {
    this.dataType = dataType;
  }

  /**
   * Kind of data to print (pdf, ps)
   *
   * @return A number representing the document type.
   */
  public int getDataType() {
    return dataType;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final File          datafile;
  private boolean             delete;

  // properties
  private String		title;
  private String                media;
  private int                   documentType;
  private int                   dataType;
  private int			numberCopy;
  private int			numberOfPages;
  private Rectangle             format;

  public static int             DAT_PDF = 1;
  public static int             DAT_PS  = 2;

  // A5, A4, A3, Letter and Legal page format (portrait)
  public static Rectangle       FORMAT_A5     = PageSize.A5;
  public static Rectangle       FORMAT_A4     = PageSize.A4;
  public static Rectangle       FORMAT_A3     = PageSize.A3;
  public static Rectangle       FORMAT_LETTER = PageSize.LETTER;
  public static Rectangle       FORMAT_LEGAL  = PageSize.LEGAL;

  // A5, A4, A3, Letter and Legal page format (landscape)
  @SuppressWarnings("deprecation")
  public static Rectangle       FORMAT_A5_R     = new Rectangle(PageSize.A5.rotate().width(), PageSize.A5.rotate().height());
  @SuppressWarnings("deprecation")
  public static Rectangle       FORMAT_A4_R     = new Rectangle(PageSize.A4.rotate().width(), PageSize.A4.rotate().height());
  @SuppressWarnings("deprecation")
  public static Rectangle       FORMAT_A3_R     = new Rectangle(PageSize.A3.rotate().width(), PageSize.A3.rotate().height());
  @SuppressWarnings("deprecation")
  public static Rectangle       FORMAT_LETTER_R = new Rectangle(PageSize.LETTER.rotate().width(), PageSize.LETTER.rotate().height());
  @SuppressWarnings("deprecation")
  public static Rectangle       FORMAT_LEGAL_R  = new Rectangle(PageSize.LEGAL.rotate().width(), PageSize.LEGAL.rotate().height());

  // Raw format (Used for label printers)
  public static Rectangle       FORMAT_RAW = new Rectangle(-1, -1);
}
