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

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.l10n.MessageLocalizer;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This class handles localized messages.
 */
public class MessageCode {


  // ----------------------------------------------------------------------
  // STATIC METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns a message (convenience routine).
   * key must be of the form CCC-DDDDD (exp: KOP-00001)
   * where CCC is a 3 capital character module id
   * and DDDDD a 5 digit message id
   *
   * @param     key            the message key
   * @return    the requested message
   */
  public static String getMessage(String key) {
    LocalizationManager         manager;
    String                      module;
    String                      ident;
    String                      src;

    if (keyPattern.matcher(key).matches()){
      module = key.substring(0, 3);
      ident = key.substring(4, 9);
      if (Application.getRegister() != null) {
        src = Application.getRegister().getMessageSource(module);
      } else {
        throw new InconsistencyException("No Register set for this application.");
      }
    } else {
      throw new InconsistencyException("Malformed message key '" + key + "'");
    }
    if (src == null) {
      throw new InconsistencyException("No message source found for module '" + module + "'");
    }

    try {
      manager = new LocalizationManager(Locale.getDefault());
      return manager.getMessageLocalizer(src, ident).getText();
    } catch (InconsistencyException e) {
      return "!" + key + "!";
    }
  }
  

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final Pattern          keyPattern = Pattern.compile("^[A-Z][A-Z][A-Z]-\\d\\d\\d\\d\\d$");
}
