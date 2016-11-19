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

package org.kopi.vkopi.lib.ui.vaadin.plotly.layout;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.ChartProperty;

public class Annotation implements ChartProperty {

  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------
  
  public Annotation(Object x,
                    Object y,
                    String text,
                    String xanchor,
                    String yanchor,
                    boolean showarrow)
  {
    this.x = x;
    this.y = y;
    this.text = text;
    this.xanchor = xanchor;
    this.yanchor = yanchor;
    this.showarrow = showarrow;
  }
  
  public Annotation(Object x, Object y, String text) {
    this(x, y, text, "center", "bottom", false);
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  @Override
  public JsonObject getValue() {
    JsonObjectBuilder           obj;

    obj = Json.createObjectBuilder();
    if (x instanceof String) {
      obj.add("x", (String)x);
    } else if (x instanceof Number) {
      obj.add("x", ((Number)x).doubleValue());
    } else {
      obj.add("x", x.toString());
    }
    if (y instanceof String) {
      obj.add("y", (String)y);
    } else if (y instanceof Number) {
      obj.add("y", ((Number)y).doubleValue());
    } else {
      obj.add("y", y.toString());
    } 
    obj.add("text", text); 
    obj.add("xanchor", xanchor); 
    obj.add("yanchor", yanchor);
    obj.add("showarrow", showarrow);

    return obj.build();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Annotation) {
      return x.equals(((Annotation) obj).x)
        && y.equals(((Annotation) obj).y);
    } else {
      return super.equals(obj);
    }
  }
  
  @Override
  public int hashCode() {
    return x.hashCode() + y.hashCode();
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------
  
  private Object                        x;
  private Object                        y;
  private String                        text;
  private String                        xanchor;
  private String                        yanchor;
  private boolean                       showarrow;
}
