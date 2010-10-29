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

package com.kopiright.vkopi.lib.report;

import com.kopiright.util.base.InconsistencyException;

public class VIntegerCodeColumn extends VCodeColumn {
  /**
   * Constructs a report column description (!!!! TO BE REMOVED)
   *
   * @param     ident           The column identifier
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VIntegerCodeColumn(String ident,
                            String type,
                            String source,
			    int options,
			    int align,
			    int groups,
			    VCalculateColumn function,
			    int width,
			    VCellFormat format,
			    String[] names,
			    Integer[] codes)
  {
    super(ident, type, source, options, align, groups, function, width, format, names);
    
    this.codes = new int[codes.length];
    throw new InconsistencyException();
    //for (int i = 0; i < codes.length; i++) // !!! what about null value
    //    this.codes[i] = codes[i].intValue();
  }

  /**
   * Constructs a report column description
   *
   * @param     ident           The column identifier
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VIntegerCodeColumn(String ident,
                            String type,
                            String source,
			    int options,
			    int align,
			    int groups,
			    VCalculateColumn function,
			    int width,
			    VCellFormat format,
			    String[] names,
			    int[] codes)
  {
    super(ident, type, source, options, align, groups, function, width, format, names);

    this.codes = codes;
    fastIndex = codes[0];
    for (int i = 1; i < codes.length; i++) {
      if (codes[i] != fastIndex + i) {
	fastIndex = -1;
	break;
      }
    }
  }

  /**
   * Get the index of the value.
   */
  public int getIndex(Object object) {
    if (fastIndex != -1) {
      return ((Integer)object).intValue() - fastIndex;
    }

    for (int i = 0; i < codes.length; i++) {
      if (((Integer)object).intValue() == codes[i]) {
	return i;
      }
    }

    return -1;
  }

  /**
   * Returns the width of cells in this column in characters
   */
  public double getPrintedWidth() {
    return getWidth() * 0.7;
  }

  public void formatColumn(PExport exporter, int index) {
    exporter.formatStringColumn(this, index);
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
    int	v1 = ((Integer)o1).intValue();
    int	v2 = ((Integer)o2).intValue();

    return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int			fastIndex = -1;		// if array = {fastIndex, fastIndex + 1, ...}
  private final int[]		codes;			// array of internal representations
}
