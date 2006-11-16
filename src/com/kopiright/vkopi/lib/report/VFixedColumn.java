/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import com.kopiright.xkopi.lib.type.NotNullFixed;

public class VFixedColumn extends VReportColumn {
  /**
   * Constructs a report column description
   *
   * @param     ident           The column identifier
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VFixedColumn(final String ident,
		      final int options,
		      final int align,
		      final int groups,
		      final VCalculateColumn function,
		      final int width,
		      final int scale,
		      final VCellFormat format)
  {
    super(ident,
	  options,
	  align,
	  groups,
	  function,
	  width,
	  1,
	  format != null ? format : new VFixedFormat(scale));
  }

  /**
   * Compare two objects.
   *
   * @param	o1	the first operand of the comparison
   * @param	o2	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public int compareTo(Object o1, Object o2) {
    return ((NotNullFixed)o1).compareTo((NotNullFixed)o2);
  }

  /**
   * Returns the width of cells in this column in characters
   */
  public double getPrintedWidth() {
    return getWidth() * 0.7;
  }

  private static class VFixedFormat extends VCellFormat {
    VFixedFormat(int scale) {
      this.scale = scale;
    }
    public String format(Object value) {
      return value == null ? "" : ((NotNullFixed)value).setScale(scale).toString();
    }
    int	scale;
  }

  public void formatColumn(PExport exporter, int index) {
    exporter.formatFixedColumn(this, index);
  }
}
