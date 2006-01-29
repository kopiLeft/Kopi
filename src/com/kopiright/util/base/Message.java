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

package com.kopiright.util.base;

/**
 * This class represents the root class for all kopic errors
 */
public class Message {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a message with an arbitrary number of parameters
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public Message(MessageDescription description, Object[] parameters) {
    this.description = description;
    this.parameters	= parameters;
  }

  /**
   * Constructs a message with two parameters
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public Message(MessageDescription description, Object parameter1, Object parameter2) {
    this(description, new Object[] { parameter1, parameter2 });
  }

  /**
   * Constructs a message with one parameter
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public Message(MessageDescription description, Object parameter) {
    this(description, new Object[] { parameter });
  }

  /**
   * Constructs a message without parameters
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public Message(MessageDescription description) {
    this(description, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the message description.
   */
  public MessageDescription getDescription() {
    return description;
  }

  /**
   * Returns the message description.
   */
  public Object[] getParams() {
    return parameters;
  }

  /**
   * Returns the severity level
   */
  public int getSeverityLevel() {
    return getDescription().getLevel();
  }

  /**
   * Returns the string explaining the error
   */
  public String getMessage() {
    return this.description.format(this.parameters);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final MessageDescription	description;
  private final Object[]		parameters;
}
