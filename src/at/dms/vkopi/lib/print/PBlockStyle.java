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

/**
 * A block style is responsible of painting a line or a paragraphe style.

 */
public class PBlockStyle extends PStyle {

  /**
   * Construct a style
   */
  public PBlockStyle(String ident,
		     String superStyle,
		     int border,
		     Color color) {
    super(ident);
    this.superStyleIdent = superStyle;
    this.border = border / 10.0f;
    this.color = color;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  // ---------------------------------------------------------------------
  // Inheritence handling
  // ---------------------------------------------------------------------

  /**
   * Returns the color of the background
   */
  protected Color getColor() {
    if (color != null) {
      return color;
    }
    if (hasParentStyle()) {
      return getParentStyle().getColor();
    }
    return null;
  }

  /**
   * Returns the border thickness
   */
  protected float getBorder() {
    return border;
  }

  /**
   * Returns true if this style has a parent
   */
  private boolean hasParentStyle() {
    return superStyleIdent != null;
  }

  /**
   * Returns the parent style or null
   */
  private PBlockStyle getParentStyle() {
    if (superStyle == null) {
      superStyle = getOwner().getBlockStyle(superStyleIdent);
    }
    return superStyle;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private String	superStyleIdent;
  private PBlockStyle	superStyle;
  private float		border;
  private Color		color;
}
