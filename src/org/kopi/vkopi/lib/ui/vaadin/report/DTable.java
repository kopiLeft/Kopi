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

package org.kopi.vkopi.lib.ui.vaadin.report;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import org.kopi.vkopi.lib.report.UReport.UTable;
import org.kopi.vkopi.lib.report.VFixnumColumn;
import org.kopi.vkopi.lib.report.VIntegerColumn;
import org.kopi.vkopi.lib.report.VReportColumn;
import org.kopi.vkopi.lib.report.VSeparatorColumn;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * The <code>DTable</code> is a vaadin {@link Table} implementing the {@link Utable}
 * specifications.
 */
@SuppressWarnings("serial")
public class DTable extends Table implements UTable, ItemClickListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DTable</code> instance.
   * @param model The table model.
   */
  public DTable(VTable model) {
    super("", model);
    this.model = model;
    setImmediate(true);
    // not really necessary.
    //alwaysRecalculateColumnWidths = true;
    addStyleName("small");
    addStyleName("borderless");
    addStyleName("report");
    setWidth("100%");
    addItemClickListener((ItemClickListener)this);
    setItemDescriptionGenerator(new DescriptionGenerator());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public int convertColumnIndexToModel(int viewColumnIndex) {
    Object[] visibleColumns = getVisibleColumns();
	    
    return ((Integer) visibleColumns[viewColumnIndex]).intValue();
  }
  
  @Override
  public int convertColumnIndexToView(int modelColumnIndex) {
    Object[] visibleColumns = getVisibleColumns();
	    
    for (int i = 0; i < visibleColumns.length; i++) {
      int modelIndex = ((Integer) visibleColumns[i]).intValue();
	      
      if (modelIndex == modelColumnIndex) {
	return i;
      }
    }
	    
    return -1;
  }
	  
  @Override
  public String getColumnHeader(Object propertyId) {
    return getModel().getColumnName(((Integer)propertyId).intValue());
  }
	  
  @Override
  public void changeVariables(Object source, Map<String, Object> variables) {
    super.changeVariables(source, variables);

    if (isColumnCollapsingAllowed()) {
      if (variables.containsKey("collapsedcolumns")) {
	final Object[] ids = (Object[]) variables.get("collapsedcolumns");

	if (hasListeners(ColumnCollapseEvent.class)) {
	  fireEvent(new ColumnCollapseEvent(this, ids));
	}
      }
    }
  }

  /**
   * Sets the table visible columns.
   * @param visibleColumns The table visible columns.
   */
  public void setVisibleColumns(int[] visibleColumns) {
    final Object[]	newVisibleColumns = new Object[visibleColumns.length];
	    
    for (int i = 0; i < visibleColumns.length; i++) {
      newVisibleColumns[i] = new Integer(visibleColumns[i]);
    }
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	DTable.super.setVisibleColumns(newVisibleColumns);
	UI.getCurrent().push();
      }
    });	    
  }

  /**
   * Notifies the table model that table structure has been changed.
   */
  public void fireStructureChanged() {
    getModel().fireStructureChanged();
  }

  /**
   * Returns the table column count.
   * @return The table column count.
   */
  public int getColumnCount() {
    return getModel().getColumnCount();
  }

  /**
   * Returns the table row count.
   * @return The table row count.
   */
  public int getRowCount() {
    return getModel().getItemIds().size();
  }
	  
  /**
   * Returns the table model.
   * @return The table model.
   */
  public VTable getModel() {
    return model;
  }

  /**
   * Registers a {@link ColumnCollapseListener} to this table.
   * @param listener The listener to be registered.
   */
  public void addListener(ColumnCollapseListener listener) {
    addListener(ColumnCollapseEvent.class, listener, ColumnCollapseEvent.METHOD);
  }

  /**
   * Returns the selected row.
   * @return The selected row.
   */
  public int getSelectedRow() {
    return selectedRow;
  }

  /**
   * Returns the selected column.
   * @return The selected column.
   */
  public int getSelectedColumn() {
    return selectedColumn;
  }
  
  /**
   * Sets the selected column.
   * @param selectedColumn The selected column.
   */
  public void setSelectedColumn(int selectedColumn) {
    this.selectedColumn = selectedColumn;
  }
  
  @Override
  public void itemClick(ItemClickEvent event) {
    selectedRow = ((Integer) event.getItemId()).intValue();
    selectedColumn = ((Integer) event.getPropertyId()).intValue();
  }

  /**
   * Reset all columns widths.
   */
  public void resetWidth() {
    for (int i = 0; i < model.getColumnCount(); i++) {
      resetColumnSize(i);
    }
    markAsDirtyRecursive();
  }

  /**
   * Resets the column size at a given position. 
   * @param pos The column position.
   */
  private void resetColumnSize(int pos) {
    VReportColumn	column = model.getModel().getAccessibleColumn(convertColumnIndexToModel(pos));
    int			width;

    if (column.isFolded() && !(column instanceof VSeparatorColumn)) {
      width = 1;
    } else if (column instanceof VFixnumColumn || column instanceof VIntegerColumn) {
      width = Math.max(column.getLabel().length(), column.getWidth());
      // Integer and Fixed column can contain , data generated by operations like sum, multiplication
      // --> compute column width occording to data.
      width = Math.max(width, model.getModel().computeColumnWidth(convertColumnIndexToModel(pos)));
    } else {
      width = Math.max(column.getLabel().length(), column.getWidth());
    }

    if (width != 0) {
      width = (width * 9) + 2;
    }
    setColumnWidth(pos, width);
  }

  /**
   * Sets the table selected row.
   * @param row The selected row.
   */
  public void setSelectedRow(int row) {
    selectedRow = row;
  }

  /**
   * Resets the table cached information.
   */
  public void resetCachedInfos() {
    selectedRow = -1;
    selectedColumn = -1;
    select(null);
  }
	  
  //---------------------------------------------------
  // COLUMN COLLAPSE LISTENER
  //---------------------------------------------------

  /**
   * The <code>ColumnCollapseListener</code> notifies registered
   * objects that a column collapse event happened.
   */
  public interface ColumnCollapseListener extends Serializable {
	    
    /**
     * Fired when a column collapse event happens.
     * @param event The columns collapse event.
     */
    public void columnCollapsed(ColumnCollapseEvent event);
  }
	  
  //----------------------------------------------
  // COLUMN COLLAPSE EVENT
  //----------------------------------------------

  /**
   * The <code>ColumnCollapseEvent</code> is a {@link Component.Event}
   * that handle column collapse events.
   */
  public static class ColumnCollapseEvent extends Component.Event {

    //-------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------
    
    /**
     * Creates a new <code>ColumnCollapseEvent</code> instance.
     * @param source The source component.
     * @param propertyIds The collapsed columns IDs.
     */
    public ColumnCollapseEvent(Component source, Object[] propertyIds) {
      super(source);
      this.propertyIds = propertyIds;
    }

    /**
     * Returns the table property IDs.
     * @return The table property IDs.
     */
    public Object[] getPropertyIds() {
      return propertyIds;
    }

    //-------------------------------------------
    // DATA MEMBERS
    //-------------------------------------------

    private Object[]			propertyIds;
    public static final Method		METHOD;

    static {
      try {
	METHOD = ColumnCollapseListener.class.getDeclaredMethod("columnCollapsed", new Class[] {ColumnCollapseEvent.class});
      } catch (final java.lang.NoSuchMethodException e) {
	// This should never happen
	throw new java.lang.RuntimeException(e);
      }
    }
  }
  
  /**
   * The <code>DescriptionGenerator</code> is the report implementation
   * of the {@link ItemDescriptionGenerator}
   */
  public class DescriptionGenerator implements ItemDescriptionGenerator {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public String generateDescription(Component source,
                                      Object itemId,
	                              Object propertyId)
    {
      if (propertyId != null) {
        return Utils.createTooltip(model.getModel().getAccessibleColumn(((Integer) propertyId).intValue()).getHelp());
      } else {
        return null;
      }
    }
  }
	  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private int				selectedRow = -1;
  private int				selectedColumn = -1;
  private VTable                        model;
}
