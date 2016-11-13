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

import java.io.Serializable;

/**
 * The <code>Sortable</code> interface defines the signatures and the 
 * constants for the sortable table.
 */
public interface Sortable extends Serializable {

  /**
   * Defines what happens when the column is sorted
   * @param mode The sort mode.
   * @return The sort indexes.
   */
  public int[] sort(int mode);

  /**
   * Constants defining the current direction of the 
   * sort on a column
   */
  public static int 		SORT_NONE = 0;
  public static int 		SORT_ASC = 1;
  public static int 		SORT_DESC = 2;
}
