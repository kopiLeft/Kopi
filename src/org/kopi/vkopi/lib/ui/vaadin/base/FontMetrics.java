/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.base;

import java.io.Serializable;

/**
 * A font metrics used for some grid calculations 
 */
@SuppressWarnings("serial")
public class FontMetrics implements Serializable {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public FontMetrics(String fontFamily, int fontSize, String text) {
    this.fontFamily = fontFamily;
    this.fontSize = fontSize;
    this.text = text;
  }
  
  //---------------------------------------------------
  // ACESSORS
  //---------------------------------------------------
  
  public int getWidth() {
    return width;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public String getFontFamily() {
    return fontFamily;
  }
  
  public int getFontSize() {
    return fontSize;
  }
  
  public String getText() {
    return text;
  }

  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------

  private final String                fontFamily;
  private final int                   fontSize;
  private final String                text;
  private int                         width;
  private int                         height;
  
  public static final FontMetrics     DIGIT = new FontMetrics("sans-serif", 12, "0");
  public static final FontMetrics     LETTER = new FontMetrics("sans-serif", 12, "X");
}
