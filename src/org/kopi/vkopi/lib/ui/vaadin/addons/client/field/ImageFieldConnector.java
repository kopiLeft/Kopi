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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.ImageField;

import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The image field connector.
 */
@SuppressWarnings("serial")
@Connect(value = ImageField.class, loadStyle = LoadStyle.DEFERRED)
public class ImageFieldConnector extends ObjectFieldConnector {

  @Override
  public VImageField getWidget() {
    return (VImageField) super.getWidget();
  }
  
  @Override
  public ImageFieldState getState() {
    return (ImageFieldState) super.getState();
  }
  
  /**
   * Sets the image width.
   */
  @OnStateChange({"imageWidth", "imageHeight"})
  /*package*/ void setImageSize() {
    getWidget().setWidth(getState().imageWidth);
    getWidget().setHeight(getState().imageHeight);
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
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  /**
   * Fired when the image is removed from the panel.
   */
  protected void fireRemoved() {
    getRpcProxy(ImageFieldServerRpc.class).onRemove();
  }
  
  /**
   * Fired when the image is clicked.
   */
  protected void fireClicked() {
    getRpcProxy(ImageFieldServerRpc.class).onClick();
  }
}
