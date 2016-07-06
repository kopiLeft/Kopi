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

package com.kopiright.util.html;

import java.util.List;

import org.jdom.Element;

public class HtmlTableRow extends Element {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public HtmlTableRow() {
    super("tr");
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Adds the table row cell.
   */
  public void addCell(HtmlTableCell cell) {
    addContent(cell);
  }
  
  /**
   * Adds the table row cells.
   */
  public void addCells(List/*<HtmlTableCell>*/ cells) {
    addContent(cells);
  }
  
  /**
   * Sets the background color of a given cell index.
   */
  public void setBackgroundColor(String color, int cell) {
    ((HtmlTableCell)getContent(cell)).setBackgroundColor(color);
  }
  
  /**
   * Sets the width of a given cell index.
   */
  public void setWidth(String width, int cell) {
    ((HtmlTableCell)getContent(cell)).setWidth(width);
  }
  
  /**
   * Sets the table row style.
   */
  public void setStyle(String style) {
    setAttribute("style", style);
  }
  
  /**
   * Returns the cell count
   */
  public int getCellCount() {
    return getContentSize();
  }
}
