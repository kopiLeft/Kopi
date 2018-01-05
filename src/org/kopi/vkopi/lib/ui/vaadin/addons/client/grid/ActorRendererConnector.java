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

import org.kopi.vkopi.lib.ui.vaadin.addons.ActorRenderer;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer.RendererClickHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

import elemental.json.JsonObject;

/**
 * The actor cell renderer based on the VActorField widget. 
 */
@SuppressWarnings("serial")
@Connect(value = ActorRenderer.class, loadStyle = LoadStyle.LAZY)
public class ActorRendererConnector extends ClickableRendererConnector<String> {

  @Override
  public ActorRendererSate getState() {
    return (ActorRendererSate) super.getState();
  }
  
  @Override
  public VActorRenderer getRenderer() {
    return (VActorRenderer) super.getRenderer();
  }
  
  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    getRenderer().caption = getState().caption;
  }
  
  @Override
  protected HandlerRegistration addClickHandler(RendererClickHandler<JsonObject> handler) {
    return getRenderer().addClickHandler(handler);
  }
}
