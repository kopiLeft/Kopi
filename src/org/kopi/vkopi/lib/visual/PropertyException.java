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
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 5151822597040726650L;

}
