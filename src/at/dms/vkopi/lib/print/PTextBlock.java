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
 * $Id: PTextBlock.java,v 1.2 2004/09/07 14:45:54 lackner Exp $
 */

package at.dms.vkopi.lib.print;

import at.dms.vkopi.lib.form.VBooleanField;

import java.util.Vector;

/**
 * A block of data to print
 */
public abstract class PTextBlock extends PBlock {

  /**
   * Constructor with a position and a default size
   */
  public PTextBlock(String ident, PPosition pos, PSize size, String style, boolean rec) {
    super(ident, pos, size, style);
    this.fixed = !rec;
  }

  /**
   * Sets to be recurrent (if inner)
   */
  public void setRecurrent() {
    //fixed = false;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public float fill(float size) throws PSPrintException {
    if (!fixed) {
      if (isFullyPrinted) {
	return 0;
      }
      currentHeight = 0;
      maxSize = size;
      engines.setSize(0);
      if (engine != null) {
	// PENDING
	engines.addElement(engine);
	engine.setNeedAscend(this);
	currentHeight += engine.getHeight();
	engine = new PLayoutEngine(engine.needDescend());
      } else {
	engine = new PLayoutEngine(false);
      }
      fillBlock();
      return currentHeight;
    } else {
      if (!pending) {
	engine = new PLayoutEngine(true);
	try {
	  print();
	} catch (Exception e) {
	  throw new PSPrintException("PTextBlock.fill(float size)", e);
	}
      }
      isFullyPrinted = engine.getHeight() <= size;
      pending = !isFullyPrinted;
      return isFullyPrinted ? (currentHeight = engine.getHeight()) : 0;
    }
  }

  /**
   * Returns the ident of the block
   */
  public boolean isShownOnThisPage() {
    return isVisible() && currentHeight > 0;
  }

  /**
   * Prints this block
   */
  public void doPrint(PPage page) throws PSPrintException {
    if (currentHeight > 0) {
      printStyle(page, getBlockWidth(), currentHeight);
    }
    if (!fixed) {
      for (int i = 0; i < engines.size(); i++) {
	PLayoutEngine engine = (PLayoutEngine)engines.elementAt(i);
	engine.getPostscript(this, page.getPostscriptStream());
      }
      currentHeight = 0;
    } else if (isFullyPrinted) {
      engine.getPostscript(this, page.getPostscriptStream());
      currentHeight = 0;
    }
    pending = false;
   }

  /**
   * Returns true if this block is fully printed
   */
  public boolean isFullyPrinted() {
    return isFullyPrinted;
  }

  // ----------------------------------------------------------------------
  // PRINTING
  // ----------------------------------------------------------------------

  /**
   * Start printing session
   */
  protected void print() throws Exception {
    SOURCE();
  }

  protected void SOURCE() throws Exception {
  }

  /**
   * Add a break and do layout
   */
  public void addBreak() throws PSPrintException {
    addBreak(false);
  }

  public void addBreak(boolean auto) throws PSPrintException {
    if (fixed) {
      isFilled = true;
    } else {
      if ((engine.getHeight() <= 0) || addToCurrentPage(engine, false)) {
        if (engine.getHeight() > 0) {
          PLayoutEngine         oldEngine = getEngine();

          engines.addElement(oldEngine);
          currentHeight += engine.getHeight();
          engine = new PLayoutEngine(engine.needDescend());
          //          paragraphStyle.setStyle(this, engine, false);
          if (auto) {
            engine.setFont(oldEngine.getFont(), oldEngine.getStyle(), oldEngine.getFontSize());
          }
        }
      } else {
	isFilled = true;
	synchronized(engines) {
	  engines.notify();
	  try{
	    engines.wait();
	  } catch (InterruptedException e) {
	    throw new PSPrintException("PTextBlock.addBreak()", e);
	  }
	}
      }
    }
  }

