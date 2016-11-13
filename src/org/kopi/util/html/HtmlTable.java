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

package org.kopi.util.html;

import java.util.List;

import org.jdom.Element;

public class HtmlTable extends Element {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public HtmlTable() {
    super("table");
    body = new Element("tbody");
    addContent(body);
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Adds a row to this table at the specified position.
   */
  public void addRow(int position, HtmlTableRow row) {
    body.addContent(position, row);
  }
  
  /**
   * Adds a row at the end this table
   */
  public void addRow(HtmlTableRow row) {
    body.addContent(row);
  }
  
  /**
   * Adds a list of rows to this table
   */
  public void addRows(List/*<HtmlTableRow>*/ rows) {
    body.addContent(rows);
  }
  
  /**
   * Sets the cell padding to be used for this table.
   */
  public void setCellPadding(int padding) {
    setAttribute("cellpadding", String.valueOf(padding));
  }
  
  /**
   * Sets the cell spacing to be used for this table.
   */
  public void setCellSpacing(int spacing) {
    setAttribute("cellspacing", String.valueOf(spacing));
  }
  
  /**
   * Sets the border to be used for this table.
   */
  public void setBorder(int border) {
    setAttribute("border", String.valueOf(border));
  }
  
  /**
   * Sets the width of this table.
   */
  public void setWidth(String width) {
    setAttribute("width", width);
  }
  
  /**
   * Sets the table style.
   */
  public void setStyle(String style) {
    setAttribute("style", style);
  }
  
  /**
   * Returns the cell count of a given row
   */
  public int getCellCount(int row) {
    return ((HtmlTableRow)body.getChildren().get(row)).getCellCount();
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final Element                 body;
}
