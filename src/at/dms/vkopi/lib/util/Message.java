/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: Message.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class handles localised messages
 */
public class Message {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a message with an arbitrary number of parameters
   * @param	key		the message key
   * @param	params		the array of parameters
   */
  public Message(String key, Object[] params) {
    this.key	= key;
    this.params	= params;
  }

  /**
   * Constructs a message with 2 parameters
   * @param	key		the message key
   * @param	param1		the first parameter
   * @param	param2		the second parameter
   */
  public Message(String key, Object param1, Object param2) {
    this(key, new Object[] { param1, param2 });
  }

  /**
   * Constructs a message with 1 parameter
   * @param	key		the message key
   * @param	param		the parameter
   */
  public Message(String key, Object param) {
    this(key, new Object[] { param });
  }

  /**
   * Constructs a message with no parameter
   * @param	key		the message key
   * @param	param		the parameter
   */
  public Message(String key) {
    this(key, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the string explaining the error
   */
  public String getMessage() {
    String	format = getString(this.key);

    if (format == null) {
      return "!" + this.key + "!";
    } else {
      return MessageFormat.format(format, this.params);
    }
  }

  /**
   * Returns the message key.
   */
  public String getKey() {
    return key;
  }

  // ----------------------------------------------------------------------
  // STATIC METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns a message (convenience routine).
   * @param	key		the message key
   */
  public static String getMessage(String key) {
    return Message.getMessage(key, (Object[]) null);
  }

  /**
   * Returns a message (convenience routine).
   * @param	key		the message key
   * @param	param1		the first parameter
   */
  public static String getMessage(String key, Object param1) {
    return Message.getMessage(key, new Object[] {param1});
  }

  /**
   * Returns a message (convenience routine).
   * @param	key		the message key
   * @param	param1		the first parameter
   * @param	param2		the second parameter
   */
  public static String getMessage(String key, Object param1, Object param2) {
    return Message.getMessage(key, new Object[] {param1, param2});
  }

  /**
   * Returns a message (convenience routine).
   * @param	key		the message key
   * @param	params		the array of parameters
   */
  public static String getMessage(String key, Object[] params) {
    String	format = getString(key);

    if (format == null) {
      return "!" + key + "!";
    } else {
      return MessageFormat.format(format, params);
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns the property
   */
  private static String getString(String key) {
    if (! bundleLoaded) {
      try {
	bundle = ResourceBundle.getBundle("vlib_messages");
      } catch (MissingResourceException e) {
	System.err.println("properties not found, error message will be sybillin");
      }

      bundleLoaded = true;
    }

    if (bundle == null) {
      return null;
    } else {
      try {
	return bundle.getString(key);
      } catch (MissingResourceException e) {
	return null;
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static boolean		bundleLoaded = false;
  private static ResourceBundle		bundle;

  private final String			key;
  private final Object[]		params;
}
