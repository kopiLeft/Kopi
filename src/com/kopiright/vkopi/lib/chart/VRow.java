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

/**
 * The chart model row. This will contain row values including
 * dimension and measure values.
 */
@SuppressWarnings("serial")
public class VRow implements Serializable {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new chart row from possible values.
   * @param dimension The dimension value.
   * @param measures The measures values.
   */
  public VRow(Object[] dimensions, Object[] measures) {
    this.dimensions = dimensions;
    this.measures = measures;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  
  /**
   * Returns the dimensions value of the given index.
   * @param index The desired index.
   * @return The measure value of the given index.
   */
  public Object getDimensionAt(int index) {
    return dimensions[index];
  }
  /**
   * Returns the measure value of the given index.
   * @param index The desired index.
   * @return The measure value of the given index.
   */
  public Object getMeasureAt(int index) {
    return measures[index];
  }
  
  /**
   * Sets the dimension value of the given index.
   * @param index the desired index.
   * @param dimension The dimension value to be set.
   */
  public void setDimensionAt(int index, Object dimension) {
    dimensions[index] = dimension;
  }
  
  /**
   * Sets the measure value of the given index.
   * @param index the desired index.
   * @param measure The measure value to be set.
   */
  public void setMeasureAt(int index, Object measure) {
    measures[index] = measure;
  }
  
  /**
   * Returns the measures count.
   * @return The measures count.
   */
  public int getDimensionsCount() {
    return dimensions.length;
  }
  
  /**
   * Returns the measures count.
   * @return The measures count.
   */
  public int getMeasuresCount() {
    return measures.length;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private Object[]					dimensions;
  private Object[]					measures;
}
