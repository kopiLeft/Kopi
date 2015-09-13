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

@SuppressWarnings("serial")
public class VBooleanDimension extends VDimension {
  
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new boolean chart column.
   * @param ident The column identifier.
   * @param isDimension Is it a dimension column ?
   */
  public VBooleanDimension(String ident, VColumnFormat format) {
    super(ident, format);
  }
  
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  
  /**
   * @Override
   */
  public String toString(Object value) {
    return value == null ? "" : Boolean.TRUE.equals(value) ? trueRep : falseRep;
  }
  
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static String		trueRep = com.kopiright.vkopi.lib.visual.VlibProperties.getString("true");
  private static String 	falseRep = com.kopiright.vkopi.lib.visual.VlibProperties.getString("false");
}
