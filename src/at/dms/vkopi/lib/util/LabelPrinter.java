/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.util;

import java.io.IOException;
import java.io.StringBufferInputStream;

/**
 * Create a new empty document to print with a label printer
 */
public class LabelPrinter {

  /**
   * Create a new empty document
   */

  public LabelPrinter() {
    buffer = new StringBuffer();
    filter = new Filter();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Resets the printer
   */
  public void reset() {
    printRawString(RESET);
  }

  /**
   * Formfeed
   */
  public void newPage() {
    printRawString(FORMFEED);
  }

  /**
   * Sets the length of the page
   */
  public void setPageLength(int lines) {
    printRawString(PAGELENGTH + (char)lines);
  }

  /**
   * Sets the length of the page in inch
   */

  public void setPageLengthInch(int i) {
    printRawString(PAGELENGTHIN + (char)i);
  }

  // ----------------------------------------------------------------------
  // FONT DISPLAY
  // ----------------------------------------------------------------------

  /**
   * Sets Draft
   */

  public void setDraft() {
    printRawString(DRAFT);
  }

  /**
   * Sets Letter
   */

  public void setLetter() {
    printRawString(LETTER);
  }

  /**
   * masterStyle
   */

  public void setMasterStyle(int i) {
    printRawString(MASTERSELECT + (char)i);
  }

  // ----------------------------------------------------------------------
  // POSITIONING
  // ----------------------------------------------------------------------

  /**
   * Get the horizontal Position in mm
   */

  public int getHorizontalPos() {
    return lastX - offsetX;
  }

  /**
   * Get the vertikal Position
   */

  public int getVerticalPos() {
    return lastY - offsetY;
  }

  /**
   * Sets the horizontal Position in mm
   */

  public void setHorizontalPos(int pos) {
    lastX = pos;
  }

  /**
   * Sets the vertikal Position
   */

  public void setVerticalPos(int pos) {
    lastY = pos;
  }

  /**
   * Sets Position absolute
   */

  public void setPos(int x, int y) {
    setVerticalPos(y + offsetY);
    setHorizontalPos(x + offsetX);
  }

  public void setOffset(int x, int y) {
    offsetX = x;
    offsetY = y;
  }

  /**
   * Prints the document immeditely. DO NOT USE this method in
   * a transaction (use createPrintTask instead and print it 
   * after finishing the transaction.
   */ 
  public void printImmediately(Printer printer) throws IOException, PrintException {
    printer.print(createPrintJob());
  }

  /**
   * Creates a print task for the documentat the specified printer
   */
  public PrintJob createPrintJob() throws IOException, PrintException {
    return new PrintJob(new StringBufferInputStream(buffer.toString()));
  }

  // ----------------------------------------------------------------------
  // PRINT COMMANDS
  // ----------------------------------------------------------------------

  /**
   * Prints a simple text
   */
  public void print(String text) {
    StringBuffer	buf = new StringBuffer(text.length());

    for (int i = 0; i< text.length(); i++) {
      buf.append(filter.convert(text.charAt(i)));
    }

    printRawString(buf.toString());
  }

  public void println(String text) {
    print(text);
    println();
  }

  /**
   * Prints a simple text
   */

  public void print(boolean val) {
    printRawString(booleanNames[val ? 0 : 1]);
  }

  public void println(boolean val) {
    print(val);
    println();
  }

  /**
   * Prints a simple value
   */
  public void print(double d) {
    printRawString("" + d);
  }
  public void println(double d) {
    print(d);
    println();
  }

  /**
   * Prints a simple char
   */
  public void print(char i) {
    printRawString("" + i);
  }
  public void println(char i) {
    print(i);
    println();
  }

  /**
   * Prints an object
   */
  public void print(Object obj) {
    printRawString("" + obj);
  }

  public void println(Object obj) {
    print(obj);
    println();
  }

  public void println() {
    printRawString("\n");
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * Prints a raw text
   */
  public void printRawString(String text) {
    buffer.append(text);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected StringBuffer	buffer;
  protected Filter filter;

  // PAGE
  protected String RESET	= "";
  protected String FORMFEED	= "";
  protected String PAGELENGTH	= "";
  protected String PAGELENGTHIN	= "";

  // STYLE
  protected String DRAFT	= "";
  protected String LETTER	= "";
  protected String PT10		= "";
  protected String PT12		= "";
  protected String PT15		= "";
  protected String MASTERSELECT	= "";

  // POS
  protected String SETHPOS	= "";
  protected String SETVPOS	= "";

  protected int lastX = 0;
  protected int lastY = 0;

  protected int offsetX = 0;
  protected int offsetY = 0;

  private static String[]	booleanNames = { Message.getMessage("true"), Message.getMessage("false") };
}
