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

import java.awt.geom.AffineTransform;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.PrintException;
import at.dms.vkopi.lib.util.PrintInformation;
import at.dms.vkopi.lib.util.Printer;
import at.dms.vkopi.lib.util.PrintJob;
import at.dms.vkopi.lib.util.Utils;
import at.dms.vkopi.lib.visual.Application;
import at.dms.vkopi.lib.visual.VExecFailedException;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

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
   * Creates the print task and prints it immediately. DO NOT USE
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
      PdfPrintJob        printJob;

      printJob = new PdfPrintJob(landscape);
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
   * Starts a print session with a printer
   */
  public PdfPrintJob printProlog() {
    try {
      PdfPrintJob        printJob;

      printJob = new PdfPrintJob(landscape);
      printJob.setDocumentType(getDocumentType());
      return printJob;
    } catch (IOException e) {
      throw new InconsistencyException(e);
    }
  }

  /**
   * Starts a print session with a printer
   */
  public void  continuePrinting(PdfPrintJob printJob,
                                boolean restartPageFromOne)
    throws PSPrintException
  {
    if (printJob.getNumberOfPages() < 0) {
      printJob.setNumberOfPages(0);
    }
    currentPage = restartPageFromOne ? 0 : printJob.getNumberOfPages();
    printBlocks(printJob);
    printJob.setNumberOfPages(printJob.getNumberOfPages() + reportPageCount); 
  }

  /**
   * Starts a print session with a printer
   */
  public int continuePrinting(PdfStamper stamper,
                              boolean restartPageFromOne,
                              int startpage,
                              int allpages)
    throws PSPrintException
  { 
    pageCount = restartPageFromOne ? reportPageCount : allpages;
    printHeaderFooter(stamper, startpage,  startpage + reportPageCount -1, pageCount);
    return startpage + reportPageCount; // = last page used + 1
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (BUILD)
  // ----------------------------------------------------------------------

  /**
   * Adds a style definition
   */
  protected void addStyle(PStyle style) {
    style.setOwner(this);
    styles.put(style.getIdent(), style);
  }

  /**
   * Adds a style definition
   */
  protected void addBlockStyle(PBlockStyle style) {
    style.setOwner(this);
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
  // ACCESSORS 
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

  public void setPageHeader(PBlock header) {
    addBlock(header);
    this.header = header;
  }

  public void setPageFooter(PBlock footer) {
    addBlock(footer);
    this.footer = footer;
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
  public void newPage() {
    try {
      document.newPage();
    } catch (Exception e) {
      throw new InconsistencyException(e);
    }

    currentPage++;
    reportPageCount++;
  }

  /**
   *
   */
  protected PrintJob startPrintIntern(PdfPrintJob printJob) throws IOException, PrintException {
    printBlocks(printJob);
    printJob.close();

    if (header != null || footer != null) {
      printJob = printHeaderFooter(printJob);
    }
    printJob.setPrintInformation(title,
                                 landscape,
                                 width,
                                 height,
                                 getCurrentPage());
    return printJob;
  }

  public void setWatermark(String watermarkResource) {
    try {  
      watermark = new PdfReader(Utils.getURLFromResource(watermarkResource, Utils.APPLICATION_DIR).toString());
    } catch (Exception e) {
      throw new InconsistencyException("Load " + watermarkResource, e);	
    }
  }

  private void addWatermark(PdfPrintJob printJob) throws PSPrintException {
    if (watermark != null) {  
      PdfContentByte    cbwater =  printJob.getWriter().getDirectContent();

      cbwater.addTemplate(printJob.getWriter().getImportedPage(watermark, 1), 1, 0, 0, 1, 0, 0);
    }
  }

  public boolean isPageCountAvailable() {
    return pageCountAvailable;
  }

  public int getPageCount() {
    return pageCount;
  }

  protected PdfPrintJob printHeaderFooter(PdfPrintJob printJob) throws PSPrintException {
    try {
      File              file = Utils.getTempFile("kopi", "prt");
      PdfReader         reader = new PdfReader(new FileInputStream(printJob.getDataFile()));
      PdfStamper        stamper = new PdfStamper(reader, new FileOutputStream(file));

      pageCount =  reader.getNumberOfPages();

      printHeaderFooter(stamper, 1, pageCount, pageCount);

      stamper.close();
      return new PdfPrintJob(file);
    } catch (Exception e) {
      throw new InconsistencyException(e);
    }
  }

  protected void  printHeaderFooter(PdfStamper stamper, int startpage, int endpage, int pageCount) throws PSPrintException {
    try {
      Rectangle           page = document.getPageSize();

      pageCountAvailable = true;

      for (int i = startpage; i <= endpage; i++) {
        currentPage = i;

        cb = stamper.getOverContent(i);
        if (cb == null) {
          continue;
        }

	if (header != null) {
          cb.saveState();
          cb.concatCTM(1,0,0,1, document.leftMargin(), page.height()-document.topMargin()) ;
          header.reinitialize();
          header.preparePrint(this);
          header.fill(5000);
          header.doPrint(this);
          cb.restoreState();
	}
        if (footer != null) { 
          float     size;
          PPosition p = footer.getPosition();

          cb.saveState();
          footer.reinitialize();
          footer.preparePrint(this);
          size = footer.fill(5000);
          cb.concatCTM(1,0,0,1, document.leftMargin()+p.getX(), size+document.bottomMargin()+p.getY()) ;
          footer.doPrint(this);      
          cb.restoreState();
	}
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new InconsistencyException(e);
    }
  }

  /**
   *
   */
  protected void printBlocks(PdfPrintJob printJob) throws PSPrintException {
    // Remove innerblocks from list
    Vector	blocks = new Vector(this.blocks.size());

    for (int i = 0; i < this.blocks.size(); i++) {
      PBlock    block = (PBlock)this.blocks.elementAt(i);

      if (block == header || block == footer) {
        // nothing
      } else if (block.getIdent().equals("_$_PAGEHEADER")) {
        header = block;
      } else if (block.getIdent().equals("_$_PAGEFOOTER")) {
        footer = block;
      } else if (!innerBlocks.containsKey(block.getIdent())) {
        blocks.addElement(block);
      }
    }

    boolean	fullyPrinted = false;
    Vector	sizes = new Vector(blocks.size());

    document = printJob.getDocument();
    writer = printJob.getWriter();
    cb = writer.getDirectContent();
    pageCountAvailable = false;

    while (!fullyPrinted) {
      sizes.setSize(0);
      fullyPrinted = true;
      newPage();
      addWatermark(printJob);
      float     cummul = 0;

      for (int i = 0; i < blocks.size(); i++) {
	// fill all blocks
	PBlock  block = (PBlock)blocks.elementAt(i);

	block.preparePrint(this);

	float   size = block.getSize().getHeight();

	sizes.addElement(new Float(block.fill(size)));
	fullyPrinted &= block.isFullyPrinted();
      }
      float     currentPos = height - border;

      isLastPage = fullyPrinted;

      for (int i = 0; i < blocks.size(); i++) {
	PBlock  block = (PBlock)blocks.elementAt(i);

	if (((Float)sizes.elementAt(i)).floatValue() > 0 && block.isShownOnThisPage()) {
 	  float y = block.getPosition().getY();

          y = y < 0 ? currentPos : height - y;

          cb.saveState();
          cb.concatCTM(1,0,0,1, block.getPosition().getX(), y) ;
 	  currentPos = y - ((Float)sizes.elementAt(i)).floatValue();
          block.doPrint(this);
          cb.restoreState();
	}
      }
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

  public PdfWriter getWriter() {
    return writer;
  }

  public PdfContentByte getPdfContentByte() {
    return cb;
  }

  public Document getDocument() {
    return document;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
  private Document  document;
  private PdfWriter writer;

  private int                   reportPageCount;
  private int                   pageCount;
  private boolean               pageCountAvailable;
  private PdfContentByte        cb;
  private PBlock                header;
  private PBlock                footer;
  private Hashtable		styles;
  private Hashtable		blockStyles;
  private Hashtable		innerBlocks;
  private Vector		blocks;
  private Hashtable		blocksByName;
  private String		title;
  private String		prolog;
  private boolean		landscape;
  private int			border;
  protected int			height;
  protected int			width;

  private int			currentPage;
  private boolean		isLastPage;

  private PdfReader             watermark;
}
