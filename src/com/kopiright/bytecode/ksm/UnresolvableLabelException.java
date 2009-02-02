/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.bytecode.ksm;

import com.kopiright.bytecode.classfile.BadAccessorException;
import com.kopiright.util.base.Message;
import com.kopiright.util.base.MessageDescription;

/**
 * This class defines exceptions for labels that cannot be resolved.
 */
public class UnresolvableLabelException extends BadAccessorException {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
   

/**
   * An exception with a formatted message as argument
   * @param	message		the formatted message
   */
  public UnresolvableLabelException(Message message) {
    super(message.getDescription().getFormat());

    this.message = message;
  }

  /**
   * An exception with an arbitrary number of parameters
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public UnresolvableLabelException(MessageDescription description, Object[] parameters) {
    this(new Message(description, parameters));
  }

  /**
   * An exception with two parameters
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public UnresolvableLabelException(MessageDescription description, Object parameter1, Object parameter2) {
    this(description, new Object[] { parameter1, parameter2 });
  }

  /**
   * An exception with one parameter
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public UnresolvableLabelException(MessageDescription description, Object parameter) {
    this(description, new Object[] { parameter });
  }

  /**
   * An exception without parameters
   * @param	description	the message description
   */
  public UnresolvableLabelException(MessageDescription description) {
    this(description, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string explaining the exception.
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

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private /* javac bug final */ Message		message;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -4621409536256706963L;
}
