/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: VIntegerCodeColumn.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import at.dms.util.base.InconsistencyException;

public class VIntegerCodeColumn extends VCodeColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VIntegerCodeColumn(String title, String column, String[] names, Integer[] codes, boolean sortAscending) {
    super(title, column, names, sortAscending);
    this.codes = codes;
  }

  /**
   * Constructs a list column.
   *
   */
  public VIntegerCodeColumn(String title, String column, String[] names, int[] codes, boolean sortAscending) {
    this(title, column, names, makeObjectArray(codes), sortAscending);
  }

  /*
   *
   */
  private static Integer[] makeObjectArray(int[] input) {
    Integer[]	output;

    output = new Integer[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = new Integer(input[i]);
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

    throw new InconsistencyException("bad code value " + ((Integer)value));
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final Integer[]	codes;		// code array
}
