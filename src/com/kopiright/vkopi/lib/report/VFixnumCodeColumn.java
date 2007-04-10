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

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.type.NotNullFixed;

public class VFixnumCodeColumn extends VCodeColumn {
  /**
   * Constructs a report column description
   *
   * @param     ident           The column identifier
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VFixnumCodeColumn(String ident,
                           String type,
                           String source,
                           int options,
                           int align,
                           int groups,
                           VCalculateColumn function,
                           int width,
                           VCellFormat format,
                           String[] names,
                           NotNullFixed[] codes)
  {
    super(ident, type, source, options, align, groups, function, width, format, names);

    this.codes = codes;
  }

  /**
   * Get the index of the value.
   */
  public int getIndex(Object object) {
    for (int i = 0; i < codes.length; i++) {
      if (object.equals(codes[i])) {
	return i;
      }
    }

    throw new InconsistencyException(">>>>" + object);
  }

  /**
   * Returns the width of cells in this column in characters
   */
  public double getPrintedWidth() {
    return getWidth() * 0.7;
  }

  public void formatColumn(PExport exporter, int index) {
    exporter.formatFixedColumn(this, index);
  }
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final NotNullFixed[]  codes;  // array of internal representations
}
