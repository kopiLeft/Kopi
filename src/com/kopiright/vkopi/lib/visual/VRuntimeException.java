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
 * This class is a package redefinition of RuntimeException that remaind
 * of a stack of exception
 * Warning: Throwing such an exception will always generate a FATAL ERROR
 * and will close the current form.
 */
public class VRuntimeException extends RuntimeException {

/**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public VRuntimeException(String message) {
    super(message);
  }


  /**
   * Constructs an exception with an other exception.
   *
   * @param	exc		the exception
   */
  public VRuntimeException(Throwable exc) {
    super(exc);
  }

  /**
   * Constructs an exception with an other exception.
   *
   * @param	exc		the exception
   */
  public VRuntimeException(String msg, Throwable exc) {
    super(msg, exc);
  }
  /**
   * Constructs an exception with no message.
   */
  public VRuntimeException() {
    super();
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -5068537789034913647L;
}
