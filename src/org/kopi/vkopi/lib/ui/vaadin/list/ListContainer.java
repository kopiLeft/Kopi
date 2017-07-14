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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kopi.vkopi.lib.form.VListDialog;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.filter.UnsupportedFilterException;

/**
 * A sortable data source container for list dialog object.
 */
@SuppressWarnings("serial")
public class ListContainer extends AbstractInMemoryContainer<Integer, Integer, ListItem> implements Sortable, Filterable, SimpleFilterable {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new list dialog data source container object.
   * @param model The list data model.
   */
  public ListContainer(VListDialog model) {
    this.model = model;
    this.propertyIds = Utils.buildIdList(model.getColumnCount());
    this.allItemIds = Utils.buildIdList(model.getCount());
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public Collection<?> getContainerPropertyIds() {
    return Collections.unmodifiableCollection(propertyIds);
  }

  @Override
  public Property<?> getContainerProperty(Object itemId, Object propertyId) {
    return new ListProperty(model, (Integer)itemId, (Integer)propertyId);
  }

  @Override
  public Class<?> getType(Object propertyId) {
    return String.class;
  }

  @Override
  protected ListItem getUnfilteredItem(Object itemId) {
    return new ListItem(this, (Integer)itemId);
  }
  
  @Override
  public int size() {
    if (!isFiltered()) {
      return model.getCount() - (model.isSkipFirstLine() ? 1 : 0);
    } else {
      return super.size();
    }
  }
  
  @Override
  protected List<Integer> getAllItemIds() {
    return allItemIds;
  }

  @Override
  public void sort(Object[] propertyId, boolean[] ascending) {
    sortContainer(propertyId, ascending);
  }

  @Override
  public Collection<?> getSortableContainerPropertyIds() {
    return getSortablePropertyIds();
  }

  @Override
  public void addContainerFilter(Filter filter) throws UnsupportedFilterException {
    addFilter(filter);
  }

  @Override
  public void removeContainerFilter(Filter filter) {
    removeFilter(filter);
  }

  @Override
  public void removeAllContainerFilters() {
    removeAllFilters();
  }
  
  @Override
  public Collection<Filter> getContainerFilters() {
    return super.getContainerFilters();
  }

  @Override
  public void addContainerFilter(Object propertyId,
                                 String filterString,
                                 boolean ignoreCase,
                                 boolean onlyMatchPrefix)
  {
    try {
      addFilter(new ListFilter(propertyId, filterString, ignoreCase, onlyMatchPrefix));
    } catch (UnsupportedFilterException e) {
      // the filter instance created here is always valid for in-memory containers
    }
  }

  @Override
  public void removeContainerFilters(Object propertyId) {
    removeFilters(propertyId);
  }
  
  @Override
  protected void fireItemSetChange() {
    super.fireItemSetChange();
  }
  
  /**
   * Returns true is the data source contains installed filters.
   * @return True is the data source contains installed filters.
   */
  public boolean hasFilters() {
    return !getFilters().isEmpty();
  }
  
  /**
   * Looks for the item ID that its first property starts with the given pattern.
   * @param pattern The search pattern.
   * @return The found item ID or null if no item corresponds the the wanted pattern.
   */
  protected Object serach(String pattern) {
    for (final Iterator<Integer> i = getVisibleItemIds().iterator(); i.hasNext();) {
      final Integer     id = i.next();
      
      if (startsWith(id, pattern)) {
        return id;
      }
    }
    
    return null;
  }
  
  /**
   * Looks if the first property of the container starts with the given pattern at the given item ID.
   * @param itemId The item ID.
   * @param pattern The search pattern
   * @return true if the first property of the container starts with the given pattern.
   */
  public boolean startsWith(Object itemId, String pattern) {
    final ListProperty          property;
    final Object                propertyValue;
    final String                value;
    final Item                  item;

    item = getUnfilteredItem(itemId);
    property = (ListProperty) item.getItemProperty(propertyIds.get(0));
    if (property == null) {
      return false;
    }
    propertyValue = property.getValue();
    if (propertyValue == null) {
      return false;
    }
    value = property.formatObject(propertyValue).toString().toLowerCase();
    if (!value.contains(pattern)) {
      return false;
    }

    return true;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VListDialog                     model;
  private final List<Integer>                   propertyIds;
  private final List<Integer>                   allItemIds;
}
