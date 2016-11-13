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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that wraps image element.
 */
public class VImage extends Widget {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new image widget.
   */
  public VImage() {
    setElement(Document.get().createImageElement());
    sinkEvents(Event.ONCLICK);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Registers a click handler on this image.
   * @param handler The click handler.
   * @return The registration handler.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }
  
  /**
   * Sets the image source.
   * @param src The image source URL.
   */
  public final void setSrc(String src) {
    getImageElement().setSrc(src);
  }
  
  /**
   * Returns the source image of this element.
   * @return The source image of this element.
   */
  public final String getSrc() {
    return getImageElement().getSrc();
  }
  
  /**
   * Sets the image width.
   * @param width The image width.
   */
  public final void setWidh(int width) {
    getImageElement().setWidth(width);
  }
  
  /**
   * Sets the image height.
   * @param width The image height.
   */
  public final void setHeight(int height) {
    getImageElement().setHeight(height);
  }
  
  /**
   * Sets the image border.
   * @param width The image border.
   */
  public final void setBorder(int border) {
    getImageElement().setPropertyInt("border", border);
  }
  
  /**
   * Returns {@code true} if no image is displayed.
   * @return {@code true} if no image is displayed.
   */
  public final boolean isEmpty() {
    return getImageElement().getSrc() == null || "".equals(getImageElement().getSrc());
  }
  
  /**
   * Returns the wrapped image element.
   * @return The wrapped image element.
   */
  protected ImageElement getImageElement() {
    return getElement().cast();
  }
}
