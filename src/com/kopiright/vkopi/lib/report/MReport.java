/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.report;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

import com.kopiright.util.base.Utils;
import com.kopiright.vkopi.lib.visual.MessageCode;

public class MReport extends AbstractTableModel implements Constants {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -7372702648334281245L;
  
  /**
   * Constructs a new report instance
   */
  public MReport() {
    userRows = new Vector(500);
  }

  /**
   * Add a row to the list of rows defined by the user
   */
  public void addLine(Object[] line) {
    userRows.addElement(new VBaseRow(line));
  }

  /**
   * Build the base row table + intialisation
   */
  protected void build() {
    int		columnCount = columns.length;
    // build accessible columns
    if (userRows.size() == 0) {
      throw new VNoRowException(MessageCode.getMessage("VIS-00015"));
    }

    createAccessibleTab();

    baseRows = (VBaseRow[])Utils.toArray(userRows, VBaseRow.class);
    userRows = null;

    // build working tables
    columnCount = getAccessibleColumnCount();

    displayOrder	= new int[columnCount];
    reverseOrder	= new int[columnCount];
    displayLevels	= new int[columnCount];

    for (int i = 0; i < columnCount; i++) {
      displayOrder[i]  = i;
      reverseOrder[i]  = i;
      displayLevels[i] = -1;
    }
  }

  // --------------------------------------------------------------------
  // MEMBER ACCESS
  // --------------------------------------------------------------------

  /**
   * Return a column definition
   *
   * @param	column		the index of the desired column
   * @return	the desired column
   */
  public VReportColumn getModelColumn(int column) {
    return columns[column];
  }

  /**
   * Returns the number of model columns
   *
   * @return	the number or columns to display
   */
  public int getModelColumnCount() {
    return columns.length;
  }

  /**
   * Return a column definition
   *
   * @param	column		the index of the desired column
   * @return	the desired column
   */
  public VReportColumn getAccessibleColumn(int column) {
    return accessiblecolumns[column];
  }

  /**
   * Returns the number of columns visible or hide
   *
   * @return	the number or columns in the model
   */
  public int getAccessibleColumnCount() {
    return accessiblecolumns.length;
  }

  /**
   * Return a row definition
   *
   * @param	row		the index of the desired row
   * @return	the desired row
   */
  public VReportRow getRow(int row) {
    return visibleRows[row];
  }

  /**
   * Return the tree used by the model
   */
  public VGroupRow getTree() {
    return root;
  }

  // --------------------------------------------------------------------
  // GROUPING TREE
  // --------------------------------------------------------------------

  protected void createTree() {
    // compute grouping columns in displayed column order
    computeGroupings();

    // sort base rows wrt to each grouping column
    sortBaseRows();

    // build the grouping tree recursively
    buildGroupingTree();

    // compute all intermediate columns
    calculateColumns();

    // create the array of displayed rows
    updateTableModel();
  }

