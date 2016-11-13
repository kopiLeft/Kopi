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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.ChartBlockLayoutState;

/**
 * The chart block layout.
 */
@SuppressWarnings("serial")
public class ChartBlockLayout extends AbstractBlockLayout implements BlockLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new chart block layout.
   * @param col The number of columns.
   * @param line The number of lines.
   */
  public ChartBlockLayout(int col, int line) {
    super(col, line);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected ChartBlockLayoutState getState() {
    return (ChartBlockLayoutState) super.getState();
  }
  
  /**
   * Sets the block ability to scroll.
   * @param scrollable The scroll ability.
   */
  public void setScrollable(boolean scrollable) {
    getState().hasScroll = scrollable;
  }
}
