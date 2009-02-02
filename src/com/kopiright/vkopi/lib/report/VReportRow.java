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

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class VReportRow extends DefaultMutableTreeNode {

  /**
   * Constructs a report row description
   */
  public VReportRow(Object[] data) {
    this.data = data;
  }

  /**
   * Return the level of the node in the grouping tree, starting with 0 at
   * the base rows (leafs).
   *
   * @return	the level of the node in the grouping tree
   */
  public abstract int getLevel();

  /**
   * Return the object at column
   *
   * @param	column		the index of the column
   * @return	the object to be displayed
   */
  public Object getValueAt(int column) {
    return data[column];
  }

  /**
   * Sets data row
   *
   * @param	column		the index of the column
   * @param	value		the value for the column
   */
  public void setValueAt(int column, Object value) {
    data[column] = value;
  }

  /**
   * Sets the visibility of the row
   *
   * @param boolean true if the row is visible
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Return true iff the row is visible.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   *
   */
  public Object[] cloneArray() {
    return (Object[])data.clone();
  }

  /**
   * Compare two rows wrt to a column
   *
   * !!! add doc
   */
  public int compareTo(VReportRow other, int pos, VReportColumn column) {
    Object      o1 = data[pos];
    Object      o2 = other.data[pos];

    // check for nulls: define null less than everything
    if (o1 == null && o2 == null) {
      return 0;
    } else if (o1 == null) {
      return -1;
    } else if (o2 == null) {
      return 1;
    } else {
      return column.compareTo(o1, o2);
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  protected Object[]		data;
  private   boolean		visible;
}
