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
import java.util.Hashtable;

/**
 * A paragraph style is responsible of painting a line or a paragraphe style.
 */
public class PParagraphStyle extends PBodyStyle {

  /**
   * Construct a style
   */
  public PParagraphStyle(String ident,
                         String superStyle,
                         int align,
                         int indentLeft,
                         int borderMode,
                         int border,
                         int marginLeft,
                         int marginRight,
                         Color color,
                         boolean noBackground,
                         PTabStop[] stops) {
    super(ident);
    this.superStyleIdent = superStyle;
    this.align = align;
    this.indentLeft = indentLeft;
    this.borderMode = borderMode;
    this.border = border / 10.0f;
    this.color = color;
    this.marginLeft = marginLeft;
    this.marginRight = marginRight;
    this.noBackground = noBackground;
    tabArray = stops;
    if (stops != null) {
      tabs = new Hashtable();
      for (int i = 0; i < stops.length; i++) {
        tabs.put(stops[i].getIdent(), stops[i]);
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Sets the style 
   */
  public void setStyle(PTextBlock list, PLayoutEngine engine, boolean hasBang) {
    engine.setParagraphStyle(list, this, hasBang);
  }

  /**
   * Needs descsends
   */
  public boolean needDescend() {
    return getBorder() > 0 || getColor() != null;
  }

  /**
   * Sets the tab on current engine
   */
  public void setTab(PTextBlock list, String tab) {
    PTabStop    stop = tabs == null ? null : (PTabStop)tabs.get(tab);
    if (stop == null) {
      if (hasParentStyle()) {
        getParentStyle().setTab(list, tab);
      } else {
        // This should have been checked by the compiler
        System.err.println("Undefined tab : " + tab);
      }
    } else {
      list.setPosition(stop.getPosition(), stop.getAlignment());
    }
  }

  /**
   * Goto next tab
   */
  public String gotoNextTab(PTextBlock list, String tab) {
    if (tabs == null && hasParentStyle()) {
      return getParentStyle().gotoNextTab(list, tab);
    }
    PTabStop    stop = tabs == null || tab == null ? null : (PTabStop)tabs.get(tab);
    if (stop == null) {
      stop =  tabArray[0];
    } else {
      for (int i = 0; i < tabArray.length; i++) {
        if (tabArray[i].getIdent().equals(tab)) {
          stop = tabArray[i == tabArray.length - 1 ? i : i + 1];
          break;
        }
      }
    }
    if (stop != null) {
      list.setPosition(stop.getPosition(), stop.getAlignment());
      return stop.getIdent();
    }
    return null;
  }

  /**
   * Returns true if this block define a non null style (border or background)
   */
  public boolean empty() {
    return border <= 0 && color == null && !noBackground;
  }

  /**
   * Returns the default alignment
   */
  public int getAlignment() {
   if (align != -1) {
      return align;
    }
    if (hasParentStyle()) {
      return getParentStyle().getAlignment();
    }
    return -1;
  }

  /**
   * Returns left indent
   */
  public int getIndentLeft() {
    if (indentLeft != -1) {
      return indentLeft;
    }
    if (hasParentStyle()) {
      return getParentStyle().getIndentLeft();
    }
    return 0;
  }

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
   * Returns the border 
   */
  protected int getBorderMode() {
    return borderMode;
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
  private PParagraphStyle getParentStyle() {
    if (superStyle == null) {
      superStyle = (PParagraphStyle) getOwner().getStyle(superStyleIdent);
    }
    return superStyle;
  }

  

  // ---------------------------------------------------------------------
  // DATA MEMBERS
 // ---------------------------------------------------------------------

  public static final   int     BRD_TOP         = 1 << 0;
  public static final   int     BRD_BOTTOM      = 1 << 1;
  public static final   int     BRD_LEFT        = 1 << 2;
  public static final   int     BRD_RIGHT       = 1 << 3;
  public static final   int     BRD_ALL         = BRD_TOP + BRD_BOTTOM + BRD_LEFT + BRD_RIGHT;

  private String        superStyleIdent;
  private PParagraphStyle       superStyle;
  private boolean       noBackground;
  private PTabStop[]    tabArray;
  private int           align;
  private int           indentLeft;
  private int           marginLeft;
  private int           marginRight;
  private float         border;
  private int           borderMode;
  private Color         color;
  private Hashtable     tabs;
}
