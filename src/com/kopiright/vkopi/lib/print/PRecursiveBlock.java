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

package com.kopiright.vkopi.lib.print;

import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;

import com.lowagie.text.pdf.PdfWriter;

/**
 * A block of data to print
 */
public class PRecursiveBlock extends PBlock {

  /**
   * Constructor with a position and a default size
   */
  public PRecursiveBlock(String ident, PPosition pos, PSize size, String style, String[] blocks) {
    super(ident, pos, size, style);

    this.blockIdents = blocks;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (FROM PBLOCK)
  // ----------------------------------------------------------------------

  /*
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public float fill(float size) throws PSPrintException {
    checkBlocks();
    currentHeight = 0;

    for (from = to; to < blocks.length; to++) {
      blocks[to].preparePrint(this);

      float	temp;
      float	tsize = size;//Math.min(blocks[to].getSize().getHeight(), size);

      if (blocks[to].isVisible()) {
        // evaluate if block is visible
        temp = blocks[to].fill(tsize);
        if (!blocks[to].isShownOnThisPage()) {
          temp = 0;
        }
        sizes.addElement(new Float(temp));
        currentHeight += temp;
        size -= temp;
        if (!blocks[to].isFullyPrinted()) {
          break;
        }
      } else {
        sizes.addElement(new Float(0));
      }
    }

    isFullyPrinted = to == blocks.length;

    return currentHeight;
  }

  /**
   * Returns true if this block is fully printed
   */
  public boolean isFullyPrinted() {
    return isFullyPrinted;
  }

  /**
   * Prints this block
   */
  public void doPrint(PPage page) throws PSPrintException {
    float       currentPos = 0;
    int		count = 0;

    for (int i = from; i <= to && i < blocks.length; i++) {
      if (((Float)sizes.elementAt(count)).floatValue() > 0 && blocks[i].isShownOnThisPage()) {
        page.getPdfContentByte().saveState();
        page.getPdfContentByte().concatCTM(1,0,0,1,
                                           blocks[i].getPosition().getX(),
                                           currentPos);

        blocks[i].doPrint(page);
	currentPos -= ((Float)sizes.elementAt(count++)).floatValue();
        page.getPdfContentByte().restoreState();
      } else {
	count++;
      }
    }
    sizes.setSize(0);
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
	if (blocks[i] == null) {
	  throw new InconsistencyException(">>>>>>>>>>" + blockIdents[i]);
	}
	if (blocks[i] instanceof PTextBlock) {
	  ((PTextBlock)blocks[i]).setRecurrent();
	}
      }
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private	int			from;
  private	int			to;
  private	boolean			isFullyPrinted;
  private       float			currentHeight;
  private       PBlock[]		blocks;
  private	String[]		blockIdents;
  private	Vector			sizes = new Vector();
}
