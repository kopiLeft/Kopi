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

package com.kopiright.vkopi.lib.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.OutputStream;
import java.io.PrintStream;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.NotImplementedException;
import java.text.AttributedCharacterIterator;

/**
 * A class to paint in a postscript file instead of screen
 */
public class AWTToPS extends Graphics {

  public AWTToPS(PrintStream stream, boolean clone) {
    this.stream = stream;
    if (!clone) {
      emitProlog();
    }
  }

  public AWTToPS(OutputStream stream, boolean clone) {
    this(new PrintStream(stream), clone);
  }

  public AWTToPS(OutputStream o) {
    this(o, false);
  }

  /**
   *
   */
  public void setBoundingBox(int x, int y, int width, int height) {
    stream.println("%%BoundingBox: " + x + " " + y + " " + width + " " + height);
  }

  /**
   *
   */
  public void setScale(double x, double y) {
    emitScale(x, y);
  }

  /**
   * Creates a new AWTToPS Object that is a copy of the original AWTToPS Object.
   */
  public Graphics create() {
    AWTToPS	g = new AWTToPS(stream, true);
    g.font = font;
    g.clippingRect = clippingRect;
    g.clr = clr;
    g.transColor = transColor;
    return g;
  }

  /**
   * Creates a new Graphics Object with the specified parameters,
   * based on the original
   * Graphics Object.
   * This method translates the specified parameters, x and y, to
   * the proper origin coordinates and then clips the Graphics Object to the
   * area.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the area
   * @param height the height of the area
   * @see #translate
   */
  public Graphics create(int x, int y, int width, int height) {
    Graphics g = create();
    g.translate(x, y);
    g.clipRect(0, 0, width, height);
    return g;
  }

  /**
   * Translates the specified parameters into the origin of
   * the graphics context. All subsequent
   * operations on this graphics context will be relative to this origin.
   * @param x the x coordinate
   * @param y the y coordinate
   * @see #scale
   */
  public void translate(int x, int y) {
    emitTranslate(x, -y);
  }

  /**
   * Scales the graphics context. All subsequent operations on this
   * graphics context will be affected.
   * @param sx the scaled x coordinate
   * @param sy the scaled y coordinate
   * @see #translate
   */
  public void scale(float sx, float sy) {
    emitScale(sx, sy);
  }

  /**
   * Gets the current color.
   * @see #setColor
   */
  public Color getColor() {
    return clr;
  }

  /**
   * Gets the current color.
   * @see #setColor
   */
  public void setBackground(Color c) {
    backClr = c;
  }

  /**
   *
   */
  public void setTransparentColor(Color trans) {
    transColor =
      (trans.getRed() << 16) + (trans.getGreen() << 8) + trans.getBlue();
  }

  /**
   * Sets the current color to the specified color. All subsequent graphics operations
   * will use this specified color.
   * @param c the color to be set
   * @see Color
   * @see #getColor
   */
  public void setColor(Color c) {
    if (c != null) {
      clr = c;
      if (clr.getRed() != lastRed ||
	  clr.getGreen() != lastGreen ||
	  clr.getBlue() != lastBlue) {
	stream.print((lastRed = clr.getRed())/255.0);
	stream.print(" ");
	stream.print((lastGreen = clr.getGreen())/255.0);
	stream.print(" ");
	stream.print((lastBlue = clr.getBlue())/255.0);
	stream.println(" setrgbcolor");
      }
    }
  }

  /**
   * Sets the default paint mode to overwrite the destination with the
   * current color. PstreamtScript has only paint mode.
   */
  public void setPaintMode() {
  }

  /**
   * Sets the paint mode to alternate between the current color
   * and the new specified color. PstreamtScript does not support XOR mode.
   * @param c1 the second color
   */
  public void setXORMode(Color c1) {
    // not used in ps
  }

  /**
   * Gets the current font.
   * @see #setFont
   */
  public Font getFont() {
    return font;
  }

