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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.ActorRendererSate;

import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;

/**
 * An actor field renderer that uses the actor editor field widget to display. 
 */
@SuppressWarnings("serial")
public class ActorRenderer extends ClickableRenderer<String> implements RendererClickListener {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  /**
   * Creates a new actor renderer.
   * @param caption The renderer caption.
   */
  public ActorRenderer(String caption) {
    super(String.class);
    getState().caption = caption;
    addClickListener(this);
  }

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  @Override
  protected ActorRendererSate getState() {
    return (ActorRendererSate) super.getState();
  }

  @Override
  public void click(RendererClickEvent event) {
    // to be redefined in subclasses
  }
}
