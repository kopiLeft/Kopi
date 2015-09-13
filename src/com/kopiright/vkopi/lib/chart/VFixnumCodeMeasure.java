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

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.visual.VColor;
import com.kopiright.xkopi.lib.type.NotNullFixed;

@SuppressWarnings("serial")
public class VFixnumCodeMeasure extends VCodeMeasure {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  
  /**
   * Creates a new code decimal measure.
   * @param ident The measure identifier.
   * @param color The measure color.
   * @param type The measure type.
   * @param source The localization source.
   * @param idents The code identifiers.
   * @param codes The decimal codes.
   */
  public VFixnumCodeMeasure(String ident,
                            VColor color,
                            String type,
                            String source,
                            String[] idents,
                            NotNullFixed[] codes)
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
      if (value.equals(codes[i])) {
	return i;
      }
    }

    throw new InconsistencyException("Object not found " + value + ", " + getIdent());
  }
  
  /**
   * @Override
   */
  protected Number toNumber(Object value) {
    for (int i = 0; i < codes.length; i++) {
      if (value.equals(codes[i])) {
	return codes[i];
      }
    }
    
    return null;
  }
  
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final NotNullFixed[]  				codes;  // array of internal representations
}
