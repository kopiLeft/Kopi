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

package com.kopiright.vkopi.lib.report;

import com.kopiright.vkopi.lib.visual.UWindow;

/**
 * {@code UReport} is the top-level interface that must be implemented
 * by all kopi dynamic reports. It is the visual component of the {@link VReport} model.
 */
public interface UReport extends UWindow, ReportListener {

  /**
   * Builds the report;
   */
  public void build();

  /**
   * Redisplays the report
   */
  public void redisplay();

  /**
   * Fired when report columns has moved.
   * @param pos The new columns positions
   */
  public void columnMoved(final int[] pos);

  /**
   * Removes a column having the position <code>position</code>
   * @param position The column position
   */
  public void removeColumn(int position);

  /**
   * Adds a column at the position <code>position</code>
   * @param position The column position
   */
  public void addColumn(int position);

  /**
   * Adds a column at the end of the report
   */
  public void addColumn();

  /**
   * Returns the report table.
   */
  public UTable getTable();

  /**
   * Reset columns width
   */
  public void resetWidth();

  /**
   * Returns the selected column
   */
  public int getSelectedColumn();

  /**
   * Returns the coordinate of the selected cell
   * The index of the column is relative to the model
   */
  public Point getSelectedCell();

  /**
   * Sets the column label.
   * @param column The column number.
   * @param label The column label
   */
  public void setColumnLabel(int column, String label);

  /**
   * {@code UTable} is a report table ensuring conversion between
   * visible indexes and model indexes
   */
  public interface UTable {

    /**
     * Maps the index of the column in the view at
     * <code>viewColumnIndex</code> to the index of the column
     * in the table model.
     */
    public int convertColumnIndexToModel(int viewColumnIndex);

    /**
     * Maps the index of the column in the table model at
     * <code>modelColumnIndex</code> to the index of the column
     * in the view.
     */
    public int convertColumnIndexToView(int modelColumnIndex);
  }
}
