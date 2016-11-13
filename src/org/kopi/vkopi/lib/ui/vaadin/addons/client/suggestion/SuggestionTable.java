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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion;

import java.util.List;

import org.gwt.advanced.client.ui.widget.AdvancedFlexTable;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * Suggestion table widget to show all possible columns
 */
public class SuggestionTable extends AdvancedFlexTable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Default constructor
   */
  public SuggestionTable() {
    sinkEvents(Event.ONCLICK);
  }
  
  /**
   * Creates the suggestion table widget.
   * @param suggestions The suggestion list.
   */
  public SuggestionTable(List<AutocompleteSuggestion> suggestions) {
    setSuggestions(suggestions);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the suggestions list for this table.
   * @param suggestions The suggestions list.
   */
  public void setSuggestions(List<AutocompleteSuggestion> suggestions) {
    this.suggestions = suggestions;
    init();
  }
  
  /**
   * The widget initialization.
   */
  protected void init() {
    for (int row = 0; row < suggestions.size(); row ++) {
      for (int col = 0; col < suggestions.get(row).getColumnCount(); col++) {
	setHTML(row, col, suggestions.get(row).getDisplayStringAsHTML(col));
      }
    }
  }

  /**
   * Setter for property 'currentRow'.
   * @param currentRow Value to set for property 'currentRow'.
   */
  public void setCurrentRow(int currentRow) {
    HTMLTable.RowFormatter 	rowFormatter = getRowFormatter();

    if (selectedRow >= 0) {
      rowFormatter.removeStyleName(selectedRow, "selected-row");
    }

    if (selectedRow == currentRow) {
      return;
    }

    if (currentRow >= 0 && currentRow < getRowCount()) {
      selectRow(currentRow);
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
   * Returns the selected suggestion.
   * @return The selected suggestion.
   */
  public Suggestion getSelectedSuggestion() {
    return getSuggestion(selectedRow);
  }
  
  /**
   * Returns the suggestion encapsulated in the given row.
   * @param row The table row.
   * @return The suggestion encapsulated in the given row.
   */
  protected Suggestion getSuggestion(int row) {
    if (suggestions == null || row == -1) {
      return null;
    } else {
      return new DefaultSuggestion(suggestions.get(row), 0);
    }
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
    getBodyElement().getStyle().setDisplay(Display.BLOCK);
    getBodyElement().getStyle().setPosition(Position.RELATIVE);
    getBodyElement().getStyle().setLeft(0, Unit.PX);
    getBodyElement().getStyle().setTop(0, Unit.PX);
    getBodyElement().getStyle().setOverflowY(Overflow.AUTO);
    getBodyElement().getStyle().setHeight(Math.max(0, Window.getClientHeight() - getBodyElement().getAbsoluteTop() - 35), Unit.PX);
  }
  
  /**
   * Updates the cell sizes
   */
  protected void updateCellsSizes() {
    int         rowWidth = 0;
    
    for (int column = 0; column < suggestions.get(0).getColumnCount(); column++) {
      int	width = getMaxCellWidth(column);
      
      for (int row = 0; row < getRowCount(); row++) {
	getCellFormatter().getElement(row, column).getStyle().setWidth(width, Unit.PX);
      }
      
      rowWidth += width;
    }
    
    setRowsWidth(rowWidth);
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
   * Calculates the table width.
   */
  protected void calculateTableWidth() {
    int		width = 0;
    
    for (int column = 0; column < getCellCount(0); column++) {
      width += getMaxCellWidth(column);
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
   * Sets the scroll top position.
   * @param scrollTop The top position.
   */
  public final void setScrollTop(int scrollTop) {
    getBodyElement().setScrollTop(scrollTop);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<AutocompleteSuggestion>		suggestions;
  private int					selectedRow = -1;
}
