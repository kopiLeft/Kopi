/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.form;

import at.dms.util.base.InconsistencyException;

public class VEnumColumn extends VCodeColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VEnumColumn(String title, String column, String[] names, boolean sortAscending) {
    super(title, column, names, sortAscending);
  }

  /*
   * Returns the index.of given object
   */
  protected int getObjectIndex(Object value) {
    for (int i = 0; i < names.length; i++) {
      if ((value).equals(names[i])) {
	return i;
      }
    }

    throw new InconsistencyException("bad code value " + value);
  }
}
