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

import org.kopi.vkopi.lib.ui.vaadin.addons.GridListDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogState.SelectionTarget;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

import elemental.json.JsonObject;

/**
 * Connector implementation for grid based list dialog
 */
@SuppressWarnings("serial")
@Connect(value = GridListDialog.class, loadStyle = LoadStyle.DEFERRED)
public class GridListDialogConnector extends AbstractSingleComponentContainerConnector {

  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
  }
  
  @Override
  public GridListDialogState getState() {
    return (GridListDialogState) super.getState();
  }
  
  @Override
  public VGridListDialog getWidget() {
    return (VGridListDialog) super.getWidget();
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {}

  @SuppressWarnings("unchecked")
  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    final Grid<JsonObject>      table = (Grid<JsonObject>)getContentWidget();

    if (table != null) {
      getWidget().setTable(table);
    }
  }
  
  /**
   * Notifies the server side that the dialog is closed.
   * @param escaped Is it closed with escape code ?
   * @param newForm Is it closed with new form code ?
   */
  protected void fireClosed(boolean escaped, boolean newForm) {
    getWidget().close();
    getRpcProxy(GridListDialogCloseServerRpc.class).closed(escaped, newForm);
  }
  
  /**
   * Notifies server side that the client side requested a selection target.
   * @param target The selection targe
   */
  protected void fireSelection(SelectionTarget target) {
    getRpcProxy(GridListDialogSelectionServerRpc.class).select(target);
  }
  
  /**
   * Notifies the server side that the client side requests a search for the given pattern.
   * @param pattern The search pattern.
   */
  protected void fireSearch(String pattern) {
    getRpcProxy(GridListDialogSearchServerRpc.class).search(pattern);
  }
  
  @Override
  public void onUnregister() {
    getWidget().close();
    getWidget().clear();
    getWidget().removeFromParent();
    super.onUnregister();
  }
}
