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

import com.kopiright.vkopi.lib.util.Message;

public class VSeparatorColumn extends VReportColumn {
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
  public VSeparatorColumn(String name,
			String help,
			int options,
			int align,
			int groups,
			VCalculateColumn function,
			int width,
			VCellFormat format) {
    super("", Message.getMessage("separator"), 0, 0, 0, null, 1, 1, null);
  }

  /**
   * No text here
   */
  public String format(Object o) {
    return "";
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
    return 0;
  }

  /**
   * Returns the visibility of the column
   */
  public boolean isFolded() {
    return true;
  }
}
