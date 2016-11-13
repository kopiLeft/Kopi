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

import org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries.AbstractDataSeries;
import org.kopi.vkopi.lib.ui.vaadin.plotly.data.line.LineShape;

/**
 * Uses all the properties of LineData except for its line shape that needs to be spline.
 */
public class SplineData extends LineData {
  
  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------
  
  public SplineData(String name, AbstractDataSeries series) {
    super(name, series); 
    this.getLine().setShape(LineShape.SPLINE);
  }

  public SplineData(AbstractDataSeries series) {
    super(series);
    this.getLine().setShape(LineShape.SPLINE);
  }
  
  public SplineData(String name) {
    super(name);
    this.getLine().setShape(LineShape.SPLINE);
  }

  public SplineData() {
    this.getLine().setShape(LineShape.SPLINE);
  }
}
