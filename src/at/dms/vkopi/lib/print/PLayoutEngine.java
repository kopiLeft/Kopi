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
 * $Id: PLayoutEngine.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.print;

import java.awt.Dimension;
import java.util.Vector;

/**
 * This class is in charge to draw text and styles in ps (a set of line)
 */
class PLayoutEngine {
  public PLayoutEngine(boolean firstLine) {
    reset(firstLine);
  }

  /**
   * Adds text to buffer
   */
  public void addText(String text) {
    if (text != null && text.length() > 0) {
      hasText = true;
      lineHeight = Math.max(lineHeight, getSize());
      lineDescend = Math.max(lineDescend, getDescend());
      buf.append(text);
    }
  }

  /**
   * Adds text to buffer
   */
  public void addImage(javax.swing.ImageIcon image) {
    if (image != null) {
      hasText = true;
      checkText();
      lineHeight = Math.max(lineHeight, maxImageSize(image).height + lineDescend);
      //lineDescend = Math.max(lineDescend, getDescend());
      commands.addElement(new ImageCommand(image));
    }
  }

  public static Dimension maxImageSize(javax.swing.ImageIcon image) {
    Dimension   dim = new Dimension();

    if (image.getIconHeight() > 450 || image.getIconWidth() > 450) {
      int       x, y;
      double    scale;

      scale = Math.min(450.0/image.getIconHeight(), 450.0/image.getIconWidth());
      dim.width = (int) (image.getIconWidth() * scale);
      dim.height = (int) (image.getIconHeight() * scale);
    } else {
      dim.width = image.getIconWidth();
      dim.height = image.getIconHeight();      
    }
    return dim;
  }

  /**
   * End a session
   */
  public void endTrigger(PTextBlock list, PParagraphStyle paragraphStyle) {
    if (hasText) {
      newLine(list, paragraphStyle);
    }
    hasText = false;
  }

  /**
   * Set paragraph style
   */
  public void setParagraphStyle(PTextBlock list, PParagraphStyle style, boolean hasBang) {
    blockStyle.setStyle(list, style, hasBang);
  }

  /**
   * Prepares a new line
   */
  public void newLine(PTextBlock list, PParagraphStyle style) {
    checkText();
    lineHeight = lineHeight == 0 ? getSize() : lineHeight;
    lineDescend = lineDescend == 0 ? getDescend() : lineDescend;
    height += lineHeight;
    blockStyle.setStyle(list, style, false);
    blockStyle.add(lineHeight);
    commands.addElement(new TranslateCommand(lineDescend));
    commands.addElement(blockStyle = new BlockPainter(blockStyle));
    if (style != null && !style.needDescend(list) && !firstLine) {
      needDescend = false;
      translate.setSize(lineHeight - lineDescend - lineDescend);
      blockStyle.add(-lineDescend);
      height -= lineDescend;
    } else {
      translate.setSize(lineHeight - lineDescend);
      needDescend = true;
    }
    commands.addElement(translate = new TranslateCommand());
    lineHeight = 0;
    lineDescend = 0;
  }

  public boolean needDescend() {
    return needDescend;
  }

  public void setNeedAscend(PTextBlock text) {
    if (((BlockPainter)commands.elementAt(0)).getStyle() == null) {
      // !!!
    } else if (!((BlockPainter)commands.elementAt(0)).getStyle().needDescend(text)) {
      ((TranslateCommand)commands.elementAt(1)).addSize(getDescend());
      height += getDescend();
    }
  }

  /**
   * Sets the current x position (from tabs)
   */
  public void setPosition(float x) {
    checkText();
    commands.addElement(new PositionCommand(x));
  }

  /**
   * Gets the dimension (height) of buffers
   */
  public float getHeight() {
    return height;
  }

  /**
   * Dumps the postscipt of buffer
   */
  public void getPostscript(PTextBlock list, PPostscriptStream ps) throws PSPrintException {
    try {
      for (int i = 0; i < commands.size(); i++) {
	((Command)commands.elementAt(i)).genPostscript(ps);
      }
      new TranslateCommand(lineDescend).genPostscript(ps);
    } catch (Exception e) {
      throw new PSPrintException("PLayoutEngine.getPostscript(PTextBlock list, PPostscriptStream ps)", e);
    }
  }

  /**
   * Resets the buffer (clear)
   */
  public void reset(boolean firstLine) {
    hasText = false;
    height = 0;
    lineHeight = 0;
    lineDescend = 0;
    lastLineHeight = 0; // !!!
    buf = getStringBuffer();
    this.firstLine = firstLine;
    commands.setSize(0);
    commands.addElement(blockStyle = new BlockPainter(null));
    commands.addElement(translate = new TranslateCommand());
  }

  /**
   * Sets the current x position (from tabs)
   */
  public void addPageCount() {
    checkText();
    commands.addElement(new PageCountCommand());
  }

  // ----------------------------------------------------------------------
  // STYLE HANDLING
  // ----------------------------------------------------------------------

