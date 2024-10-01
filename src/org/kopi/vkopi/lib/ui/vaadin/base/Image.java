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

package org.kopi.vkopi.lib.ui.vaadin.base;

import com.vaadin.server.Resource;

/**
 * The vaadin implementation of an image model.
 */
@SuppressWarnings("serial")
public class Image implements org.kopi.vkopi.lib.base.Image {

  //----------------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------------

  /**
   * Creates a new <code>Image</code> from a vaadin resource.
   * @param resource The {@link Resource} attached to this image.
   */
  public Image(Resource resource) {
    this.resource = resource;
  }

  //---------------------------------------------------
  // IMAGE IMPLEMENTATION
  //---------------------------------------------------

  /**
   * @Override
   */
  public int getImageWidth() {
    return -1;
  }

  /**
   * @Override
   */
  public int getImageHeight() {
    return -1;
  }

  /**
   * @Override
   */
  public String getDescription() {
    return resource.getMIMEType();
  }

  /**
   * @Override
   */
  public org.kopi.vkopi.lib.base.Image getScaledInstance(int width,
                                                         int height,
                                                         int hints)
  {
    // FIXME: return the scaled image from theme images
    return this;
  }

  public Resource getResource() {
    return resource;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final Resource			resource;
}
