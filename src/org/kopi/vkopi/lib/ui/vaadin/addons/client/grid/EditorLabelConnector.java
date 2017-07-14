/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorLabel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The editor label connector
 */
@SuppressWarnings("serial")
@Connect(value = GridEditorLabel.class, loadStyle = LoadStyle.DEFERRED)
public class EditorLabelConnector extends AbstractComponentConnector implements ClickHandler {

  @Override
  protected void init() {
    super.init();
    handlerRegistration = getWidget().addClickHandler(this);
  }
  
  @Override
  public VEditorLabel getWidget() {
    return (VEditorLabel) super.getWidget();
  }
  
  @Override
  public EditorLabelState getState() {
    return (EditorLabelState) super.getState();
  }
  
  @OnStateChange("caption")
  /*package*/ void setText() {
    getWidget().setCaption(getState().caption);
  }

  @Override
  public void onClick(ClickEvent event) {
    if (isEnabled()) {
      getRpcProxy(EditorLabelClickServerRpc.class).clicked();
    }
  }
  
  @Override
  public void onUnregister() {
    super.onUnregister();
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
    }
  }
  
  /**
   * Stored to be removed when the connector is unregistered.
   */
  private HandlerRegistration           handlerRegistration;
}
