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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VImage;

/**
 * Resources utilities.
 */
public abstract class ResourcesUtil {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  private ResourcesUtil() {
    // don't Instantiate please.
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Returns the translated URL of a given resource URI.
   * @param connection The application connection.
   * @param uri The resource URI.
   * @return The translated URL.
   */
  public static String getThemeURL(ApplicationConnection connection, String uri) {
    if (connection == null) {
      return null;
    }
    
    return connection.translateVaadinUri(THEME_RESOURCE_PREFIX + uri);
  }
  
  /**
   * Creates a new theme image from a given image name.
   * @param connection The application connection.
   * @param img The image name.
   * @param style The style to be applied.
   * @return The created image.
   */
  public static VImage createThemeImage(ApplicationConnection connection, String img, String style) {
    String			url;
    VImage			image;
    
    image = GWT.create(VImage.class);
    image.setStyleName(style);
    url = getThemeURL(connection, THEME_RESOURCE_PREFIX + img);
    if (url != null) {
      image.setUrl(url);
    }
    
    return image;
  }
  
  /**
   * Returns the image URL from theme directory.
   * @param connection The application directory.
   * @param name The image name.
   * @return The image translated URL.
   */
  public static String getImageURL(ApplicationConnection connection, String name) {
    if (connection == null) {
      return null;
    }
    
    return connection.translateVaadinUri("theme://img/" + name);
  }
  
  /**
   * Returns the resource simple name from its application URI.
   * A theme resource URI have this general form : ${APPLICATION_PATH}/VAADIN/themes/${theme}/name.extension.
   * What we want is to retrieve the resource name.
   * @param uri The resource application URI.
   * @return The simple name of the resource.
   */
  public static String getResourceName(String uri) {
    int                 lastDotIndex;
    int                 lastSlashIndex;
    
    // look for the last /
    lastSlashIndex = uri.lastIndexOf('/');
    // look for the last .
    lastDotIndex = uri.lastIndexOf('.');
    if (lastDotIndex == -1 && lastSlashIndex == -1) {
      return uri;
    } else if (lastDotIndex != -1 && lastSlashIndex != 1) {
      // found it is OK now, the resource name is between the the / and the .
      return uri.substring(lastSlashIndex + 1, lastDotIndex);
    } else if (lastDotIndex != -1 && lastSlashIndex == 1) {
      // no / found, the resource name starts from the beginning.
      return uri.substring(0, lastDotIndex);
    } else {
      // no . found take it until the end
      return uri.substring(lastSlashIndex);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static final String			THEME_RESOURCE_PREFIX = "theme://resource/";
}
