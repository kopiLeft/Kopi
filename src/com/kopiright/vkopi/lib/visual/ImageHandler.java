/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.visual;

import com.kopiright.vkopi.lib.ui.base.Image;

public abstract class ImageHandler {

  //------------------------------------------------------------------
  // ACCESSORS
  //------------------------------------------------------------------

  public static ImageHandler getImageHandler() {
    return imageHandler;
  }

  public static void setImageHandler(ImageHandler handler) {
    assert handler != null : "ImageHandler cannot be null";

    imageHandler = handler;
  }

  //------------------------------------------------------------------
  // ABSTRACT METHODS
  //------------------------------------------------------------------

  /**
   * Returns the {@link Image} having the {@code image} name.
   * @param image The image name.
   * @return The {@link Image} having the {@code image} name.
   */
  public abstract Image getImage(String image);

  /**
   * Returns the {@link Image} having the {@code image} content.
   * @param image The image content.
   * @return The {@link Image} having the {@code image} content.
   */
  public abstract Image getImage(byte[] image);

  /**
   * Returns the URL of a given image name.
   * @param image The image name.
   * @return The URL of the image.
   */
  public abstract String getURL(String image);

  //------------------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------------------

  private static ImageHandler			imageHandler;
}
