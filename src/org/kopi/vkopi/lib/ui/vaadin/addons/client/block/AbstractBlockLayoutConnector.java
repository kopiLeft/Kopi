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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;

/**
 * The abstract layout connector. Not attached to server.
 */
@SuppressWarnings("serial")
public abstract class AbstractBlockLayoutConnector extends AbstractComponentContainerConnector {
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled
  }
  
  @Override
  public VAbstractBlockLayout getWidget() {
    return (VAbstractBlockLayout) super.getWidget();
  }
  
  @Override
  public AbstractBlockLayoutState getState() {
    return (AbstractBlockLayoutState) super.getState();
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
    if (!handleHierarchyChange) {
      return; // we do need to layout components. we suit here.
    }
    if (event.getOldChildren() == null || event.getOldChildren().size() == 0) {
      initSize();
      handleHierarchyChange();
      layout();
    }
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }
  
  /**
   * Returns the component constraints.
   * @param connector The component connector.
   * @return The component constraints.
   */
  protected ComponentConstraint getConstraint(ComponentConnector connector) {
    return getState().constrains.get(connector);
  }
  
  /**
   * Layouts components
   */
  protected void layout() {
    getWidget().layout();
  }
  
  /**
   * Sets the necessity to layout components.
   * @param handleHierarchyChange Should we layout components.
   */
  protected void setHandleHierarchyChange(boolean handleHierarchyChange) {
    this.handleHierarchyChange = handleHierarchyChange;
  }
  
  /**
   * Handles the hierarchy change event.
   */
  protected abstract void handleHierarchyChange();
  
  /**
   * Initialize the size of the layout
   */
  protected abstract void initSize();
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean 			handleHierarchyChange = true;
}
