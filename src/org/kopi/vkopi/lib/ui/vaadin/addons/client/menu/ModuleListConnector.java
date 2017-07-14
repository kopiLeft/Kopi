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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.menu;

import org.kopi.vkopi.lib.ui.vaadin.addons.ModuleList;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VConstants;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The module list connector.
 */
@SuppressWarnings("serial")
@Connect(value = ModuleList.class, loadStyle = LoadStyle.DEFERRED)
public class ModuleListConnector extends AbstractHasComponentsConnector {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    registerRpc(ModuleListClientRpc.class, new ModuleListClientRpc() {

      @Override
      public void doItemAction(String itemId) {
        getWidget().doItemAction(itemId);
      }
    });
  }
  
  @Override
  public VModuleList getWidget() {
    return (VModuleList) super.getWidget();
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // no caption handling
  }
  
  @OnStateChange("openRootOnHover")
  /*package*/ void setOpenRootOnHover() {
    getWidget().setAutoOpen(getState().openRootOnHover);
  }
  
  @OnStateChange("animated")
  /*package*/ void setAnimated() {
    getWidget().setAnimation(getState().animated);
  }
  
  @OnStateChange("type")
  /*package*/ void setMain() {
    getWidget().setMain(isMainMenu());
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    VModuleListMenu			rootMenu;
    
    rootMenu = new VModuleListMenu(getConnection(), true); // this is the root menu for the module list
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof ModuleItemConnector) {
	// it should be
	rootMenu.addItem(((ModuleItemConnector)child).getWidget(), true, false);
      }
    }
    // sets the root menu.
    getWidget().setMenu(rootMenu);
  }
  
  @Override
  public ModuleListState getState() {
    return (ModuleListState) super.getState();
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }
  
  /**
   * Returns true if this menu represents the application main menu.
   * @return True if this menu represents the application main menu.
   */
  protected boolean isMainMenu() {
    return getState().type == VConstants.MAIN_MENU;
  }
  
  /**
   * Fires an item click action for the given item ID.
   * @param itemId The clicked item ID.
   */
  protected void fireItemClicked(String itemId) {
    getRpcProxy(ModuleListServerRpc.class).onClick(itemId);
  }
}
