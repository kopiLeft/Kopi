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

package at.dms.vkopi.lib.print;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.PrintException;
import at.dms.vkopi.lib.util.PrintInformation;
import at.dms.vkopi.lib.util.Printer;
import at.dms.vkopi.lib.util.PrintJob;
import at.dms.vkopi.lib.visual.Application;
import at.dms.vkopi.lib.visual.VExecFailedException;

/**
 * Handle the generation of a document
 */
public abstract class PPage {

  /**
   * Construct a new Page description
   */
  public PPage() {
    this.styles = new Hashtable();
    this.blockStyles = new Hashtable();
    this.innerBlocks = new Hashtable();
    this.blocks = new Vector();
    this.blocksByName = new Hashtable();
    this.title = "untitled";

    internalInitLoadDefinition();
    internalInitLoadBlock();
  }

  protected void internalInitLoadDefinition() {
    // default: set to A4 Portrait
    this.setProlog("German.ps", 595, 842, 25, false);
  }

  protected abstract void internalInitLoadBlock();

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return the border
   */
  protected final int getBorder() {
    return border;
  }

  /**
   * @return the height
   */
  protected final int getHeight() {
    return height;
  }

  /**
   * @return the width
   */
  protected final int getWidth() {
    return width;
  }

  /**
   * Sets the title of this page
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Creates the print task and prints it immediately. DON NOT USE
   * this method in a transaction.
   */
  public void printImmediately(Printer printer) {
    try {
      printer.print(createPrintJob());
     } catch (Exception e) {
      throw new InconsistencyException("PPage.printImmediately(Printer printer):2", e);
    }
  }

  /**
   * Starts a print session with a printer
   */
  public PrintJob createPrintJob() {
    try {
      PostscriptPrintJob        printJob;

      printJob = new PostscriptPrintJob();
      ps = printJob.getPostscriptStream();
      printJob.setDocumentType(getDocumentType());

      return startPrintIntern(printJob);
    } catch (IOException e) {
      throw new InconsistencyException(e);
    } catch (PrintException e) {
      throw new InconsistencyException(e);
    }
  }

  public int getDocumentType() {
    return Printable.DOC_UNKNOWN;
  }

  /**
   * Starts a multi print session with a printer
   */
  public PostscriptPrintJob printProlog() throws IOException, PSPrintException {
    PostscriptPrintJob  printJob = new PostscriptPrintJob();

    ps = printJob.getPostscriptStream();
    ps.addHeader(prolog, landscape, width, height);
    printJob.setPrintInformation(title,
                                 landscape,
                                 width,
                                 height,
                                 0); // will be set later
    return printJob;
  }

