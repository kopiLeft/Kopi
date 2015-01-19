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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

/**
 * The <code>KopiLayout</code> is a common layout for all blocks layout.
 */
public interface KopiLayout extends Layout {
  
  /**
   * Adds the specified component to the layout, using the specified
   * constraint object.
   * @param   comp         the component to be added.
   * @param   constraints  an object that specifies how and where
   */
  public void addLayoutComponent(Component comp, Object constraints);
  
  /**
   * Lays out the components added to the layout.
   */
  public void layoutContainer();
  
  /**
   * Returns the position of a column
   * @param x The column model position.
   */
  public int getColumnPos(int x);
}
