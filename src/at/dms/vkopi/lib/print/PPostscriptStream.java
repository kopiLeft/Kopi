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
 * $Id: PPostscriptStream.java,v 1.2 2004/11/12 17:30:50 lackner Exp $
 */

package at.dms.vkopi.lib.print;

import java.io.*;
import java.awt.Color;
import java.util.Hashtable;

import at.dms.util.base.InconsistencyException;

/**
 * A stream that understand postscript and perform some caching optimizations.
 * This stream also allow to define some values "at end"
 */
public class PPostscriptStream extends PrintStream {

  /**
   * Constructs a new printstream with or witout "at end" capabilities
   */
  public PPostscriptStream(PrintStream os) throws UnsupportedEncodingException {
    this(os, new ByteArrayOutputStream());
  }

  private PPostscriptStream(PrintStream os, ByteArrayOutputStream tempStream) throws UnsupportedEncodingException {
    super(tempStream, true, "ISO-8859-15");
    // header, function definitions (for font) are written directly to the stream
    // other postscript code is written to the byte array and at the end
    // to the stream.  
   temp = tempStream;
    realOutput = os;
  }

  /**
   * Prints the number of page (not yet known)
   */
  public void addHeader(String prolog, boolean landscape, float width, float height) throws PSPrintException {
    try {
      realOutput.print("%!PS-Adobe-3.0\n");
      realOutput.print("%%Creator: Kopi report generator\n");
      realOutput.print("%%Pages: (atend)\n");
      if (landscape) {
	realOutput.print("%%BoundingBox: 0 0 " + height + " " + width + "\n");
	realOutput.print("\n");
	realOutput.print("%%BeginDefaults\n");
	realOutput.print("%%PageOrientation: Landscape\n");
      } else {
	realOutput.print("%%BoundingBox: 0 0 " + width + " " + height + "\n");
	realOutput.print("\n");
	realOutput.print("%%BeginDefaults\n");
	realOutput.print("%%PageOrientation: Portrait\n");
      }
      realOutput.print("%%EndDefaults\n");
      realOutput.print("/toprinter {true} def\n");
      realOutput.print("\n");
      if (prolog != null) {
	InputStreamReader       is;
        BufferedReader          i;
	String                  s;
	StringBuffer            buf = new StringBuffer();

	is = new InputStreamReader(at.dms.vkopi.lib.visual.Utils.getFile(prolog), "ISO-8859-15");
	i = new BufferedReader(is);
	while ( (s = i.readLine()) != null) {
	  buf.append(s);
	  buf.append("\n");
	}
	realOutput.print(buf.toString());
      }
      realOutput.print("/rt { dup stringwidth pop 0 exch sub 0 rmoveto show } def\n");
      realOutput.print("/ct { dup stringwidth pop 2 div 0 exch sub 0 rmoveto show } def\n");
    } catch (IOException e) {
      throw new PSPrintException("PPostscriptStream.addHeader(String prolog, boolean landscape, float width, float height)", e);
    }
  }

  public void close(int nbPages) throws PSPrintException {
    flush();
    try {
      realOutput.print("/nbsheet            (" + nbPages + ")               def\n");

      InputStreamReader         is;
      BufferedReader            i;
      String                    s;
 
      is = new InputStreamReader(new ByteArrayInputStream(temp.toByteArray()), "ISO-8859-15");
      i = new BufferedReader(is);
      while ((s = i.readLine()) != null) {
        realOutput.print(s);
        realOutput.print("\n");
      }
      realOutput.flush();
      //realOutput.close();
    } catch (IOException e) {
      throw new PSPrintException("PPostscriptStream.close("+nbPages+")", e);
    }
  }

