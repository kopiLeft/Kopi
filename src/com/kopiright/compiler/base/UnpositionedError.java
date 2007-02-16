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

package com.kopiright.compiler.base;

import com.kopiright.util.base.Message;
import com.kopiright.util.base.MessageDescription;

/**
 * This class is the root class for all compiler errors without a reference
 * to the source text.
 */

public class UnpositionedError extends Throwable {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
 
/**
   * Creates an error with a formatted message as argument.
   * @param	message		the formatted message
   */
  public UnpositionedError(Message message) {
    super(message.getDescription().getFormat());

    this.message = message;
  }

  /**
   * Creates an error with an arbitrary number of parameters.
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public UnpositionedError(MessageDescription description, Object[] parameters) {
    this(new Message(description, parameters));
  }

  /**
   * Creates an error with two parameters.
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public UnpositionedError(MessageDescription description, Object parameter1, Object parameter2) {
    this(description, new Object[] { parameter1, parameter2 });
  }

  /**
   * Creates an error with one parameter.
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public UnpositionedError(MessageDescription description, Object parameter) {
    this(description, new Object[] { parameter });
  }

  /**
   * Creates an error without parameters.
   * @param	description	the message description
   */
  public UnpositionedError(MessageDescription description) {
    this(description, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string explaining the error.
   */
  public String getMessage() {
    return message.getMessage();
  }

  /**
   * Returns the formatted message.
   */
  public Message getFormattedMessage() {
    return message;
  }

  /**
   * Returns true iff the error has specified description.
   */
  public boolean hasDescription(MessageDescription description) {
    return message.getDescription() == description;
  }

  /**
   * Returns an error with a reference to the source file.
   * @param	where		the position in the source file responsible for the error
   */
  public PositionedError addPosition(TokenReference where) {
    return new PositionedError(where, message);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private /* javac bug final */ Message		message;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -7655687110734308852L;
}
