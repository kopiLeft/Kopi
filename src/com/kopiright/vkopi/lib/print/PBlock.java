/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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


/**
 * A block of data to print
 */
public abstract class PBlock {

  /**
   * Constructor with a position and a default size
   */
  public PBlock(String ident, PPosition position, PSize size, String style) {
    this.ident = ident;
    this.position = position;
    this.size = size;
    this.style = style;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of the block
   */
  public String getIdent() {
    int		dot = ident.indexOf('.');
    return dot >= 0 ? ident.substring(dot + 1) : ident;
  }


  /**
   * Returns the position of this block on page
   */
  public PPosition getPosition() {
    return position;
  }

  /**
   * Returns the position of this block on page
   */
  public PSize getSize() {
    return size;
  }

  /**
   * Returns true if the block should be printed or not
   */
  public boolean isVisible() {
    return page.showBlock(index);
  }

  /**
   * Returns true if this block is shown on this page
   */
  public boolean isShownOnThisPage() {
    return page.showBlock(index);
  }

  /**
   * Returns the position of this block on page
   */
  public String getStyle() {
    return style;
  }

  /**
   * Returns the position of this block on page
   */
  public PPage getPage() {
    return page;
  }

  public int getIndex() {
    return index;
  }

  /**
   * Returns the position of this block on page
   */
  public void setPage(PPage page, int index) {
    this.page = page;
    this.index = index;
  }

  /**
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public abstract float fill(float size) throws PSPrintException;

//   public abstract float fill(PdfWriter writer) throws PSPrintException;

  /**
   * Returns true if this block is fully printed
   */
  public abstract boolean isFullyPrinted();

  /**
   * Prints this block
   */
  public abstract void doPrint(PPage page) throws PSPrintException;

  public void preparePrint(PBlock block) {
    size.preparePrint(this);
  }

  public void preparePrint(PPage page) {
    size.preparePrint(this);
  }

  public void reinitialize() {
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private	String		ident;
  private	PPage		page;
  private	PPosition	position;
  private	PSize		size;
  private	String		style;
  private	int		index;
}