  public void showPage() {
    println("showpage");
    currentPosX = currentPosY = 0;
    psPosX = psPosY = 0.0f;
    currentFontSize = psFontSize = -1;
    psFont = currentFont = null;
    currentColor = psColor = null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (TEXT HANDLING)
  // ----------------------------------------------------------------------

  /**
   * Prints a text with specified alignment
   */
  public void printText(String text, int align) throws PSPrintException {
    checkCachedInfos();
    PPostscriptEffects.printText(this, text, align);
  }

  /**
   * Prints a text with specified alignment
   */
  public void printImage(javax.swing.ImageIcon image) throws PSPrintException {
    checkCachedInfos();
    PPostscriptEffects.printImage(this, image);
  }

  /**
   * Prints a text with specified alignment
   */
  public void pageCount() throws PSPrintException {
    checkCachedInfos();
    println("nbsheet show");
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (POSITIONING)
  // ----------------------------------------------------------------------

  /**
   * Translates the position of cursor on page
   */
  public void moveTo(float x, float y) {
    checkPosition();
    PPostscriptEffects.moveTo(this, x, y);
  }

  /**
   * Translates the position of cursor on page
   */
  public void translate(float x, float y) {
    currentPosX += x;
    currentPosY += y;
  }

  /**
   * Translates the position of cursor on page
   */
  public void translateAbsolute(float x, float y) {
    currentPosX -= psPosX;
    currentPosX = x;
    currentPosY -= psPosY;
    currentPosY = y;
  }

  /**
   *
   */
  public float getX() {
    return currentPosX;
  }

  /**
   *
   */
  public float getY() {
    return currentPosY;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (STYLING)
  // ----------------------------------------------------------------------

  /**
   * Translates the position of cursor on page
   */
  public void setColor(Color color) {
    currentColor = color;
  }

  /**
   * Translates the position of cursor on page
   */
  public void setFont(String font, int size) {
    if (font == null) {
      throw new InconsistencyException();
    }
    currentFont = font;
    currentFontSize = size;
  }

  // ---------------------------------------------------------------------
  // PRIVATE METHODS
  // ---------------------------------------------------------------------

  protected void checkCachedInfos() throws PSPrintException {
    checkPosition();
    checkStyle();
  }

  private void checkStyle() throws PSPrintException {
    if (psColor == null || !psColor.equals(currentColor)) {
      psColor = currentColor;
      PPostscriptEffects.setColor(this, psColor);
    }
    if (psFont == null || !psFont.equals(currentFont) || psFontSize != currentFontSize) {
       if (currentFont != null) {
	 psFont = currentFont;
      }
      if (currentFontSize != -1) {
	psFontSize = currentFontSize;
      }
      String    key = psFont + "#" + psFontSize; //$$$
      Integer   code = (Integer)styles.get(key);

      if (code == null) {
	// define new style
	code = new Integer(styles.size());
	styles.put(key, code);

        realOutput.print("/stl");
        realOutput.print(String.valueOf(code));
        realOutput.print("{");
        
        // AS FONT CAN BE NULL SET IN THIS CASE TO STDFONT COURIER - LEMI 14.01.01
        realOutput.print("/" + (psFont == null ? "Courier" : psFont) + " findfont\n");
        realOutput.print(String.valueOf(psFontSize));
        realOutput.print(" scalefont setfont\n");

        realOutput.print("} def\n");
      }
      print("stl");
      println(code);
    }
  }

  private void checkPosition() {
    if (psPosX != currentPosX || psPosY != currentPosY) {
      PPostscriptEffects.translate(this, psPosX - currentPosX, currentPosY - psPosY);
      psPosX = currentPosX;
      psPosY = currentPosY;
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private Color				currentColor;
  private Color				psColor;

  private String			currentFont;
  private String			psFont;

  private int				currentFontSize;
  private int				psFontSize;

  private float				currentPosX;
  private float				currentPosY;
  private float				psPosX;
  private float				psPosY;

  private PrintStream                   realOutput;
  private ByteArrayOutputStream		temp;
  private Hashtable			styles = new Hashtable();
}
