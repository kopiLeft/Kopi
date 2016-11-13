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
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.SortableLabelServerRpc;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.LegacyComponent;

/**
 * The server side of the sortable label component.
 */
@SuppressWarnings({ "serial", "deprecation" })
public class SortableLabel extends Label implements LegacyComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new sortable label component.
   * @param caption The label caption
   */
  public SortableLabel(String caption) {
    super(caption);
    registerRpc(rpc);
    listeners = new ArrayList<SortableLabelListener>();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  
  /**
   * Sets the label to be sortable.
   * @param sortable is it a sortable label ?
   */
  public void setSortable(boolean sortable) {
    getState().sortable = sortable;
  }
  
  /**
   * Registers a new sortable label listener
   * @param l The listener to be registered.
   */
  public void addSortableLabelListener(SortableLabelListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a new sortable label listener
   * @param l The listener to be removed.
   */
  public void removeSortableLabelListener(SortableLabelListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires a sort event.
   */
  protected void fireOnSort(int mode) {
    for (SortableLabelListener l : listeners) {
      if (l != null) {
	l.onSort(mode);
      }
    }
  }
  
  @Override
  public void changeVariables(Object source, Map<String, Object> variables) {
    if (variables.containsKey("sort")) {
      fireOnSort(((Integer) variables.get("sort")).intValue());
    }
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    // no paint mechanism
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<SortableLabelListener>	listeners;
  private final SortableLabelServerRpc		rpc = new SortableLabelServerRpc() {
    
    @Override
    public void onSort(int mode) {
      fireOnSort(mode);
    }
  };
}
