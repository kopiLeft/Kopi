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

import com.kopiright.xkopi.lib.type.Week;

@SuppressWarnings("serial")
public class VWeekDimension extends VDimension {
  
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new week chart column.
   * @param ident The column identifier.
   * @param format The dimension format.
   */
  public VWeekDimension(String ident, VColumnFormat format) {
    super(ident, format);
  }

  /**
   * @Override
   */
  public String toString(Object value) {
    if (value == null) {
      return CConstants.EMPTY_TEXT;
    } else if (value instanceof Week) {
      return ((Week) value).toString();
    } else {
      return value.toString();
    }
  }
}