  /*
   * Returns an array of grouping columns in displayed column order
   * For each column, the value is the column of the next (lower) level
   * grouping or -1 if the column has no further sub-grouping.
   * The columns are given in displayed column order
   */
  private void computeGroupings() {
    int		columnCount   = accessiblecolumns.length;
    int[]	defaultGroups = new int[columnCount];
    int[]	displayGroups = new int[columnCount];
    int		separatorPos = Integer.MAX_VALUE;

    // retrieve the groups in original column order
    for (int i = 0; i < columnCount; i++) {
      defaultGroups[i] = columns[i].getGroups();
    }

    // reorder the groups in displayed column order
    for (int i = 0; i < columnCount; i++) {
      if (defaultGroups[displayOrder[i]] == -1) {
	displayGroups[i] = -1;
      } else if (defaultGroups[displayOrder[i]] >= reverseOrder.length) {
	// not shown
	displayGroups[i] = reverseOrder.length;
      } else {
	displayGroups[i] = reverseOrder[defaultGroups[displayOrder[i]]];
      }
    }

    // retrieve separator
    for (int i = 0; i < columnCount; i++) {
      if (accessiblecolumns[displayOrder[i]] instanceof VSeparatorColumn) {
	separatorPos = i;
      }
    }

    /*
    for (int i = 0; i < columnCount; i++) {
      System.err.print(" " + displayGroups[i]);
    }
    System.err.println();
    */

    int		level = 0;
    //top is reached
    for (int i = 0; i < displayGroups.length; i++) {
      displayLevels[i] = level;
      if (accessiblecolumns[displayOrder[i]].isVisible()) {
	if (displayGroups[i] == -1 || i == separatorPos) {
	  for (; i < displayGroups.length; i++) {
	    displayLevels[i] = level;
	  }
	  break;
	} else if (displayGroups[i] > i) {
	  for (; i + 1 < displayGroups.length && (displayGroups[i + 1] <= i + 1 && displayGroups[i + 1] != -1); i++) {
	    displayLevels[i + 1] = level;
	  }
	  level++;
	}
      }
    }

    // renumber levels from highest to lowest (0)
    for (int i = 0; i < columnCount; i++) {
      displayLevels[i] = level - displayLevels[i];
    }

    /*
    for (int i = 0; i < columnCount; i++) {
      System.err.print(" " + displayLevels[i]);
    }
    System.err.println();
    */
  }

  /*
   * Sort base rows wrt to each grouping column
   * Note: we assume that the sorting algorithm ist stable: we thus
   * can sort the complete table for each column, starting with the
   * last grouping column.
   */
  private void sortBaseRows() {
    visibleRows = new VReportRow[baseRows.length];
    sortBaseRows(0);
  }

  private void sortBaseRows(int column) {
    if (displayLevels[column] > 0) {
      int	next = column + 1;
      while (displayLevels[next] == displayLevels[next - 1]) {
	next += 1;
      }
      sortBaseRows(next);
    }
    // sort in ascending order
    int i = column;
    while (!accessiblecolumns[i].isVisible()) {
      i += 1;
    }
    if (i >= 0) {
      sortArray(baseRows, displayOrder[i], 1);
      // this value is overwritten in each pass: at the end
      // of the recursion it will hold the first column
      sortedColumn = displayOrder[i];
      sortingOrder = 1;
    }
  }

  /*
   * Build the grouping tree
   */
  private void buildGroupingTree() {
    maxRowCount = baseRows.length + 1;
    root = new VGroupRow(new Object[getModelColumnCount()], displayLevels[0] + 1);
    // even if column 0 is hidden, it has the highest level
    buildGroupingTree(root, 0, baseRows.length - 1, 0);

    visibleRows = new VReportRow[maxRowCount];
    root.setVisible(true);
    for (int i = 0; i < root.getChildCount(); i++) {
      ((VReportRow)root.getChildAt(i)).setVisible(true);
    }
  }

  private void buildGroupingTree(VReportRow tree, int loRow, int hiRow, int start) {
    if (displayLevels[start] == 0) {	// even if the 0-index column is hidden, its displayLevels == 0
      for (int i = loRow; i <= hiRow; i++) {
	tree.add(baseRows[i]);
      }
    } else {
      int	next;

      // get the interval of columns at this level
      next = start + 1 ;
      while(displayLevels[next] == displayLevels[start]){
    	  next ++;
      }
      
      while (!accessiblecolumns[start].isVisible()) {
	  // to get the first visible column of this level
	  start++;
      }

      do {
	Object		value = baseRows[loRow].getValueAt(displayOrder[start]);
	int		split = loRow;

	while (split <= hiRow && ((value == null && baseRows[split].getValueAt(displayOrder[start]) == null)
				  || (value != null && value.equals(baseRows[split].getValueAt(displayOrder[start]))))) {
	  split += 1;
	}

	VGroupRow	newRow = new VGroupRow(new Object[getModelColumnCount()], displayLevels[start]);
	maxRowCount++;

	for (int i = 0; i < next; i++) {
	  newRow.setValueAt(displayOrder[i], baseRows[loRow].getValueAt(displayOrder[i]));
	}

	buildGroupingTree(newRow, loRow, split - 1, next);
	tree.add(newRow);

	loRow = split;
      } while (loRow <= hiRow);
    }
  }

