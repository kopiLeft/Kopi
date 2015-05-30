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

package com.kopiright.vkopi.lib.list;

import java.io.Serializable;

import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.l10n.ListLocalizer;

@SuppressWarnings("serial")
public class VList implements VConstants, Serializable {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list.
   *
   * @param     ident           the identifier of the list type
   * @param     source          the qualified name of the source file defining the list
   */
  public VList(String ident,
               String source,
               String newForm,
               VListColumn[] columns,
               int table,
               boolean hasShortcut)
  {
    this.ident = ident;
    this.source = source;
    this.newForm = newForm;
    this.columns = columns;
    this.table = table;
    this.hasShortcut = hasShortcut;
  }

  /**
   * Constructs a list.
   */
  public VList(String ident,
               String source,
               VListColumn[] columns,
               int table,
               Class<VForm> newForm,
               boolean hasShortcut)
  {
    this(ident,
         source,
         newForm == null ? null : newForm.getName(),
         columns,
         table,
         hasShortcut);
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

  /**
   * Localize this object.
   *
   * @param     manager
   */
  public void localize(LocalizationManager manager) {
    ListLocalizer       loc;

    loc = manager.getListLocalizer(source, ident);
    for (int i = 0; i < columns.length; i++) {
      columns[i].localize(loc);
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final String                  ident;
  private final String                  source;
  private final String			newForm;
  private final VListColumn[]		columns;
  private final int			table;
  private final boolean			hasShortcut;
}
