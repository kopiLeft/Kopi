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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.Orientation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Annotation;

/**
 * A single data model representation.
 */
public class SingleData extends AbstractDataSeries {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public SingleData() {
    values = new HashMap<Integer, Number>();
  }

  public SingleData(String name) {
    super(name);
    values = new HashMap<Integer, Number>();
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Override
  public Collection<Annotation> getAnnotations(Orientation orientation) {
    return Collections.emptyList();
  }

  @Override
  public JsonArray  getMeasures() {
    JsonArrayBuilder            builder;
    
    builder = Json.createArrayBuilder();
    for (int index = 0; index< values.size();index++){
      builder.add(values.get(index).doubleValue());
    }

    return builder.build();
  }
  
  @Override
  public void addData(int value) {
    int                 index;
    index = values.size();
    values.put(index, value);
  }

  @Override
  public void addData(double value) {
    int                 index;
    
    index = values.size();
    values.put(index, value);
  }
  
  @Override
  public DataSeriesType getType() {
    return DataSeriesType.SINGLE;
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private Map<Integer, Number>                  values;
}
