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
 * $Id: VCodeColumn.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.report;

public abstract class VCodeColumn extends VReportColumn {
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
  public VCodeColumn(String name,
		     String help,
		     int options,
		     int align,
		     int groups,
		     VCalculateColumn function,
		     int width,
		     VCellFormat format,
		     String[] names) {
    super(name,
	  help,
	  options,
	  align,
	  groups,
	  function,
	  width,
	  1,
	  format);

    for (int i = 0; i < names.length; i++) {
      this.width = Math.max(this.width, names[i].length());
    }

    this.names = names;
  }

  /**
   * Compares two objects.
   *
   * @param	o1	the first operand of the comparison
   * @param	o2	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public int compareTo(Object o1, Object o2) {
    if (o1 == null || o2 == null) {
      return o1 == null ? -1 : (o2 == null ? 0 : 1);
    } else {
      int	v1 = ((Integer)o1).intValue();
      int	v2 = ((Integer)o2).intValue();

      return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
    }
  }

  public String format(Object o) {
    return isFolded() || o == null ?
      "" :
      getFormat() != null ? getFormat().format(o) : names[getIndex(o)];
  }

  /**
   * Get the index of the value.
   */
  public abstract int getIndex(Object object);

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  protected String[]		names;	// array of external representations
}
