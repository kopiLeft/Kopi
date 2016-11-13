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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import org.kopi.vkopi.lib.ui.vaadin.addons.PopupWindow;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The popup window component connector.
 */
@SuppressWarnings("serial")
@Connect(value = PopupWindow.class, loadStyle = LoadStyle.LAZY)
public class PopupWindowConnector extends AbstractSingleComponentContainerConnector {

  @Override
  protected void init() {
    super.init();
  }
  
  @Override
  public VPopupWindow getWidget() {
    return (VPopupWindow) super.getWidget();
  }
  
  @Override
  public PopupWindowState getState() {
    return (PopupWindowState) super.getState();
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    // do not delegate caption handling
    return false;
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
    getWidget().setContent(getContentWidget()); // content will be cleared.
  }
  
  @Override
  public void onUnregister() {
    getWidget().close();
    getWidget().clear();
    getWidget().removeFromParent();
    super.onUnregister();
  }
  
  @Override
  public boolean hasTooltip() {
    /*
     * Tooltip event handler always needed on the popup window widget to make sure
     * tooltips are properly hidden.
     */
    return true;
  }
}
