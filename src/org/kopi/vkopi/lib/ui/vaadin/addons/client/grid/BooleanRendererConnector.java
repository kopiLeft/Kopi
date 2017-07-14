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

import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanRenderer;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer.RendererClickHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

import elemental.json.JsonObject;

/**
 * The boolean renderer connector that provides true and false representation to
 * the renderer widget.
 */
@SuppressWarnings("serial")
@Connect(value = BooleanRenderer.class, loadStyle = LoadStyle.DEFERRED)
public class BooleanRendererConnector extends ClickableRendererConnector<Boolean> {

  @Override
  protected void init() {
    super.init();
    getRenderer().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
      
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        getRpcProxy(BooleanRendererValueChangeServerRpc.class).valueChanged(event.getValue());
      }
    });
  }
  
  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    getRenderer().yes = getState().trueRepresentation;
    getRenderer().no = getState().falseRepresentation;
  }
  
  @Override
  public VBooleanRenderer getRenderer() {
    return (VBooleanRenderer) super.getRenderer();
  }
  
  @Override
  protected HandlerRegistration addClickHandler(RendererClickHandler<JsonObject> handler) {
    return getRenderer().addClickHandler(handler);
  }
  
  @Override
  public BooleanRendererState getState() {
    return (BooleanRendererState) super.getState();
  }
}
