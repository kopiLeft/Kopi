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

import java.util.Hashtable;

import org.kopi.vkopi.lib.visual.VColor;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ThemeResource;

/**
 * Some vaadin version utilities to obtain images and resources.
 */
public class Utils extends org.kopi.vkopi.lib.base.Utils {

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Returns image from theme
   * @param img Must be an image from resource theme path separator is "/"
   * @return An Image or null if not found.
   */
  public static Image getImage(String image) {
    Image	img = cache.get(image);
    
    if (img == null) {
      img = getImageImpl(image);
      cache.put(image, img);
    }

    return img;
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource directory path separator is "/"
   * @return An Image or null if not found.
   */
  private static Image getImageImpl(String img) {
    Image icon = getDefaultImage(img);
	    
    if (icon == null) {
      icon =  getKopiResourceImage(img);
    }
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
   * Returns image from theme.
   * @param img Must be an image from resource directory path separator is "/"
   * @return An imageIcon or null if not found
   */
  public static Image getDefaultImage(String img) {
    return getImageFromResource(VAADIN_RESOURCE_DIR, img);
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource application directory
   * path separator is "/"
   * @return An Image or null if not found
   */
  public static Image getApplicationImage(String img) {
    return getImageFromResource(APPLICATION_DIR, img);
  }

  /**
   * Returns an image from kopi resources. 
   * @param img Must be an image from resource application directory
   * path separator is "/"
   * @return An Image or null if not found
   */
  public static Image getKopiResourceImage(String img) {
    return getImageFromResource(RESOURCE_DIR, img);
  }

  /**
   * Return image from resources or null if not found.
   * @param directory The image directory.
   * @param name The image name.
   * @return An Image or null if not found
   */
  public static Image getImageFromResource(String directory, String name) {
    if (Utils.class.getClassLoader().getResource(directory + "/" + name) != null) {
      return new Image(new ClassResource("/" + directory + "/" + name));
    }
	    
    return null;
  }
  
  /**
   * Returns the corresponding CSS color of a given {@link Color}.
   * @param color The AWT color.
   * @return The CSS color.
   */
  public static String getCSSColor(VColor color) {
    if (color == null) {
      return "inherit !important ;";
    } else {
      return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ") !important ;";
    }
  }
	  
  // --------------------------------------------------
  // PRIVATE DATA
  // --------------------------------------------------

  private static final String			VAADIN_RESOURCE_DIR = "org/kopi/vkopi/lib/ui/vaadin/resource";
  private static final String			THEME_DIR = "resource";
  private static final String			APPLICATION_DIR = "resources";
  private static final String			RESOURCE_DIR	= "org/kopi/vkopi/lib/resource";
  public  static final Image			UKN_IMAGE = new Image(new ThemeResource(THEME_DIR + "/" + "unknown.png"));
	  
  private static Hashtable<String, Image> 	cache = new Hashtable<String, Image>();
}