  /*
   * Sorts an array of rows wrt to given column using stright two-way merge sorting.
   *
   * @param	array		The array to sort
   * @param	column		The index of the column on which to sort
   * @param	order		The sorting order (1: ascending, -1: descending)
   */
  private void sortArray(VReportRow[] array, int column, int order) {
    mergeSort(array, column, order, 0, array.length - 1, visibleRows);
  }

  private void mergeSort(VReportRow[] array,
			 int column,
			 int order,
			 int lo,
			 int hi,
			 VReportRow[] scratch) {
    // a one-element array is always sorted
    if (lo < hi) {
      int	mid = (lo + hi)/2;

      // split into 2 sublists and sort them
      mergeSort(array, column, order, lo, mid, scratch);
      mergeSort(array, column, order, mid+1, hi, scratch);

      // Merge sorted sublists
      int	t_lo = lo;
      int	t_hi = mid+1;

      for (int k = lo; k <= hi; k++) {
	if (t_lo > mid || (t_hi <= hi && order * array[t_hi].compareTo(array[t_lo], column, getModelColumn(column)) < 0)) {
	  scratch[k] = array[t_hi++];
	} else {
	  scratch[k] = array[t_lo++];
	}
      }

      // Copy back to array
      for (int k = lo; k <= hi; k++) {
	array[k] = scratch[k];
      }
    }
  }

  /**
   * Calculate all columns which need to be calculated
   */
  void calculateColumns() {
    for (int i = 0; i < columns.length; i++) {
      VCalculateColumn	function = columns[i].getFunction();

      if (function != null) {
	function.init();
	function.calculate(root, i);
      }
    }
  }

  /**
   * fill table of visible rows
   */
  private void updateTableModel() {
    maxRowCount = addRowsInArray(root, 0);
    fireTableChanged(new TableModelEvent(this));
  }

  /**
   * add visible rows in a vector
   *
   * @param VReportRow node to test
   */
  private int addRowsInArray(VReportRow node, int pos) {
    if (node.isVisible()) {
      visibleRows[pos++] = node;

      for (int i = 0; i < node.getChildCount(); i++) {
	VReportRow	row = (VReportRow)node.getChildAt(i);
	if (row.getLevel() == 0) {
	  if (row.isVisible()) {
	    visibleRows[pos++] = row;
	  }
	} else {
	  pos = addRowsInArray((VReportRow)node.getChildAt(i), pos);
	}
      }
    }
    return pos;
  }

  // --------------------------------------------------------------------
  // EVENTS FROM DISPLAY
  // --------------------------------------------------------------------

  /**
   * Sort the displayed tree wrt to a column
   *
   * @param	column		the model column index used for sorting in display order.
   */
  public void sortColumn(int column) {
    sortTree(column);
    calculateColumns();
    updateTableModel();
  }

  /**
   * Sort the display tree wrt to a column; if it is already sorted
   * wrt to this column, invert the sorting order.
   *
   * @param	column		The model column index given in display order
   */
  private void sortTree(int column) {
    int		order;

    order  = column != sortedColumn ? 1 : -sortingOrder;
    sortTree(root, column, order);
    sortedColumn = column;
    sortingOrder = order;
  }

