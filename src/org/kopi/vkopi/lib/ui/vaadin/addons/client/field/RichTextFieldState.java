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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.ui.textfield.AbstractTextFieldState;

/**
 * Shared state implementation for for the rich text field 
 */
@SuppressWarnings("serial")
public class RichTextFieldState extends AbstractTextFieldState {
  
  /**
   * The number of columns handled by the rich text area
   */
  @NoLayout
  public int                    col;
  
  /**
   * 
   */
  @NoLayout
  public int                    rows;
  
  /**
   * 
   */
  @NoLayout
  public int                    visibleRows;
  
  /**
   * 
   */
  @NoLayout
  public boolean                noEdit;
}
