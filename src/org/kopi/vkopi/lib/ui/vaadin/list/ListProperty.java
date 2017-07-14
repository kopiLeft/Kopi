/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.list;

import org.kopi.vkopi.lib.form.VListDialog;

import com.vaadin.data.Property;

/**
 * A list dialog property model based on object container property
 */
@SuppressWarnings("serial")
public class ListProperty implements Property<Object> {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new list property object.
   * @param model The list data model.
   * @param row The property row index.
   * @param col The property column index.
   */
  protected ListProperty(VListDialog model, int row, int col) {
    this.model = model;
    this.row = row;
    this.col = col;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public Object getValue() {
    return model.getValueAt(model.getTranslatedIdents()[row], col);
  }

  @Override
  public final void setValue(Object newValue) throws ReadOnlyException {}

  @Override
  public final boolean isReadOnly() {
    return true;
  }

  @Override
  public final void setReadOnly(boolean newStatus) {}
  
  @Override
  public Class<?> getType() {
    return model.getColumns()[col].getDataType();
  }
  
  @Override
  public String toString() {
    return formatObject(model.getValueAt(model.getTranslatedIdents()[row], col));
  }
  
  /**
   * Formats an object according to the property nature.
   * @param o The object to be formatted.
   * @return The formatted property object.
   */
  protected String formatObject(Object o) {
    return model.getColumns()[col].formatObject(o).toString();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  protected final int                   row;
  protected final int                   col;
  protected final VListDialog           model;
}
