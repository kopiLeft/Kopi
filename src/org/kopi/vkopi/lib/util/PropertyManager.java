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

package org.kopi.vkopi.lib.util;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class is used to set a hierachical structure for handling propreties:
 * On a properties' file we can define the "parent" property which containes
 * one or a space separated list of parents.
 */
public class PropertyManager {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param file               the properties file name
   */
  public PropertyManager(String file) {
    this.file = file;
  }

  /**
   * Gets a string for the given key from the resource bundle of this property manager
   * or one of its parents.
   * @param key                the key for the desired string
   * @return                   the string for the given key
   */
  public String getString(String key) {
    return getString(file, new ArrayList<String>(), key);
  }


  /**
   * Gets a string for the given key from the resource bundle of this property manager
   * or one of its parents.
   * @param resourceName       the current resource to look into
   * @param visitedResources   list of visited resource files (used to avoid circular dependencies)
   * @param key                the key for the desired string
   * @return                   the string for the given key
   */
  public String getString(String resourceName, ArrayList<String> visitedResources, String key) {
    ResourceBundle resource;

    if (resourceName == null) {
      return null;
    } else if (resources.containsKey(resourceName)) {
      if (resources.get(resourceName) instanceof ResourceBundle) {
        resource = (ResourceBundle)resources.get(resourceName);
      } else {
        return null;
      }
    } else {
      try {
        // create a new bundle and store it on the table
        // to avoid creating a new one for each lookup
        resource = ResourceBundle.getBundle(resourceName);
        resources.put(resourceName, resource);
      } catch (MissingResourceException mre) {
        System.err.println(resourceName + ".properties not found, will use default properties");
        resources.put(resourceName, "");
        resource = null;
      }
    }

    if (resource == null) {
      return null;
    } else {
      String value;

      try {
        value = resource.getString(key);
      } catch (Exception e) {
        value = null;
      }

      if (value != null) {
        return value;
      } else {
        // lookup on parents resources
        StringTokenizer st;
        String          p;

        // mark the current resource as visited
        visitedResources.add(resourceName);

        try {
          st = new StringTokenizer(resource.getString("parent"));
        } catch (Exception e) {
          return null;
        }

        while (value == null && st.hasMoreTokens()) {
          p = st.nextToken();

          // check this resource (p) if not already visited
          if(!visitedResources.contains(p)) {
            try {
              value = getString(p, visitedResources, key);
            } catch (Exception e) {
              value = null;
            }
          }
        }
      }
      return value;
    }
  }


  // ----------------------------------------------------------------------
  // DEBUGGING ACCESSORS
  // ----------------------------------------------------------------------

  public String getResourecsFileName() {
    return file;
  }


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String                     			file;

  private static Hashtable<String, Object>           	resources = new Hashtable<String, Object>();
}
