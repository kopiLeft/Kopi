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

import com.kopiright.util.base.InconsistencyException;

public class DColumnStyle implements Constants {

  /**
   * Sets the background color of this component.
   *
   * @param	color	The color to become this component's background color.
   */
  public void setBackground(int color) {
    this.background = color;
  }

  /**
   * Gets the background color of this component.
   */
  public int getBackgroundCode() {
    return background;
  }

  /**
   * Sets the foreground color of this component.
   *
   * @param	color	The color to become this component's foreground color.
   */
  public void setForeground(int color) {
    this.foreground = color;
  }

  /**
   * Gets the foreground color of this component.
   */
  public int getForegroundCode() {
    return foreground;
  }

  /**
   * Sets the font of this component.
   *
   * @param	font	The font to become this component's font.
   */
  public void setFont(int font) {
    this.fontName = font;
  }

  /**
   * Gets the font of this component.
   */
  public int getFontName() {
    return fontName;
  }

  /**
   * Sets the font style of this component.
   *
   * @param	style	The font to become this component's font.
   */
  public void setFontStyle(int style) {
    this.fontStyle = style;
  }

  /**
   * Gets the font style of this component.
   */
  public int getFontStyle() {
    return fontStyle;
  }

  /**
   * Sets the type of value
   *
   * @param state		in Constants.STA_*
   */
  public void setState(int state) {
    this.state = state;
  }

  /**
   * Gets the background color of this component.
   *
   * @return	color	The color to become this component's background color.
   */
  @SuppressWarnings("deprecation")
  public Color getBackground() {
    switch (background) {
    case CLR_WHITE:
      return Color.white;
    case CLR_BLACK:
      return Color.black;
    case CLR_RED:
      return Color.red;
    case CLR_GREEN:
      return Color.green;
    case CLR_BLUE:
      return Color.blue;
    case CLR_YELLOW:
      return Color.yellow;
    case CLR_PINK:
      return Color.pink;
    case CLR_CYAN:
      return Color.cyan;
    case CLR_GRAY:
      return Color.gray;
    }
    throw new InconsistencyException();
  }

  /**
   * Gets the foreground color of this component.
   *
   * @return	color	The color to become this component's foreground color.
   */
  @SuppressWarnings("deprecation")
  public Color getForeground() {
    switch (foreground) {
    case CLR_WHITE:
      return Color.white;
    case CLR_BLACK:
      return Color.black;
    case CLR_RED:
      return Color.red;
    case CLR_GREEN:
      return Color.green;
    case CLR_BLUE:
      return Color.blue;
    case CLR_YELLOW:
      return Color.yellow;
    case CLR_PINK:
      return Color.pink;
    case CLR_CYAN:
      return Color.cyan;
    case CLR_GRAY:
      return Color.gray;
    }
    throw new InconsistencyException();
  }

  /**
   * Gets the font of this component.
   *
   * @return	font	The font to become this component's font.
   */
  @SuppressWarnings("deprecation")
  public Font getFont() {
    String	font;
    switch (fontName) {
    case 0:
      font = com.kopiright.vkopi.lib.visual.Constants.FNT_FIXED_WIDTH;
      break;
    case 1:
      font = "Helvetica";
      break;
    case 2:
      font = "Geneva";
      break;
    case 3:
      font = "Courier";
      break;
    default:
      throw new InconsistencyException();
    }
    return new Font(font, fontStyle, 12);
  }

  /**
   * Gets the type of value
   *
   * @return state		in Constants.STA_*
   */
  public int getState() {
    return state;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int foreground;
  private int background;
  private int fontName;
  private int fontStyle;
  private int state;
}
