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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.label;

import org.kopi.vkopi.lib.ui.vaadin.addons.Label;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.WindowConnector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The label connector.
 */
@SuppressWarnings("serial")
@Connect(value = Label.class, loadStyle = LoadStyle.DEFERRED)
public class LabelConnector extends AbstractComponentConnector implements ClickHandler {

  @Override
  public VLabel getWidget() {
    return (VLabel) super.getWidget();
  }
  
  @OnStateChange("infoText")
  /*package*/ void setInfoText() {
    getWidget().setInfoText(getState().infoText);
  }
  
  @OnStateChange("hasAction")
  /*package*/ void setHasAction() {
    if (getState().hasAction) {
      getWidget().setHasAction();
      getWidget().addClickHandler(this);
    }
  }
  
  @OnStateChange("caption")
  /*package*/ void setText() {
    getWidget().setText(getState().caption);
  }
  
  @OnStateChange("visible")
  /*package*/ void setVisible() {
    getWidget().setVisible(getState().visible);
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @Override
  public LabelState getState() {
    return (LabelState) super.getState();
  }

  @Override
  public void onClick(ClickEvent event) {
    if (!getWidget().isEnabled()) {
      return;
    }
    // send dirty values to the server side before.
    getWindow().cleanDirtyValues(getBlock());
    getRpcProxy(LabelServerRpc.class).onClick();
  }
  
  /**
   * Returns {@code true} if the label has the action trigger feature. 
   * @return {@code true} if the label has the action trigger feature. 
   */
  public boolean hasAction() {
    return getState().hasAction;
  }
  
  /**
   * Returns the window that this label belongs to.
   * @return The parent window of this label.
   */
  protected WindowConnector getWindow() {
    return ConnectorUtils.getParent(this, WindowConnector.class);
  }
  
  /**
   * Returns the parent block connector.
   * @return The parent block connector.
   */
  protected BlockConnector getBlock() {
    return ConnectorUtils.getParent(this, BlockConnector.class);
  }
}
