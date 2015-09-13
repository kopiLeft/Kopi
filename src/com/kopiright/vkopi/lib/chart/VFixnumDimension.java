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

package com.kopiright.vkopi.lib.chart;

import com.kopiright.xkopi.lib.type.NotNullFixed;

@SuppressWarnings("serial")
public class VFixnumDimension extends VDimension {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new fixed chart column.
   * @param ident The column identifier.
   * @param format The dimension format ?
   * @param maxScale The max scale to be used for the fixed value.
   * @param exactScale Should we use the max scale column for fixed values having a minor scale ?
   */
  public VFixnumDimension(String ident,
                          VColumnFormat format,
                          int maxScale,
                          boolean exactScale)
  {
    super(ident, format);
    this.maxScale = maxScale;
    this.exactScale = exactScale;
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  
  /**
   * @Override
   */
  public String toString(Object value) {
    if (value == null) {
      return VConstants.EMPTY_TEXT;
    } else if (value instanceof Integer) {
      return value.toString();
    } else if (value instanceof NotNullFixed) {
      return (((NotNullFixed)value).getScale() > maxScale || exactScale) ?
        ((NotNullFixed)value).setScale(maxScale).toString() : value.toString();
    } else {
      return value.toString();
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final int            				maxScale;
  private final boolean					exactScale;
}
