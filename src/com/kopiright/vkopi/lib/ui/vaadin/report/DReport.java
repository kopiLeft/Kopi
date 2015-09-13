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

import java.awt.Color;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;

import com.kopiright.vkopi.lib.report.MReport;
import com.kopiright.vkopi.lib.report.Parameters;
import com.kopiright.vkopi.lib.report.Point;
import com.kopiright.vkopi.lib.report.UReport;
import com.kopiright.vkopi.lib.report.VReport;
import com.kopiright.vkopi.lib.report.VSeparatorColumn;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.report.DTable.ColumnCollapseEvent;
import com.kopiright.vkopi.lib.ui.vaadin.report.DTable.ColumnCollapseListener;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DWindow;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.Table.ColumnReorderListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;

/**
 * The <code>DReport</code> is the visual part of the {@link VReport} model.
 * <p>The <code>DReport</code> ensure the implementation of the {@link UReport}
 * specifications.</p>
 */
@SuppressWarnings("serial")
public class DReport extends DWindow implements UReport {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DReport</code> instance.
   * @param report The report model.
   */
  public DReport(VReport report) {
    super(report);
    setImmediate(true);
    this.report = report;
    model=report.getModel();
    model.addReportListener(this);
    getModel().setDisplay(this);
    setSizeFull();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void run() throws VException {
    report.initReport();
    report.setMenu();
    table.focus();
    setInfoTable();
  }

  @Override
  public void build() {
    // load personal configuration
    parameters = new Parameters(new Color(2, 71, 166));
    table = new DTable(new VTable(model));
    table.setSelectable(true);
    table.setMultiSelect(false);
    table.setMultiSelectMode(MultiSelectMode.SIMPLE);
    table.setColumnReorderingAllowed(true);
    table.setColumnCollapsingAllowed(true);
    table.setNullSelectionAllowed(false);   
    table.setCellStyleGenerator(new ReportCellStyleGenerator(model, parameters));
    // 200 px is approximately the header window size + the actor pane size
    table.setHeight(UI.getCurrent().getPage().getBrowserWindowHeight() - 200, Unit.PIXELS);
    setContent(table);
    resetWidth();
    addTableListeners();
  }

  @Override
  public void redisplay() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	((ReportCellStyleGenerator)table.getCellStyleGenerator()).updateStyles();
	
	table.refreshRowCache();
	table.markAsDirty();
	UI.getCurrent().push();
      }
    });
  }

  /**
   * Reorders the report columns.
   * @param newOrder The new columns order.
   */
  public void reorder(final int[] newOrder) {
    model.columnMoved(newOrder);
    table.setVisibleColumns(newOrder);
    
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	for (int col = 0; col < model.getAccessibleColumnCount(); col++) {
	  if (model.getAccessibleColumn(col).isFolded() &&
              !(model.getAccessibleColumn(col) instanceof VSeparatorColumn))
	  {
	    table.setColumnCollapsed(col, true);
	  } else {
	    table.setColumnCollapsed(col, false);
	  }
	}
	UI.getCurrent().push();
      }
    });
  }
  
  @Override
  public void removeColumn(int position) {
    model.removeColumn(position);
    model.initializeAfterRemovingColumn(table.convertColumnIndexToView(position));
	    
    // set new order.
    int[]	pos = new int[model.getAccessibleColumnCount()];
	    
    for (int i = 0; i < model.getAccessibleColumnCount(); i ++) {
      pos[i] = (model.getDisplayOrder(i) > position ) ? model.getDisplayOrder(i) - 1 :  model.getDisplayOrder(i);
    }
	    
    table.fireStructureChanged();
    report.columnMoved(pos);
  }
  
  @Override
  public void addColumn(int position) {
    String	headerLabel;
    position = table.convertColumnIndexToView(position);
    position = position + 1;    
    headerLabel ="col" + (model.getColumnCount());
    model.addColumn(headerLabel, position);
    // move last column to position.
    int[] pos = new int[model.getAccessibleColumnCount()];

    for (int i = 0; i < position; i ++) {
      pos[i] = model.getDisplayOrder(i);
    }
	    
    for (int i = position + 1; i < model.getAccessibleColumnCount(); i ++) {
      pos[i] = model.getDisplayOrder(i - 1);
    }
	    
    pos[position] = model.getDisplayOrder(model.getAccessibleColumnCount() - 1);
    
    table.fireStructureChanged();
    report.columnMoved(pos);
  }
  
  @Override
  public void addColumn() {
    addColumn(table.convertColumnIndexToModel(table.getColumnCount()-1));
  }

  @Override
  public UTable getTable() {
    return table;
  }
  
  @Override
  public void contentChanged() {
    if (table != null) {
      ((VTable)table.getModel()).fireContentChanged();
      UI.getCurrent().access(new Runnable() {  
	
        @Override
        public void run() {
          table.refreshRowCache();
        }
      });
    }
  }
  
  @Override
  public void columnMoved(int[] pos) {
    reorder(pos);
    model.columnMoved(pos);
    redisplay();
  }
  
  @Override
  public void resetWidth() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	table.resetWidth();
      }
    });
  }
  
  @Override
  public int getSelectedColumn() {
    return table.getSelectedColumn();
  }
  
  @Override
  public Point getSelectedCell() {
    return new Point(table.getSelectedColumn(), table.getSelectedRow());
  }
  
  @Override
  public void setColumnLabel(final int column, final String label) {
    UI.getCurrent().access(new Runnable() {
      
      @Override
      public void run() {
	table.setColumnHeader(column, label);
      }
    }); 
  }

  /**
   * Notify the report table that the report content has been
   * change in order to update the table content.
   */
  public void fireContentChanged() {
    if (table != null) {
      table.getModel().fireContentChanged();
	      
      synchronized (table) {
	report.setMenu(); 
      }
    }
  }
	  
  /**
   * Return the columns display order.
   * @return The columns display order.
   */
  public int[] getDisplayOrder() {
    int[] displayOrder = new int[model.getColumnCount()];
	    
    for (int i = 0; i < model.getColumnCount(); i++) {
      displayOrder[i] = table.convertColumnIndexToModel(i);
    }

    return displayOrder;
  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  /**
   * Returns the number of columns displayed in the table
   * @return tThe number or columns displayed
   */
  public int getColumnCount() {
    return table.getColumnCount();
  }
	  
  /**
   * Add listeners to the report table.
   */
  private void addTableListeners() {
    final MReport currentModel = model;
    
    final ContextMenu labelPopupMenu = new ContextMenu();
    
    labelPopupMenu.addItemClickListener(new ContextMenuItemClickListener() {
      
      @Override
      public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
	ContextMenuItem clickedItem = (ContextMenuItem)event.getSource();

	if (clickedItem.getData().equals(VlibProperties.getString("set_column_info"))) {
	  table.setSelectedColumn(selectedColumn);
	  getModel().performAsyncAction(new KopiAction("set_column_info") {
	    public void execute() throws VException {
	      try {
	        report.setColumnInfo();
	      } catch(VException ve) {
	        // exception thrown by trigger.
	        throw ve;
	      }
	    }
          });
	} else if (clickedItem.getData().equals(VlibProperties.getString("sort_ASC"))) {
	  currentModel.sortColumn(selectedColumn, 1);
	  redisplay();
	} else if (clickedItem.getData().equals(VlibProperties.getString("sort_DSC"))) {
	  currentModel.sortColumn(selectedColumn, -1);
	  redisplay();
	} else if (clickedItem.getData().equals(VlibProperties.getString("add_column"))) {
	  addColumn(selectedColumn);
	} else if (clickedItem.getData().equals(VlibProperties.getString("remove_column"))) {
	  removeColumn(selectedColumn);
	} else if (clickedItem.getData().equals(VlibProperties.getString("set_column_data"))) {
	  table.setSelectedColumn(selectedColumn);
	  getModel().performAsyncAction(new KopiAction("set_column_data") {
	    
	    @Override
	    public void execute() throws VException {
	      try {
	        report.setColumnData();
	      } catch(VException ve) {
	        // exception thrown by the trigger.
	        throw ve;
              }
	    }
	  });
	}
	labelPopupMenu.hide();
      }
    });
    
    labelPopupMenu.setAsTableContextMenu(table);
    
    table.addItemClickListener(new ItemClickListener() {
      
      @SuppressWarnings("deprecation")
      @Override
      public void itemClick(ItemClickEvent event) {
	int	row = ((Integer) event.getItemId()).intValue();
	int	col = ((Integer) event.getPropertyId()).intValue();

	if (event.getButton() == ClickEvent.BUTTON_LEFT) {
	  if (event.isDoubleClick()) {
	    if (currentModel.isRowLine(row)) {
	      try {
		report.editLine();
	      } catch (VException ef) {
	    	ef.printStackTrace();
	      }
	    } else {
	      if (row >= 0) {
		if (currentModel.isRowFold(row, col)) {
		  currentModel.unfoldingRow(row, col);
		} else {
		  currentModel.foldingRow(row, col);
		}
	      } 
	    }
	  } else if (event.isShiftKey() && event.isCtrlKey()) {
	    currentModel.sortColumn(col);
	  } else if (event.isCtrlKey()) {
	    if (row >= 0) {
	      if (currentModel.isRowFold(row, col)) {
		currentModel.unfoldingRow(row, col);
	      } else {
		currentModel.foldingRow(row, col);
	      }
	    } 
	  } else if (event.isShiftKey()) {
	    if (currentModel.isColumnFold(col)) {
	      currentModel.unfoldingColumn(col);
	    } else {
	      currentModel.foldingColumn(col);
	    }
	  } else {
	    table.refreshRowCache();
	    synchronized (table) {
	      report.setMenu(); 
	    }
	  }
	} else if (event.getButton() == ClickEvent.BUTTON_RIGHT) {
	  // labelPopupMenu.hide();
	  if (row >= 0) {
	    if (currentModel.isRowFold(row, col)) {
	      currentModel.unfoldingRow(row, col);
	    } else {
	      currentModel.foldingRow(row, col);
	    }
	  } 
	} else if (event.getButton() == ClickEvent.BUTTON_MIDDLE) {
	  if (currentModel.isColumnFold(col)) {
	    currentModel.unfoldingColumn(col);
	  } else {
	    currentModel.foldingColumn(col);
	  }
	}
      }
    });

    table.addColumnReorderListener(new ColumnReorderListener() {
      
      @Override
      public void columnReorder(ColumnReorderEvent event) {
	int[]		newColumnOrder = new int[model.getColumnCount()];
	Object[] 	visibleColumns = table.getVisibleColumns();
	int		hiddenColumnsCount = 0;

	for (int i = 0; i < newColumnOrder.length; i++) {
	  if (!model.getAccessibleColumn(i).isVisible()) {
	    hiddenColumnsCount += 1;
	    newColumnOrder[i] = model.getDisplayOrder(i);
	  } else {
	    newColumnOrder[i] = ((Integer) visibleColumns[i - hiddenColumnsCount]).intValue();
	  }
	}

	model.columnMoved(newColumnOrder);
      }
    });

    table.addListener(new ColumnCollapseListener() {
      
      @Override
      public void columnCollapsed(ColumnCollapseEvent event) {
	for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
	  model.getAccessibleColumn(i).setFolded(false);
	 
	}

	for (Object propertyId : event.getPropertyIds()) {
	  int	col = Integer.parseInt((String) propertyId) - 1;

	  model.getAccessibleColumn(col).setFolded(true);
	}
	
	table.fireStructureChanged();
      }
    });

    
    table.addHeaderClickListener(new HeaderClickListener() {
      
      @SuppressWarnings("deprecation")
      @Override
      public void headerClick(HeaderClickEvent event) {
	final int	column = ((Integer) event.getPropertyId()).intValue();

	if (event.getButton() == ClickEvent.BUTTON_LEFT) {
	  if (event.isCtrlKey()) {
	    if (currentModel.isColumnFold(column)) {
	      currentModel.unfoldingColumn(column);
	    } else {
	      currentModel.foldingColumn(column);
	    }
	  } else if (event.isShiftKey()) {
	    currentModel.sortColumn(column);
	    redisplay();
	  } 
	} else if (event.getButton() == ClickEvent.BUTTON_RIGHT) {
	  selectedColumn = column;
	  
	  labelPopupMenu.removeAllItems();
	  
	  labelPopupMenu.addItem(VlibProperties.getString("set_column_info")).setData(VlibProperties.getString("set_column_info"));
	  labelPopupMenu.addItem(VlibProperties.getString("sort_ASC")).setData(VlibProperties.getString("sort_ASC"));
	  labelPopupMenu.addItem(VlibProperties.getString("sort_DSC")).setData(VlibProperties.getString("sort_DSC"));
	  labelPopupMenu.addItem(VlibProperties.getString("add_column")).setData(VlibProperties.getString("add_column"));
	  
	  if (currentModel.getAccessibleColumn(selectedColumn).isAddedAtRuntime()) {
	    labelPopupMenu.addItem(VlibProperties.getString("remove_column")).setData(VlibProperties.getString("remove_column"));
	    labelPopupMenu.addItem(VlibProperties.getString("set_column_data")).setData(VlibProperties.getString("set_column_data"));
	  } 
	}
      }
    });

    table.addValueChangeListener(new ValueChangeListener() {
      
      public void valueChange(ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
          table.setSelectedRow(((Integer) event.getProperty().getValue()).intValue());
        }
        report.setMenu();
      }
    });
	    
    table.addItemSetChangeListener(new ItemSetChangeListener() {
	      
      public void containerItemSetChange(ItemSetChangeEvent event) {
	setInfoTable();
	table.resetCachedInfos();
      }
    });
  }
	  
  /**
   * Display table informations in the footer of the table
   */
   private void setInfoTable() {
     setStatisticsText(+ table.getRowCount()
                       + "/"
                       + model.getBaseRowCount()
                       + "/"
                       + model.getVisibleRowCount());
  }
  
  /**
   * Returns the parent report tab sheet.
   * @return The parent report tab sheet.
   */
  public Tab getParentTab() {
    return parentTab;
  }
  
  /**
   * Sets the parent report tab sheet.
   * @param parentTab The parent report tab sheet.
   */
  public void setParentTab(Tab parentTab) {
    this.parentTab = parentTab;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final MReport				model; // report model
  private VReport				report;
  private DTable				table;
  private Parameters				parameters;
  private int					selectedColumn;
  private Tab					parentTab;
}