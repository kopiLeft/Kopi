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

import java.util.Collection;

import javax.json.JsonArray;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.Orientation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Annotation;

/**
 * All things that should be done by a data series
 */
public interface DataSeries {
  
  /**
   * Returns the list of the layout annotations to be used.
   * @param orientation TODO
   * @return The list of the layout annotations to be used.
   */
  Collection<Annotation> getAnnotations(Orientation orientation);
  
  /**
   * @return an array of the dimensions of the data to be displayed.
   */
  JsonArray getDimensions();

  /**
   * @return an array of the measures of the data to be displayed.
   */
  JsonArray getMeasures();

  /**
   * @return an array of the range of the data to be displayed.
   */
  JsonArray getRange();

  /**
   * @return the name of the series 
   */
  String getName();

  /**
   * @return the name of the series 
   */
  DataSeriesType getType();

  /**
   * Sets the series name.
   * @param name
   */
  void setName(String name);

  /**
   * adds singular data of int.
   * @param value
   * @throws DataMismatchException 
   */
  void addData(int value) throws DataMismatchException;

  /**
   * adds singular data of double.
   * @param value
   * @throws DataMismatchException 
   */
  void addData(double value) throws DataMismatchException;

  /**
   * adds a couple of data with int measure.
   * @param value
   * @throws DataMismatchException 
   */
  void addData(Object value1, int value2) throws DataMismatchException;

  /**
   * adds a couple of data of double measure.
   * @param value
   * @throws DataMismatchException 
   */

  void addData(Object value1, double value2) throws DataMismatchException;

  /**
   * adds a range of data with int measures.
   * @param value
   * @throws DataMismatchException 
   */

  void addData(Object value1, int value2, int value3) throws DataMismatchException;

  /**
   * adds a range of data of double measures.
   * @param value
   * @throws DataMismatchException 
   */
  void addData(Object value1, double value2, double value3) throws DataMismatchException;

  /**
   * adds a range of data with int and double measures.
   * @param value
   * @throws DataMismatchException 
   */
  void addData(Object value1, int value2, double value3) throws DataMismatchException;

  /**
   * adds a range of data of double and int measures.
   * @param value
   * @throws DataMismatchException 
   */
  void addData(Object value1, double value2, int value3) throws DataMismatchException;
}
