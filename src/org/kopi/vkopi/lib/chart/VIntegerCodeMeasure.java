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

import org.kopi.vkopi.lib.visual.VColor;

@SuppressWarnings("serial")
public class VIntegerCodeMeasure extends VCodeMeasure {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  
  /**
   * Creates a new Integer code measure.
   * @param ident The measure identifier.
   * @param color The color to be used.
   * @param type The localization type.
   * @param source The localization source.
   * @param idents The codes identifiers.
   * @param codes The integer codes.
   */
  public VIntegerCodeMeasure(String ident,
                             VColor color,
                             String type,
                             String source,
                             String[] idents,
                             int[] codes)
  {
    super(ident, color, type, source, idents);
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
      if (((Integer)value).intValue() == codes[i]) {
	return i;
      }
    }

    return -1;
  }

  /**
   * @Override
   */
  protected Number toNumber(Object value) {
    for (int i = 0; i < codes.length; i++) {
      if (((Integer)value).intValue() == codes[i]) {
	return new Integer(codes[i]);
      }
    }
    
    return null;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  
  private final int[]		codes;			// array of internal representations
}
