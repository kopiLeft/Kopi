/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.form;

public class VList implements VConstants {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list. (!!! TO BE REMOVED)
   * &&&&&&&&&&&&&
   */
  public VList(String newForm, VListColumn[] columns, int table) {
    this.newForm = newForm;
    this.columns = columns;
    this.table   = table;
    this.hasShortcut = false;
  }

  /**
   * Constructs a list.
   * &&&&&&&&&&&&&
   */
  public VList(String newForm, VListColumn[] columns, int table, boolean hasShortcut) {
    this.newForm = newForm;
    this.columns = columns;
    this.table   = table;
    this.hasShortcut = hasShortcut;
  }

  /**
   * Constructs a list.
   */
  public VList(VListColumn[] columns, int table, Class newForm, boolean hasShortcut) {
    this.newForm = newForm == null ? null : newForm.getName();
    this.columns = columns;
    this.table   = table;
    this.hasShortcut = hasShortcut;
  }

  /**
   * Returns the trigger ID for the evaluation of the table
   */
  public int getTable() {
    return table;
  }

  /**
   * Returns the number of columns.
   */
  public int columnCount() {
    return columns.length;
  }

  /**
   * Returns the column at index.
   */
  public VListColumn getColumn(int pos) {
    return columns[pos];
  }

  /**
   * Returns the column at index.
   */
  public VListColumn[] getColumns() {
    return columns;
  }

  /**
   * Returns the new form name
   */
  public String getNewForm() {
    return newForm;
  }

  /**
   * Returns the new form name
   */
  public boolean hasShortcut() {
    return hasShortcut;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final String			newForm;
  private final VListColumn[]		columns;
  private final int			table;
  private final boolean			hasShortcut;
}
