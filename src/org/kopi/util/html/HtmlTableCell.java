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

import org.jdom.Element;

public class HtmlTableCell extends Element {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public HtmlTableCell() {
    super("td");
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * Sets the cell value.
   */
  public void setValue(String value) {
    setText(value);
  }
  
  /**
   * Sets the cell value.
   */
  public void setValue(int value) {
    setValue(String.valueOf(value));
  }
  
  /**
   * Sets the colspan on this cell.
   */
  public void setColspan(int colspan) {
    setAttribute("colspan", String.valueOf(colspan));
  }
  
  /**
   * Sets this cell to have a bold content
   */
  public void bold() {
    setStyle("font-weight : bold");
  }
  
  /**
   * Sets the cell font.
   */
  public void setFont(String color) {
    Element             font;
    
    font = new Element("font");
    font.setAttribute("color", color);
    font.setText(getText());
    setText(null);
    addContent(font);
  }
  
  /**
   * Centers the content of this cell.
   */
  public void center() {
    setAttribute("align", "center");
    setAttribute("valign", "center");
  }
  
  /**
   * Aligns the content of the cell at the left 
   */
  public void alignLeft() {
    setAttribute("align", "left");
    setAttribute("valign", "center");
  }
  
  /**
   * Aligns the content of the cell at the right 
   */
  public void alignRight() {
    setAttribute("align", "right");
    setAttribute("valign", "center");
  }
  
  /**
   * Sets the cell background color
   */
  public void setBackgroundColor(String color) {
    setAttribute("bgcolor", color);
  }
  
  /**
   * Sets the table cell style.
   */
  public void setStyle(String style) {
    setAttribute("style", style);
  }
  
  /**
   * Sets the width of this cell table.
   */
  public void setWidth(String width) {
    setAttribute("width", width);
  }
}
