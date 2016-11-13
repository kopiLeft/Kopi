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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.Orientation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Annotation;

/**
 * A couple of dimensions and measures data series.
 */
public class CoupleOfData extends AbstractDataSeries {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public CoupleOfData() {
    numbers = new HashMap <Integer, Number>();
    objects = new HashMap <Integer, Object>();
  }

  public CoupleOfData(String name){
    super(name);
    numbers = new HashMap <Integer, Number>();
    objects = new HashMap <Integer, Object>();
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  @Override
  public Collection<Annotation> getAnnotations(Orientation orientation) {
    List<Annotation>            annotations;
    
    annotations = new ArrayList<Annotation>();
    for (int index = 0; index < objects.size(); index++) {
      Object            x;
      Object            y;
      String            text;
      String            xanchor;
      String            yanchor;
      
      if (orientation != null && orientation.equals(Orientation.HORIZONTAL)) {
        x = numbers.get(index);
        y = objects.get(index);
        text = x.toString();
        xanchor = "left";
        yanchor = "center";
      } else {
        x = objects.get(index);
        y = numbers.get(index);
        text = y.toString();
        xanchor = "center";
        yanchor = "bottom";
      }
      
      annotations.add(new Annotation(x, y, text, xanchor, yanchor, false));
    }
    
    return annotations;
  }

  @Override
  public JsonArray getDimensions() {
    JsonArrayBuilder            builder;
    
    builder = Json.createArrayBuilder();
    for (int index = 0; index < objects.size(); index++) {
      if (objects.get(index) instanceof String) {
        builder.add((String)objects.get(index));
      } else if (objects.get(index) instanceof Number) {
        builder.add(String.valueOf(((Number)objects.get(index)).doubleValue()));
      } else {
        builder.add(((Number)objects.get(index)).toString());
      }
    }

    return builder.build();
  }

  @Override
  public JsonArray getMeasures() {
    JsonArrayBuilder            builder;
    
    builder = Json.createArrayBuilder();
    for (int index = 0; index < numbers.size(); index++) {
      builder.add(numbers.get(index).doubleValue());
    }

    return builder.build();
  }
  
  @Override
  public void addData(Object value1, int value2) {
    int                 index;
    
    index = objects.size();
    objects.put(index, value1);
    numbers.put(index, value2);
  }

  @Override
  public void addData(Object value1, double value2) {
    int                 index;
    
    index = objects.size();
    objects.put(index, value1);
    numbers.put(index, value2);
  }
  
  @Override
  public DataSeriesType getType() {
    return DataSeriesType.COUPLE;
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private Map<Integer, Number>          numbers;
  private Map<Integer, Object>          objects;
}
