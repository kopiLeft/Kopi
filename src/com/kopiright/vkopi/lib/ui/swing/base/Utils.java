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

package com.kopiright.vkopi.lib.ui.swing.base;

import java.net.URL;
import java.util.Hashtable;

import javax.swing.ImageIcon;

/**
 * loading of image
 * usage:
 * To load image first in Application directory, then in default directory:
 *  Utils.getImage("name");
 * To load default images (that appear in com.kopiright.vkopi.lib.util/resources)
 *  Utils.getDefaultImage("name");
 * To load Application images (that appear in resources)
 *  Utils.getApplicationImage("name");
 *
 */
public class Utils extends com.kopiright.vkopi.lib.base.Utils {

  /**
   * return image from classpath or jar file
   * @param img must be an image from resource directory
   * path separator is "/"
   * @return an imageIcon
   */
  public static ImageIcon getImage(String image) {
    ImageIcon	img = cache.get(image);
    if (img == null) {
      img = getImageImpl(image);
      cache.put(image, img);
    }

    return img;
  }

  /**
   * return image from classpath or jar file
   * @param img must be an image from resource directory
   * path separator is "/"
   * @return an imageIcon
   */
  private static ImageIcon getImageImpl(String img) {
    ImageIcon icon = getDefaultImage(img);

    if (icon == null) {
      icon = getApplicationImage(img);
    }

    if (icon == null) {
      System.err.println("Utils ==> cant load: " + img);
      return UKN_IMAGE;
    }

    return icon;
  }

  /**
   * return image from classpath or jar file
   * @param img must be an image from resource directory
   * path separator is "/"
   * @return an imageIcon or null if not found
   */
  public static ImageIcon getDefaultImage(String img) {
    return getImageFromResource(img, RESOURCE_DIR);
  }

  /**
   * return image from classpath or jar file
   * @param img must be an image from resource directory
   * path separator is "/"
   * @return an imageIcon or null if not found
   */
  public static ImageIcon getApplicationImage(String img) {
    return getImageFromResource(img, APPLICATION_DIR);
  }

  /**
   * return image from resources or null if not found
   */
  public static ImageIcon getImageFromResource(String name, String directory) {
    URL       url = getURLFromResource(name, directory);

    return url == null ? null :new ImageIcon(url);
  }
  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private static Hashtable<String, ImageIcon>	cache = new Hashtable<String, ImageIcon>();

  public static final String    APPLICATION_DIR = "resources";
  public static final ImageIcon UKN_IMAGE = new ImageIcon("unknown");
  public static final String	RESOURCE_DIR	= "com/kopiright/vkopi/lib/resource";
}
