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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.Field;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.FieldListener;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The field connector.
 */
@SuppressWarnings("serial")
@Connect(value = Field.class, loadStyle = LoadStyle.DEFERRED)
public class FieldConnector extends AbstractSingleComponentContainerConnector implements FieldListener {

  @Override
  protected void init() {
    super.init();
    getWidget().setApplicationConnectiont(getConnection());
    getWidget().addFieldListener(this);
  }
  
  @Override
  public VField getWidget() {
    return (VField) super.getWidget();
  }
  
  @Override
  public FieldState getState() {
    return (FieldState) super.getState();
  }
  
  @OnStateChange({"hasIncrement", "hasDecrement"})
  /*package*/ void iniWidget() {
    getWidget().init(getConnection(), getState().hasIncrement, getState().hasDecrement);
  }
  
  @OnStateChange("visible")
  /*package*/ void setVisible() {
    getWidget().setVisible(getState().visible);
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    // do not delegate caption handling
    return false;
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled.
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    Widget	content = getContentWidget();
    
    if (content instanceof VTextField) {
      getWidget().setTextField((VTextField)content);
    } else if (content instanceof VObjectField) {
      getWidget().setObjectField((VObjectField) content);
    } else if (content instanceof VDragAndDropWrapper) {
      getWidget().setDnDWrapper((VDragAndDropWrapper) content);
    }
  }

  @Override
  public void onIncrement() {
    getRpcProxy(FieldServerRpc.class).onIncrement();
  }

  @Override
  public void onDecrement() {
    getRpcProxy(FieldServerRpc.class).onDecrement();
  }

  @Override
  public void onClick() {
    getRpcProxy(FieldServerRpc.class).onClick();
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }
}
