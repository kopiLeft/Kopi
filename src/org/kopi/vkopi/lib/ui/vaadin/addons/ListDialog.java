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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.ListDialogServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.ListDialogState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.TableModel.ColumnType;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * The list dialog server component.
 */
@SuppressWarnings("serial")
public class ListDialog extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new list dialog component.
   */
  public ListDialog() {
    registerRpc(rpc);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the data model.
   * @param columns The list dialog columns.
   * @param types The list dialog columns types.
   * @param aligns The columns alignment.
   * @param idents The rows identifiers.
   * @param data The list dialog data.
   * @param count The row count.
   */
  public void setModel(String[] columns,
                       ColumnType[] types,
                       int[] aligns,
                       int[] idents,
                       String[][] data,
                       int count)
  {
    getState().model.columns = columns;
    getState().model.types = types;
    getState().model.aligns = aligns;
    getState().model.idents = idents;
    getState().model.data = data;
    getState().model.count = count;
  }
  
  /**
   * Show the list dialog relative to the given component.
   * @param reference The reference component.
   */
  public void showRelativeTo(Component reference) {
    getState().reference = reference;
  }
  
  /**
   * Sets the new button text.
   * @param newText The new text.
   */
  public void setNewText(String newText) {
    getState().newText = newText;
  }
  
  @Override
  protected ListDialogState getState() {
    return (ListDialogState) super.getState();
  }
  
  /**
   * Registers a new list dialog listener.
   * @param l The listener to be registered.
   */
  public void addListDialogListener(ListDialogListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a new list dialog listener.
   * @param l The listener to be removed.
   */
  public void removeListDialogListener(ListDialogListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires a selection event.
   * @param row The selected row.
   * @param escaped Should we escape selection.
   * @param newForm Should we do a new form.
   */
  protected void fireOnSelection(int row, boolean escaped, boolean newForm) {
    for (ListDialogListener l : listeners) {
      if (l != null) {
	l.onSelection(row, escaped, newForm);
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<ListDialogListener>	listeners = new ArrayList<ListDialogListener>();
  private ListDialogServerRpc		rpc = new ListDialogServerRpc() {
    
    @Override
    public void onSelection(int row, boolean escaped, boolean newForm) {
      fireOnSelection(row, escaped, newForm);
    }
  };
}