  /**
   * Sets the current font (style tag reached) [cached]
   */
  public void setFont(String name, int style, int size) {
    if (font == null || !font.equals(name) || style != this.style || size != this.size) {
      String psName = name;
      this.font = name;
      this.style = style;
      this.size = size;
      if (((style & PTextStyle.FCE_BOLD) > 0) && ((style & PTextStyle.FCE_ITALIC) > 0)) {
	psName += "-BoldOblique";
      } else  if ((style & PTextStyle.FCE_BOLD) > 0) {
	psName += "-Bold";
      } else  if ((style & PTextStyle.FCE_ITALIC) > 0) {
	psName += "-Oblique";
      }
      checkText();
      commands.addElement(new StyleCommand(psName, size));
    }
  }

  /**
   * SetAlignments of current mode
   */
  public void setAlignment(int align) {
    this.align = align;
  }

  /**
   * Accessors to current styles for style toggling
   */
  public String getFont() {
    return font;
  }

  /**
  * Accessors to current styles for style toggling
  */
  public int getStyle() {
    return style;
  }

  /**
   * Accessors to current styles for style toggling
   */
  public int getFontSize() {
    return size;
  }

  /**
   * Accessors to current styles for style toggling
   */
  public float getSize() {
    return Metrics.getSize(font, size);
  }

  /**
   * Accessors to current styles for style toggling
   */
  public float getDescend() {
    return Metrics.getDescend(font, size);
  }

  // ---------------------------------------------------------------------
  // INNER CLASSES
  // ---------------------------------------------------------------------

  private interface Command {
    void genPostscript(PPostscriptStream ps) throws PSPrintException;
  }

  private class TextCommand implements Command {
    TextCommand(String text, int align) {
      this.text = text;
      this.align = align;
    }
    public void genPostscript(PPostscriptStream ps) throws PSPrintException {
      ps.printText(text, align);
    }
    private String	text;
    private int		align;
  }

  private class ImageCommand implements Command {
    ImageCommand(javax.swing.ImageIcon image) {
      this.image = image;
    }
    public void genPostscript(PPostscriptStream ps) throws PSPrintException {
      ps.printImage(image);
    }
    private javax.swing.ImageIcon image;
  }

  private class PositionCommand implements Command {
    PositionCommand(float x) {
      this.x = x;
    }
    public void genPostscript(PPostscriptStream ps) {
     ps.moveTo(x, 0);
    }
    private float x;
  }

  private class TranslateCommand implements Command {
    TranslateCommand() {
    }
    TranslateCommand(float y) {
      this.y = y;
    }
    public void setSize(float y) {
      this.y = y;
    }
    public void addSize(float y) {
      this.y += y;
    }
    public void genPostscript(PPostscriptStream ps) {
      if (y != 0) {
	ps.translate(0, -y);
      }
    }
    private float y;
  }

  private class BlockPainter implements Command {
    BlockPainter(BlockPainter parent) {
      this.parent = parent;
    }
    public void add(float height) {
      if (parent != null &&
	  (parent.style == style || style == null || style.empty()) &&
	  mergingAllowed) {
	parent.add(height);
      } else {
	this.height += height;
      }
    }
    public void setStyle(PTextBlock list, PParagraphStyle style, boolean hasBang) {
      if (this.style == null) {
	this.list = list;
	this.style = style;
	mergingAllowed &= !hasBang;
      }
    }
    public PParagraphStyle getStyle() {
      return style;
    }
    public void genPostscript(PPostscriptStream ps) throws PSPrintException {
      if (height != 0 && style != null) {
	style.paintStyle(list, 0, 0, list.getBlockWidth(), height);
      }
    }
    private BlockPainter        parent;
    private float	        height;
    private PParagraphStyle	style;
    private PTextBlock	        list;
    private boolean		mergingAllowed = true;
  }

  private class StyleCommand implements Command {
    StyleCommand(String font, int size) {
      this.font = font;
      this.size = size;
    }
    public void genPostscript(PPostscriptStream ps) {
      ps.setFont(font, size);
    }
    private String	font;
    private int		size;
  }

  private class PageCountCommand implements Command {
    PageCountCommand() {
    }
    public void genPostscript(PPostscriptStream ps) throws PSPrintException {
      ps.pageCount();
    }
  }

  // ---------------------------------------------------------------------
  // PRIVATE METHODS
  // ---------------------------------------------------------------------

  private void checkText() {
    if (buf.length() != 0) {
      commands.addElement(new TextCommand(buf.toString(), align));
      buf.setLength(0);
    }
  }

  /**
   * StringBufferFactory
   */
  private StringBuffer getStringBuffer() {
    return new StringBuffer();
    // $$$
  }

  // ---------------------------------------------------------------------
  // STYLE CACHE
  // ---------------------------------------------------------------------

  private	String	font;
  private	int	style;
  private	int	size;

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int		align;
  private int		lastLineHeight;
  private boolean	firstLine;
  private boolean	needDescend;
  private boolean	hasText;
  private float		lineHeight;
  private float		lineDescend;
  private float		height;

  private Vector	commands = new Vector();
  private TranslateCommand translate;
  private BlockPainter	blockStyle;
  private StringBuffer  buf;
}
