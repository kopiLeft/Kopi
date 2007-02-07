/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
import java.util.regex.Pattern;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;

/**
 * This class handles localized messages.
 */
public class MessageCode {


  // ----------------------------------------------------------------------
  // STATIC METHODS
  // ----------------------------------------------------------------------


  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message key
   * @return    the requested message
   */
  public static String getMessage(String key) {
    return getMessage(key, null);
  }

  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message key
   * @param     param           a message parameter
   * @return    the requested message
   */
  public static String getMessage(String key, Object param) {
    return getMessage(key, new Object[] {param});
  }

  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message ke
   * @param     param1          the first message parameter
   * @param     param1          the second message parameter
   * @return    the requested message
   */
  public static String getMessage(String key, Object param1, Object param2) {
    return getMessage(key, new Object[] {param1, param2});
  }

  /**
   * Returns a message (convenience routine).
   * key must be of the form CCC-DDDDD (exp: KOP-00001)
   * where CCC is a 3 capital character module id
   * and DDDDD a 5 digit message id
   *
   * @param     key             the message key
   * @param     params          the array of message parameters
   * @return    the requested message
   */
  public static String getMessage(String key, Object[] params) {
    LocalizationManager         manager;
    String                      domain;
    String                      ident;
    String                      src;
    
    if (!keyPattern.matcher(key).matches()) {
      throw new InconsistencyException("Malformed message key '" + key + "'");
    }
    domain = key.substring(0, 3);
    ident = key.substring(4, 9);
    if (Application.getRegistry() == null) {
      throw new InconsistencyException("No Registry set for this application.");
    }
    src = Application.getRegistry().getMessageSource(domain);
    if (src == null) {
      throw new InconsistencyException("No message source found for module '"
                                       + domain + "'");
    }
    
    try {
      String            format;
      
      manager = new LocalizationManager(Locale.getDefault(), Application.getDefaultLocale());

      // Within a String, "''" represents a single quote in java.text.MessageFormat.
      format = manager.getMessageLocalizer(src, ident).getText().replaceAll("'", "''");
      return key + ": " + MessageFormat.format(format, params);
    } catch (InconsistencyException e) {
      return key + ": " + "message for !" + key + "! not found!";
    }
  }
  

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final Pattern          keyPattern = Pattern.compile("^[A-Z][A-Z][A-Z]-\\d\\d\\d\\d\\d$");
}
