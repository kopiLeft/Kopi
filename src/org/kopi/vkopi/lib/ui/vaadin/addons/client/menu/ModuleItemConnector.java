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

import org.kopi.vkopi.lib.ui.vaadin.addons.ModuleItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The module item connector.
 */
@SuppressWarnings("serial")
@Connect(value = ModuleItem.class, loadStyle = LoadStyle.DEFERRED)
public class ModuleItemConnector extends AbstractHasComponentsConnector {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public VModuleItem getWidget() {
    return (VModuleItem) super.getWidget();
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @OnStateChange("caption")
  /*package*/ void setCaption() {
    getWidget().setCaption(getState().caption);
  }
  
  @OnStateChange("itemId")
  /*package*/ void setId() {
    getWidget().setId(getState().itemId);
  }
  
  @OnStateChange("isLeaf")
  /*package*/ void setCommand() {
    getWidget().setScheduledCommand(createCommand());
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // no caption handling
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    VModuleListMenu		subMenu;
    
    subMenu = new VModuleListMenu(getConnection(), false); // it is always sub menus.
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof ModuleItemConnector) {
	// it should be
	subMenu.addItem(((ModuleItemConnector)child).getWidget(), false);
      }
    }
    
    getWidget().setSubMenu(subMenu);
  }

  @Override
  public ModuleItemState getState() {
    return (ModuleItemState) super.getState();
  }
  
  @Override
  public TooltipInfo getTooltipInfo(Element element) {
    // cannot be handled for z-index reasons
    return null;
  }
  
  /**
   * Creates the item command for leaf items.
   * @return The item command.
   */
  protected ScheduledCommand createCommand() {
    if (getState().isLeaf) {
      // sets the item command if it is a leaf item.
      return new ScheduledCommand() {
        
        @Override
        public void execute() {
          ModuleListConnector		owner;
          
          owner = getOwner();
          if (owner != null) {
            owner.fireItemClicked(getState().itemId);
          }
        }
      };
    }
    
    return null;
  }
  
  /**
   * Builds the widget content.
   */
  protected void buildContent() {
    getWidget().buildContent();
  }
  
  /**
   * Searches the module list owner of this item.
   * @return The owner module list owner of this item or{@code null} if no parent is found.
   */
  protected ModuleListConnector getOwner() {
    ServerConnector		parent;
    
    parent = getParent();
    while (parent != null) {
      if (parent instanceof ModuleListConnector) {
	return (ModuleListConnector)parent;
      }
      
      parent = parent.getParent();
    }
    
    return null;
  }
}
