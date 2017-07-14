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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ApplicationConnection;

/**
 * The image editor field widget. 
 */
public class VEditorImageField extends VFocusableEditorField<String> implements HasClickHandlers {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public VEditorImageField() {
    image = new VImage();
    image.setBorder(0);
    image.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
    setWidget(image);
    setStyleName("editor-imagefield");
    sinkEvents(Event.ONCLICK);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public ApplicationConnection getConnection() {
    return null;
  }

  @Override
  public String getValue() {
    return image.getSrc();
  }

  @Override
  public void setBlink(boolean blink) {}

  @Override
  public void setColor(String foreground, String background) {}

  @Override
  public void validate() throws InvalidEditorFieldException {}
  

  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }
  
  @Override
  protected EditorImageFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorImageFieldConnector.class);
  }
  
  /**
   * Sets the image width.
   * @param width The image width.
   */
  public void setImageWidth(int width) {
    // BREAKS THE IMAGE IN THE GRID
    //image.setWidh(width);
    //image.setWidth(width + "px");
  }
  
  /**
   * Sets the image height.
   * @param height The image height.
   */
  public void setImageHeight(int height) {
    // BREAKS THE IMAGE IN THE GRID
    //image.setHeight(height);
    //image.setHeight(height + "px");
  }
  
  /**
   * Sets the image URL.
   * @param url The image URL.
   */
  public void setImage(String url) {
    image.setSrc(url);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VImage                  image;
}
