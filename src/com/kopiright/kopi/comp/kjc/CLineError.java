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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.Message;
import com.kopiright.util.base.MessageDescription;

/**
 * This class represents Line errors in error hierarchy
 */
public class CLineError extends CBlockError {
  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  
/**
   * An error with a formatted message as argument
   * @param	where		the position in the source code
   * @param	message		the formatted message
   */
  public CLineError(TokenReference where, Message message) {
    super(where, message);
  }

  /**
   * An error with an arbitrary number of parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public CLineError(TokenReference where, MessageDescription description, Object[] parameters) {
    super(where, description, parameters);
  }

  /**
   * An error with two parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public CLineError(TokenReference where,
		    MessageDescription description,
		    Object parameter1,
		    Object parameter2)
  {
    super(where, description, parameter1, parameter2);
  }

  /**
   * An error with one parameter
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public CLineError(TokenReference where, MessageDescription description, Object parameter) {
    super(where, description, parameter);
  }

  /**
   * An error without parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   */
  public CLineError(TokenReference where, MessageDescription description) {
    super(where, description);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 4854273767953453511L;
}
