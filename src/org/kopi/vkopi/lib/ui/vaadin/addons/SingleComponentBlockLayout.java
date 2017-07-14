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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import com.vaadin.ui.Component;

/**
 * A block layout that contains a single component inside.
 */
@SuppressWarnings("serial")
public class SingleComponentBlockLayout extends SimpleBlockLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a single component block layout instance.
   */
  public SingleComponentBlockLayout() {
    super(1, 1);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void addComponent(Component component,
                           int x,
                           int y,
                           int width,
                           int height,
                           boolean alignRight,
                           boolean useAll)
  {
    // always put the component at the only available position
    
    if (x > 0) {
      x = 0;
    }
    
    if (y > 0) {
      y = 0;
    }
    
    if (width > 1 || width < 0) {
      width = 1;
    }
    
    super.addComponent(component, x, y, width, height, alignRight, useAll);
  }
  
  @Override
  public void setBlockAlignment(Component ori, int[] targets, boolean isChart) {
    // not supported feature
  }
}
