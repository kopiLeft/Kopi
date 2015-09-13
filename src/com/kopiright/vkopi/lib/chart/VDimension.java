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

/**
 * A chart dimension represented by its name and its key.
 */
@SuppressWarnings("serial")
public abstract class VDimension extends VColumn {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new dimension column.
   * @param ident The dimension identifier.
   * @param format The dimension format.
   */
  protected VDimension(String ident, VColumnFormat format) {
    super(ident);
    this.format = format;
  }

  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  
  /**
   * Formats the dimension value.
   * @param value The value to be formatted.
   * @return The string representation of the object value.
   */
  protected abstract String toString(Object value);

  //---------------------------------------------------------------------
  // UTILS
  //---------------------------------------------------------------------
  
  /**
   * Returns the String representation of the dimension value.
   * @param value The dimension value.
   * @return The formatted value.
   */
  public String format(Object value) {
    return format != null ? format.format(value) : toString(value);
  }

  //---------------------------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------------------------
  
  /**
   * Returns the dimension format.
   * @return The dimension format.
   */
  public VColumnFormat getFormat() {
    return format;
  }
  
  /**
   * Sets the dimension format.
   * @param format The dimension format.
   */
  public void setFormat(VColumnFormat format) {
    this.format = format;
  }
  
  //---------------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------------
  
  private VColumnFormat				format;
}
