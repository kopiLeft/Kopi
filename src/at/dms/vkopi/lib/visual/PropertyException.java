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
 * $Id: PropertyException.java,v 1.1 2004/08/18 15:31:04 lackner Exp $
 */

package at.dms.vkopi.lib.visual;

/**
 */
public class PropertyException extends Exception {

  /**
   * Constructs an exception with no message.
   */
  public PropertyException() {
    super();
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public PropertyException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param	message		the associated message
   * @param     cause           the cause  (null value permited
   */
  public PropertyException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new exception with the specified cause and a detail message
   *
   * @param     cause           the cause  (null value permited
   */
  public PropertyException(Throwable cause) {
    super(cause);
  }
}
