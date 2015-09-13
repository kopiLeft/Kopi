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
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.NotNullFixed;

@SuppressWarnings("serial")
public class VFixnumMeasure extends VMeasure {
  
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new decimal measure.
   * @param ident The measure identifier.
   * @param color The measure color.
   * @param maxScale The max scale to be used.
   */
  public VFixnumMeasure(String ident, VColor color, int maxScale) {
    super(ident, color);
    this.maxScale = maxScale;
  }

  /**
   * @Override
   */
  protected Number toNumber(Object value) {
    if (value == null) {
      return null;
    } if (value instanceof Fixed) {
      return (Fixed)value;
    } else if (value instanceof Number) {
      return new NotNullFixed(((Number)value).longValue(), maxScale);
    } else {
      return null;
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final int            				maxScale;
}
