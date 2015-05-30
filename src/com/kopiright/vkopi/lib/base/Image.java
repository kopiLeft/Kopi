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

package com.kopiright.vkopi.lib.base;

import java.io.Serializable;

/**
 * The interface class <code>Image</code> is the superInterface of all
 * classes that represent graphical images. The image must be
 * obtained in a platform-specific manner.
 */
public interface Image extends Serializable {

  /**
   * Returns the <code>Image</code> width
   * @return The image width
   */
  public int getWidth();

  /**
   * Returns the <code>Image</code> height
   * @return The image height
   */
  public int getHeight();

  /**
   * Returns the <code>Image</code> description
   * @return The image description
   */
  public String getDescription();

  /**
   * Creates a scaled version of this image.
   * A new <code>Image</code> object is returned which will render
   * the image at the specified <code>width</code> and
   * <code>height</code> by default.
   *
   * @param width the width to which to scale the image.
   * @param height the height to which to scale the image.
   * @param hints flags to indicate the type of algorithm to use
   * for image resampling.
   * @return a scaled version of the image.
   * @exception IllegalArgumentException if <code>width</code> or <code>height</code> is zero.
   */
  public Image getScaledInstance(int width, int height, int hints);

  //-----------------------------------------------------
  // SCALING CONSTANTS
  //-----------------------------------------------------

  int 				SCALE_DEFAULT 		= 1;
  int 				SCALE_FAST 		= 2;
  int 				SCALE_SMOOTH 		= 4;
  int 				SCALE_REPLICATE 	= 8;
  int 				SCALE_AREA_AVERAGING 	= 16;
}
