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

/**
 * A layout configuration without axis 
 */
public class LayoutWithoutAxis extends Layout {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public LayoutWithoutAxis(int height, int width, String title) {
    super(height, width, title);
  }

  public LayoutWithoutAxis(String title) {
    super(title);
  }

  public LayoutWithoutAxis(int height, int width) {
    super(height, width);
  }

  public LayoutWithoutAxis() {
    super();
  }

  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------

  @Override
  public JsonObject getValue() {
    JsonObjectBuilder 		obj;
    
    obj = Json.createObjectBuilder();
    if (this.getHeight() !=0) {
      obj.add("height", this.getHeight());
    }
    if (this.getWidth() != 0) {
      obj.add("width", this.getWidth());
    }
    if (this.getGraphTitle() != null) {
      obj.add("title", this.getGraphTitle());
    }
    obj.add("showlegend", this.isLegendShown());
    if (this.getBackgroundColor() != null) {
      obj.add("paper_bgcolor", this.getBackgroundColor().getCSS());
      obj.add("plot_bgcolor", this.getBackgroundColor().getCSS());
    }
    
    return obj.build();
  }
}
