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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockAlignment;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.SimpleBlockLayoutState;

import com.vaadin.ui.Component;

/**
 * The simple block layout.
 */
@SuppressWarnings("serial")
public class SimpleBlockLayout extends AbstractBlockLayout {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a simple block layout.
   * @param col The column number.
   * @param line The line number.
   */
  public SimpleBlockLayout(int col, int line) {
    super(col, line);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the alignment information for this simple layout.
   * @param ori The original block to align with.
   * @param targets The alignment targets.
   * @param isChart Is the original block chart ? 
   */
  public void setBlockAlignment(Component ori, int[] targets, boolean isChart) {
    BlockAlignment		align = new BlockAlignment();
    
    align.isChart = isChart;
    align.targets = targets;
    align.ori = ori;
    getState().align = align;
  }
  
  @Override
  protected SimpleBlockLayoutState getState() {
    return (SimpleBlockLayoutState) super.getState();
  }
}
