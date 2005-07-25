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

import com.lowagie.text.pdf.PdfWriter;

/**
 * A block of data to print
 */
public class PHorizontalBlock extends PBlock {

  /**
   * Constructor with a position and a default size
   */
  public PHorizontalBlock(String ident, PPosition pos, PSize size, String style, String[] blocks) {
    super(ident, pos, size, style);
    this.blockIdents = blocks;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (FROM PBLOCK)
  // ----------------------------------------------------------------------

  /**
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public float fill(float size) throws PSPrintException {
    if (/*currentHeight == 0 && */!isFullyPrinted) {
      checkBlocks();
      isFullyPrinted = true;
      for (int i = 0; i < blocks.length; i++) {
	if (getPage().showBlock(blocks[i].getIndex())) {
	  blocks[i].preparePrint(this);
	  float	temp = blocks[i].fill(size);
	  currentHeight = Math.max(temp + blocks[i].getPosition().getY(), currentHeight);
	  isFullyPrinted &= blocks[i].isFullyPrinted();
	}
      }
    }
    return !isFullyPrinted || currentHeight > size ? 0 : currentHeight;
  }

  /**
   * Returns true if this block is fully printed
   */
  public boolean isFullyPrinted() {
    return isFullyPrinted;// || currentHeight > 0;
  }

  /**
   * Prints this block
   */
  public void doPrint(PPage page) throws PSPrintException {
    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i].isShownOnThisPage()) {
	blocks[i].doPrint(page);
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (FOR PAGE)
  // ----------------------------------------------------------------------

  /**
   * Set page
   */
  public void setPage(PPage page, int index) {
    super.setPage(page, index);
    for (int i = 0; i < blockIdents.length; i++) {
      page.addInnerBlock(blockIdents[i]);
    }
  }

  /**
   * Checks block
   */
  private void checkBlocks() {
    if (blocks == null) {
      blocks = new PBlock[blockIdents.length];
      for (int i = 0; i < blocks.length; i++) {
	blocks[i] = getPage().getBlock(blockIdents[i]);
	if (blocks[i] instanceof PTextBlock) {
	  ((PTextBlock)blocks[i]).setRecurrent();
	}
      }
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private	boolean			isFullyPrinted;
  private       float			currentHeight;
  private       PBlock[]		blocks;
  private	String[]		blockIdents;
}