  public void doBreak() throws PSPrintException {
    if (fixed) {
      isFilled = true;
    } else {
      isFilled = true;
      synchronized(engines) {
        engines.notify();
        try{
          engines.wait();
        } catch (InterruptedException e) {
          throw new PSPrintException("PTextBlock.addBreak()", e);
        }
      }
    }
  }

  // ----------------------------------------------------------------------
  // FORMATING
  // ----------------------------------------------------------------------

  /**
   * Add any kind of value
   */
  public void addExpression(Object o) throws PSPrintException {
    if (o == null) {
      return;
    }

    if (o instanceof Boolean) {
      addText(VBooleanField.toText((Boolean)o));
    } else if (o instanceof javax.swing.ImageIcon) {
      addImage((javax.swing.ImageIcon)o);
    } else {
      addText(o.toString());
    }
  }

  /**
   *
   */
  public void addExpression(double i) throws PSPrintException {
    addText(String.valueOf(i));
  }

  /**
   *
   */
  public void addExpression(long i) throws PSPrintException {
    addText(String.valueOf(i));
  }

  /**
   *
   */
  public void addExpression(int i) throws PSPrintException {
    addText(String.valueOf(i));
  }

  /**
   *
   */
  public void addExpression(char i) throws PSPrintException {
    addText(String.valueOf(i));
  }

  /**
   *
   */
  public void addExpression(boolean i) throws PSPrintException {
    addExpression(i ? Boolean.TRUE : Boolean.FALSE);
  }

  public void setAutoBreak(boolean autoBreak) {
    this.autoBreak = autoBreak;
  }
  public boolean getAutoBreak() {
    return this.autoBreak;
  }

  /**
   * Adds a text in current layout engine
   */
  public void addText(String s) throws PSPrintException {
    if (newLine) {
      newLine = false;
      float	pos = paragraphStyle.getIndentLeft(this);
      int	align = paragraphStyle.getAlignment(this);
      if (align == PParagraphStyle.ALN_RIGHT) {
	pos = getBlockWidth() - pos;
      } else if (align == PParagraphStyle.ALN_CENTER) {
	pos = getBlockWidth() / 2;
      }
      engine.setPosition(pos);
      engine.setAlignment(align);
    }
    int		rt = s.indexOf("\n");
    if (rt > 0) {
      addText(s.substring(0, rt));
      engine.newLine(this, paragraphStyle);
      lastTab = null;
      newLine = true;
      if (rt + 1 < s.length()) {
        if (autoBreak) {
          addBreak(true);
        }
	addText(s.substring(rt + 1));
      }
    } else if (rt == 0) {
      engine.newLine(this, paragraphStyle);
      newLine = true;
      if (rt + 1 < s.length()) {
        if (autoBreak) {
          addBreak(true);
        }
	addText(s.substring(rt + 1));
      }
    } else {
      // no carriage return, tab ?
      rt = s.indexOf("\t");
      if (rt >= 0) {
	engine.addText(s.substring(0, rt));
	gotoNextTab();
	if (rt + 1 < s.length()) {
	  addText(s.substring(rt + 1));
	}
      } else {
	engine.addText(s);
      }
    }
  }

  /**
   * Adds a text in current layout engine
   */
  public void addImage(javax.swing.ImageIcon image) {
    if (newLine) {
      newLine = false;
      float	pos = paragraphStyle.getIndentLeft(this);
      int	align = paragraphStyle.getAlignment(this);
      if (align == PParagraphStyle.ALN_RIGHT) {
	pos = getBlockWidth() - pos;
      } else if (align == PParagraphStyle.ALN_CENTER) {
	pos = getBlockWidth() / 2;
      }
      engine.setPosition(pos);
      engine.setAlignment(align);
    }
    engine.addImage(image);
  }

  /**
   *
   */
  public void addPageCount() {
    engine.addPageCount();
  }

  /**
   * Sets the tab position
   */
  public void setTab(String s) {
    lastTab = s;
    paragraphStyle.setTab(this, s);
  }

