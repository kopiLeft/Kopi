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

@SuppressWarnings("serial")
public class VBooleanCodeDimension extends VCodeDimension {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Creates a new boolean code column.
   * @param ident The column identifier.
   * @param format The column format.
   * @param type The column type.
   * @param source The column localization source.
   * @param idents The column displayed labels.
   * @param codes The boolean codes.
   */
  @SuppressWarnings("deprecation")
  public VBooleanCodeDimension(String ident,
                               VColumnFormat format,
                               String type,
                               String source,
                               String[] idents,
                               boolean[] codes)
  {
    super(ident, format, type, source, idents);
    this.codes = codes;
    if (codes.length > 2) {
      throw new InconsistencyException();
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * @Override
   */
  protected int getIndex(Object value) {
    return ((Boolean)value).booleanValue() == codes[0] ? 0 : 1;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final boolean[]				codes;	// array of internal representations
}
