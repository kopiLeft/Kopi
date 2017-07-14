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

import org.kopi.xkopi.lib.type.Fixed;

@SuppressWarnings("serial")
public class VFixnumColumn extends VListColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VFixnumColumn(String title,
                       String column,
                       int align,
                       int width,
                       int scale,
                       boolean sortAscending) {
    super(title, column, align, width, sortAscending);
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------
  
  public Class getDataType() {
    return Fixed.class;
  }
}
