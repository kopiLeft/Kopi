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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.MessageDescription;

/**
 * This class represents the root class for all elements of the parsing tree
 */
public abstract class JPhylum extends com.kopiright.compiler.base.Phylum implements Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct an element of the parsing tree
   * @param where the token reference of this node
   */
  public JPhylum(TokenReference where) {
    super(where);
  }

  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  /**
   * Adds a compiler error.
   * Redefine this method to change error handling behaviour.
   * @param	context		the context in which the error occurred
   * @param	description	the message ident to be displayed
   * @param	params		the array of parameters
   *
   */
  protected void fail(CContext context, MessageDescription description, Object[] params)
    throws PositionedError
  {
    throw new CLineError(getTokenReference(), description, params);
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   * @param	params		the array of parameters
   */
  public final void check(CContext context, boolean cond, MessageDescription description, Object[] params)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, params);
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   * @param	param1		the first parameter
   * @param	param2		the second parameter
   */
  public final void check(CContext context, boolean cond, MessageDescription description, Object param1, Object param2)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, new Object[] { param1, param2 });
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   * @param	param		the parameter
   */
  public final void check(CContext context, boolean cond, MessageDescription description, Object param)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, new Object[] { param });
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   */
  public final void check(CContext context, boolean cond, MessageDescription description)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, null);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public abstract void accept(KjcVisitor p);

  /**
   * Sets the line number of this phylum in the code sequence.
   *
   * @param	code		the bytecode sequence
   */
  public void setLineNumber(CodeSequence code) {
    code.setLineNumber(getTokenReference().getLine());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final JPhylum[]       EMPTY = new JPhylum[0];
}
