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

import java.util.LinkedList;
import java.util.List;

import org.gwt.advanced.client.ui.widget.AdvancedFlexTable;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable;
import com.vaadin.client.ApplicationConnection;

/**
 * The list dialog table special component.
 */
@SuppressWarnings("serial")
public class Table extends AdvancedFlexTable implements SortListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new list dialog table widget. The table is not resizable.
   * @param headers is a list of header labels (including invisible).
   * @param classes Widget classes.
   */
  public Table(ApplicationConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("The application connection should be provided");
    }
    this.connection = connection;
    setCellPadding(0);
    setCellSpacing(0);
    headers = new LinkedList<TableHeader>();
    columns = new LinkedList<TableColumn>();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the table model.
   * @param model The table model.
   */
  public void setModel(TableModel model) {
    if (model == null) {
      throw new IllegalArgumentException("The table model should be provided");
    }
    this.model = model;
    this.modelRows = model.idents;
  }
  
  /**
   * Renders the table content.
   */
  public void render() {
    drawHeaders();
    drawContent();
  }
  
  @Override
  public void onSort(int sortIndex, int mode) {
    TableColumn		column = columns.get(sortIndex);
    int[]		newOrder = column.sort(mode);
    
    // update all columns rows
    clear();
    for (TableColumn col : columns) {
      col.refresh(this, newOrder);
    }
    // update model rows
    modelRows = refreshModel(newOrder);
  }
  
  /**
   * Returns the model row count. If no model is defined, the DOM row number will be returned.
   * @return The  model row count.
   */
  public int getModelRowCount() {
    return model.getRowCount();
  }
  
  /**
   * Refreshes the model rows.
   * @param newOrder The new order.
   * @return The new model rows.
   */
  protected int[] refreshModel(int[] newOrder) {
    int[]	newModel = new int[newOrder.length];
    
    for (int i = 0; i < newOrder.length; i++) {
      // newOrder[i] --> i
      // modelRows[i] --> modelRows[newOrder[i]]
      newModel[i] = modelRows[newOrder[i]];
    }
    
    return newModel;
  }
  
  /**
   * This method renders column headers.
   */
  public void drawHeaders() {
    for (int column = 0; column < model.getColumnCount(); column++) {
      renderTableHeader(model.getColumnName(column), column);
    }
  }
  
  /**
   * This method renders grid content.
   *
   * @param data The content data.
   */
  public void drawContent() {
    TableCell[][]	cells = new TableCell[model.getColumnCount()][model.getRowCount()];
    
    for (int column = 0; column < model.getColumnCount(); column++) {
      for (int row = 0; row < model.getRowCount(); row++) {
	cells[column][row] = new TableCell(row, column, model.getColumnType(column), model.getColumnAlign(column));
	cells[column][row].setText(model.getValueAt(row, column) == null ? "" : model.getValueAt(row, column));
      }
    }
    // draw columns
    for (int column = 0; column < cells.length; column++) {
      drawColumn(cells[column], column);
    }
  }

  /**
   * Draws a column.
   * @param cells The column cells.
   * @param colIndex The column index.
   */
  public void drawColumn(TableCell[] cells, int colIndex) {
    TableColumn			column;
    
    column = new TableColumn(colIndex, model.getColumnType(colIndex), model.getColumnAlign(colIndex), cells);
    columns.add(colIndex, column);
    column.attach(this);
  }

  /**
   * This method converts grid row number to model row number.
   *
   * @param row is a row number.
   * @return a row number.
   */
  public int getModelRow(int row) {
    return modelRows[row];
  }
  
  /**
   * Returns the displayed value at the given row and column.
   * @param row The row number.
   * @param col The column number.
   * @return The row value.
   */
  public String getDisplayedValueAt(int row, int col) {
    if (row >= 0 && row < model.getRowCount() && col >= 0 && col < getColumnCount()) {
      return model.getValueAt(getModelRow(row), col);
    } else {
      return "";
    }
  }
  
  /**
   * Renders a table header with the given caption and index.
   * @param caption The header caption.
   * @param column The column index.
   */
  protected void renderTableHeader(String caption, int column) {
    TableHeader		header;
    
    header = new TableHeader();
    header.setCaption(caption);
    header.setHeaderIndex(column);
    header.setNoneImage(getNoneImage());
    header.setAscImage(getASCImage());
    header.setDescImage(getDESCImage());
    header.addSortListener(this);
    headers.add(header);
    // attach the header to the table
    setHeaderWidget(column, header);
  }
  
  /**
   * Returns the none sorting image.
   * @return The none sorting image.
   */
  protected String getNoneImage() {
    return ResourcesUtil.getImageURL(connection, Icons.ARROW);
  }
  
  /**
   * Returns the ASC sorting image.
   * @return The ASC sorting image.
   */
  protected String getASCImage() {
    return ResourcesUtil.getImageURL(connection, Icons.ARROW_UP);
  }
  
  /**
   * Returns the DESC sorting image.
   * @return The DESC sorting image.
   */
  protected String getDESCImage() {
    return ResourcesUtil.getImageURL(connection, Icons.ARROW_DOWN);
  }

  /**
   * Setter for property 'currentRow'.
   * @param currentRow Value to set for property 'currentRow'.
   */
  public void setCurrentRow(int currentRow) {
    HTMLTable.RowFormatter 	rowFormatter = getRowFormatter();

    if (selectedRow == currentRow) {
      return;
    }
    
    if (selectedRow >= 0) {
      rowFormatter.removeStyleName(selectedRow, "selected-row");
    }

    if (currentRow >= 0 && currentRow < getRowCount()) {
      selectRow(currentRow);
    }
  }
  
  /**
   * Sets the given cell as a highlighted one.
   * @param row The cell row
   * @param col The cell column.
   */
  public void highlightCell(int row, int col) {
    HTMLTable.CellFormatter             cellFormatter;
    
    cellFormatter = getCellFormatter();
    if (highlightedCell != null && highlightedCell[0] == row && highlightedCell[1] == col) {
      return;
    }
    if (highlightedCell != null) {
      cellFormatter.removeStyleName(highlightedCell[0], highlightedCell[1], "highlighted-cell");
    }
    if (row >= 0 && row < getRowCount() && col >= 0 && col < getCellCount(row)) {
      cellFormatter.removeStyleName(row, col,  "highlighted-cell");
      cellFormatter.addStyleName(row, col,  "highlighted-cell");
      if (highlightedCell == null) {
        highlightedCell = new int[2];
      }
      highlightedCell[0] = row;
      highlightedCell[1] = col;
    }
  }
  
  /**
   * Removes highlighted cell style if needed.
   */
  public void unhighlightCell() {
    HTMLTable.CellFormatter             cellFormatter;
    
    cellFormatter = getCellFormatter();
    if (highlightedCell != null) {
      cellFormatter.removeStyleName(highlightedCell[0], highlightedCell[1], "highlighted-cell");
    }
  }
  
  /**
   * This method marks the specified row as selected.<p/>
   * It works similarly to the {@link #setCurrentRow(int)} method but doesn't clear a previous selection.
   * If the multiple rows selection is disabled it checks whether there is at least one selected row and if no
   * it makes selection. Otherwise it does nothing.<p/>
   * If multiple mode is enabled it always selects a row.
   *
   * @param row is a row number to make selected.
   */
  protected void selectRow(int row) {
    HTMLTable.RowFormatter	rowFormatter = getRowFormatter();
    
    rowFormatter.removeStyleName(row, "selected-row");
    rowFormatter.addStyleName(row, "selected-row");
    selectedRow = row;
    calculateVisibleRegion();
  }
  
  /**
   * Returns the selected row.
   * @return The selected row.
   */
  public int getSelectedRow() {
    return selectedRow;
  }
  
  /**
   * Returns the column count.
   * @return The column count.
   */
  public int getColumnCount() {
    return model.getColumnCount();
  }
  
  /**
   * Returns the selected model row.
   * @return The selected model row.
   */
  public int getSelectedModelRow() {
    return getModelRow(selectedRow);
  }
  
  /**
   * Returns the clicked row
   * @param event The click event.
   * @return The clicked row.
   */
  public int getClickedRow(ClickEvent event) {
    Cell	cell = getCellForEvent(event);
    
    if (cell != null) {
      return cell.getRowIndex();
    }
    
    return -1;
  }
  
  /**
   * Shifts up a step row.
   * @param step The row step
   */
  public void shiftUp(int step) {
    setCurrentRow(Math.max(0, selectedRow - step));
  }
  
  /**
   * Shifts down a step row.
   * @param step The row step
   */
  public void shiftDown(int step) {
    setCurrentRow(Math.min(getRowCount(), selectedRow + step));
  }
  
  /**
   * Calculates the visible region of the table rows.
   */
  protected void calculateVisibleRegion() {
    int		rowHeight = getRowFormatter().getElement(0).getClientHeight();
    int		bodyHeight = getBodyElement().getClientHeight();
    int		scrollTop = getBodyElement().getScrollTop();
    int 	minVisibleRow = (int)(scrollTop / rowHeight);
    int 	maxVisibleRow = minVisibleRow + (int)(bodyHeight / rowHeight) - 1;
    
    if (selectedRow > maxVisibleRow) {
      setScrollTop(scrollTop + ((selectedRow - maxVisibleRow) * rowHeight));
    }
    if (selectedRow < minVisibleRow) {
      setScrollTop(scrollTop - ((minVisibleRow - selectedRow) * rowHeight));
    }
  }
  
  /**
   * Returns the header element of a given column.
   * @param column The column index.
   * @return The header element.
   */
  protected Element getHeaderElement(int column) {
    return DOM.getChild(DOM.getFirstChild(getTHeadElement()), column);
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
	// scroll table body when it is needed
	if (getTableHeight() > Math.max(0, Window.getClientHeight() - getAbsoluteTop())) {
	  updateTableStyle();
	  updateCellsSizes();
	  getBodyElement().getStyle().setOverflowX(Overflow.HIDDEN);
	}
	
	if (getTableWidth() > Window.getClientWidth()) {
	  calculateTableWidth();
	  getElement().getStyle().setOverflowX(Overflow.AUTO);
	  getElement().getStyle().setWidth(Window.getClientWidth(), Unit.PX);
	}
      }
    });
  }
  
  /**
   * Updates the table styles for vertical scroll
   */
  protected void updateTableStyle() {
    // display table header and table body as block to allow vertical scroll
    getElement().getStyle().setDisplay(Display.BLOCK);
    getTHeadElement().getStyle().setDisplay(Display.BLOCK);
    getBodyElement().getStyle().setDisplay(Display.BLOCK);
    getBodyElement().getStyle().setPosition(Position.RELATIVE);
    getBodyElement().getStyle().setLeft(0, Unit.PX);
    getBodyElement().getStyle().setTop(0, Unit.PX);
    getBodyElement().getStyle().setOverflowY(Overflow.AUTO);
    getBodyElement().getStyle().setHeight(Math.max(0, Window.getClientHeight() - getBodyElement().getAbsoluteTop() - 35), Unit.PX);
    // set head position as relative
    getTHeadElement().getStyle().setPosition(Position.RELATIVE);
    getTHeadElement().getStyle().setLeft(0, Unit.PX);
    getTHeadElement().getStyle().setTop(0, Unit.PX);
    for (int row = 0; row  < getRowCount(); row++) {
      getRowFormatter().getElement(row).getStyle().setDisplay(Display.BLOCK);
    }
    for (int column = 0; column < columns.size(); column++) {
      getHeaderElement(column).getStyle().setDisplay(Display.INLINE_BLOCK);
    }
    DOM.getFirstChild(getTHeadElement()).getStyle().setDisplay(Display.BLOCK);
  }
  
  /**
   * Updates the cell sizes
   */
  protected void updateCellsSizes() {
    for (int column = 0; column < columns.size(); column++) {
      int	width = Math.max(getHeaderElement(column).getClientWidth(), getMaxCellWidth(column));
      
      getHeaderElement(column).getStyle().setWidth(width, Unit.PX);
      if (column == columns.size() - 1) {
	getHeaderElement(column).getStyle().setWidth(width + columns.size(), Unit.PX);
      }
      for (int row = 0; row < getRowCount(); row++) {
	getCellFormatter().getElement(row, column).getStyle().setWidth(width, Unit.PX);
      }
    }
    setRowsWidth(getTHeadElement().getClientWidth());
  }
  
  /**
   * Calculates the table width.
   */
  protected void calculateTableWidth() {
    int		width = 0;
    
    for (int column = 0; column < getCellCount(0); column++) {
      width += getHeaderElement(column).getClientWidth();
    }
    
    // set table width.
    setTableWidth(width);
  }
  
  /**
   * Sets the table width.
   * @param width The table width.
   */
  public void setTableWidth(int width) {
    getBodyElement().getStyle().setWidth(width, Unit.PX);
    getTHeadElement().getStyle().setWidth(width, Unit.PX);
  }
  
  /**
   * Returns the maximum cell width.
   * @param column The column index.
   * @return The maximum width in a cell.
   */
  protected int getMaxCellWidth(int column) {
    int		width = 0;
    
    for (int row = 0; row < getRowCount(); row++) {
      width = Math.max(width, getCellFormatter().getElement(row, column).getClientWidth());
    }

    return width;
  }
  
  /**
   * Sets the rows to have the given width.
   * @param value The rows width.
   */
  protected void setRowsWidth(int value) {
    for (int row = 0; row < getRowCount(); row++) {
      getRowFormatter().getElement(row).getStyle().setWidth(value, Unit.PX);
    }
  }
  
  /**
   * Sets the scroll top position.
   * @param scrollTop The top position.
   */
  public final void setScrollTop(int scrollTop) {
    getBodyElement().setScrollTop(scrollTop);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final ApplicationConnection		connection;
  private TableModel				model;
  private List<TableHeader>			headers;
  private List<TableColumn>			columns;
  private int[]					modelRows;
  private int					selectedRow = -1;
  private int[]                                 highlightedCell;
}
