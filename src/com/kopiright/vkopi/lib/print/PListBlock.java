/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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


/**
 * A block of data to print
 */
public abstract class PListBlock extends PTextBlock {

  /**
   * Constructor with a position and a default size
   */
  public PListBlock(String ident, PPosition pos, PSize size, String style, boolean rec) {
    super(ident, pos, size, style, true);
  }

  // ---------------------------------------------------------------------
  // LAYOUT ENGINE
  // ---------------------------------------------------------------------
  protected boolean addToCurrentPage(PLayoutEngine engine, boolean last) {
    if (!last) {
      return super.addToCurrentPage(engine, last);
    } else {
      return engine.getHeight() > 0
        && engine.getHeight() + currentHeight
           < getMaxHeight()- lastfooter.getHeight() + footer.getHeight();
           // footer is not on the last page, so the height can be used
           // footer is already removed from maxHeight
    }
  }

  /**
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public float fill(float size) throws PSPrintException {
    if (isFullyPrinted) {
      return 0;
    }
    if (!isVisible()) {
      // not shown, dont'avance in the list
      return 0.0001f;
    }

    size = getSize().getHeight() > 0 ? Math.min(getSize().getHeight(), size) : size;
    currentHeight = 0;
    maxSize = size;
    engines.setSize(0);
    if (engine != null) {
      // PENDING
      engines.addElement(engine);
      engine.setNeedAscend(this);
      currentHeight += engine.getHeight();
    }

    //    prepare(HEADER);
    prepare(LISTHEADER);
    prepare(LISTFOOTER);

    maxHeight = size - Math.max(footer.getHeight(), lastfooter.getHeight()) - listheader.getHeight();

    PLayoutEngine       oldEngine = engine;

    engine = new PLayoutEngine(engine == null ? true : engine.needDescend());
    if (getAutoBreak() && oldEngine != null) {
      engine.setFont(oldEngine.getFont(), oldEngine.getStyle(), oldEngine.getFontSize());
    }
    if (!wholebody) {
      fillBlock();
    } else {
      isFullyPrinted = true;
    }
    if (isFullyPrinted) {
     if (engine.getHeight() > 0 && !engines.contains(engine)) {
        isFullyPrinted = false;
        wholebody = true;
      }
    }

    if (isFullyPrinted) {
      engine = new PLayoutEngine(engine.needDescend());
      try {
	LASTLISTFOOTER();
      } catch (Exception e) {
	throw new PSPrintException("PListBlock.fill():1", e);
      }
      engines.addElement(engine);
      currentHeight += engine.getHeight();
    } else {
      PLayoutEngine pendingEngine = engine;

      try {
        engine = new PLayoutEngine(true);
        LISTFOOTER();
      } catch (Exception e) {
	throw new PSPrintException("PListBlock.fill():2", e);
      }
      engines.addElement(engine);
      currentHeight += engine.getHeight();
      engine = pendingEngine;
    }
    currentHeight += listheader.getHeight();
    firstpage = false;
    return currentHeight;
  }

  public void doPrint(PPage page) throws PSPrintException {
    doPrint(page, 0, 0);
  }

  public void doPrint(PPage page, float transX, float transY) throws PSPrintException {
    page.getPdfContentByte().concatCTM(1,0,0,1,0, -listheader.getHeight());

    for (int i = 0; i < engines.size(); i++) {
      PLayoutEngine     engine = (PLayoutEngine)engines.elementAt(i);

      engine.generate(page);
    }
    page.getPdfContentByte().concatCTM(1,0,0,1,0, currentHeight);
    listheader.generate(page);
    currentHeight = 0;
  }

  protected float getMaxHeight() {
    return maxHeight;
  }

//   protected void PAGEHEADER() throws Exception {}
//   protected void PAGEFOOTER() throws Exception {}
  protected void FIRSTLISTHEADER() throws Exception { LISTHEADER(); }
  protected void LISTHEADER() throws Exception {}

  protected void LASTLISTFOOTER() throws Exception { LISTFOOTER(); }
  protected void LISTFOOTER() throws Exception { FOOTER(); } //replaces FOOTER

  /**
   * because of name conventions, this is
   * @deprecated.
   */
  protected void FOOTER() throws Exception {} // deprecated

  /**
   * Call special trigger
   */
  public void prepare(int which) throws PSPrintException {
    try {
      PLayoutEngine std = engine;
      PParagraphStyle	stl = paragraphStyle;
      switch (which) {
      case LISTHEADER:
	engine = listheader;
	engine.reset(true);
	if (firstpage) {
          FIRSTLISTHEADER();
        } else {
          LISTHEADER();
        }
	break;
      case LISTFOOTER:
	engine = footer;
	engine.reset(true);
	LISTFOOTER();
	engine = lastfooter;
	engine.reset(true);
	LASTLISTFOOTER();
	break;
      }
      engine = std;
      paragraphStyle = stl;
    } catch (Exception e) {
      throw new PSPrintException("PListBlock.prepare()", e);
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  //  private PLayoutEngine	header = new PLayoutEngine(true);
  private PLayoutEngine	lastfooter = new PLayoutEngine(true);
  private PLayoutEngine	footer = new PLayoutEngine(true);
  private PLayoutEngine	listheader = new PLayoutEngine(true); // first and last
  private float		maxHeight;

  // not necessary the first page of the doc. but the first page with this list
  private boolean       firstpage = true;
  private boolean       wholebody = false;

  //  private static final int	HEADER = 0;
  private static final int	LISTFOOTER = 1;
  private static final int	LISTHEADER = 2;
}
