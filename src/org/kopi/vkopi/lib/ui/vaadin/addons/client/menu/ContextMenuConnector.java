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

import org.kopi.vkopi.lib.ui.vaadin.addons.ContextMenu;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VPopupWindow;
import org.vaadin.peter.contextmenu.client.ContextMenuClientRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuServerRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;
import org.vaadin.peter.contextmenu.client.ContextMenuWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VTree;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Rebuild context menu connector to fix the overlay error.
 */
@Connect(value = ContextMenu.class, loadStyle = LoadStyle.DEFERRED)
@SuppressWarnings({ "serial", "deprecation" })
public class ContextMenuConnector extends org.vaadin.peter.contextmenu.client.ContextMenuConnector {
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    widget = GWT.create(ContextMenuWidget.class);
    contextMenuCloseHandlerRegistration = widget.addCloseHandler(contextMenuCloseHandler);
    registerRpc(ContextMenuClientRpc.class, serverToClientRPC);
  }

  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    widget.clearItems();
    widget.setHideAutomatically(getState().isHideAutomatically());
    for (ContextMenuItemState rootItem : getState().getRootItems()) {
      widget.addRootMenuItem(rootItem, this);
    }
  }

  @Override
  protected void extend(ServerConnector extensionTarget) {
    this.extensionTarget = ((ComponentConnector) extensionTarget).getWidget();
    // Tree is currently handled by it's internal ItemClickListener
    if (this.extensionTarget instanceof VTree) {
      return;
    }

    contextMenuHandlerRegistration = this.extensionTarget.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
    widget.setExtensionTarget(this.extensionTarget);
  }

  @Override
  public void onUnregister() {
    contextMenuCloseHandlerRegistration.removeHandler();
    contextMenuHandlerRegistration.removeHandler();
    widget.unregister();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ContextMenuWidget                     widget;
  private Widget                                extensionTarget;
  private ContextMenuServerRpc                  clientToServerRPC = RpcProxy.create(ContextMenuServerRpc.class, this);
  private CloseHandler<PopupPanel>              contextMenuCloseHandler = new CloseHandler<PopupPanel>() {
    
    @Override
    public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
      clientToServerRPC.contextMenuClosed();
    }
  };
  private final ContextMenuHandler              contextMenuHandler = new ContextMenuHandler() {

    @Override
    public void onContextMenu(ContextMenuEvent event) {
      EventTarget               eventTarget;
      ComponentConnector        connector;
      Widget                    clickTargetWidget;
      Element                   element;
      
      event.preventDefault();
      event.stopPropagation();
      eventTarget = event.getNativeEvent().getEventTarget();
      element = (Element) eventTarget.cast();
      // here the connector can be null if the target element belongs to an overlay.
      connector = Util.getConnectorForElement(getConnection(), getConnection().getUIConnector().getWidget(), element);
      // add a check if the connector is null and get it from an overlay context
      if (connector == null) {
        // No connector found, element is possibly inside a VOverlay
        // If the overlay has an owner, try to find the owner's connector
        VPopupWindow        popupWindow = WidgetUtil.findWidget(element, VPopupWindow.class);
        
        if (popupWindow != null) {
          connector = Util.getConnectorForElement(getConnection(), popupWindow, element);
        } else {
          connector =  null;
        }
      }
      // if no connector found don't do any thing and this will avoid null pointer exceptions
      // thrown by original addon implementation.
      if (connector != null) {
        clickTargetWidget = connector.getWidget();
        if (extensionTarget.equals(clickTargetWidget)) {
          if (getState().isOpenAutomatically()) {
            widget.showContextMenu(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
          } else {
            clientToServerRPC.onContextMenuOpenRequested(event.getNativeEvent().getClientX(),
                                                         event.getNativeEvent().getClientY(),
                                                         connector.getConnectorId());
          }
        }
      }
    }
  };

  private HandlerRegistration                   contextMenuCloseHandlerRegistration;
  private HandlerRegistration                   contextMenuHandlerRegistration;
  private ContextMenuClientRpc                  serverToClientRPC = new ContextMenuClientRpc() {

    @Override
    public void showContextMenu(int x, int y) {
      widget.showContextMenu(x, y);
    }

    @Override
    public void showContextMenuRelativeTo(String connectorId) {
      ServerConnector   connector = ConnectorMap.get(getConnection()).getConnector(connectorId);

      if (connector instanceof AbstractComponentConnector) {
        AbstractComponentConnector      componentConnector;
        
        componentConnector = (AbstractComponentConnector) connector;
        widget.showContextMenu(componentConnector.getWidget());
      }
    }

    @Override
    public void hide() {
      widget.hide();
    }
  };
}
