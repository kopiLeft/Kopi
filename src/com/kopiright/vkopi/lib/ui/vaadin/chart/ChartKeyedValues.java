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

package com.kopiright.vkopi.lib.ui.vaadin.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.kopi.vaadin.highchartsapi.model.series.HighChartsSeries;

/**
 * A data structure that stores zero, one or many chart values, where each value
 * is associated with two keys (a 'dimension' key and a 'measure' key).
 */
public class ChartKeyedValues {
  
  //-------------------------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------------------------
  
  /**
   * Create a new {@link ChartKeyedValues} instance.
   */
  protected ChartKeyedValues() {
    measures = new ArrayList<String>();
    dimensions = new ArrayList<Comparable<?>>();
    values = new ArrayList<KeyedValues>();
  }
  
  //-------------------------------------------------------------------
  // IMPLEMENTATIONS
  //-------------------------------------------------------------------
  
  /**
   * Maps a given number value to a dimension and a measure keys.
   * 
   * @param dimension The dimension key (<code>null</code> not permitted).
   * @param measure  The measure key (<code>null</code> not permitted).
   * @param value the value (<code>null</code> permitted).
   */
  public void addValue(Comparable<?> dimension, String measure, Number value) {
    KeyedValues                 keyedValue;
    int                         measureIndex;
    int                         dimensionIndex;
    
    measureIndex = getMeasureIndex(measure);
    // measure is already keyed.
    if (measureIndex >= 0) {
      keyedValue = values.get(measureIndex);
    } else {
      keyedValue = new KeyedValues();
      // map the measure value.
      measures.add(measure);
      // map the value to the given measure
      values.add(keyedValue);
    }
    // map the dimension with its corresponding value
    keyedValue.setValue(dimension, value);
    dimensionIndex = dimensions.indexOf(dimension);
    // the dimension is not mapped yet so we add it.
    if (dimensionIndex < 0) {
      dimensions.add(dimension);
    }
  }
  
  /**
   * Returns the X Axis labels to be used in high charts API.
   * These categories are in fact the distinct sorted dimension values.
   * @return The X Axis labels in the final chart.
   */
  public List<Comparable<?>> getXAxisCategories() {
    return Collections.unmodifiableList(dimensions);
  }
  
  /**
   * Returns the measure list.
   * @return The measures list.
   */
  public List<String> getMeasures() {
    return Collections.unmodifiableList(measures);
  }
  
  /**
   * Creates the list of charts series to be sent to client side an drawn in the chart.
   * First we will collect values by measures so we have finally one measure with all its values.
   * This constrain is applied be the high charts API as it does not have its own keyed values.
   * @param type The chart type.
   * @return The list of the charts series to be drawn.
   */
  public List<HighChartsSeries> createChartsSeries(DAbstractChartType type) {
    List<HighChartsSeries>              chartsSeries;
    
    chartsSeries = new ArrayList<HighChartsSeries>();
    for (String measure : getMeasures()) {
      int               measureIndex;
      KeyedValues       value;
      
      measureIndex = getMeasureIndex(measure);
      value = values.get(measureIndex);
      chartsSeries.add(type.createChartSeries(measure, value.getValues()));
    }

    return Collections.unmodifiableList(chartsSeries);
  }
  
  /**
   * Returns the measure index in the chart value.
   * @param measure The measure name.
   * @return The measure index.
   */
  public int getMeasureIndex(String measure) {
    if (measure == null) {
      throw new IllegalArgumentException("Measure cannot be null");
    }

    return measures.indexOf(measure);
  }
  
  //-------------------------------------------------------------------
  // INNER CLASSES
  //-------------------------------------------------------------------
  
  /**
   * An ordered list of (key, value) items. 
   */
  /*package*/ static class KeyedValues {
    
    //-------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------
    
    /**
     * Creates a new collection (initially empty).
     */
    public KeyedValues() {
      keys = new ArrayList<Comparable<?>>();
      values = new ArrayList<Object>();
      indexMap = new HashMap<Comparable<?>, Integer>();
    }
    
    //-------------------------------------------------------
    // IMPLEMENTATIONS
    //-------------------------------------------------------
    
    /**
     * Updates an existing value, or adds a new value to the collection.
     *
     * @param key  the key (<code>null</code> not permitted).
     * @param value  the value (<code>null</code> permitted).
     */
    public void setValue(Comparable<?> key, Number value) {
      int       keyIndex = getIndex(key);
      
      if (keyIndex >= 0) {
        keys.set(keyIndex, key);
        values.set(keyIndex, value);
      } else {
        keys.add(key);
        values.add(value);
        indexMap.put(key, new Integer(keys.size() - 1));
      }
    }

    /**
     * Returns the index for a given key.
     *
     * @param key  the key (<code>null</code> not permitted).
     * @return The index, or {@code -1} if the key is not recognized.
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     */
    public int getIndex(Comparable<?> key) {
      if (key == null) {
        throw new IllegalArgumentException("key must not be null");
      }
      
      Integer   index = indexMap.get(key);
      
      if (index == null) {
        return -1;  // key not found
      }
      
      return index.intValue();
    }
    
    /**
     * Returns the number of keys contained in this map.
     * @return The number of keys contained in this map.
     */
    public int getCount() {
      return indexMap.size();
    }

    /**
     * Returns the keys for the values in the collection.
     *
     * @return The keys (never <code>null</code>).
     */
    public List<Comparable<?>> getKeys() {
      return Collections.unmodifiableList(keys);
    }
    
    /**
     * Returns the list of values encapsulated by this keyed value.
     * @return The list of values encapsulated by this keyed value.
     */
    public List<Object> getValues() {
      return Collections.unmodifiableList(values);
    }
    
    /**
     * Returns the value of the given item.
     * @param index The item index.
     * @return The mapped value.
     */
    public Object getValue(int index) {
      return values.get(index);
    }
    
    /**
     * Returns the key of the given index.
     * @param index The key index.
     * @return The key value.
     */
    public Comparable<?> getKey(int index) {
      return keys.get(index);
    }
    
    //-------------------------------------------------------
    // DATA MEMBERS
    //-------------------------------------------------------
    
    /** Storage for the keys. */
    private List<Comparable<?>>                 keys;

    /** Storage for the values. */
    private List<Object>                        values;

    /**
     * Contains (key, Integer) mappings, where the Integer is the index for
     * the key in the list.
     */
    private HashMap<Comparable<?>, Integer>     indexMap;
  }
  
  //-------------------------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------------------------
  
  // The measures keys
  private List<String>                  measures;

  // The dimensions keys
  private List<Comparable<?>>           dimensions;

  // The measure data by a given dimension
  private List<KeyedValues>             values;
}
