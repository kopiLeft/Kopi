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

import com.kopiright.vkopi.lib.l10n.FieldLocalizer;
import com.kopiright.vkopi.lib.l10n.TypeLocalizer;

public abstract class VCodeColumn extends VReportColumn {
  /**
   * Constructs a report column description
   *
   * @param     ident           The column identifier
   * @param     options         The column options as bitmap
   * @param     align           The column alignment
   * @param     groups          The index of the column grouped by this one or -1
   * @param     function        An (optional) summation function
   */
  public VCodeColumn(String ident,
                     String type,
                     String source,
		     int options,
		     int align,
		     int groups,
		     VCalculateColumn function,
		     int width,
		     VCellFormat format,
		     String[] idents)
  {
    super(ident,
	  options,
	  align,
	  groups,
	  function,
	  width,
	  1,
	  format);

    this.type = type;
    this.source = source;
    this.idents = idents;
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

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  protected void localize(FieldLocalizer parent) {
    TypeLocalizer       loc;
    
    System.err.println("... source: " + source + " type: " + type);
    
    loc = parent.getManager().getTypeLocalizer(source, type);
    names = new String[idents.length];
    for (int i = 0; i < names.length; i++) {
      names[i] = loc.getCodeLabel(idents[i]);
      this.width = Math.max(this.width, names[i].length());
    }
  }


  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String                type;
  private String                source;
  private String[]              idents;
  protected String[]            names;  // array of external representations
}
