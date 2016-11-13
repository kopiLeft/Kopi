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

import java.io.Serializable;

/**
 * The list dialog table model. This should be serializable
 * to be transferred via shared state.
 */
@SuppressWarnings("serial")
public class TableModel implements Serializable {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Needed by GWT compiler.
   */
  public TableModel() {}
  
  /**
   * Creates a new <code>TableModel</code> instance.
   * @param columns The table columns (names).
   * @param types The columns types.
   * @param aligns The columns alignment.
   * @param idents The row identifiers.
   * @param data The data to be displayed.
   * @param count The total row count.
   */
  public TableModel(String[] columns,
                    ColumnType[] types,
                    int[] aligns,
                    int[] idents,
                    String[][] data,
                    int count)
  {
    this.columns = columns;
    this.types = types;
    this.aligns = aligns;
    this.idents = idents;
    this.data = data;
    this.count = count;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Returns the number of rows in the model.
   * A <code>VListDialogTable</code> uses this method to determine how many rows it
   * should display. This method should be quick, as it
   * is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return count;
  }

  /**
   * Returns the number of columns in the model. A
   * <code>VListDialogTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return columns.length;
  }

  /**
   * Returns the name of the column at <code>columnIndex</code>. This is used
   * to initialize the table's column header name.  Note: this name does
   * not need to be unique; two columns in a table can have the same name.
   *
   * @param   columnIndex     The index of the column
   * @return  the name of the column
   */
  public String getColumnName(int columnIndex) {
    return columns[columnIndex];
  }

  /**
   * Returns the type of the column at <code>columnIndex</code>. This is used
   * to perform sort operation and to transform the column value to an object
   * with a suitable type and then perform a sort operation. This is because some
   * string based returns wrong sort results especially for dates.
   *
   * @param   columnIndex     The index of the column
   * @return  the type of the column
   */
  public ColumnType getColumnType(int columnIndex) {
    return types[columnIndex];
  }
  
  /**
   * Returns the alignment of the column at <code>columnIndex</code>.
   * @param columnIndex  The index of the column
   * @return The alignment of the column.
   */
  public int getColumnAlign(int columnIndex) {
    return aligns[columnIndex];
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param   rowIndex        the row whose value is to be queried
   * @param   columnIndex     the column whose value is to be queried
   * @return  the value Object at the specified cell
   */
  public String getValueAt(int rowIndex, int columnIndex) {
    return data[columnIndex][rowIndex];
  }
  
  /**
   * Returns the row identifier of the the given row index.
   * 
   * @param rowIndex the visible row index.
   * @return The row identifier. 
   */
  public int getIdent(int rowIndex) {
    return idents[rowIndex];
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * A column type enumeration.
   */
  public static enum ColumnType {
    
    /**
     * A boolean column.
     */
    BOOLEAN,
    
    /**
     * A number column.
     */
    NUMBER,
    
    /**
     * A date column.
     */
    DATE,
    
    /**
     * A time column
     */
    TIME,
    
    /**
     * A time stamp column
     */
    TIMESTAMP,
    
    /**
     * A month column.
     */
    MONTH,
    
    /**
     * A week column.
     */
    WEEK,
    
    /**
     * A text or string column.
     */
    STRING;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  public String[]			columns;
  public ColumnType[]                   types;
  public int[]				aligns; // column alignment
  public int[]				idents;
  public String[][]			data;
  public int				count;
}
