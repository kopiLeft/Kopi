/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Event;

/**
 * The image field widget.
 */
public class VImageField extends VObjectField implements KeyDownHandler {

  //---------------------------------------------------
  // CONSTRCUTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VImageField</code> instance.
   */
  public VImageField() {
    image = new VImage();
    image.setBorder(0);
    image.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
    setWidget(image);
    setStyleName("k-imagefield");
    addKeyDownHandler(this);
    sinkEvents(Event.ONCLICK);
    addDomHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	((ImageFieldConnector)getConnector()).fireClicked();
      }
    }, ClickEvent.getType());
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the image width.
   * @param width The image width.
   */
  public void setWidth(int width) {
    image.setWidh(width);
    image.setWidth(width + "px");
  }
  
  /**
   * Sets the image height.
   * @param height The image height.
   */
  public void setHeight(int height) {
    image.setHeight(height);
    image.setHeight(height + "px");
  }
  
  /**
   * Sets the image URL.
   * @param url The image URL.
   */
  public void setImage(String url) {
    image.setSrc(url);
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
      event.preventDefault();
      clearImage();
    }
  }
  
  /**
   * Clears the image content
   */
  protected void clearImage() {
    image.setSrc(null);
    ((ImageFieldConnector)getConnector()).fireRemoved();
  }
  
  @Override
  protected boolean isNull() {
    return image == null || image.isEmpty();
  }
  
  @Override
  public void setValue(Object o) {
    image.setSrc((String) o);
  }
  
  @Override
  protected void setColor(String foreground, String background) {
    // no color for image field
  }
  
  @Override
  protected Object getValue() {
    return image.getSrc();
  }
  
  @Override
  protected void checkValue(int rec) {
    // nothing to perform
  }
  
  @Override
  public void clear() {
    super.clear();
    image = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VImage				image;
}
