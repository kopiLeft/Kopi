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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.server.Resource;
import com.vaadin.ui.Grid;

/**
 * Data source container for grid block
 */
@SuppressWarnings("serial")
public class DGridBlockContainer extends AbstractInMemoryContainer<Integer, String, DGridBlockContainer.GridBlockItem>
  implements Sortable, Filterable, SimpleFilterable
{
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public DGridBlockContainer(final VBlock model) {
    this.model = model;
    this.propertyIds = new ArrayList<Integer>();
    for (int i = 0; i < model.getFieldCount(); i++) {
      if (!model.getFields()[i].isInternal() && !model.getFields()[i].noChart()) {
        this.propertyIds.add(i);
      }
    }
    this.allItemIds = Utils.buildIdList(model.getBufferSize());
    // redefine sort strategy for empty records
    setItemSorter(new DGridBlockItemSorter(model));
    // filters deleted records
    addFilter(new Filter() {
      
      @Override
      public boolean passesFilter(Object itemId, Item item)
        throws UnsupportedOperationException
      {
        return !model.isRecordDeleted((Integer)itemId);
      }
      
      @Override
      public boolean appliesToProperty(Object propertyId) {
        return true;
      }
    });
  }
  
  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  
  @Override
  public Collection<?> getContainerPropertyIds() {
    return Collections.unmodifiableCollection(propertyIds);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Property getContainerProperty(Object itemId, Object propertyId) {
    return new GridBlockProperty((Integer)itemId, model.getFields()[(Integer)propertyId]);
  }

  @Override
  public Class<?> getType(Object propertyId) {
    if (model.getFields()[(Integer)propertyId].getDataType() == byte[].class) {
      return Resource.class;
    } else {
      return String.class;
    }
  }

  @Override
  protected GridBlockItem getUnfilteredItem(Object itemId) {
    return new GridBlockItem((Integer)itemId);
  }
  
  /**
   * Notifies registered listeners that content has changed.
   */
  protected void fireContentChanged() {
    doFilterContainer(!getFilters().isEmpty());
    fireItemSetChange();
  }
  
  @Override
  protected void fireContainerPropertySetChange() {
    super.fireContainerPropertySetChange();
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
      addFilter(new SimpleStringFilter(propertyId, filterString, ignoreCase, onlyMatchPrefix));
    } catch (UnsupportedFilterException e) {
      // the filter instance created here is always valid for in-memory containers
    }
  }

  @Override
  public void removeContainerFilters(Object propertyId) {
    removeFilters(propertyId);
  }
  
  @Override
  public int size() {
    if (isReallyFilled()) {
      return super.size();
    } else {
      return model.getNumberOfValidRecord();
    }
  }
  
  /**
   * This is a workaround for not changed records that contains non deleted records
   * but records are not really marked filled in the block since they are not changed
   * or fetched.
   * @return If the block model is filled but not marked as changed or fetched. 
   */
  protected boolean isReallyFilled() {
    return model.getNumberOfFilledRecords() == 0
      && model.getNumberOfValidRecord() == 0
      && super.size() > 0;
  }
  
  /*
   * Grid class declares an ItemSetChangeListener that cancels the editor
   * when #fireItemSetChange() is called. This will close editors widgets
   * and any reference done to the editors connectors is lost. To avoid
   * this side effect, we will not add the grid listener to do not cancel
   * the editor if an item set change event is fired. 
   */
  @Override
  public void addItemSetChangeListener(ItemSetChangeListener listener) {
    if (!Grid.class.isAssignableFrom(listener.getClass().getEnclosingClass())) {
      super.addItemSetChangeListener(listener);
    }
  }
  
  @Override
  public void removeItemSetChangeListener(ItemSetChangeListener listener) {
    if (!Grid.class.isAssignableFrom(listener.getClass().getEnclosingClass())) {
      super.removeItemSetChangeListener(listener);
    }
  }
  
  @Override
  protected void doSort() {
    super.doSort();
  }
  
  // --------------------------------------------------
  // INNER CLASSES
  // --------------------------------------------------
  
  /**
   * Grid block data source item
   */
  public class GridBlockItem implements Item {
    
    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------
    
    public GridBlockItem(int record) {
      this.record = record;
    }
    
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    
    @Override
    public Property<?> getItemProperty(Object id) {
      return new GridBlockProperty(record, model.getFields()[(Integer)id]);
    }

    @Override
    public Collection<?> getItemPropertyIds() {
      return getContainerPropertyIds();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean addItemProperty(Object id, Property property)
      throws UnsupportedOperationException
    {
      return false;
    }

    @Override
    public boolean removeItemProperty(Object id)
      throws UnsupportedOperationException
    {
      return false;
    }
    
    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------
    
    private final int           record;
  }
  
  /**
   * Grid Block data source property.
   */
  public static class GridBlockProperty implements Property<Object> {
    
    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------
    
    public GridBlockProperty(int record, VField field) {
      this.record = record;
      this.field = field;
    }
    
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    
    @Override
    public Object getValue() {
      return field.getObject(record);
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException {
      // not used, parse displayed value to set the model value
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Object> getType() {
      return field.getDataType();
    }

    @Override
    public boolean isReadOnly() {
      return field.isNoEdit();
    }

    @Override
    public void setReadOnly(boolean newStatus) {}
    
    // --------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------
    
    private final int                   record;
    private final VField                field;
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  private final VBlock                  model;
  private final List<Integer>           propertyIds;
  private final List<Integer>           allItemIds;
}
