/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.chart;

import org.kopi.util.base.InconsistencyException;
import org.kopi.xkopi.lib.type.NotNullFixed;

@SuppressWarnings("serial")
public class VFixnumCodeDimension extends VCodeDimension {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  
  /**
   * Creates a new fixed code chart column.
   * @param ident The column identifier.
   * @param format Is it a dimension format
   * @param type The column type.
   * @param source The column localization source.
   * @param idents The column labels.
   * @param codes The column codes.
   */
  public VFixnumCodeDimension(String ident,
                           boolean isDimension,
                           VColumnFormat format,
                           String type,
                           String source,
                           String[] idents,
                           NotNullFixed[] codes)
  {
    super(ident, format, type, source, idents);
    this.codes = codes;
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  
  /**
   * @Override
   */
  protected int getIndex(Object value) {
    for (int i = 0; i < codes.length; i++) {
      if (value.equals(codes[i])) {
	return i;
      }
    }

    throw new InconsistencyException("Object not found " + value + ", " + getIdent());
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final NotNullFixed[]  				codes;  // array of internal representations
}
