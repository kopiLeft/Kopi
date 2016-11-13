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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.AbstractDataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.DataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.DataMode;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.HoverInfo;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.features.Orientation;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.line.Line;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker.Marker;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.DataMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.PlotlyException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions.TypeMismatchException;
import org.kopi.vkopi.lib.ui.vaadin.plotly.layout.Annotation;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A common implementation for chart data.
 */
public abstract class AbstractData implements ChartData {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public AbstractData(String name, DataSeries series) {
    this.name = name;
    this.series = series;
    if (isPieOrDonut()) {
      hoverinfo = HoverInfo.PIE_LABEL_VALUE;
    } else {
      hoverinfo = HoverInfo.X_Y_NAME;
    }
  }

  public AbstractData(AbstractDataSeries series) {
    this.name = null;
    this.series = series;
    if (isPieOrDonut()){
      hoverinfo = HoverInfo.PIE_LABEL_VALUE;
    } else {
      hoverinfo = HoverInfo.X_Y_NAME;
    }
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Override
  public List<JsonObject> getValue() throws DataMismatchException {
    List<JsonObject>            list;
    JsonObjectBuilder           builder;

    list = new ArrayList<JsonObject>();
    if (isRangeArea()) {
      JsonObjectBuilder                 builderinf;

      builderinf = Json.createObjectBuilder();
      if (getData().getDimensions() != null) {
        builderinf.add("x", getData().getDimensions());
      }
      
      builderinf.add("y", getData().getMeasures());

      builderinf.add("type", getType().name().toLowerCase());

      if (getName() != null) {
        builderinf.add("name", getName() + "-inf");
      }

      builderinf.add("mode", getMode().getValue());
      builderinf.add("line", getLine().getValue());
      builderinf.add("hoverinfo", getHoverInfo().getValue());

      list.add(builderinf.build());
    }

    builder = Json.createObjectBuilder();

    if (isBubble()) {
      if(getMarker() != null) {
        if (getData().getRange()!=null) {
          getMarker().setSizes(getData().getRange());
        }
      }
    }

    if (isPieOrDonut()) {
      if(getData().getDimensions()!=null) {
        builder.add("labels", getData().getDimensions());
      }
      builder.add("values", getData().getMeasures());
      if (getDomain() !=null ) {
        builder.add("domain", Json.createObjectBuilder()
                    .add("x", Json.createArrayBuilder()
                         .add(getDomain()[0])
                         .add(getDomain()[1])
                         .build())
                    .add("y", Json.createArrayBuilder()
                         .add(getDomain()[2])
                         .add(getDomain()[3])
                         .build())
                    .build()
            );
      }
    } else {
      if (getOrientation() != null) {
        if (getData().getDimensions() !=null) {
          builder.add("y", getData().getDimensions());
        }
        builder.add("x", getData().getMeasures());
      } else {
        if (getData().getDimensions() != null) {
          builder.add("x", getData().getDimensions());
        }
        if (isRangeArea()) {
          if (getData().getRange() != null){
            builder.add("y", getData().getRange());
          } else {
            builder.add("y", getData().getMeasures());
          }
        } else {
          builder.add("y", getData().getMeasures());
        }
      }
    }

    builder.add("type", getType().name().toLowerCase());
    if (getName() != null) {
      if (isRangeArea()) {
        builder.add("name", getName()+"-sup");
      } else {
        builder.add("name", getName());
      }
    }

    if (getMode() != null) { 
      builder.add("mode", getMode().getValue());
    }

    if (getMarker() != null) {
      builder.add("marker", getMarker().getValue());
    }

    if (getLine() != null) {
      builder.add("line", getLine().getValue());
    }

    if (getFill() != null) {
      builder.add("fill", getFill());
    }

    builder.add("hoverinfo", getHoverInfo().getValue());
    if (getHole() != 0) {
      builder.add("hole", getHole());
    }

    if (getOrientation() != null) {
      builder.add("orientation", getOrientation().getValue());
    }

    list.add(builder.build());

    return list;
  }
  
  /**
   * Returns the layout annotations.
   * @return The layout annotations.
   */
  public Collection<Annotation> getAnnotations() {
    if (series != null) {
      return series.getAnnotations(orientation);
    } else {
      return Collections.emptyList();
    }
  }
  
  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public DataSeries getData() {
    return this.series;
  }

  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public double getHole() {
    return 0;
  }

  @Override
  public Marker getMarker() {
    return null;
  }

  @Override
  public DataMode getMode() {
    return null;
  }

  @Override
  public Line getLine() {
    return null;
  }

  @Override
  public String getFill() {
    return null;
  }
  
  @Override
  public Color getColor() {
    return null;
  }

  @Override
  public Orientation getOrientation() {
    return null;
  }

  @Override
  public void setHoverinfo(HoverInfo hoverinfo) {
    this.hoverinfo = hoverinfo;
  }

  @Override
  public HoverInfo getHoverInfo() {
    return hoverinfo;
  }

  @Override
  public boolean isPieOrDonut() {
    return false;
  }

  @Override
  public boolean isRangeArea() {
    return false;
  }

  @Override
  public boolean isBubble() {
    return false;
  }

  @Override
  public boolean isBoxPlot() {
    return false;
  }

  @Deprecated
  @Override 
  public void setColors(List<Color> colors){}

  @Override
  public void setColor(Color color) throws TypeMismatchException {   
    if(getType().equals(DataType.PIE)) {
      throw new TypeMismatchException("this function is not authotorized for Pie or donut");
    }
  }

  @Override
  public void setDomain(double x1, double x2, double y1, double y2) throws TypeMismatchException {   
    if (!getType().equals(DataType.PIE)) {
      throw new TypeMismatchException("this function is only authotorized for Pie or donut");
    }
  }
  
  @Override
  public void setDataSeries(AbstractDataSeries series)
    throws PlotlyException
  {
    this.series = series;
  }

  @Override
  public double[] getDomain() {
    return null;
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  protected String                      name;
  protected HoverInfo                   hoverinfo;
  protected DataSeries                  series;
  protected DataType                    type;
  protected DataMode                    mode;
  protected Line                        line;
  protected String                      fill;
  protected Marker                      marker;
  protected Orientation                 orientation;
}
