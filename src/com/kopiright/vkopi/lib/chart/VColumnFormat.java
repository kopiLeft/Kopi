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

import java.io.Serializable;

@SuppressWarnings("serial")
public class VColumnFormat implements Serializable {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new column format instance
   */
  public VColumnFormat() {}

  //---------------------------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------------------------
  
  /**
   * Returns the formatted value of the object.
   * @param value The object to be formatted.
   * @return The formatted value.
   */
  public String format(Object value) {
    return value == null ? VConstants.EMPTY_TEXT : value.toString();
  }
}
