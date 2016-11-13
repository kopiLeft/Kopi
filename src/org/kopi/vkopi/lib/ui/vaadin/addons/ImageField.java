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

import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.ImageFieldServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.ImageFieldState;

/**
 * The server side component of the image field.
 */
@SuppressWarnings("serial")
public class ImageField extends ObjectField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>ImageField</code> component.
   */
  public ImageField() {
    listeners = new LinkedList<ImageFieldListener>();
    registerRpc(rpc);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the image width.
   * @param width The image width.
   */
  public void setImageWidth(int width) {
    getState().imageWidth = width;
  }
  
  /**
   * Sets the image height.
   * @param height The image height.
   */
  public void setImageHeight(int height) {
    getState().imageHeight = height;
  }
  
  @Override
  protected ImageFieldState getState() {
    return (ImageFieldState) super.getState();
  }
  
  /**
   * Registers a new image field listener.
   * @param l The image field listener.
   */
  public void addImageFieldListener(ImageFieldListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes an image field listener.
   * @param l The image field listener.
   */
  public void removeImageFieldListener(ImageFieldListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fired when the image is removed.
   */
  protected void fireRemoved() {
    for (ImageFieldListener l : listeners) {
      if (l != null) {
	l.onRemove();
      }
    }
  }
  
  /**
   * Fired when the image is clicked.
   */
  protected void fireClicked() {
    for (ImageFieldListener l : listeners) {
      if (l != null) {
	l.onImageClick();
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<ImageFieldListener>	listeners;
  private final ImageFieldServerRpc		rpc = new ImageFieldServerRpc() {
    
    @Override
    public void onRemove() {
      fireRemoved();
    }

    @Override
    public void onClick() {
      fireClicked();
    }
  };
}
