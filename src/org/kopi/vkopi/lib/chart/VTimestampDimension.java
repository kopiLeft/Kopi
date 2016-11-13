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

import org.kopi.xkopi.lib.type.Timestamp;

@SuppressWarnings("serial")
public class VTimestampDimension extends VDimension {
  
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new time stamp chart column.
   * @param ident The column identifier.
   * @param format The time stamp format to be used to format the time stamp value.
   */
  public VTimestampDimension(String ident, VColumnFormat format) {
    super(ident, format);
  }

  /**
   * @Override
   */
  public String toString(Object value) {
    if (value == null) {
      return CConstants.EMPTY_TEXT;
    } else if (value instanceof Timestamp) {
      return ((Timestamp) value).toString();
    } else {
      return value.toString();
    }
  }
}
