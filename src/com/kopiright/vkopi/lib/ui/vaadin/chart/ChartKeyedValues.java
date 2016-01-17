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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import at.downdrown.vaadinaddons.highchartsapi.model.series.HighChartsSeries;

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
    /*
     * The map structure is the following :
     * 
     * 1- The first sorted map key will contain the dimension value. The sorted map
     * is used to sort the entered dimension directly when traversing the map entries.
     * 
     * 2- The second map is the measure/value map per dimension in a way that we get
     * only one value for a dimension/measure couple.
     */
    // ====>>>>>>>>>>>>>>> <dimesnion, Map<measure, value>>
    valuesMap = new TreeMap<Comparable, Map<String, Object>>();
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
  public void addValue(Comparable dimension, String measure, Number value) {
    System.out.println("DIM = " + dimension + "; MES = " + measure + "; VAL = " + value);
    // The measure is not mapped yet so we add it
    if (!valuesMap.containsKey(dimension)) {
      valuesMap.put(dimension, new HashMap<String, Object>());
    }
    // now map the measure and its value.
    // !!! warning measure's old value are always overwritten
    // we always take the last value of the measure
    valuesMap.get(dimension).put(measure, value);
  }
  
  /**
   * Returns the X Axis labels to be used in high charts API.
   * These categories are in fact the distinct sorted dimension values.
   * @return The X Axis labels in the final chart.
   */
  public List<Comparable> getXAxisCategories() {
//!!!DEBUG graf 20160117 - REMOVEME
    for (Comparable x : valuesMap.keySet()) {
      System.out.println("x-axis : " + x.toString());
    }
//!!!DEBUG graf 20160117 - REMOVEME
    return new LinkedList<Comparable>(valuesMap.keySet());
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
    Map<String, List<Object>>           measuresMap;        
    
    chartsSeries = new LinkedList<HighChartsSeries>();
    measuresMap = createMeasuresMap();
    for (Map.Entry<String, List<Object>> measure : measuresMap.entrySet()) {
//!!!DEBUG graf 20160117 - REMOVEME
      for (Object v : measure.getValue()) {
        System.out.println("series : " + measure.getKey() + " => " + v);
      }
//!!!DEBUG graf 20160117 - REMOVEME
      //valuesMap.va
      chartsSeries.add(type.createChartSeries(measure.getKey(), measure.getValue()));
    }

    return chartsSeries;
  }
  
  /**
   * Creates the measures map. In this step, we will loop all over
   * dimensions measures map and we will collect for each distinct measure
   * all its values. 
   * @return The measures and its value list map.
   */
  protected Map<String, List<Object>> createMeasuresMap() {
    Map<String, List<Object>>           measuresMap;
    
    // the measures map having the measure key as a name
    // and the list of all its values already keyed.
    measuresMap = new HashMap<String, List<Object>>();
    // first get the collection of the dimensions/ measures map.
    for (Map<String, Object> measure : valuesMap.values()) {
      // now loop on each map and create the new map structure
      // we notice that entry.getKey() returns the measure name
      // and the entry.getValue() returns the measure value.
      for (Map.Entry<String, Object> entry : measure.entrySet()) {
        if (!measuresMap.containsKey(entry.getKey())) {
          measuresMap.put(entry.getKey(), new LinkedList<Object>());
        }
        
        measuresMap.get(entry.getKey()).add(entry.getValue());
      }
    }
    
    return measuresMap;
  }
  
  //-------------------------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------------------------
  
  private final SortedMap<Comparable, Map<String, Object>>          valuesMap;
}
