/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.compiler.tools.optgen;

import com.kopiright.util.base.FormattedException;
import com.kopiright.util.base.Message;
import com.kopiright.util.base.MessageDescription;

/**
 * Error thrown on problems encountered while running the program.
 */
public class OptgenError extends FormattedException {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  
/**
   * An exception with a formatted message as argument
   * @param	message		the formatted message
   */
  public OptgenError(Message message) {
    super(message);
  }

  /**
   * An exception with an arbitrary number of parameters
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public OptgenError(MessageDescription description, Object[] parameters) {
    super(description, parameters);
  }

  /**
   * An exception with two parameters
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public OptgenError(MessageDescription description, Object parameter1, Object parameter2) {
    super(description, parameter1, parameter2);
  }

  /**
   * An exception with one parameter
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public OptgenError(MessageDescription description, Object parameter) {
    super(description, parameter);
  }

  /**
   * An exception without parameters
   * @param	description	the message description
   */
  public OptgenError(MessageDescription description) {
    super(description);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -1940949686284187233L;

}
