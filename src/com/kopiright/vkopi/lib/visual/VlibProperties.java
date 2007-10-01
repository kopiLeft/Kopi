/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

import java.text.MessageFormat;
import java.util.Locale;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.visual.Application;


/**
 * This class handles localized properties
 */
public class VlibProperties {
  
  // ----------------------------------------------------------------------
  // STATIC METHODS
  // ----------------------------------------------------------------------
  
  /**
   * Returns the value of the property for the given key.
   *
   * @param     key             the property key
   * @return    the requested property value 
   */
  public static String getString(String key) {
    return getString(key, null);
  }


  /**
   * Returns the value of the property for the given key.
   *
   * @param     key             the property key
   * @param     param           property parameter
   * @return    the requested property value 
   */
  public static String getString(String key, Object param) {
    return getString(key, new Object[] {param});
  }
  
  /**
   * Returns the value of the property for the given key.      
   *
   * @param     key             the property key
   * @param     param1          the first property parameter
   * @param     param1          the second property parameter
   * @return    the requested property value
   */
  public static String getString(String key, Object param1, Object param2) {
    return getString(key, new Object[] {param1, param2});
  }

  public static String getString(String key, Object[] params) {
    LocalizationManager         manager;
    String                      format;

    if (Application.getApplication() != null) {
      manager = Application.getLocalizationManager();
    } else {
      manager = new LocalizationManager(Locale.getDefault(), null);
    }
    
    try {
      // Within a String, "''" represents a single quote in java.text.MessageFormat.
      format = manager.getPropertyLocalizer(VLIB_PROPERTIES_RESOURCE_FILE, key).getValue().replaceAll("'", "''");
      return MessageFormat.format(format, params);
    } catch (InconsistencyException e) {
      Application.reportTrouble("localize Property",
                                "com.kopiright.vkopi.lib.visual.VlibProperties.getString(String key, Object[] params)",
                                e.getMessage(),
                                e);
      System.err.println("ERROR: " + e.getMessage());
      return "!" + key + "!";
    }
  }
 
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final static String VLIB_PROPERTIES_RESOURCE_FILE= "com/kopiright/vkopi/lib/resource/VlibProperties" ;
}
