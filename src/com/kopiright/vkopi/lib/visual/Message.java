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
 * This class handles localized messages
 */
public class Message {
  
  // ----------------------------------------------------------------------
  // STATIC METHODS
  // ----------------------------------------------------------------------
  
  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @return    the requested message
   */
  public static String getMessage(String ident) {
    return getMessage(ident, null);
  }


  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @param     param           a message parameter
   * @return    the requested message
   */
  public static String getMessage(String ident, Object param) {
    return getMessage(ident, new Object[] {param});
  }
  
  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @param     param1          the first message parameter
   * @param     param1          the second message parameter
   * @return    the requested message
   */
  public static String getMessage(String ident, Object param1, Object param2) {
    return getMessage(ident, new Object[] {param1, param2});
  }

  public static String getMessage(String ident, Object[] params) {
    LocalizationManager         manager;
    String                      format;

    try {
      manager = new LocalizationManager(Locale.getDefault(), Application.getDefaultLocale());
      //   Within a String, "''" represents a single quote in java.text.MessageFormat.
      format = manager.getMessageLocalizer(VISUAL_KOPI_MESSAGES_LOCALIZATION_RESOURCE, ident).getText().replaceAll("'", "''");
      return MessageFormat.format(format, params);
    } catch (InconsistencyException e) {
      System.err.println("ERROR: " + e.getMessage());
      return "!" + ident + "!";
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private static final String   VISUAL_KOPI_MESSAGES_LOCALIZATION_RESOURCE = "com/kopiright/vkopi/lib/resource/VKMessages";
}