  /**
   * Sets the font for all subsequent text-drawing operations.
   * @param font the specified font
   * @see Font
   * @see #getFont
   * @see #drawString
   * @see #drawBytes
   * @see #drawChars
   */
  public void setFont(Font f) {
    if (f != null) {
      this.font = f;
      String javaName = font.getName().toLowerCase();
      int javaStyle = font.getStyle();
      String psName;
      if (javaName.equals("symbol")) {
        psName = "Symbol";
      } else if (javaName.startsWith("times")) {
        psName = "Times-";
        switch (javaStyle) {
        case Font.PLAIN:
          psName += "Roman"; break;
        case Font.BOLD:
          psName += "Bold"; break;
        case Font.ITALIC:
          psName += "Italic"; break;
        case (Font.ITALIC + Font.BOLD):
          psName += "BoldItalic"; break;
        }
      } else {
        psName = javaName.equals("helvetica") ? "Helvetica" : "Courier";
        switch (javaStyle) {
        case Font.PLAIN:
          break;
        case Font.BOLD:
          psName += "-Bold"; break;
        case Font.ITALIC:
          psName += "-Oblique"; break;
        case (Font.ITALIC + Font.BOLD):
          psName += "-BoldOblique"; break;
        }
      }

      if (!psName.equals(oldName) || font.getSize() != oldSize) {
	oldSize = font.getSize();
	oldName = psName;

	stream.println("/" + psName + " findfont");
	stream.print(font.getSize());
	stream.println(" scalefont setfont");
      }
    }
  }

  /**
   * Gets the current font metrics.
   * @see #getFont
   */
  public FontMetrics getFontMetrics() {
    return getFontMetrics(getFont());
  }

  /**
   * Gets the current font metrics for the specified font.
   * @param f the specified font
   * @see #getFont
   * @see #getFontMetrics
   */
  @SuppressWarnings("deprecation")
  public FontMetrics getFontMetrics(Font f) {
    return toolkit.getFontMetrics(f);
  }


  /**
   * Returns the bounding rectangle of the current clipping area.
   * @see #clipRect
   */
  public Rectangle getClipRect() {
    return clippingRect;
  }

  /**
   * Clips to a rectangle. The resulting clipping area is the
   * intersection of the current clipping area and the specified
   * rectangle. Graphic operations have no effect outside of the
   * clipping area.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see #getClipRect
   */
  public void clipRect(int x, int y, int width, int height) {
    y = swapCoord(y);
    clippingRect = new Rectangle(x, y, width, height);

    stream.println("initclip");
    emitMoveto(x, y);
    emitLineto(x + width, y);
    emitLineto(x + width, y - height);
    emitLineto(x, y - height);
    stream.println("closepath eoclip newpath");
  }

