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

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

@SuppressWarnings("serial")
public final class ListFilter implements Filter {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public ListFilter(Object propertyId,
                    String filterString,
                    boolean ignoreCase,
                    boolean onlyMatchPrefix)
  {
    this.propertyId = propertyId;
    this.filterString = ignoreCase ? filterString.toLowerCase() : filterString;
    this.ignoreCase = ignoreCase;
    this.onlyMatchPrefix = onlyMatchPrefix;
  }
 
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public boolean passesFilter(Object itemId, Item item)
    throws UnsupportedOperationException
  {
    final ListProperty  property;
    final Object        propertyValue;
    final String        value;
    
    property = (ListProperty) item.getItemProperty(propertyId);
    if (property == null) {
      return false;
    }
    propertyValue = property.getValue();
    if (propertyValue == null) {
      return false;
    }
    value = ignoreCase ? property.formatObject(propertyValue).toString().toLowerCase(): property.formatObject(propertyValue).toString();
    if (onlyMatchPrefix) {
      if (!value.startsWith(filterString)) {
        return false;
      }
    } else {
      if (!value.contains(filterString)) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public boolean appliesToProperty(Object propertyId) {
    return this.propertyId.equals(propertyId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    // Only ones of the objects of the same class can be equal
    if (!(obj instanceof ListFilter)) {
      return false;
    }
    
    final ListFilter o = (ListFilter) obj;

    // Checks the properties one by one
    if (propertyId != o.propertyId && o.propertyId != null && !o.propertyId.equals(propertyId)) {
      return false;
    }
    if (filterString != o.filterString && o.filterString != null && !o.filterString.equals(filterString)) {
      return false;
    }
    if (ignoreCase != o.ignoreCase) {
      return false;
    }
    if (onlyMatchPrefix != o.onlyMatchPrefix) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (propertyId != null ? propertyId.hashCode() : 0) ^ (filterString != null ? filterString.hashCode() : 0);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Object                  propertyId;
  private final String                  filterString;
  private final boolean                 ignoreCase;
  private final boolean                 onlyMatchPrefix;
}
