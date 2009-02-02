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

import com.kopiright.util.base.InconsistencyException;

public class VBooleanCodeColumn extends VCodeColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VBooleanCodeColumn(String title, String column, String[] names, Boolean[] codes, boolean sortAscending) {
    super(title, column, names, sortAscending);
    this.codes = codes;
  }

  /**
   * Constructs a list column.
   *
   */
  public VBooleanCodeColumn(String title, String column, String[] names, boolean[] codes, boolean sortAscending) {
    this(title, column, names, makeObjectArray(codes), sortAscending);
  }

  /*
   *
   */
  private static Boolean[] makeObjectArray(boolean[] input) {
    Boolean[]	output;

    output = new Boolean[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = new Boolean(input[i]);
    }

    return output;
  }

  /*
   * Returns the index.of given object
   */
  protected int getObjectIndex(Object value) {
    for (int i = 0; i < codes.length; i++) {
      if (value.equals(codes[i])) {
	return i;
      }
    }

    throw new InconsistencyException("bad code value " + value);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final Boolean[]	codes;		// code array
}
