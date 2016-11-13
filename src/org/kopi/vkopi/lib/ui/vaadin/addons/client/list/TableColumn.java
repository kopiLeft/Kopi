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

import java.util.Arrays;
import java.util.Collections;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.TableModel.ColumnType;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * A table column model to enable sort feature.
 */
@SuppressWarnings("serial")
public class TableColumn implements Sortable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>TableColumn</code> instance.
   * @param column The column index.
   * @param cells The column cells.
   * @param type The column type
   * @param align The column align
   * @param cells The cells that belongs to this column.
   */
  public TableColumn(int column, ColumnType type, int align, TableCell[] cells) {
    this.column = column;
    this.type = type;
    this.align = align;
    this.cells = cells;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Attach the entire column to the given table.
   * @param table The table where the column should attached.
   */
  public void attach(FlexTable table) {
    for (TableCell cell : cells) {
      if (cell != null) {
	cell.attach(table);
      }
    }
  }

  @Override
  public int[] sort(int mode) {
    int[]	newOrder = new int[cells.length];

    switch (mode) {
    case SORT_NONE:
    case SORT_ASC:
      Arrays.sort(cells); // only sort
      break;
    case SORT_DESC:
      Collections.reverse(Arrays.asList(cells)); // sort and reverse
      break;
    default:
      break; // do not action
    }
    
    for (int i = 0; i < newOrder.length; i++) {
      newOrder[i] = cells[i].row;
    }
    
    return newOrder;
  }
  
  /**
   * Returns the column index.
   * @return The column index.
   */
  public int getIndex() {
    return column;
  }
  
  /**
   * Sets the new column order.
   * @param table The table where the column is attached.
   * @param newOrder The new sort order.
   */
  public void refresh(FlexTable table, int[] newOrder) {
    TableCell[]		old = copy(cells);
    
    for (int i = 0; i < newOrder.length; i++) {
      cells[i] = new TableCell(i, column, type, align);
      cells[i].setText(getCellTextAt(old, newOrder[i]));
      cells[i].attach(table);
    }
  }
  
  /**
   * Returns the cell of a given row.
   * @param row The row index.
   * @return The searched cell.
   */
  protected String getCellTextAt(TableCell[] cells, int row) {
    for (TableCell cell : cells) {
      if (cell.row == row) {
	return cell.getText();
      }
    }
    
    return null;
  }
  
  /**
   * Copies table cells
   * @param cells The table cells to be copied.
   * @return The copy table cells.
   */
  protected TableCell[] copy(TableCell[] cells) {
    TableCell[]		copy = new TableCell[cells.length];
    
    for (int i = 0; i < cells.length; i++) {
      copy[i] = new TableCell(cells[i].row, cells[i].column, cells[i].type, cells[i].align);
      copy[i].setText(cells[i].getText());
    }
    
    return copy;
  }
  
  /**
   * Returns the max offset width of the column cells.
   * @return The max offset width of the column cells.
   */
  public int getMaxOffsetWidth() {
    int		max = 0;
    
    for (TableCell cell : cells) {
      max = Math.max(max, cell.getOffsetWidth());
    }
    
    return max;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final int				column;
  private final ColumnType                      type;
  private final int				align;
  private TableCell[] 				cells;
}
