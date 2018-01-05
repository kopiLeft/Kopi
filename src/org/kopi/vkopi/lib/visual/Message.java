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

package org.kopi.vkopi.lib.visual;

import java.util.Locale;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.base.ExtendedMessageFormat;
import org.kopi.vkopi.lib.l10n.LocalizationManager;

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
    return getMessage(ident, (Object)null);
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
    return getMessage(VISUAL_KOPI_MESSAGES_LOCALIZATION_RESOURCE, ident, params);
  }

  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @return    the requested message
   */
  public static String getMessage(String source, String ident) {
    return getMessage(source, ident, null);
  }

  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @param     param           a message parameter
   * @return    the requested message
   */
  public static String getMessage(String source, String ident, Object param) {
    return getMessage(source, ident, new Object[] {param});
  }

  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @param     param1          the first message parameter
   * @param     param1          the second message parameter
   * @return    the requested message
   */
  public static String getMessage(String source, String ident, Object param1, Object param2) {
    return getMessage(source, ident, new Object[] {param1, param2});
  }

  /**
   * Returns the formatted message identified by its unique key and formatted
   * using the given parameters objects.
   *
   * @param source The localization XML source.
   * @param ident The message identifiers.
   * @param params The message parameters.
   * @return The formatted message.
   */
  public static String getMessage(String source, String ident, Object[] params) {
    LocalizationManager         manager;
    String                      format;

    if (ApplicationContext.getApplicationContext() != null
        && ApplicationContext.getApplicationContext().getApplication() != null) {
      manager = ApplicationContext.getLocalizationManager();
    } else {
      manager = new LocalizationManager(Locale.getDefault(), null);
    }
    try {
      //   Within a String, "''" represents a single quote in java.text.MessageFormat.
      format = manager.getMessageLocalizer(source, ident).getText().replaceAll("'", "''");
      return ExtendedMessageFormat.formatMessage(format, params);
    } catch (InconsistencyException e) {
      System.err.println("ERROR: " + e.getMessage());
      return "!" + ident + "!";
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final String   VISUAL_KOPI_MESSAGES_LOCALIZATION_RESOURCE = "org/kopi/vkopi/lib/resource/VKMessages";
}
