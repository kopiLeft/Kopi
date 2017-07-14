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
import com.vaadin.ui.HasComponents;

/**
 * The block layout should provides its children
 * and set a given component in a specified position.
 */
public interface BlockLayout extends HasComponents {
  
  /**
   * Adds a component to this block layout.
   * @param component The component to be added.
   * @param x the x position.
   * @param y The y position.
   * @param width the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll Use all available area ?
   */
  public void addComponent(Component component, int x, int y, int width, int height, boolean alignRight, boolean useAll);
}
