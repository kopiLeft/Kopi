/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import com.kopiright.util.base.FormattedException;
import com.kopiright.util.base.Message;
import com.kopiright.util.base.MessageDescription;

/**
 * This class is the root class for all compiler errors with a reference
 * to the source text.
 */

public class PositionedError extends FormattedException {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  
/**
   * An error with a formatted message as argument
   * @param	where		the position in the source code
   * @param	message		the formatted message
   */
  public PositionedError(TokenReference where, Message message) {
    super(message);
    this.where = where;
  }

  /**
   * An error with an arbitrary number of parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public PositionedError(TokenReference where, MessageDescription description, Object[] parameters) {
    super(description, parameters);
    this.where = where;
  }

  /**
   * An error with two parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public PositionedError(TokenReference where,
			 MessageDescription description,
			 Object parameter1,
			 Object parameter2)
  {
    super(description, parameter1, parameter2);
    this.where = where;
  }

  /**
   * An error with one parameter
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public PositionedError(TokenReference where, MessageDescription description, Object parameter) {
    super(description, parameter);
    this.where = where;
  }

  /**
   * An error without parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   */
  public PositionedError(TokenReference where, MessageDescription description) {
    super(description);
    this.where = where;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the position in the source code.
   */
  public TokenReference getTokenReference() {
    return where;
  }

  /**
   * Returns the string explaining the error.
   */
  public String getMessage() {
    return where.getFile() + ":" + where.getLine() + ": " + super.getMessage();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final TokenReference		where;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 40711485077424146L;

}
