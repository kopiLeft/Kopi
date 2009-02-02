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

public class PTextStyle extends PBodyStyle {

  /**
   * Construct a style
   */
  public PTextStyle(String ident, String font, int style, int size) {
    super(ident);
    this.font = font;
    this.style = style;
    this.size = size;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * The list printer ask us to set the style
   */
  public void setStyle(PTextBlock list, PLayoutEngine engine, boolean hasBang) {
    boolean		changed = false;

    int style;
    String font;
    int size;

    if (this.font != null && !this.font.equals(engine.getFont())) {
      changed = true;
      font = this.font;
    } else {
      font = engine.getFont();
    }

    if (this.size != -1 && engine.getSize() != this.size) {
      size = this.size;
      changed = true;
    } else {
      size = engine.getFontSize();
    }

    if (this.style != -1) {
      if (hasBang) {
	style = engine.getStyle() & ~this.style;
      } else {
	style = engine.getStyle() | this.style;
      }
      changed = true;
    } else {
      style = engine.getStyle();
    }

    if (changed) {
      engine.setFont(font, style, size);
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  // -1 = no change
  public static final	int	FCE_BOLD		= 1 << 0;
  public static final	int	FCE_ITALIC		= 1 << 1;
  public static final	int	FCE_SUBSCRIPT		= 1 << 2;
  public static final	int	FCE_SUPERSCRIPT		= 1 << 3;
  public static final	int	FCE_UNDERLINE		= 1 << 4;
  public static final	int	FCE_STRIKETHROUGHT	= 1 << 5;
  public static final	int	FCE_NO_BOLD		= 1 << 8;
  public static final	int	FCE_NO_ITALIC		= 1 << 9;
  public static final	int	FCE_NO_SUBSCRIPT	= 1 << 10;
  public static final	int	FCE_NO_SUPERSCRIPT	= 1 << 11;
  public static final	int	FCE_NO_UNDERLINE	= 1 << 12;
  public static final	int	FCE_NO_STRIKETHROUGHT	= 1 << 13;

  private String	font;
  private int		style;
  private int		size;
}
