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

package com.kopiright.vkopi.lib.report;

import java.awt.Color;
import java.awt.Font;

public class DParameters {

  /**
   * Constructor
   *
   * @param color	a color defined in Constants
   */
  public DParameters(Color color) {
    Color reverseColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());

    font = new Font(com.kopiright.vkopi.lib.visual.Constants.FNT_FIXED_WIDTH, Font.PLAIN, 12);

    bgcolors = new Color[10];
    for (int i = 0; i < bgcolors.length; i++) {
      bgcolors[i] = new Color(255 - i * reverseColor.getRed() / 17,
			      255 - i * reverseColor.getGreen() / 17,
			      255 - i * reverseColor.getBlue() / 17);
    }

    fgcolors = new Color[10];
    for (int i = 0; i < fgcolors.length; i++) {
      fgcolors[i] = new Color(0,0,0);
    }
  }

  /**
   * Returns the size of the font
   */
  public Font getFont() {
    return font;
  }

  /**
   * Returns the background color which corresponds to the level
   */
  public Color getBackground(int level) {
    return level >= bgcolors.length ? Color.white : bgcolors[level];
  }

  /**
   * Returns the foreground color which corresponds to the level
   */
  public Color getForeground(int level) {
    return level >= fgcolors.length ? Color.black : fgcolors[level];
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private Font			font;
  private Color[]		bgcolors;
  private Color[]		fgcolors;
}
