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

import java.awt.Color;
import java.awt.Dimension;

/**
 * A class that provides some effects in PS
 * Safe methods don't move the cursor
 */
public class PPostscriptEffects {

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Prints a rectangle (safe)
   */
  public static void fillRect(PPostscriptStream ps, float borderSize, float x, float y, float width, float height) throws PSPrintException {
    ps.checkCachedInfos();
    doRect(ps, borderSize, x, y, width, height, true);
  }

  /**
   * Prints a rectangle (safe)
   */
  public static void drawRect(PPostscriptStream ps, float borderSize, float x, float y, float width, float height) throws PSPrintException {
    ps.checkCachedInfos();
    if (width == 1 || height == 1) {
      if (width == 1) {
	width = 0;
      }
      if (height == 1) {
	height = 0;
      }
      moveTo(ps, x, y);
      lineTo(ps, x + width, y + height);
      ps.println((borderSize) + " setlinewidth ");
      ps.println("stroke");
    } else {
      doRect(ps, borderSize, x,y,width,height,false);
    }
  }

  /**
   * Prints a rectangle (safe)
   */
  public static void drawLine(PPostscriptStream ps, float borderSize, float x1, float y1, float x2, float y2) throws PSPrintException {
    ps.checkCachedInfos();
    moveTo(ps, x1, y1);
    lineTo(ps, x2, y2);
    ps.println((borderSize) + " setlinewidth ");
    ps.println("stroke");
  }

  /**
   * Prints some text depending on specified alignements (unsafe)
   */
  public static void printText(PPostscriptStream ps, String text, int align) {
    // compute the size it takes
    ps.print("(");
    ps.print(toPostscript(text));
    ps.print(")");
    switch (align) {
    case PBlockStyle.ALN_RIGHT:
      ps.println("rt");
      break;
    case PBlockStyle.ALN_CENTER:
      ps.println("ct");
      break;
    case PBlockStyle.ALN_LEFT:
      ps.println("show");
      break;
    }
  }

  /**
   * Prints some text depending on specified alignements (unsafe)
   */
  public static void printImage(PPostscriptStream ps, javax.swing.ImageIcon image) {
    //  new at.dms.vkopi.lib.util.AWTToPS(ps, true).doBWImage(image.getImage(), 0, image.getIconHeight()/4, image.getIconWidth()/4, image.getIconHeight()/4, null, null);
    Dimension   dim = PLayoutEngine.maxImageSize(image);
    new at.dms.vkopi.lib.util.AWTToPS(ps, true).doColorImage(image.getImage(), 0, dim.height, dim.width, dim.height, null, Color.white);
    ps.print(dim.width);
    ps.print(" ");
    ps.print(0);
    ps.println(" rmoveto");
  }

  /**
   * Moves the current point (unsafe)
   */
  public static void moveTo(PPostscriptStream ps, float x, float y) {
    ps.print(x);
    ps.print(" ");
    ps.print(y);
    ps.println(" moveto");
  }

  /**
   * Moves the current point (unsafe)
   */
  public static void lineTo(PPostscriptStream ps, float x, float y) {
    ps.print(x);
    ps.print(" ");
    ps.print(y);
    ps.println(" lineto");
  }

  /**
   * Moves the current point (unsafe)
   */
  public static void translate(PPostscriptStream ps, float x, float y) {
    ps.print(x);
    ps.print(" ");
    ps.print(y);
    ps.println(" translate");
  }

  // ----------------------------------------------------------------------
  // EXPORTED EFFECTS
  // ----------------------------------------------------------------------

  /**
   * Sets the current color (safe)
   */
  protected static void setColor(PPostscriptStream ps, Color clr) {
    if (clr != null) {
      ps.print(clr.getRed()/255.0);
      ps.print(" ");
      ps.print(clr.getGreen()/255.0);
      ps.print(" ");
      ps.print(clr.getBlue()/255.0);
      ps.println(" setrgbcolor");
    }
  }

  /**
   * Replace '(' by '\(' and ')' by '\)'
   */
  static String toPostscript(final String str) {
    return str.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
  }

  // ----------------------------------------------------------------------
  // PRIVATE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Prints a rectangle (safe)
   */
  private static void doRect(PPostscriptStream ps, float borderSize, float x, float y, float width, float height, boolean fill) {
    moveTo(ps, x, y);
    lineTo(ps, x + width, y);
    lineTo(ps, x + width, y - height);
    lineTo(ps, x, y - height);
    lineTo(ps, x, y);
    ps.println((borderSize) + " setlinewidth ");
    if (fill) {
      ps.println("eofill");
    } else {
      ps.println("stroke");
    }
  }
}
