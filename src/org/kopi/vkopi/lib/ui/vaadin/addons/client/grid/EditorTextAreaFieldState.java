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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import com.vaadin.shared.annotations.NoLayout;

/**
 * Shared state for text area editors. 
 */
@SuppressWarnings("serial")
public class EditorTextAreaFieldState extends EditorTextFieldState {
  
  /**
   * The row number.
   */
  @NoLayout
  public int                            rows;
  
  /**
   * The visible rows
   */
  @NoLayout
  public int                            visibleRows;
  
  /**
   * Use fixed new line transformer in multiple line field ?
   * Dynamic new line means that we use '\n' for line break.
   * Fixed new line means that we complete the messing field columns
   * with space character instead of using line separator.
   */
  @NoLayout
  public boolean                        fixedNewLine;
}