  /**
   * gotoNextTab
   */
  public void gotoNextTab() {
    lastTab = paragraphStyle.gotoNextTab(this, lastTab);
  }

  // ----------------------------------------------------------------------
  // LAYOUT
  // ----------------------------------------------------------------------

  /**
   * Sets the current style
   */
  protected void setStyle(String style, boolean hasBang) {
    PBodyStyle	stl = (PBodyStyle)getPage().getStyle(style);
    stl.setStyle(this, engine, hasBang);
    if (stl instanceof PParagraphStyle) {
      paragraphStyle = (PParagraphStyle)stl;
    }
  }

  /**
   * Return the LayoutEngine
   */
  protected PLayoutEngine getEngine() {
    return engine;
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Init a block
   */
  protected final void initTrigger() {
    paragraphStyle = DEFAULT_BLOCK_STYLE;
    newLine = true;
    engine.setFont(DEFAULT_FONT, DEFAULT_STYLE, DEFAULT_SIZE);
  }

  /**
   * Ends a block
   */
  protected final void endTrigger() {
    engine.endTrigger(this, paragraphStyle);
  }

  /**
   * Sets the position
   */
  protected void setPosition(float pos, int align) {
    engine.setPosition(pos);
    engine.setAlignment(align);
    newLine = false;
  }

  // ---------------------------------------------------------------------
  // LAYOUT ENGINE
  // ---------------------------------------------------------------------

  protected boolean addToCurrentPage(PLayoutEngine engine, boolean last) {
    return engine.getHeight() > 0 && engine.getHeight() + currentHeight < getMaxHeight();
  } 

  protected void fillBlock() throws PSPrintException {
    boolean	doStart = worker == null;

    isFilled = false;

    if (worker == null) {
      worker = new Thread() {
	public void run() {
	  try {
	    print();
	  } catch (Exception e) {
	    error = e;
            synchronized(engines) {
              engines.notify();
            }
            return;
	  }
	  isFilled = true;
	  isFullyPrinted = true;
	  if (addToCurrentPage(engine, isFullyPrinted)) {
	    engines.addElement(getEngine());
	    currentHeight += engine.getHeight();
	  }
	  synchronized(engines) {
	    engines.notify();
	  }
	  return;
	}
      };
    }

    try {
      synchronized(engines) {
	if (doStart) {
	  worker.start();
	} else {
	  engines.notify();
	  //worker.resume();
	}
	engines.wait();
      }
    } catch (InterruptedException e) {
      // throw new PSPrintException("PTextBlock.fillBlock():2", e);
    }
    if (error != null) {
      throw new PSPrintException("PTextBlock.fillBlock():1", error);
    }
  }

  // ---------------------------------------------------------------------
  // LAYOUT ENGINE
  // ---------------------------------------------------------------------

  protected float getBlockWidth() {
    return getSize().getWidth();
  }

  protected float getBlockHeight() {
    return maxSize;
  }

  protected float getMaxHeight() {
    return maxSize;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static final String	DEFAULT_FONT = "Courier";
  private static final int	DEFAULT_STYLE = 0;
  private static final int	DEFAULT_SIZE = 12;
  private static final PParagraphStyle DEFAULT_BLOCK_STYLE = new PParagraphStyle("!@#$%", null, PParagraphStyle.ALN_LEFT, 5, -1, -1, 0, 0, null, false, null);

  private Thread		worker;
  private String		lastTab;
  private boolean               autoBreak = false;

  protected Vector		engines = new Vector();
  protected boolean		fixed;
  protected Throwable		error;
  protected boolean		isFilled;
  protected boolean		pending;
  protected boolean		isFullyPrinted;
  protected boolean		newLine = true;
  protected PLayoutEngine	engine;
  protected PParagraphStyle	paragraphStyle;
  protected float		currentHeight;
  protected float		maxSize;
}
