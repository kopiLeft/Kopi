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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.list;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.NoLayout;

/**
 * Shared state for grid based list dialog
 */
@SuppressWarnings("serial")
public class GridListDialogState extends AbstractComponentState {
  
  /**
   * The reference connector.
   * This is used to show the dialog relative to the corresponding widget.
   */
  @NoLayout
  public Connector                      reference;
  
  /**
   * This is used to display a new button under the dialog.
   * No button will be drawn when it is {@code null}.
   */
  @NoLayout
  public String                         newText;
  
  /**
   * The list dialog selection target.
   */
  public static enum SelectionTarget {
    
    /**
     * Selects the current row and close the list.
     */
    CURRENT_ROW,
    
    /**
     * Navigates to the next row.
     */
    NEXT_ROW,
    
    /**
     * Navigates to the previous row.
     */
    PREVIOUS_ROW,
    
    /**
     * Navigates to the next page.
     */
    NEXT_PAGE,
    
    /**
     * Navigates to the previous page.
     */
    PREVIOUS_PAGE,
    
    /**
     * Navigates to the first row.
     */
    FIRST_ROW,
    
    /**
     * Navigates to the last row.
     */
    LAST_ROW;
  }
}
