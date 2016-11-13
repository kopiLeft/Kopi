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

package org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.plotly.base.Colors;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A Pie marler that should provide its colors.
 */
public class PieMarker extends AbstractMarker {
  
  //---------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------

  public PieMarker() {
    colors = new ArrayList<Color>();
    colors.add(Colors.LIGHTSKYBLUE);
    colors.add(Colors.LIGHTGREEN);
    colors.add(Colors.LIGHTSALMON);
    colors.add(Colors.LIGHTCORAL);
    colors.add(Colors.LIGHTSTEELBLUE);
    colors.add(Colors.LIGHTGRAY);
  }
  
  public PieMarker(List<Color> colors) {
    this.colors = colors;
  }
  
  //---------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------
  
  /**
   * Used to set the colors of the pie markers.
   * @param colors
   */
  public void setColors(List<Color> colors) {
    this.colors = colors;
  }

  @Override
  public Collection<Color> getColors() {
    return colors;
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private List<Color>                   colors;
}
