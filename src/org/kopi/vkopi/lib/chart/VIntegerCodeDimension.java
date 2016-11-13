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

@SuppressWarnings("serial")
public class VIntegerCodeDimension extends VCodeDimension {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new integer code chart column.
   * @param ident The column identifier.
   * @param format The dimension format
   * @param type the column type.
   * @param source The column localization source.
   * @param idents The columns labels.
   * @param codes The column codes.
   */
  public VIntegerCodeDimension(String ident,
                               VColumnFormat format,
                               String type,
                               String source,
                               String[] idents,
                               int[] codes)
  {
    super(ident, format, type, source, idents);
    this.codes = codes;
    fastIndex = codes[0];
    for (int i = 1; i < codes.length; i++) {
      if (codes[i] != fastIndex + i) {
	fastIndex = -1;
	break;
      }
    }
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  
  /**
   * @Override
   */
  protected int getIndex(Object value) {
    if (fastIndex != -1) {
      return ((Integer)value).intValue() - fastIndex;
    }

    for (int i = 0; i < codes.length; i++) {
      if (((Integer)value).intValue() == codes[i]) {
	return i;
      }
    }

    return -1;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int			fastIndex = -1;		// if array = {fastIndex, fastIndex + 1, ...}
  private final int[]		codes;			// array of internal representations
}
