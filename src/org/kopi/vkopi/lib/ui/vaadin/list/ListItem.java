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

package org.kopi.vkopi.lib.ui.vaadin.list;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class ListItem implements Item {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new list item object.
   * @param model The list data model.
   * @param row The item row index.
   */
  public ListItem(Container parent, int row) {
    this.parent = parent;
    this.row = row;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public Property<?> getItemProperty(Object id) {
    return parent.getContainerProperty(row, id);
  }

  @Override
  public Collection<?> getItemPropertyIds() {
    return parent.getContainerPropertyIds();
  }

  @SuppressWarnings("rawtypes")
  @Override
  public final boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
    return false;
  }

  @Override
  public final boolean removeItemProperty(Object id) throws UnsupportedOperationException {
    return false;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Container                       parent;
  private final int                             row;
}
