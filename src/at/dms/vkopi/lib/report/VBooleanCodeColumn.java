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
 * $Id: VBooleanCodeColumn.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.report;

import at.dms.util.base.InconsistencyException;

public class VBooleanCodeColumn extends VCodeColumn {
  /**
   * Constructs a report column description
   *
   * @param name The column label
   * @param help A help text to be displayed as tool tip
   * @param options The column options as bitmap
   * @param align The column alignment
   * @param groups The index of the column grouped by this one or -1
   * @param function An (optional) summation function
   */
  public VBooleanCodeColumn(String name,
			    String help,
			    int options,
			    int align,
			    int groups,
			    VCalculateColumn function,
			    int width,
			    VCellFormat format,
			    String[] names,
			    boolean[] codes) {
    super(name, help, options, align, groups, function, width, format, names);

    this.codes = codes;
    if (codes.length > 2) {
      throw new InconsistencyException();
    }
  }

  /**
   * Get the index of the value.
   */
  public int getIndex(Object object) {
    return ((Boolean)object).booleanValue() == codes[0] ? 0 : 1;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final boolean[]	codes;	// array of internal representations
}
