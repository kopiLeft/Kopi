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

package com.kopiright.vkopi.lib.print;

import com.kopiright.vkopi.lib.util.PrintException;

/**
 * Postscript Print Failure
 */
public class PSPrintException extends PrintException {
  
/**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public  PSPrintException(String message) {
    super(message, EXC_FATAL);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param	message		the associated message
   * @param     cause           the cause  (null value permited
   */
  public  PSPrintException(String message, Throwable cause) {
    super(message, cause, EXC_FATAL);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -2888904066024784526L;
}
