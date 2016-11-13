/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.bytecode.ksm;

import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.tools.antlr.runtime.RecognitionException;
import org.kopi.util.base.Message;
import org.kopi.util.base.MessageDescription;

/**
 * Error thrown on problems encountered while running the assembler itself.
 */
public class KsmError extends RecognitionException {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  
/**
   * An error with a formatted message as argument
   * @param	where		the reference to token where error happen
   * @param	message		the formatted message
   */
  public KsmError(TokenReference where, Message message) {
    super(message.getDescription().getFormat());
    this.where = where;
    this.message = message;
  }

  /**
   * An error with with an arbitrary number of parameters
   * @param	where		the reference to token where error happen
   * @param	desc		the message ident to be displayed
   * @param	params		the array of parameters
   */
  public KsmError(TokenReference where, MessageDescription desc, Object[] params) {
    this(where, new Message(desc, params));
  }

  /**
   * An error with two parameters
   * @param	where		the reference to token where error happen
   * @param	desc		the message ident to be displayed
   * @param	param1		the first parameter
   * @param	param2		the second parameter
   */
  public KsmError(TokenReference where, MessageDescription desc, Object param1, Object param2) {
    this(where, desc, new Object[] { param1, param2 });
  }

  /**
   * An error with one parameter
   * @param	where		the reference to token where error happen
   * @param	desc		the message ident to be displayed
   * @param	param		the parameter
   */
  public KsmError(TokenReference where, MessageDescription desc, Object param) {
    this(where, desc, new Object[] { param });
  }

  /**
   * An error without parameters
   * @param	where		the reference to token where error happen
   * @param	desc		the message ident to be displayed
   */
  public KsmError(TokenReference where, MessageDescription desc) {
    this(where, desc, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public MessageDescription getMessageDescription() {
    return message.getDescription();
  }

  /**
   * Returns a string explaining the error and the token reference.
   */
  public String getMessage() {
    if (where != null) {
      return where.getFile() + ":" + where.getLine() + ": " + message.getMessage();
    } else {
      return message.getMessage();
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected TokenReference	where;
  protected Message		message;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -2912460560028564490L;
}
