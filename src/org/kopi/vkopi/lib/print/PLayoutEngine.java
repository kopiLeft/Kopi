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

package org.kopi.vkopi.lib.print;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.base.Utils;
import org.kopi.vkopi.lib.visual.ApplicationContext;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;

/**
 * This class is in charge to draw text and styles in ps (a set of line)
 */
class PLayoutEngine {

  public PLayoutEngine(boolean firstLine) {
    reset(firstLine);
  }

  public void addText(String text) {
    if (text != null && text.length() > 0) {
      hasText = true;
      lineHeight = Math.max(lineHeight, getSize());
      lineDescend = Math.max(lineDescend, getDescend());

      if (currentPhrase == null) {
        currentPhrase = new Phrase(new Chunk(text, currentFont));
      } else {
        currentPhrase.add(new Chunk(text, currentFont));
      }
    }
  }

  /**
   * Adds text to buffer
   */
  public void addImage(ImageIcon image) {
    if (image != null) {
      hasText = true;
      endChunk();
      lineHeight = Math.max(lineHeight, maxImageSize(image).height + lineDescend);
      commands.addElement(new ImageCommand(image, currentPos, -lineDescend));
    }
  }

  public static Dimension maxImageSize(ImageIcon image) {
    Dimension   dim = new Dimension();

    if (image.getIconHeight() > 500 || image.getIconWidth() > 500) {
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
    endChunk();
    lineHeight = lineHeight == 0 ? getSize() : lineHeight;
    lineDescend = lineDescend == 0 ? getDescend() : lineDescend;
    height += lineHeight;
    blockStyle.setStyle(list, style, false);
    blockStyle.add(lineHeight);
    commands.addElement(new TranslateCommand(lineDescend));
    commands.addElement(blockStyle = new BlockPainter(blockStyle));

    if (style != null && !style.needDescend() && !firstLine) {
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
    currentPos = 0;
  }

  public boolean needDescend() {
    return needDescend;
  }

  public void setNeedAscend(PTextBlock text) {
    if (((BlockPainter)commands.elementAt(0)).getStyle() == null) {
      // !!!
    } else if (!((BlockPainter)commands.elementAt(0)).getStyle().needDescend()) {
      ((TranslateCommand)commands.elementAt(1)).addSize(getDescend());
      height += getDescend();
    }
  }

  /**
   * Sets the current x position (from tabs)
   */
  public void setPosition(float x) {
    if (currentPhrase != null) {
      commands.add(new TextCommand(currentPhrase, currentPos, align));
    }

    currentPhrase = null;
    currentPos = x;
  }

  /**
   * Gets the current x position.
   */
  public float getPosition() {
    return currentPos;
  }

  /**
   * Gets the dimension (height) of buffers
   */
  public float getHeight() {
    return height;
  }

  public void generate(PPage page) throws PSPrintException {
    PdfContentByte cb = page.getPdfContentByte();
    try {
      for (int i = 0; i < commands.size(); i++) {
	((Command)commands.elementAt(i)).generate(cb);
      }
      new TranslateCommand(lineDescend).generate(cb);
    } catch (Exception e) {
      throw new PSPrintException("PLayoutEngine.generate(PPage page)", e);
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
    this.firstLine = firstLine;
    currentPhrase = null;
    commands.setSize(0);
    commands.addElement(blockStyle = new BlockPainter(null));
    commands.addElement(translate = new TranslateCommand());
  }

  /**
   * Sets the current x position (from tabs)
   */
  public void addPageCount(int nummer) {
      addText(String.valueOf(nummer));
  }

  // ----------------------------------------------------------------------
  // STYLE HANDLING
  // ----------------------------------------------------------------------

  /**
   * Sets the current font (style tag reached) [cached]
   */
  public void setFont(String name, int style, int size) {
    try {
      this.font = name;
      this.style = style;
      this.size = size;

      int fontStyle = Font.NORMAL;

      String ttfName = font;

      if (((style & PTextStyle.FCE_BOLD) > 0) && ((style & PTextStyle.FCE_ITALIC) > 0)) {
        ttfName += "italicbold";
      } else  if ((style & PTextStyle.FCE_BOLD) > 0) {
        ttfName += "bold";
      } else  if ((style & PTextStyle.FCE_ITALIC) > 0) {
        ttfName += "italic";
      }



      URL url = Utils.getURLFromResource(ttfName+".TTF", "resources");

      if (url != null) {
        BaseFont base = BaseFont.createFont(url.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        currentFont = new Font(base, size);
      } else {
        //!!! graf 20060218: signal missing font!!!

        // default font
        if (((style & PTextStyle.FCE_BOLD) > 0) && ((style & PTextStyle.FCE_ITALIC) > 0)) {
          fontStyle = Font.BOLDITALIC;
        } else  if ((style & PTextStyle.FCE_BOLD) > 0) {
          fontStyle = Font.BOLD;
        } else  if ((style & PTextStyle.FCE_ITALIC) > 0) {
          fontStyle = Font.ITALIC;
        }

        currentFont = FontFactory.getFont(name, BaseFont.CP1252, size, fontStyle);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new InconsistencyException(e);
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
   * gets the length of a string in pixels
   */
  @SuppressWarnings("deprecation")
  public float getStringWidth(String str) {
    if (ApplicationContext.getApplicationContext().isWebApplicationContext()) {
      return currentFont.getCalculatedBaseFont(true).getWidthPoint(str, currentFont.size());
    } else {
      return currentFont.getBaseFont().getWidthPoint(str,currentFont.size());
    }
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
    // return (currentFont.getCalculatedBaseFont(false).getAscentPoint("Xg", size) - currentFont.getCalculatedBaseFont(false).getDescentPoint("Xg", size))*1.4f;
  }

  /**
   * Accessors to current styles for style toggling
   */
  public float getDescend() {
    // return currentFont.getCalculatedBaseFont(false).getDescentPoint("Xg", size);
    return Metrics.getDescend(font, size);
  }

  // ---------------------------------------------------------------------
  // INNER CLASSES
  // ---------------------------------------------------------------------

  private interface Command {
    void generate(PdfContentByte cb) throws PSPrintException;
  }

  private static class TextCommand implements Command {
    TextCommand(Phrase chunk, float position, int alignment) {
      this.chunk = chunk;
      this.position = position;
      this.alignment = alignment;
    }

    public void generate(PdfContentByte cb) throws PSPrintException {
      int               align;

      switch (alignment) {
      case PStyle.ALN_DEFAULT:
      case PStyle.ALN_LEFT:
        align = PdfContentByte.ALIGN_LEFT;
        break;
      case PStyle.ALN_RIGHT:
        align = PdfContentByte.ALIGN_RIGHT;
        break;
      case PStyle.ALN_CENTER:
        align = PdfContentByte.ALIGN_CENTER;
        break;
// not supported by PdfContentByte
//       case PBlockStyle.ALN_JUSTIFIED:
//         align = PdfContentByte.ALIGN_JUSTIFIED;
//         break;
      default:
        throw new InconsistencyException("Unsupported alignment: " + alignment);
      }
      ColumnText.showTextAligned(cb,
                                 align,
                                 chunk,
                                 position,
                                 0,
                                 0);
    }

    final Phrase chunk;
    final float position;
    final int alignment;
  }

  private static class ImageCommand implements Command {
    ImageCommand(ImageIcon image, float x, float y) {
      this.image = image;
      this.y = y;
      this.x = x;
    }
    public void generate(PdfContentByte cb) throws PSPrintException {
      try {
        Image img = Image.getInstance(image.getImage(), Color.white);

        img.setAbsolutePosition(x, y);
        cb.addImage(img);
      } catch (Exception e) {
        e.printStackTrace();
        throw new PSPrintException("Can't print image");
      }
    }

    private ImageIcon image;
    private float x;
    private float y;
  }

  private static class TranslateCommand implements Command {
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
    public void generate(PdfContentByte cb) throws PSPrintException {
      if (y != 0) {
        cb.concatCTM(1, 0, 0, 1, 0, -y);
      }
    }
    private float y;
  }

  private static class BlockPainter implements Command {
    BlockPainter(BlockPainter parent) {
      this.parent = parent;
      this.translate = -1;
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

    @SuppressWarnings("unused")
    public float getTranslation() {
      return translate;
    }

    @SuppressWarnings("unused")
    public void translate(float translate) {
      this.translate = translate;
    }
     public void generate(PdfContentByte cb) throws PSPrintException {
       generate(cb, 0, 0);
     }

    public void generate(PdfContentByte cb, float x, float y) {
      if (height == 0 || style == null) {
        return;
      }
      cb.saveState();

      Color     styleColor = style.getColor();

      if (styleColor != null) {

        cb.setColorStroke(Color.white);
        cb.setRGBColorFill(styleColor.getRed(), styleColor.getGreen(), styleColor.getBlue());
        cb.rectangle(x, y-translate-height, list.getSize().getWidth(), height);
        cb.fillStroke();
      }

      if (style.getBorder() > 0) {
        cb.setLineWidth(style.getBorder());
        cb.setColorStroke(Color.black);
        if (style.getBorderMode() == PParagraphStyle.BRD_ALL) {
          cb.rectangle(x, y-translate-height, list.getSize().getWidth(), height);
          cb.stroke();
        } else {
          if ((style.getBorderMode() & PParagraphStyle.BRD_TOP) > 0) {
            cb.moveTo(x, y-translate);
            cb.lineTo(x+list.getSize().getWidth(), y-translate);
            cb.stroke();
          }
          if ((style.getBorderMode() & PParagraphStyle.BRD_BOTTOM) > 0) {
            cb.moveTo(x, y-translate-height);
            cb.lineTo(x+list.getSize().getWidth(), y-translate-height);
            cb.stroke();
          }
          if ((style.getBorderMode() & PParagraphStyle.BRD_LEFT) > 0) {
            cb.moveTo(x, y-translate);
            cb.lineTo(x, y-translate-height);
            cb.stroke();
          }
          if ((style.getBorderMode() & PParagraphStyle.BRD_RIGHT) > 0) {
            cb.moveTo(x+list.getSize().getWidth(), y-translate);
            cb.lineTo(x+list.getSize().getWidth(), y-translate-height);
            cb.stroke();
          }
        }
      }
      cb.restoreState();
    }

    private float translate;
    private BlockPainter        parent;
    private float	        height;
    private PParagraphStyle	style;
    private PTextBlock	        list;
    private boolean		mergingAllowed = true;
  }

  // ---------------------------------------------------------------------
  // PRIVATE METHODS
  // ---------------------------------------------------------------------

  private void endChunk() {
    if (currentPhrase != null) {
      commands.add(new TextCommand(currentPhrase, currentPos, align));
      currentPhrase = null;
    }
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

  private float         		currentPos;
  private Phrase        		currentPhrase;
  private Font          		currentFont = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL, java.awt.Color.magenta);

  private int				align;
  private boolean			firstLine;
  private boolean			needDescend;
  private boolean			hasText;
  private float			        lineHeight;
  private float			        lineDescend;
  private float			        height;

  private Vector<Command>  		commands = new Vector<Command>();
  private TranslateCommand      	translate;
  private BlockPainter          	blockStyle;
}