  private void sortTree(VReportRow tree, int column, int order) {
    VReportRow[]	rowTab;

    // place the childs of the root in an array
    rowTab = new VReportRow[tree.getChildCount()];
    for (int i = 0; i < tree.getChildCount(); i++) {
      rowTab[i] = (VReportRow)tree.getChildAt(i);
    }

    // sort the array wrt to column: if already sorted, invert order
    sortArray(rowTab, column, order);

    // re-add the rows as childs
    tree.removeAllChildren();
    for (int i = 0; i < rowTab.length; i++) {
      tree.add(rowTab[i]);
    }

    // sort sub-trees recursively
    if (tree.getLevel() > 1) {
      for (int i = 0; i < tree.getChildCount(); i++) {
	sortTree((VReportRow)tree.getChildAt(i), column, order);
      }
    }
  }

  /**
   * Returns true is the specified column is fold
   */
  public boolean isColumnFold(int column) {
    if (root.getLevel() > 1) {
      int level = displayLevels[reverseOrder[column]];
      return (!root.isUnfolded(level));
    } else {
      return false;
    }
  }

  /**
   * Returns true if the specified row is fold at the specified column
   */
  public boolean isRowFold(int row, int column) {
    if (root.getLevel() > 1) {
      int		level = displayLevels[reverseOrder[column]];
      VReportRow	currentRow = visibleRows[row];

      while (currentRow.getLevel() < level) {
	currentRow = (VReportRow)currentRow.getParent();
      }
      return currentRow instanceof VGroupRow ?
	!((VGroupRow)currentRow).isUnfolded(level) :
	true;
    } else {
      return false;
    }
  }

  /**
   * Folds the specified column
   */
  public void foldingColumn(int column) {
    if (root.getLevel() > 1) {
      int level = displayLevels[reverseOrder[column]];
      root.setChildNodesInvisible(level);
      updateTableModel();
    }
  }

  /**
   * Unfolds the specified column
   */
  public void unfoldingColumn(int column) {
    if (root.getLevel() > 1) {
      int level = displayLevels[reverseOrder[column]];
      root.setChildNodesVisible(level);
      updateTableModel();
    }
  }

  /**
   * Folds the specified column
   */
  public void setColumnFolded(int column, boolean fold) {
    getModelColumn(column).setFolded(fold);
    fireTableChanged(new TableModelEvent(this));
  }

  /**
   * Folds the specified column
   */
  public void switchColumnFolding(int column) {
    getModelColumn(column).setFolded(!getModelColumn(column).isFolded());
    fireTableChanged(new TableModelEvent(this));
  }

  /**
   * Folds the specified row to specified column
   *
   *  @param	column		the model index of the column
   */
  public void foldingRow(int row, int column) {
    if (root.getLevel() > 1) {
      int		level = displayLevels[reverseOrder[column]];
      VReportRow	currentRow = visibleRows[row];

      while (currentRow.getLevel() < level) {
        currentRow = (VReportRow)currentRow.getParent();
      }

      if (currentRow instanceof VGroupRow) {
	((VGroupRow)currentRow).setChildNodesInvisible(level);
      }
      updateTableModel();
    }
  }

  /**
   * Unfolds the specified row to specified column
   *
   *  @param	column		the model index of the column
   */
  public void unfoldingRow(int row, int column) {
    if (root.getLevel() > 1) {
      int		level = displayLevels[reverseOrder[column]];
      VReportRow	currentRow = visibleRows[row];

      if (currentRow instanceof VGroupRow) {
	((VGroupRow)currentRow).setChildNodesVisible(level);
      }

      updateTableModel();
    }
  }

  /**
   * Returns true if the specified row is fold at the specified column
   */
  public boolean isRowLine(int row) {
    if (visibleRows != null) {
      return row >= 0 && row < maxRowCount && visibleRows[row].getLevel() == 0;
    } else {
      return false;
    }
  }

  /**
   * @param	newOrder	the table of the display order columns
   */
  public void columnMoved(int[] newOrder) {
    displayOrder = newOrder;

    // rebuild column mapping from model to display
    //    for (int i = 0; i < columns.length; i++)
    for (int i = 0; i < displayOrder.length; i++) {
      reverseOrder[displayOrder[i]] = i;
    }

    createTree();

    fireTableChanged(new TableModelEvent(this));
  }

