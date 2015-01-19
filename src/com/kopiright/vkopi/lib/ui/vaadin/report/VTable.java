/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.kopiright.vkopi.lib.report.MReport;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.report.VTable.TableModelItem;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;

/**
 * The <code>VTable</code> is a vaadin {@link Table} data container adapted
 * to dynamic reports needs.
 */
@SuppressWarnings("serial")
public class VTable extends AbstractInMemoryContainer<Integer, String, TableModelItem>
  implements Container.PropertySetChangeNotifier
{
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VTable</code> instance.
   * @param model The table model.
   */
  public VTable(MReport model) {
    this.model = model;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  public Collection<?> getContainerPropertyIds() {
    return Collections.unmodifiableCollection(buildIds(model.getColumnCount()));
  }

  @SuppressWarnings("rawtypes")
  public Property getContainerProperty(Object itemId, Object propertyId) {
    return new TableModelProperty(((Integer)itemId).intValue(), ((Integer)propertyId).intValue());
  }

  public Class<?> getType(Object propertyId) {
    return String.class;
  }

  @Override
  protected TableModelItem getUnfilteredItem(Object itemId) {
    return new TableModelItem(((Integer)itemId).intValue());
  }
  
  @Override
  public int size() {
    return model.getRowCount();
  }
  
  @Override
  protected List<Integer> getAllItemIds() {
    return buildIds(model.getRowCount());
  }

  @Override
  public void addPropertySetChangeListener(PropertySetChangeListener listener){
    super.addPropertySetChangeListener(listener);
  }
  
  @Override
  public void removePropertySetChangeListener(PropertySetChangeListener listener){
    super.removePropertySetChangeListener(listener);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void addListener(PropertySetChangeListener listener) {
    super.addListener(listener);
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void removeListener(PropertySetChangeListener listener) {
    super.removeListener(listener);
  }
  
  /**
   * Returns the column name of a given column index.
   * @param column The column index.
   * @return The column name.
   */
  public String getColumnName(int column) {  
    String	label = model.getAccessibleColumns()[column].getLabel();
    
    if (label == null || label.length() == 0) {
      return "";
    }
    
    return label;
  }
  
  /**
   * Notifies the table container that content has been changed.
   */
  public void fireContentChanged() {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
	fireItemSetChange();
      }
    });
  }
  
  /**
   * Notifies the table container that structure has been changed.
   */
  public void fireStructureChanged() {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        fireContainerPropertySetChange();
      }
    });
  }
  
  /**
   * Returns the column align.
   * @param column The column index.
   * @return The column align.
   */
  public int getColumnAlign(int column) {
    return model.getAccessibleColumn(column).getAlign();
  }
  
  /**
   * Returns the column count.
   * @return the column count.
   */
  public int getColumnCount() {
    return model.getColumnCount();
  }
  
  /**
   * Returns the {@link MReport} model.
   * @return The {@link MReport} model.
   */
  public MReport getModel() {
    return model;
  }
 
  /**
   * Builds a {@link List} if {@link Integer} IDs.
   * @param length The ID list length.
   * @return The IDs list.
   */
  private List<Integer> buildIds(int length) {
    List<Integer>	ids = new ArrayList<Integer>(length);
    
    for (int i = 0; i < length; i++) {
      ids.add(new Integer(i));
    }
    
    return ids;
  }

  //---------------------------------------------------
  // TABLE MODEL ITEM
  //---------------------------------------------------
  
  /**
   * The <code>TableModelItem</code> is the report table
   * model {@link Item} implementation.
   */
  public class TableModelItem implements Item {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>TableModelItem</code> instance.
     * @param rowIndex The row index.
     */
    public TableModelItem(int rowIndex) {
      this.rowIndex = rowIndex;
    }

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @SuppressWarnings("rawtypes")
    @Override
    public Property getItemProperty(Object id) {
      return new TableModelProperty(rowIndex, (Integer)id);
    }

    @Override
    public Collection<?> getItemPropertyIds() {
      return Collections.unmodifiableCollection(buildIds(model.getColumnCount()));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final int			rowIndex;
  }

  //---------------------------------------------------
  // TABLE MODEL PROPERTY
  //---------------------------------------------------
  
  /**
   * The <code>TableModelProperty</code> is the report table
   * model {@link Property} implementation.
   */
  @SuppressWarnings("rawtypes")
  public class TableModelProperty implements Property {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>TableModelProperty</code> instance.
     * @param rowIndex The row index.
     * @param columnIndex The column index.
     */
    public TableModelProperty(int rowIndex, int columnIndex) {
      this.rowIndex = rowIndex;
      this.columnIndex = columnIndex;
    }

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public Object getValue() {
      return model.getAccessibleColumn(columnIndex).format(model.getValueAt(rowIndex, columnIndex));
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
      throw new ReadOnlyException();
    }
    
    @Override
    public Class<?> getType() {
      return String.class;
    }
    
    @Override
    public boolean isReadOnly() {
      return true;
    }
    
    @Override
    public void setReadOnly(boolean newStatus) {}
    
    @Override
    public String toString() {
      return model.getAccessibleColumn(columnIndex).format(getValue());
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final int			rowIndex;
    private final int			columnIndex;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final MReport 			model;
}
