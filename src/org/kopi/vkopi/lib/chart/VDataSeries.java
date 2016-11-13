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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A chart data series includes a dimension and its measures.
 */
@SuppressWarnings("serial")
public class VDataSeries implements Serializable {

  //---------------------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------------------
  
  public VDataSeries(VDimensionData dimensionData) {
    this.dimensions = dimensionData;
    this.measures = new LinkedList<VMeasureData>();
  }

  //---------------------------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------------------------
  
  /**
   * @return the measures
   */
  public VDimensionData getDimension() {
    return dimensions;
  }
  
  /**
   * @return the measures
   */
  public VMeasureData[] getMeasures() {
    return measures.toArray(new VMeasureData[measures.size()]);
  }

  //---------------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------------
  
  /*package*/ final VDimensionData			dimensions;
  /*package*/ final List<VMeasureData>			measures;
}