  /**
   * Starts a print session with a printer
   */
  public void continuePrinting(PostscriptPrintJob printJob, 
                               boolean restartPageFromOne)
    throws PSPrintException 
  {
    this.ps = printJob.getPostscriptStream();
    currentPage = restartPageFromOne ? 0 : printJob.getNumberOfPages();
    printBlocks();
    if (restartPageFromOne) {
      printJob.setNumberOfPages(printJob.getNumberOfPages()+currentPage);
    } else {
      printJob.setNumberOfPages(currentPage);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (BUILD)
  // ----------------------------------------------------------------------

  /**
   * Adds a style definition
   */
  protected void addStyle(PStyle style) {
    styles.put(style.getIdent(), style);
  }

  /**
   * Adds a style definition
   */
  protected void addBlockStyle(PBlockStyle style) {
    blockStyles.put(style.getIdent(), style);
  }

  /**
   * Adds a style definition
   */
  protected void addBlock(PBlock block) {
    blocks.addElement(block);
    blocksByName.put(block.getIdent(), block);
    block.setPage(this, blocks.size() - 1);
  }

  /**
   * Returns a block from a name
   */
  public PBlock getBlock(String ident) {
    return (PBlock)blocksByName.get(ident);
  }

  /**
   * Adds a style definition
   */
  protected void addInnerBlock(String name) {
    innerBlocks.put(name, name);
  }

  protected abstract boolean showBlock(int index);

  // ----------------------------------------------------------------------
  // ACCESSORS (POSTSCRIPT)
  // ----------------------------------------------------------------------

  /**
   *
   */
  public PStyle getStyle(String style) {
    return (PStyle)styles.get(style);
  }

  /**
   *
   */
  public PBlockStyle getBlockStyle(String style) {
    return (PBlockStyle)blockStyles.get(style);
  }

  /**
   * Return the LayoutEngine
   */
  public PPostscriptStream getPostscriptStream() {
    return ps;
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  protected void setProlog(String fileName,
                           int width,
                           int height,
                           int border,
                           boolean landscape)
  {
    if (!fileName.equals("NO PROLOG")) {
      this.prolog = fileName;
      this.landscape = landscape;
      if (!landscape) {
	this.width = width;
	this.height = height;
      } else {
	this.width = height;
	this.height = width;
      }
      this.border = border;
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * Prepare a new page
   */
  private void newPage() {
    currentPage++;
    write("%%Page: " + currentPage + " " + currentPage);
    if (landscape) {
      ps.print("toprinter\n {");
      ps.print(height + " 0 translate\n");
      ps.print("90 rotate\n");
      ps.print("} if\n");
    }
  }

  /**
   *
   */
  private void write(String s) {
    ps.println(s);
  }

//   /**
//    *
//    */
//   protected PrintTask startPrintIntern(PrintJob printJob) throws IOException, PrintException {
//     ps.addHeader(prolog, landscape, width, height);
//     printBlocks();
//     ps.close(getCurrentPage());
//     printJob.setPrintInformation(title,
//                                  landscape,
//                                  width,
//                                  height,
//                                  getCurrentPage());
//     return printer.print(printJob);
//   }
  /**
   *
   */
  protected PrintJob startPrintIntern(PrintJob printJob) throws IOException, PrintException {
    ps.addHeader(prolog, landscape, width, height);
    printBlocks();
    ps.close(getCurrentPage());
    printJob.setPrintInformation(title,
                                 landscape,
                                 width,
                                 height,
                                 getCurrentPage());
    return printJob; 
  }

  /**
   *
   */
  protected void printBlocks() throws PSPrintException {
    // Remove innerblocks from list
    Vector	blocks = new Vector(this.blocks.size());
    for (int i = 0; i < this.blocks.size(); i++) {
      PBlock block = (PBlock)this.blocks.elementAt(i);
      if (!innerBlocks.containsKey(block.getIdent())) {
	blocks.addElement(block);
      }
    }
    boolean	fullyPrinted = false;
    Vector	sizes = new Vector(blocks.size());

    while (!fullyPrinted) {
      sizes.setSize(0);
      fullyPrinted = true;
      newPage();
      float		cummul = 0;

      for (int i = 0; i < blocks.size(); i++) {
	// fill all blocks
	PBlock	block = (PBlock)blocks.elementAt(i);
	block.preparePrint(this);
	float	size = block.getSize().getHeight();

	sizes.addElement(new Float(block.fill(size)));
	fullyPrinted &= block.isFullyPrinted();
      }
      float		currentPos = height - border;
      isLastPage = fullyPrinted;
      for (int i = 0; i < blocks.size(); i++) {
	PBlock	block = (PBlock)blocks.elementAt(i);
	if (((Float)sizes.elementAt(i)).floatValue() > 0 && block.isShownOnThisPage()) {
	  float		y = block.getPosition().getY();
          y = y < 0 ? currentPos : height - y;
	  ps.translateAbsolute(-block.getPosition().getX(), y);
	  currentPos = y - ((Float)sizes.elementAt(i)).floatValue();
	  block.doPrint(this);
	}
      }
      showPage();
    }
  }

  /**
   * FATAL ERROR HANDLING
   */
  public static final void fatalError(Object data, String line, Exception reason) {
    Application.reportTrouble("PPage", line, data.toString(), reason);
  }

  public String toString() {
    return super.toString()+" "+title;
  }
  // ---------------------------------------------------------------------
  // ACCESS TO VARIABLES
  // ---------------------------------------------------------------------

  /**
   * Return the number of the current page (start from 1)
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * Return the number of the current page (start from 1)
   */
  public boolean isLastPage() {
    return isLastPage;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF PRINTING
  // ----------------------------------------------------------------------

  /**
   * Call special trigger
   */
  private void showPage() {
    ps.showPage();
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private Hashtable		styles;
  private Hashtable		blockStyles;
  private Hashtable		innerBlocks;
  private Vector		blocks;
  private Hashtable		blocksByName;
  //  private Printer		printer;
  private PPostscriptStream	ps;
  private String		title;
  private String		prolog;
  private boolean		landscape;
  private int			border;
  protected int			height;
  protected int			width;

  private int			currentPage;
  private boolean		isLastPage;
}
