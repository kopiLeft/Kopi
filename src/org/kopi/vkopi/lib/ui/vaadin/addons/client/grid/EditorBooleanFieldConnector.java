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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorBooleanField;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The connector implementation of the boolean editor.
 */
@SuppressWarnings("serial")
@Connect(value = GridEditorBooleanField.class, loadStyle = LoadStyle.DEFERRED)
public class EditorBooleanFieldConnector extends EditorFieldConnector implements ValueChangeHandler<Boolean> {

  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
    handlerRegistration = getWidget().addValueChangeHandler(this);
    registerRpc(EditorBooleanFieldClientRpc.class, new EditorBooleanFieldClientRpc() {
      
      @Override
      public void setValue(Boolean value) {
        getWidget().setValue(value);
      }
    });
  }
  
  @Override
  public VEditorBooleanField getWidget() {
    return (VEditorBooleanField) super.getWidget();
  }
  
  @Override
  public EditorBooleanFieldState getState() {
    return (EditorBooleanFieldState) super.getState();
  }
  
  /**
   * Sets the caption of the label tag attached with the widget radio button
   */
  @OnStateChange({"label", "trueRepresentation", "falseRepresentation"})
  /*package*/ void setLabel() {
    getWidget().setLabel(getState().label, getState().trueRepresentation, getState().falseRepresentation);
  }

  @Override
  public void onValueChange(ValueChangeEvent<Boolean> event) {
    getRpcProxy(EditorBooleanFieldValueChangeServerRpc.class).valueChanged(event.getValue());
  }
  
  @Override
  protected void sendValueToServer() {
    getRpcProxy(EditorBooleanFieldValueChangeServerRpc.class).valueChanged(getWidget().getValue());
  }
  
  @Override
  public void onUnregister() {
    super.onUnregister();
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
    }
  }
  
  private HandlerRegistration           handlerRegistration;
}
