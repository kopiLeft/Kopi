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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorTextAreaFieldState;

/**
 * A text area editor for grid block
 */
@SuppressWarnings("serial")
public class GridEditorTextAreaField extends GridEditorTextField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public GridEditorTextAreaField(int width,
                                 int height,
                                 int visibleHeight,
                                 boolean fixedNewLine)
  {
    super(width);
    getState().rows = height;
    getState().visibleRows = visibleHeight;
    getState().fixedNewLine = fixedNewLine;
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  protected EditorTextAreaFieldState getState() {
    return (EditorTextAreaFieldState) super.getState();
  }
}
