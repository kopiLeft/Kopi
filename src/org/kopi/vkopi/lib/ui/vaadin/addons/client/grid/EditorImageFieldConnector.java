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

import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorImageField;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

@SuppressWarnings("serial")
@Connect(value = GridEditorImageField.class, loadStyle = LoadStyle.DEFERRED)
public class EditorImageFieldConnector extends EditorFieldConnector implements ClickHandler {

  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
    handlerRegistration = getWidget().addClickHandler(this);
  }
  
  @Override
  public EditorImageFieldState getState() {
    return (EditorImageFieldState) super.getState();
  }
  
  @Override
  public VEditorImageField getWidget() {
    return (VEditorImageField) super.getWidget();
  }
  
  /**
   * Sets the image of the field.
   */
  @OnStateChange("resources")
  /*package*/ void setImage() {
    if (getIcon() != null) {
      getWidget().setImage(getIconUri());
    } else {
      getWidget().setImage(null);
    }
  }

  @Override
  public void onClick(ClickEvent event) {
    // TODO
  }
  
  @Override
  public void onUnregister() {
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
    }
    super.onUnregister();
  }
  
  private HandlerRegistration           handlerRegistration;
}
