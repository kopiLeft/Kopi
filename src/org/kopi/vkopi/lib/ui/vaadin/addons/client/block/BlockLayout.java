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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;

/**
 * A block layout is a visible component that defines the layout
 * of the components inside a block
 */
public interface BlockLayout extends HasVisibility {
  
  /**
   * Adds a component to the layout according to its constraints.
   * @param widget The widget component to add.
   * @param constraints The widget constraints.
   */
  public void add(Widget widget, ComponentConstraint constraints);
  
  /**
   * Lays out the components added to the layout.
   */
  public void layout();
  
  /**
   * Adds extra widgets to this layout.
   * @param widget The widget to add.
   * @param constraint The widget constraints.
   */
  public void addAlignedWidget(Widget widget, ComponentConstraint constraint);
  
  /**
   * Lays out some extra widgets. This used to render not standard widgets.
   */
  public void layoutAlignedWidgets();
  
  /**
   * Casts this layout to {@link Widget}.
   * @return This layout to {@link Widget}.
   */
  public <T extends Widget> T cast();
  
  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  public void updateScroll(int pageSize, int maxValue, boolean enable, int value);
}
