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

import com.kopiright.vkopi.lib.visual.VColor;

@SuppressWarnings("serial")
public class VIntegerMeasure extends VMeasure {
  
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new integer measure.
   * @param ident The measure identifier.
   * @param color The color to be used for the measure.
   */
  public VIntegerMeasure(String ident, VColor color) {
    super(ident, color);
  }

  /**
   * @Override
   */
  protected Number toNumber(Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof Integer) {
      return (Integer)value;
    } else if (value instanceof Number) {
      return new Integer(((Number)value).intValue());
    } else {
      return null;
    }
  }

}
