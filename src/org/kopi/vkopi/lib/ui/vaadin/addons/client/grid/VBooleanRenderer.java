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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * The boolean renderer widget attached to the boolean editor. 
 */
public class VBooleanRenderer extends ClickableRenderer<Boolean, VEditorBooleanField> implements HasValueChangeHandlers<Boolean> {

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  public VEditorBooleanField createWidget() {
    final VEditorBooleanField           widget;
    
    widget = new VEditorBooleanField(true);
    widget.init(null); // application connection is not needed.
    widget.setValue(null);
    widget.addStyleDependentName("renderer");
    widget.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
      
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        ValueChangeEvent.fire(VBooleanRenderer.this, event.getValue());
      }
    });
    
    return widget;
  }
  
  @Override
  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    
    return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
  }

  @Override
  public void render(RendererCellReference cell, Boolean data, VEditorBooleanField widget) {
    widget.setValue(data);
    widget.setLabel("label", yes, no); 
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  /*package*/ String                    yes;
  /*package*/ String                    no;
  private HandlerManager                handlerManager;
}
