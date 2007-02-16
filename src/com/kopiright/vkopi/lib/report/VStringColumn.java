/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

public class VStringColumn extends VReportColumn {
  /**
   * Constructs a report column description
   *
   * @param     ident           The column ident
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VStringColumn(String ident,
		       int options,
		       int align,
		       int groups,
		       VCalculateColumn function,
		       int width,
		       int height,
		       VCellFormat format)
  {
    super(ident,
	  options,
	  align,
	  groups,
	  function,
	  width,
	  height,
	  format);
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
    return ((String)o1).compareTo((String)o2);
  }

  public void formatColumn(PExport exporter, int index) {
    exporter.formatStringColumn(this, index);
  }
}
