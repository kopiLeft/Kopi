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

package org.kopi.vkopi.lib.list;

@SuppressWarnings("serial")
public class VStringColumn extends VListColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VStringColumn(String title, String column, int align, int width, boolean sortAscending) {
    super(title, column, align, width, sortAscending);
  }

  /**
   * Returns a string representation of value
   */
  public Object formatObject(Object value) {
    if (value == null) {
      return VConstants.EMPTY_TEXT;
    }

    String	str = (String)value;
    int		strLength = str.length();
    int		width = getWidth();

    if (strLength > width) {
      str = str.substring(0, width) + "..." + str.substring(width);
    }

    return str;
  }
  
  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------
  
  public Class getDataType() {
    return String.class;
  }
}
