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

import java.awt.Color;

import com.lowagie.text.pdf.PdfContentByte;



/**
 * A simple rectangle
 */
public class PRectangleBlock extends PBlock {

  /**
   * Constructs the block
   */
  public PRectangleBlock(String ident, PPosition pos, PSize size, String style) {
    super(ident, pos, size, style);
  }

  /**
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public float fill(float size) {
    if (size >= getSize().getHeight()) {
      return getSize().getHeight();
    } else {
      return 0;
    }
  }

  /**
   * Prints this block
   */
  public void doPrint(PPage page) throws PSPrintException {
    PdfContentByte cb = page.getWriter().getDirectContent();

    if (getStyle() != null) {
      PBlockStyle       style = page.getBlockStyle(getStyle());
      Color             stylecolor = style.getColor();

      cb.saveState();  // !!!!! merge with PLayoutEngine

      if (stylecolor != null) {
        cb.setColorStroke(Color.white);
        cb.setRGBColorFill(stylecolor.getRed(), stylecolor.getGreen(), stylecolor.getBlue());
        cb.rectangle(0, 0, getSize().getWidth(), getSize().getHeight());
        cb.fillStroke(); 
      }

      if (style.getBorder() > 0) {
        cb.setLineWidth(style.getBorder());
        cb.setColorStroke(Color.black);
        cb.rectangle(0, 0, getSize().getWidth(), getSize().getHeight());
        cb.stroke();
      }

      cb.restoreState();
    }
  }

  /**
   * Returns true if this block is fully printed
   */
  public boolean isFullyPrinted() {
    return true;
  }
}
