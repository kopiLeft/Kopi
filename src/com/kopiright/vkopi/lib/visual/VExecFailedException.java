/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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
 * This class represents exceptions occuring during execution process.
 */
public class VExecFailedException extends VException {
  
/**
   * Constructs an exception with no message.
   *
   * @param	message		the associated message
   * @param	origin		the exception that generated this one
   */
  public VExecFailedException(String message, Throwable origin) {
    super(message, origin);
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public VExecFailedException(String message) {
    super(message);
  }

  /**
   * Constructs an exception with no message.
   *
   * @param	origin		the exception that generate this one
   */
  public VExecFailedException(Throwable origin) {
    super(origin.getMessage(), origin);
  }

  /**
   * Constructs an exception with no message.
   */
  public VExecFailedException() {
    super();
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -4610334020287734455L;
}
