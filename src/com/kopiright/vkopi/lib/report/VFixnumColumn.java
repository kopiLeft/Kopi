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
import com.kopiright.vkopi.lib.form.VFixnumField;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.xkopi.lib.type.NotNullFixed;

public class VFixnumColumn extends VReportColumn {
  /**
   * Constructs a report column description
   *
   * @param     ident           The column identifier
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VFixnumColumn(final String ident,
                       final int options,
                       final int align,
                       final int groups,
                       final VCalculateColumn function,
                       final int digits,
                       final int maxScale,
                       final VCellFormat format)
  {
    super(ident,
	  options,
	  align,
	  groups,
	  function,
	  VFixnumField.computeWidth(digits, maxScale, null, null),
	  1,
	  format != null ? format : new VFixedFormat(maxScale));
    this.maxScale = maxScale;
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
    /*
     * sets all column values to the same scale
     */
    public VFixedFormat(int maxScale, boolean exactScale) {
      this.maxScale = maxScale;
      this.exactScale = exactScale;
    }

    public VFixedFormat(int maxScale) {
      this(maxScale, false);
    }

    public String format(Object value) {
      if (value == null) {
        return "";
      } else if (value instanceof Integer) {
        return value.toString();
      } else if (value instanceof NotNullFixed) {
        return (((NotNullFixed)value).getScale() > maxScale || exactScale) ? 
          ((NotNullFixed)value).setScale(maxScale).toString() :
          value.toString();
      } else {
        throw new InconsistencyException("bad type for " + value);
      }
    }

    private final boolean     exactScale;
    private final int         maxScale;
  }

  public void formatColumn(PExport exporter, int index) {
    exporter.formatFixedColumn(this, index);
  }

  public int getMaxScale() {
    return maxScale;
  }

  /*
   * Sets display scale to maxScale
   * all values will be set to the same scale
   */
  public void setDisplayScale(int scale) {
    setFormat(new VFixedFormat(scale, true));
    this.maxScale = scale;
  }

  /*
   * Sets maxScale
   * all values with scale superior than maxScale will have 
   * maxScale as scale, and the other values will keep their scale.
   */
  public void setMaxScale(int scale) {
    setFormat(new VFixedFormat(scale));
    this.maxScale = scale;
  }
  
  public void setFormula(String formula) {
    this.formula = formula;
  }
  
  public String getFormula() {
    return formula;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String         formula;
  private int            maxScale;
}
