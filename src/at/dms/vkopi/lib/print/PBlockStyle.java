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
 * $Id: PBlockStyle.java,v 1.1 2004/07/28 18:43:27 imad Exp $
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

  /**
   * Paints the styles before body is dumped (border, background)
   */
  public void paintStyle(PPage page, float x, float y, float width, float height) throws PSPrintException {
    PPostscriptStream	ps = page.getPostscriptStream();

    Color	color = getColor(page);
    if (color != null) {
      PPostscriptEffects.setColor(ps, color);
      PPostscriptEffects.fillRect(ps, 0, x, y, width, height);
      PPostscriptEffects.setColor(ps, Color.black);
    }
    float border = getBorder(page);
    if (border > 0) {
      PPostscriptEffects.drawRect(ps, border, x, y, width, height);
    }
  }

  // ---------------------------------------------------------------------
  // Inheritence handling
  // ---------------------------------------------------------------------

  /**
   * Returns the color of the background
   */
  private Color getColor(PPage page) {
    if (color != null) {
      return color;
    }
    if (hasParentStyle()) {
      return getParentStyle(page).getColor(page);
    }
    return null;
  }

  /**
   * Returns the border thickness
   */
  private float getBorder(PPage page) {
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
  private PBlockStyle getParentStyle(PPage page) {
    if (superStyle == null) {
      superStyle = page.getBlockStyle(superStyleIdent);
    }
    return superStyle;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  public static final	int	ALN_LEFT	= 0;
  public static final	int	ALN_RIGHT	= 1;
  public static final	int	ALN_CENTER	= 2;
  public static final	int	ALN_JUSTIFIED	= 3;

  private String	superStyleIdent;
  private PBlockStyle	superStyle;
  private float		border;
  private Color		color;
}