  /**
   * Copies an area of the screen.
   * @param x the x-coordinate of the source
   * @param y the y-coordinate of the source
   * @param width the width
   * @param height the height
   * @param dx the horizontal distance
   * @param dy the vertical distance
   * Note: copyArea not supported by PstreamtScript
   */
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    throw new InconsistencyException("copyArea not supported");
  }

  /**
   * Draws a line between the coordinates (x1, y1) and (x2, y2). The line is drawn
   * below and to the left of the logical coordinates.
   * @param x1 the first point's x coordinate
   * @param y1 the first point's y coordinate
   * @param x2 the second point's x coordinate
   * @param y2 the second point's y coordinate
   */
  public void drawLine(int x1, int y1, int x2, int y2) {
    y1 = swapCoord(y1);
    y2 = swapCoord(y2);
    emitMoveto(x1, y1);
    emitLineto(x2, y2);
    stream.println("stroke");
  }

  /**
   * Fills the specified rectangle with the current color.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see #drawRect
   * @see #clearRect
   */
  public void fillRect(int x, int y, int width, int height) {
    doRect(x, y, width, height, true);
  }

  /**
   * Draws the outline of the specified rectangle using the current color.
   * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
   * rectangle.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see #fillRect
   * @see #clearRect
   */
  public void drawRect(int x, int y, int width, int height) {
    doRect(x, y, width, height, false);
  }

  /**
   * Clears the specified rectangle by filling it with the current background color
   * of the current drawing surface.
   * Which drawing surface it selects depends on how the graphics context
   * was created.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see #fillRect
   * @see #drawRect
   */
  public void clearRect(int x, int y, int width, int height) {
    stream.println("gsave");
    Color c = getColor();
    setColor(backClr);
    doRect(x, y, width, height, true);
    setColor(c);
    stream.println("grestore");
  }


  /**
   * Draws an outlined rounded corner rectangle using the current color.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param arcWidth the diameter of the arc
   * @param arcHeight the radius of the arc
   * @see #fillRoundRect
   */
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    doRoundRect(x, y, width, height, arcWidth, arcHeight, false);
  }

  /**
   * Draws a rounded rectangle filled in with the current color.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param arcWidth the diameter of the arc
   * @param arcHeight the radius of the arc
   * @see #drawRoundRect
   */
  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    doRoundRect(x, y, width, height, arcWidth, arcHeight, true);
  }

  /**
   * Draws a highlighted 3-D rectangle.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param raised a boolean that states whether the rectangle is raised or not
   */
  public void draw3DRect(int x, int y, int width, int height, boolean raised) {
    Color c = getColor();
    Color brighter = c.brighter();
    Color darker = c.darker();

    setColor(raised ? brighter : darker);
    drawLine(x, y, x, y + height);
    drawLine(x + 1, y, x + width - 1, y);
    setColor(raised ? darker : brighter);
    drawLine(x + 1, y + height, x + width, y + height);
    drawLine(x + width, y, x + width, y + height);
    setColor(c);
  }

  /**
   * Paints a highlighted 3-D rectangle using the current color.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param raised a boolean that states whether the rectangle is raised or not
   */
  public void fill3DRect(int x, int y, int width, int height, boolean raised) {
    Color c = getColor();
    Color brighter = c.brighter();
    Color darker = c.darker();

    if (!raised) {
      setColor(darker);
    }
    fillRect(x+1, y+1, width-2, height-2);
    setColor(raised ? brighter : darker);
    drawLine(x, y, x, y + height - 1);
    drawLine(x + 1, y, x + width - 2, y);
    setColor(raised ? darker : brighter);
    drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
    drawLine(x + width - 1, y, x + width - 1, y + height - 1);
    setColor(c);
  }

  /**
   * Draws an oval inside the specified rectangle using the current color.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see #fillOval
   */
  public void drawOval(int x, int y, int width, int height) {
    doArc(x, y, width, height, 0, 360, false);
  }

  /**
   * Fills an oval inside the specified rectangle using the current color.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see #drawOval
   */
  public void fillOval(int x, int y, int width, int height) {
    doArc(x, y, width, height, 0, 360, true);
  }

  /**
   * Draws an arc bounded by the specified rectangle from startAngle to
   * endAngle. 0 degrees is at the 3-o'clock pstreamition.Pstreamitive arc
   * angles indicate counter-clockwise rotations, negative arc angles are
   * drawn clockwise.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param startAngle the beginning angle
   * @param arcAngle the angle of the arc (relative to startAngle).
   * @see #fillArc
   */
  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    doArc(x, y, width, height, startAngle, arcAngle, false);
  }

  /**
   * Fills an arc using the current color. This generates a pie shape.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the arc
   * @param height the height of the arc
   * @param startAngle the beginning angle
   * @param arcAngle the angle of the arc (relative to startAngle).
   * @see #drawArc
   */
  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    doArc(x, y, width, height, startAngle, arcAngle, true);
  }


  /**
   * Draws a polygon defined by an array of x points and y points.
   * @param xPoints an array of x points
   * @param yPoints an array of y points
   * @param nPoints the total number of points
   * @see #fillPolygon
   */
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    doPolygon(xPoints, yPoints, nPoints, false);
  }

  /**
   * Draws a polygon defined by the specified point.
   * @param p the specified polygon
   * @see #fillPolygon
   */
  public void drawPolygon(Polygon p) {
    doPolygon(p.xpoints, p.ypoints, p.npoints, false);
  }

  /**
   * Fills a polygon with the current color.
   * @param xPoints an array of x points
   * @param yPoints an array of y points
   * @param nPoints the total number of points
   * @see #drawPolygon
   */
  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    doPolygon(xPoints, yPoints, nPoints, true);
  }

  /**
   * Fills the specified polygon with the current color.
   * @param p the polygon
   * @see #drawPolygon
   */
  public void fillPolygon(Polygon p) {
    doPolygon(p.xpoints, p.ypoints, p.npoints, true);
  }

  /**
   * Draws the specified String using the current font and color.
   * The x, y pstreamition is the starting point of the baseline of the String.
   * @param str the String to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @see #drawChars
   * @see #drawBytes
   */
  public void drawString(String str, int x, int y) {
    y = swapCoord(y);

    emitMoveto(x, y);
    stream.print("(");
    stream.print(str);
    stream.println(") show stroke");
  }

  /**
   * !!! coco 20/12/00 : this function has been added to compile with JDK 1.3
   * Draws the text given by the specified iterator, using this graphics context's
   * current color. The iterator has to specify a font for each character.
   * The baseline of the first character is at position (x, y) in this graphics
   * context's coordinate system.
   * @param iterator the iterator whose text is to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void drawString(AttributedCharacterIterator iterator,
				   int x,
				   int y) {
    System.err.println("the function drawString in kopi/vlib/report/AWTToPS");
  }

  /**
   * Draws the specified characters using the current font and color.
   * @param data the array of characters to be drawn
   * @param offset the start offset in the data
   * @param length the number of characters to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @see #drawString
   * @see #drawBytes
   */
  public void drawChars(char[] data, int offset, int length, int x, int y) {
    drawString(new String(data, offset, length), x, y);
  }

  /**
   * Draws the specified bytes using the current font and color.
   * @param data the data to be drawn
   * @param offset the start offset in the data
   * @param length the number of bytes that are drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @see #drawString
   * @see #drawChars
   */
  public void drawBytes(byte[] data, int offset, int length, int x, int y) {
    drawString(new String(data, offset, length), x, y);
  }

  /**
   * Draws the specified image at the specified coordinate (x, y). If the image is
   * incomplete the image observer will be notified later.
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param observer notifies if the image is complete or not
   * @see Image
   * @see ImageObserver
   */
  public boolean drawImage(Image img, int width, int height) {
    return drawImage(img, 0, swapCoord(0) - height, width, height, null);
  }

  /**
   * Draws the specified image at the specified coordinate (x, y). If the image is
   * incomplete the image observer will be notified later.
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param observer notifies if the image is complete or not
   * @see Image
   * @see ImageObserver
   */
  public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
    return doImage(img, x, y, 0, 0, observer, null);
  }

  /**
   * Draws the specified image inside the specified rectangle. The image is
   * scaled if necessary. If the image is incomplete the image observer will be
   * notified later.
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param observer notifies if the image is complete or not
   * @see Image
   * @see ImageObserver
   */
  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
    return doImage(img, x, y, width, height, observer, null);
  }

  /**
   * Draws the specified image at the specified coordinate (x, y). If the image is
   * incomplete the image observer will be notified later.
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param bgcolor the background color
   * @param observer notifies if the image is complete or not
   * @see Image
   * @see ImageObserver
   */
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    return doImage(img, x, y, 0, 0, observer, bgcolor);
  }

  /**
   * Draws the specified image inside the specified rectangle. The image is
   * scaled if necessary. If the image is incomplete the image observer will be
   * notified later.
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param bgcolor the background color
   * @param observer notifies if the image is complete or not
   * @see Image
   * @see ImageObserver
   */
  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    return doImage(img, x, y, width, height, observer, bgcolor);
  }

  /**
   * Disposes of this graphics context.  The Graphics context cannot be used after
   * being disposed of.
   * @see #finalize
   */
  public void dispose() {
    stream.flush();
  }

  /**
   * Disposes of this graphics context once it is no longer referenced.
   * @see #dispose
   */
  public void finalize() {
    dispose();
  }

  /**
   *
   */
  public String toString() {
    return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
  }

  /**
   *
   */
  protected int swapCoord(int pos) {
    return PAGEHEIGHT - pos;
  }

  /**
   *
   */
  protected void emitProlog() {
    stream.println("%!PS-Adobe-2.0");
  }

  protected void emitColorImageProlog(int xdim) {
    stream.print("/pix ");
    stream.print(xdim*3);
    stream.println(" string def");

    stream.println("% define space for color conversions");
    stream.print("/grays ");
    stream.print(xdim);
    stream.println(" string def  % space for gray scale line");
    stream.println("/npixls 0 def");
    stream.println("/rgbindx 0 def");

    stream.println("% define 'colorimage' if it isn't defined");
    stream.println("%   ('colortogray' and 'mergeprocs' come from xwd2ps");
    stream.println("%     via xgrab)");
    stream.println("/colorimage where   % do we know about 'colorimage'?");
    stream.println("{ pop }           % yes: pop off the 'dict' returned");
    stream.println("{                 % no:  define one");
    stream.println("/colortogray {  % define an RGB->I function");
    stream.println("/rgbdata exch store    % call input 'rgbdata'");
    stream.println("rgbdata length 3 idiv");
    stream.println("/npixls exch store");
    stream.println("/rgbindx 0 store");
    stream.println("0 1 npixls 1 sub {");
    stream.println("grays exch");
    stream.println("rgbdata rgbindx       get 20 mul    % Red");
    stream.println("rgbdata rgbindx 1 add get 32 mul    % Green");
    stream.println("rgbdata rgbindx 2 add get 12 mul    % Blue");
    stream.println("add add 64 idiv      % I = .5G + .31R + .18B");
    stream.println("put");
    stream.println("/rgbindx rgbindx 3 add store");
    stream.println("} for");
    stream.println("grays 0 npixls getinterval");
    stream.println("} bind def");
    stream.println("");
    stream.println("% Utility procedure for colorimage operator.");
    stream.println("% This procedure takes two procedures off the");
    stream.println("% stack and merges them into a single procedure.");
    stream.println("");
    stream.println("/mergeprocs { % def");
    stream.println("dup length");
    stream.println("3 -1 roll");
    stream.println("dup");
    stream.println("length");
    stream.println("dup");
    stream.println("5 1 roll");
    stream.println("3 -1 roll");
    stream.println("add");
    stream.println("array cvx");
    stream.println("dup");
    stream.println("3 -1 roll");
    stream.println("0 exch");
    stream.println("putinterval");
    stream.println("dup");
    stream.println("4 2 roll");
    stream.println("putinterval");
    stream.println("} bind def");
    stream.println("");
    stream.println("/colorimage { % def");
    stream.println("pop pop     % remove 'false 3' operands");
    stream.println("{colortogray} mergeprocs");
    stream.println("image");
    stream.println("} bind def");
    stream.println("} ifelse          % end of 'false' case");
  }

  public void gsave() {
    stream.println("gsave");
  }

  public void grestore() {
    stream.println("grestore");
  }

  public void emitThis(String s) {
    stream.println(s);
  }

  public Rectangle getClipBounds() {
    return getClipRect();
  }

  public void setClip(int a, int b, int c, int d) {
    clipRect(a, b, c, d);
  }

  public void showPage() {
    stream.println("showpage");
  }

  public java.awt.Shape getClip() {
    return clippingRect;
  }
  public void setClip(java.awt.Shape s) {
    clippingRect = (Rectangle)s;
  }

  public void drawPolyline(int[] param1, int[] param2, int param3) {
    throw new NotImplementedException();
  }

  public boolean drawImage(Image param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, int param9, ImageObserver param10) {
    return drawImage(param1, param2, param3, param4, param5, param10);
  }

  public boolean drawImage(Image param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, int param9, Color param10, ImageObserver param11) {
    return drawImage(param1, param2, param3, param4, param5, param11);
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private void doPolygon(int[] xPoints, int[] yPoints, int nPoints, boolean fill) {
    if (nPoints < 2) {
      return;
    }

    int[] newYPoints = new int[nPoints];

    for (int i = 0; i < nPoints; i++) {
      newYPoints[i] = swapCoord(yPoints[i]);
    }

    emitMoveto(xPoints[0], newYPoints[0]);

    // !!! should start at i = 1 ???
    for (int i = 0; i < nPoints; i++) {
      emitLineto(xPoints[0], newYPoints[0]);
    }

    stream.println(fill ? "eofill" : "stroke");
  }

  private void doRect(int x, int y, int width, int height, boolean fill) {
    y = swapCoord(y);

    emitMoveto(x, y);
    emitLineto(x + width, y);
    emitLineto(x + width, y - height);
    emitLineto(x, y - height);
    emitLineto(x, y);
    stream.println(fill ? "eofill" : "stroke");
  }

  private void doRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
    y = swapCoord(y);

    emitMoveto(x+arcHeight, y);

    // top, left to right
    stream.print(x+width);
    stream.print(" ");
    stream.print(y);
    stream.print(" ");
    stream.print(x+width);
    stream.print(" ");
    stream.print(y-height);
    stream.print(" ");
    stream.print(arcHeight);
    stream.println(" arcto");
    stream.println("4 {pop} repeat");

    // right, top to bottom
    stream.print(x+width);
    stream.print(" ");
    stream.print(y-height);
    stream.print(" ");
    stream.print(x);
    stream.print(" ");
    stream.print(y-height);
    stream.print(" ");
    stream.print(arcHeight);
    stream.println(" arcto");
    stream.println("4 {pop} repeat");

    // top, left to right
    stream.print(x);
    stream.print(" ");
    stream.print(y-height);
    stream.print(" ");
    stream.print(x);
    stream.print(" ");
    stream.print(y);
    stream.print(" ");
    stream.print(arcHeight);
    stream.println(" arcto");
    stream.println("4 {pop} repeat");

    // left, top to bottom
    stream.print(x);
    stream.print(" ");
    stream.print(y);
    stream.print(" ");
    stream.print(x+width);
    stream.print(" ");
    stream.print(y);
    stream.print(" ");
    stream.print(arcHeight);
    stream.println(" arcto");
    stream.println("4 {pop} repeat");

    stream.println(fill ? "eofill" : "stroke");
  }

  private void doArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean fill) {
    y = swapCoord(y);
    stream.println("gsave");

    // cx, cy is the center of the arc
    // translate the page to be centered there
    emitTranslate(x + (float)width/2, y - (float)height/2);
    emitScale(1, (float)height/(float)width);

    if (fill) {
      emitMoveto(0, 0);
    }

    // now draw the arc.
    float endAngle = startAngle + arcAngle;
    stream.print("0 0 ");
    stream.print((float)width/2.0);
    stream.print(" ");
    stream.print(startAngle);
    stream.print(" ");
    stream.print(endAngle);
    stream.println(" arc");

    if (fill) {
      stream.println("closepath eofill");
    } else {
      stream.println("stroke");
    }
    stream.println("grestore");
  }


  public boolean doColorImage(Image img, int x, int y, int width, int height,
                              ImageObserver observer, Color bgcolor)
  {
    // This class fetches the pixels in its constructor.
    PixelConsumer pc = new PixelConsumer(img);

    for (int i = 0; i < 10000 && !pc.isComplete(); i ++) {
      try {
        Thread.sleep(1);
      } catch (Throwable e) {
	// ignore it
      }
    }

    int                 imgWidth = img.getWidth(observer);
    int                 imgHeight = img.getHeight(observer);
    int[]               pix = new int[ imgWidth * imgHeight ];
    PixelGrabber        pg = new PixelGrabber(img, 0, 0, imgWidth, imgHeight, pix, 0, imgWidth);
    boolean             result = false;

    try {
        result = pg.grabPixels();
    } catch (InterruptedException ie) {
        // nothing
    } finally {
      if (!result) {
        stream.println("%warning: error on image grab");
        System.err.println("warning: error on image grab: " + pg.getStatus());
        return false;
      }
    }

    int numYPixels = imgHeight;
    int numXPixels = imgWidth;

    gsave();

    stream.println("currentpoint translate");
    stream.println("% build a temporary dictionary");
    stream.println("20 dict begin");
    emitColorImageProlog(numXPixels);

    stream.println("% lower left corner");
    emitTranslate(x, y);

    stream.println("% size of image");
    emitScale(width, height);

    stream.print(numXPixels);
    stream.print(" ");
    stream.print(numYPixels);
    stream.println(" 8");

    stream.print("[");
    stream.print(numXPixels);
    stream.print(" 0 0 -");
    stream.print(numYPixels);
    stream.print(" 0 ");
    stream.print(0);
    stream.println("]");

    stream.println("{currentfile pix readhexstring pop}");
    stream.println("false 3 colorimage");
    stream.println("");


    int         offset;
    char[]      sb = new char[charsPerRow + 1];
    int         bg = (bgcolor == null) ? -1 : bgcolor.getRGB();

    for (int i=0; i < imgHeight; i++) {
      offset = 0;

      for (int j=0; j < imgWidth; j++) {
        int     coord = i * imgWidth + j;
        int     n = pix[coord];
        int     alpha = n & 0xFF000000;

        if (alpha == 0) {
          n = bg;
        }

        sb[offset++] = hd[(n & 0xF00000) >> 20];
        sb[offset++] = hd[(n & 0xF0000)  >> 16];
        sb[offset++] = hd[(n & 0xF000)   >> 12];
        sb[offset++] = hd[(n & 0xF00)    >>  8];
        sb[offset++] = hd[(n & 0xF0)     >>  4];
        sb[offset++] = hd[(n & 0xF)           ];

        if (offset >= charsPerRow) {
	  stream.println(String.copyValueOf(sb, 0, offset));
          stream.println();
          offset = 0;
        }
      }

      if (offset != 0) {
        stream.println(String.copyValueOf(sb, 0, offset));
        stream.println();
      }
    }

    stream.println();
    stream.println("end");
    grestore();

    return true;
  }



  public boolean doImage(Image img, int x, int y, int width, int height, ImageObserver observer, Color bgcolor) {
    return doImage(new PixelConsumer(img), x, y, width, height, observer, bgcolor);
  }

  public boolean doImage(PixelConsumer pc, int x, int y, int width, int height, ImageObserver observer, Color bgcolor) {
    y = swapCoord(y);

    stream.println("gsave");

    stream.println("20 dict begin");
    emitColorImageProlog(pc.getDimensions().width);

    emitTranslate(x, y);

    // compute image size. First of all, if width or height is 0, image is 1:1.
    if (height == 0 || width == 0) {
      height = pc.getDimensions().height;
      width = pc.getDimensions().width;
    }
    emitScale(width, height);

    stream.print(pc.getDimensions().width);
    stream.print(" ");
    stream.print(pc.getDimensions().height);
    stream.println(" 8");

    stream.print("[");
    stream.print("0 ");
    stream.print(pc.getDimensions().width);
    stream.print(" -" + pc.getDimensions().height);
    stream.print(" 0 0 0");
    stream.println("]");

    stream.println("{currentfile pix readhexstring pop}");
    stream.println("false 3 colorimage");
    stream.println("");


    int		offset;
    // array to hold a line of pixel data
    char[] sb = new char[charsPerRow + 1];

    for (int i=0; i<pc.getDimensions().height; i++) {
      offset = 0;
      if (bgcolor == null) {
	for (int j = 0; j < pc.getDimensions().width; j++) {
	  int n = !pc.isTransparent(i, j) ? transColor : pc.getPixelAt(i, j);

	  sb[offset++] = hd[(n & 0xF00000) >> 20];
	  sb[offset++] = hd[(n & 0xF0000)  >> 16];
	  sb[offset++] = hd[(n & 0xF000)   >> 12];
	  sb[offset++] = hd[(n & 0xF00)    >>  8];
	  sb[offset++] = hd[(n & 0xF0)     >>  4];
	  sb[offset++] = hd[(n & 0xF)           ];

	  if (offset >= charsPerRow) {
	    stream.println(String.copyValueOf(sb, 0, offset));
	    offset = 0;
	  }
	}
      } else {
	for (int j = 0; j < pc.getDimensions().width; j++) {
	  int bg =
	    bgcolor.getGreen() << 16 + bgcolor.getBlue() << 8 + bgcolor.getRed();
	  int fg =
	    clr.getGreen() << 16 + clr.getBlue() << 8 + clr.getRed();
	  int n = (pc.getPixelAt(i, j) == 1 ? fg : bg);

	  sb[offset++] = hd[(n & 0xF0)    ];
	  sb[offset++] = hd[(n & 0xF)     ];
	  sb[offset++] = hd[(n & 0xF000)  ];
	  sb[offset++] = hd[(n & 0xF00)   ];
	  sb[offset++] = hd[(n & 0xF00000)];
	  sb[offset++] = hd[(n & 0xF0000) ];

	  if (offset >= charsPerRow) {
	    stream.println(String.copyValueOf(sb, 0, offset));
	    offset = 0;
	  }
	}
      }
      // print partial rows
      if (offset != 0) {
	stream.println(String.copyValueOf(sb, 0, offset));
      }
    }

    stream.println("");
    stream.println("end");
    stream.println("grestore");

    return true;
  }

  public boolean doBWImage(Image img, int x, int y, int width, int height, ImageObserver observer, Color bgcolor) {
    // This class fetches the pixels in its constructor.
    PixelConsumer pc = new PixelConsumer(img);

    for (int i = 0; i < 10000 && !pc.isComplete(); i ++) {
      try {
        Thread.sleep(1);
      } catch (Throwable e) {
	// ignore it
      }
    }

    if (pc.getDimensions() == null) {
      return false;
    }
    stream.println("gsave");

    stream.println("currentpoint translate");
    stream.println("% BLACK AND WHITE");
    stream.println("% lower left corner");
    emitTranslate(x, y);

    // compute image size. First of all, if width or height is 0, image is 1:1.
    if (height == 0 || width == 0) {
      height = pc.getDimensions().height;
      width = pc.getDimensions().width;
    }

    stream.println("% size of image");
    emitScale(height, width);

    stream.print(pc.getDimensions().width);
    stream.print(" ");
    stream.print(pc.getDimensions().height);
    stream.println(" 1");

    stream.print("[");
    stream.print("0 ");
    stream.print(pc.getDimensions().width);
    stream.print(" -" + pc.getDimensions().height);
    stream.print(" 0 0 0");
    stream.println("]");

    stream.println("{currentfile " + ((pc.getDimensions().width + 7) / 8) + " string readhexstring pop}");
    stream.println(" image");
/*
    stream.print("{<");
*/
    // array to hold a line of pixel data
    char[] sb = new char[charsPerRow + 1];
    byte   b = 3;
    byte   c = 0;
    int	   offset = 0;
    for (int i=0; i<pc.getDimensions().height; i++) {
	//	System.err.println("LINE" + i);
      for (int j=0; j<pc.getDimensions().width; j++) {
	int n = pc.getPixelAt(i, j);
	n = (((n & 0xFF) + ((n & 0xFF00) >> 8) + ((n & 0xFF0000) >> 16)) > 300) ? 1 : 0;
	c |= n << b--;

	if (b == -1) {
	  sb[offset++] = hd[c];
	  b = 3;
	  c = 0;
	}

	if (offset >= charsPerRow) {
	  String s = String.copyValueOf(sb, 0, offset);
	  stream.println(s);
	  offset = 0;
	}
      }
      // end of the line, where are we ?
      if (b != 3) {
	sb[offset++] = hd[c];
	b = 3;
	c = 0;
      }
      if (offset % 2 == 1) {
	sb[offset++] = '0';
      }
    }
    // print partial rows
    if (offset != 0) {
      String s = String.copyValueOf(sb, 0, offset);
      stream.print(s);
    }

/*
    stream.println(">} image");
*/

    stream.println("\ngrestore");

    return true;
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private void emitMoveto(int x, int y) {
    stream.print(x);
    stream.print(" ");
    stream.print(y);
    stream.println(" moveto");
  }

  private void emitLineto(int x, int y) {
    stream.print(x);
    stream.print(" ");
    stream.print(y);
    stream.println(" lineto");
  }

  private void emitTranslate(int x, int y) {
    stream.print(x);
    stream.print(" ");
    stream.print(y);
    stream.println(" translate");
  }

  private void emitTranslate(double x, double y) {
    stream.print(x);
    stream.print(" ");
    stream.print(y);
    stream.println(" translate");
  }

  private void emitScale(int sx, int sy) {
    stream.print(sx);
    stream.print(" ");
    stream.print(sy);
    stream.println(" scale");
  }

  private void emitScale(double sx, double sy) {
    stream.print(sx);
    stream.print(" ");
    stream.print(sy);
    stream.println(" scale");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	PAGEHEIGHT = 1200;
  private static final int	PAGEWIDTH = 1600;
  private static final char[]	hd = "0123456789ABCDEF".toCharArray();
  private static final int	charsPerRow = 12*6;

  private java.awt.Toolkit	toolkit = java.awt.Toolkit.getDefaultToolkit();

  private int			oldSize = -1;
  private String		oldName = "XXXX";
  private int			lastRed = -1;
  private int			lastGreen = -1;
  private int			lastBlue = -1;


  private PrintStream		stream;
  private Color			clr = Color.black;
  private Color			backClr = Color.white;
  private Font			font = new Font("Helvetica", Font.PLAIN, 12);
  private Rectangle		clippingRect = new Rectangle(0, 0, PAGEWIDTH, PAGEHEIGHT);
  private int			transColor;
}
