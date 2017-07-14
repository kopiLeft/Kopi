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

import java.util.Locale;

import org.kopi.vkopi.lib.form.VBooleanField;
import org.kopi.vkopi.lib.form.VListDialog;
import org.kopi.vkopi.lib.list.VBooleanColumn;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanRenderer;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionModel.HasUserSelectionAllowed;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.renderers.TextRenderer;

@SuppressWarnings("serial")
public class ListTable extends Grid  {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public ListTable(final VListDialog model) {
    super(new ListContainer(model));
    setSelectionMode(SelectionMode.SINGLE);
    ((HasUserSelectionAllowed)getSelectionModel()).setUserSelectionAllowed(false);
    setColumnCollapsingAllowed(false);
    setColumnResizeMode(ColumnResizeMode.ANIMATED);
    setColumnReorderingAllowed(true);
    setEditorEnabled(false);
    setColumnHeaders(model.getTitles());
    installConverters(model);
    setHeightMode(HeightMode.ROW);
    setTableWidth(model);
    installFilters(model);
    setCellStyleGenerator(new ListStyleGenerator(model));
    recalculateColumnWidths();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Install filters on all properties.
   */
  public void installFilters(VListDialog model) {
    HeaderRow   filterRow = appendHeaderRow();
    
    filterRow.setStyleName("list-filter");
    for (final Object propertyId : getContainerDataSource().getContainerPropertyIds()) {
      HeaderCell        cell = filterRow.getCell(propertyId);
      TextField         filter = new TextField();
      
      filter.setStyleName("filter-text");
      filter.setImmediate(true);
      filter.addTextChangeListener(new TextChangeListener() {
        
        @Override
        public void textChange(TextChangeEvent event) {
          getContainerDataSource().removeContainerFilters(propertyId);
          if (event.getText().length() > 0) {
            getContainerDataSource().addContainerFilter(propertyId,
                                                        event.getText(),
                                                        true,
                                                        false);
            // select the first item when the content is filtered
            // to not loose grid focus and thus not loose navigation shortcuts
            select(getContainerDataSource().firstItemId());
          }
        }
      });
      cell.setComponent(filter);
    }
    addStyleName("filtred");
  }
  
  /**
   * Looks the the item ID that its first column starts with the given pattern.
   * @param pattern The search pattern.
   * @return The found item ID or null if none of the search corresponds to the searched pattern
   */
  public Object search(String pattern) {
    return getContainerDataSource().serach(pattern);
  }
  
  /**
   * Calculates the table width based on its content.
   * @param model The data model.
   */
  protected void setTableWidth(VListDialog model) {
    int         width;
    
    width = 0;
    for (int col = 0; col < model.getColumnCount(); col++) {
      int       columnWidth;
      
      columnWidth = getColumnWidth(model, col) + 36;
      getColumn(col).setWidth(columnWidth);
      width += columnWidth;
    }
    
    setWidth(Math.min(width, Page.getCurrent().getBrowserWindowWidth() - 20), Unit.PIXELS);
  }
 
  /**
   * Calculates the column width based on the column rows content.
   * @param model The list data model.
   * @param col The column index.
   * @return The estimated wolumn width.
   */
  protected int getColumnWidth(VListDialog model, int col) {
    int                 width;
    
    width = 0;
    for (int row = 0; row < model.getCount(); row++) {
      String            value;
      
      value = model.getColumns()[col].formatObject(model.getValueAt(row, col)).toString();
      width = Math.max(width, Math.max(value.length(), model.getTitles()[col].length()));
    }
    
    return 8 * width;
  }
  
  protected VListColumn getListColumn(VListDialog model, Object propertyId) {
    return model.getColumns()[(Integer)propertyId];
  }
  
  /**
   * Install converters for values formatting.
   */
  protected void installConverters(VListDialog model) {
    for (Column column : getColumns()) {
      if (getListColumn(model, column.getPropertyId()) instanceof VBooleanColumn) {
        column.setRenderer(createBooleanRenderer(), createBooleanConverter());
      } else {
        column.setRenderer(new TextRenderer(), new ListConverter(model.getColumns()[(Integer)column.getPropertyId()]));
      }
    }
  }
  
  /**
   * Creates the conversion engine for grid data rendering. 
   * @return The boolean converter
   */
  protected Converter<Boolean, Object> createBooleanConverter() {
    return new Converter<Boolean, Object>() {

      @Override
      public Object convertToModel(Boolean value, Class<? extends Object> targetType, Locale locale)
        throws ConversionException
      {
        return value;
      }

      @Override
      public Boolean convertToPresentation(Object value, Class<? extends Boolean> targetType, Locale locale)
        throws ConversionException
      {
        return (Boolean) value;
      }

      @Override
      public Class<Object> getModelType() {
        return Object.class;
      }

      @Override
      public Class<Boolean> getPresentationType() {
        return Boolean.class;
      }
    };
  }

  /**
   * Creates the renderer for boolean column
   * @return The boolean renderer
   */
  protected Renderer<Boolean> createBooleanRenderer() {
    return new BooleanRenderer(getTrueRepresentation(), getFalseRepresentation());
  }
  
  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected String getTrueRepresentation() {
    return VBooleanField.toText(Boolean.TRUE);
  }
  
  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected String getFalseRepresentation() {
    return VBooleanField.toText(Boolean.FALSE);
  }


  /**
   * Sets whether column collapsing is allowed or not.
   * @param collapsingAllowed specifies whether column collapsing is allowed.
   */
  public void setColumnCollapsingAllowed(boolean collapsingAllowed) {
    for (Column column : getColumns()) {
      column.setHidable(collapsingAllowed);
    }
  }
  
  /**
   * Sets the columns headers of this list table.
   * @param headers The column headers.
   */
  public void setColumnHeaders(String[] headers) {
    for (int i = 0; i < headers.length; i++) {
      setColumnHeader(i, headers[i]);
    }
  }
  
  /**
   * Sets the column header for the specified column;
   * @param propertyId the propertyId identifying the column.
   * @param header the header to set.
   */
  public void setColumnHeader(int propertyId, String header) {
    getColumn(propertyId).setHeaderCaption(header);
  }
  
  @Override
  public ListContainer getContainerDataSource() {
    return (ListContainer) super.getContainerDataSource();
  }
  
  /**
   * Fires a container item set change.
   */
  public void tableChanged() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getContainerDataSource().fireItemSetChange();
      }
    });
  }
}
