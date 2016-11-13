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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries;

import javax.json.JsonArray;

import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;

/**
 * Common implementation of possible data series.
 */
public abstract class AbstractDataSeries implements DataSeries {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public AbstractDataSeries() {}

  public AbstractDataSeries(String name) {
    this.name = name;
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public JsonArray getDimensions() {
    return null;
  }

  @Override
  public JsonArray getRange() {
    return null;
  }

  @Override
  public void addData(int value) throws DataMismatchException {
    throw new DataMismatchException("Not enough data for a " + getType().name());
  }

  @Override
  public void addData(double value) throws DataMismatchException {
    throw new DataMismatchException("Not enough data for a " + getType().name());
  }

  @Override
  public void addData(Object value1, int value2) throws DataMismatchException {
    if (getType().equals(DataSeriesType.RANGE)) {
      throw new DataMismatchException("Not enough data for a " + getType().name());
    }
    
    if (getType().equals(DataSeriesType.SINGLE)){
      throw new DataMismatchException("Too much data for a "+ getType().name());
    }
  }

  @Override
  public void addData(Object value1, double value2) throws DataMismatchException {
    if (getType().equals(DataSeriesType.RANGE)) {
      throw new DataMismatchException("Not enough data for a " + getType().name());
    }
    
    if (getType().equals(DataSeriesType.SINGLE)) {
      throw new DataMismatchException("Too much data for a " + getType().name());
    }
  }

  @Override
  public void addData(Object value1, int value2, int value3)
    throws DataMismatchException
  {
    throw new DataMismatchException("Too much data for a " + getType().name());
  }

  @Override
  public void addData(Object value1, double value2, double value3)
    throws DataMismatchException
  {
    throw new DataMismatchException("Too much data for a " + getType().name());
  }

  @Override
  public void addData(Object value1, int value2, double value3)
    throws DataMismatchException
  {
    throw new DataMismatchException("Too much data for a "+ getType().name());
  }

  @Override
  public void addData(Object value1, double value2, int value3)
    throws DataMismatchException
  {
    throw new DataMismatchException("Too much data for a "+ getType().name());
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private String                        name;
}
