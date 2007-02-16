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

package com.kopiright.vkopi.lib.list;

import com.kopiright.vkopi.lib.l10n.ListLocalizer;
import com.kopiright.vkopi.lib.ui.base.ListDialogCellRenderer;

/**
 * !!! NEED COMMENTS
 */
public abstract class VListColumn implements VConstants, ListDialogCellRenderer.ObjectFormater {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VListColumn(String title,
                     String column,
                     int align,
                     int width,
                     boolean sortAscending) {
    this.title  = title;
    this.column = column;
    this.align  = align;
    this.width  = width;
    this.sortAscending = sortAscending;
  }

  /**
   * Returns the column title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the column's database column name
   */
  public String getColumn() {
    return column;
  }

  /**
   * Returns the column alignment
   */
  public int getAlign() {
    return align;
  }

  /**
   * Returns the column width in characters
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the column width in characters
   */
  public boolean isSortAscending() {
    return sortAscending;
  }

  /**
   * Returns a representation of value
   */
  public Object formatObject(Object value) {
    return value == null ? VConstants.EMPTY_TEXT : value.toString();
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localize this object.
   * 
   * @param     manager         
   */
  public void localize(ListLocalizer loc) {
    title = loc.getColumnTitle(column);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String			title;
  private String			column;
  private int				align;
  private int				width;
  private boolean			sortAscending;
}
