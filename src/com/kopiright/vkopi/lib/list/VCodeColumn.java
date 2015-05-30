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

package com.kopiright.vkopi.lib.list;

@SuppressWarnings("serial")
public abstract class VCodeColumn extends VListColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VCodeColumn(String title, String column, String[] names, boolean sortAscending) {
    super(title, column, ALG_LEFT, getMaxWidth(names), sortAscending);
    this.names = names;
  }

  /**
   * Returns a string representation of value
   */
  public Object formatObject(Object value) {
    return value == null ? VConstants.EMPTY_TEXT : names[getObjectIndex(value)];
  }

  // --------------------------------------------------------------------
  // PROTECTED METHODS
  // --------------------------------------------------------------------

  /*
   * Returns the index.of given object
   */
  protected abstract int getObjectIndex(Object value);

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private static int getMaxWidth(String[] names) {
    int		res = 0;

    for (int i = 0; i < names.length; i++) {
      res = Math.max(names[i].length(), res);
    }

    return res;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  protected String[]		names;
}
