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

/**
 * This class is the root of all visual kopi exception
 * According to type and message, this exception will be handled
 * in different part of the action processing. For instance, VFieldException
 * will be handled by VForm that will display an error message if the message
 * is not null and it will put the focus on the caller field
 * Warning: All exceptions occuring in commands that do not herit from VException
 * (or SQLException in proteced statement) will generate a FATAL ERROR and close
 * the current form
 */
public abstract class VException extends Exception {

  /**
   * Constructs an exception with no message.
   */
  public VException() {
    super();
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public VException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param	message		the associated message
   * @param     cause           the cause  (null value permited
   */
  public VException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new exception with the specified cause and a detail message
   *
   * @param     cause           the cause  (null value permited
   */
  public VException(Throwable cause) {
    super(cause);
  }
}
