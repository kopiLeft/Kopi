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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.kopi.vkopi.lib.form.VBlock;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ItemSorter;

/**
 * An item sorter implementation for the grid block
 */
@SuppressWarnings("serial")
public class DGridBlockItemSorter implements ItemSorter {
  
  // --------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------
  
  /**
   * Constructs a DefaultItemSorter using the default <code>Comparator</code>
   * for comparing <code>Property</code>values.
   */
  public DGridBlockItemSorter(VBlock model) {
    this.model = model;
    this.propertyValueComparator = new DefaultPropertyValueComparator();
  }
  
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  /*
   * (non-Javadoc)
   *
   * @see com.vaadin.data.util.ItemSorter#compare(java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public int compare(Object o1, Object o2) {
    Integer         rec1;
    Integer         rec2;
    
    rec1 = model.getSortedPosition((Integer)o1);
    rec2 = model.getSortedPosition((Integer)o2);
    if (isSortedRecordFilled(rec1) && isSortedRecordFilled(rec2)) {
      if (sortPropertyIds.length == 0) {
        return rec1.compareTo(rec2); // keep the records order
      } else {
        return compareProperties(rec1, rec2);
      }
    } else if (!isSortedRecordFilled(rec1) && !isSortedRecordFilled(rec2)) {
      return rec1.compareTo(rec2); // keep the records order
    } else if (isSortedRecordFilled(rec1)) {
      return -1; // empty records are always at the bottom
    } else {
      return 1; // empty records are always at the bottom
    }
  }
  
  protected boolean isSortedRecordFilled(int rec) {
    return model.isRecordChanged(model.getDataPosition(rec))
      || model.isRecordFetched(model.getDataPosition(rec));
  }
  
  /**
   * Compare the properties values.
   * @param o1 The first item.
   * @param o2 The second item.
   * @return The comparison result.
   */
  protected int compareProperties(Object o1, Object o2) {
    Item        item1 = container.getItem(o1);
    Item        item2 = container.getItem(o2);

    /*
     * Items can be null if the container is filtered. Null is considered
     * "less" than not-null.
     */
    if (item1 == null) {
      if (item2 == null) {
        return 0;
      } else {
        return 1;
      }
    } else if (item2 == null) {
      return -1;
    }

    for (int i = 0; i < sortPropertyIds.length; i++) {
      int       result = compareProperty(sortPropertyIds[i], sortDirections[i], item1, item2);

      // If order can be decided
      if (result != 0) {
        return result;
      }

    }

    return 0;
  }

  /**
   * Compares the property indicated by <code>propertyId</code> in the items
   * indicated by <code>item1</code> and <code>item2</code> for order. Returns
   * a negative integer, zero, or a positive integer as the property value in
   * the first item is less than, equal to, or greater than the property value
   * in the second item. If the <code>sortDirection</code> is false the
   * returned value is negated.
   * <p>
   * The comparator set for this <code>DefaultItemSorter</code> is used for
   * comparing the two property values.
   *
   * @param propertyId
   *            The property id for the property that is used for comparison.
   * @param sortDirection
   *            The direction of the sort. A false value negates the result.
   * @param item1
   *            The first item to compare.
   * @param item2
   *            The second item to compare.
   * @return a negative, zero, or positive integer if the property value in
   *         the first item is less than, equal to, or greater than the
   *         property value in the second item. Negated if
   *         {@code sortDirection} is false.
   */
  protected int compareProperty(Object propertyId,
                                boolean sortDirection,
                                Item item1,
                                Item item2)
  {
    // Get the properties to compare
    final Property<?>   property1 = item1.getItemProperty(propertyId);
    final Property<?>   property2 = item2.getItemProperty(propertyId);
    // Get the values to compare
    final Object        value1 = (property1 == null) ? null : property1.getValue();
    final Object        value2 = (property2 == null) ? null : property2.getValue();
    // Result of the comparison
    int                 result = 0;
    
    if (sortDirection) {
      result = propertyValueComparator.compare(value1, value2);
    } else {
      result = propertyValueComparator.compare(value2, value1);
    }

    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.vaadin.data.util.ItemSorter#setSortProperties(com.vaadin.data.
   * Container .Sortable, java.lang.Object[], boolean[])
   */
  @Override
  public void setSortProperties(Container.Sortable container,
                                Object[] propertyId,
                                boolean[] ascending)
  {
    this.container = container;
    // Removes any non-sortable property ids
    final List<Object> ids = new ArrayList<Object>();
    final List<Boolean> orders = new ArrayList<Boolean>();
    final Collection<?> sortable = container.getSortableContainerPropertyIds();
    for (int i = 0; i < propertyId.length; i++) {
      if (sortable.contains(propertyId[i])) {
        ids.add(propertyId[i]);
        orders.add(Boolean.valueOf(i < ascending.length ? ascending[i] : true));
      }
    }
    sortPropertyIds = ids.toArray();
    sortDirections = new boolean[orders.size()];
    for (int i = 0; i < sortDirections.length; i++) {
      sortDirections[i] = (orders.get(i)).booleanValue();
    }
  }

  /**
   * Provides a default comparator used for comparing {@link Property} values.
   * The <code>DefaultPropertyValueComparator</code> assumes all objects it
   * compares can be cast to Comparable.
   *
   */
  public static class DefaultPropertyValueComparator implements Comparator<Object>, Serializable {

    @Override
    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) {
      int       result = 0;
      
      // Normal non-null comparison
      if (o1 != null && o2 != null) {
        // Assume the objects can be cast to Comparable, throw
        // ClassCastException otherwise.
        result = ((Comparable<Object>) o1).compareTo(o2);
      } else if (o1 == o2) {
        // Objects are equal if both are null
        result = 0;
      } else {
        if (o1 == null) {
          result = -1; // null is less than non-null
        } else {
          result = 1; // non-null is greater than null
        }
      }

      return result;
    }
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  private final VBlock                  model;
  private java.lang.Object[]            sortPropertyIds;
  private boolean[]                     sortDirections;
  private Container                     container;
  private Comparator<Object>            propertyValueComparator;
}