  // --------------------------------------------------------------------
  // REDEFINITION OF METHODS IN AbstractTableModel
  // --------------------------------------------------------------------

  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return	the number or columns to display
   */
  public int getColumnCount() {
    return accessiblecolumns.length;
  }

  /**
   * Returns the number of records managed by the data source object.
   *
   * @return	the number or rows in the model
   */
  public int getRowCount() {
    return maxRowCount;
  }

  /**
   * Returns always false since report cells are never editable.
   *
   * @param	row		the index of the row whose value is to be looked up
   * @param	column		the index of the column whose value is to be looked up
   * @return	true if the cell is editable.
   */
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  /**
   * Returns an attribute value for a cell.
   *
   * @param	row		the index of the row whose value is to be looked up
   * @param	column		the index of the column whose value is to be looked up (column of the model)
   * @return	the value Object at the specified cell
   */
  public Object getValueAt(int row, int column) {
    return visibleRows[row].getLevel() < displayLevels[reverseOrder[column]] ?
      null :
      visibleRows[row].getValueAt(column);
  }

  /**
   * Returns the name of a column.
   * Note, this name does not need to be unique.
   *
   * @param	column		the index of the column
   * @return	the name of the column
   */
  public String getColumnName(int column) {
    String	label = accessiblecolumns[column].getLabel();
    if (label == null || label.length() == 0) {
      return "";
    }
    return accessiblecolumns[column].isFolded() ? label.substring(0, 1) : label;
  }

  /**
   * Makes the table of accessible columns from the columns variable
   */
  private void createAccessibleTab() {
    int		columnCount = columns.length;
    int		accessiblecolumnCount = 0;

    for (int i = 0; i < columnCount; i++) {
      if ((columns[i].getOptions() & Constants.CLO_HIDDEN) == 0) {
	accessiblecolumnCount += 1;
      }
    }
    accessiblecolumns     = new VReportColumn[accessiblecolumnCount];

    accessiblecolumnCount = 0;
    for (int i = 0; i < columnCount; i++) {
      if ((columns[i].getOptions() & Constants.CLO_HIDDEN) == 0) {
	accessiblecolumns[accessiblecolumnCount++] = columns[i];
      }
    }
  }

  public int getDisplayLevels(int column) {
    return displayLevels[column];
  }

  public int getReverseOrder(int column) {
    return reverseOrder[column];
  }

  public int getDisplayOrder(int column) {
    return displayOrder[column];
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  // Columns contains all columns defined by the user
  // accessiblecolumns is a part of columns which contains only visible columns
  public VReportColumn[]	columns;		// array of column definitions
  private VReportColumn[]	accessiblecolumns;	// array of visible or hide columns

  // Root is the root of the tree (which is our model to manipulate data)
  private VGroupRow		root;			// root of grouping tree

  // Baserows contains data give by the request of the user
  // visibleRows contains all data which will be displayed. It's like a buffer. visibleRows
  // is changed when a column move or one or more row are folded
  private Vector		userRows;
  private VReportRow[]		baseRows;		// array of base data rows
  private VReportRow[]		visibleRows;		// array of visible rows
  private int			maxRowCount;

  // Sortedcolumn contain the index of the sorted column
  // sortingOrder store the type of sort of the sortedColumn : ascending or descending
  private int			sortedColumn;		// the table is sorted wrt. to this column
  private int			sortingOrder;		// 1: ascending, -1: descending

  // displayOrder contains index column model in display order
  // reverseOrder is calculate with displayOrder and contains index column display into model order
  private int[]			displayOrder;		// column mapping from display to model
  private int[]			reverseOrder;		// column mapping from model to display

  // The displayLevels variable is a table which contains the level of each column
  private int[]			displayLevels;		// column levels in display order
}
