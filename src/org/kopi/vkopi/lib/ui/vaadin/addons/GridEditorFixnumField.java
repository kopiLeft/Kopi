/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFixnumFieldState;

/**
 * Server side implementation of decimal grid editor field
 */
@SuppressWarnings("serial")
public class GridEditorFixnumField extends GridEditorTextField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public GridEditorFixnumField(int width,
                               Double minValue,
                               Double maxValue,
                               int maxScale,
                               boolean fraction)
  {
    super(width);
    getState().minValue = minValue;
    getState().maxValue = maxValue;
    getState().maxScale = maxScale;
    getState().fraction = fraction;
    getState().scale = maxScale;
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  protected EditorFixnumFieldState getState() {
    return (EditorFixnumFieldState) super.getState();
  }
  
  /**
   * Sets the scale of this decimal field.
   * @param scale The field scale
   */
  public void setScale(int scale) {
    getState().scale = scale;
  }
}
