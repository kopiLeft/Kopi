/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.util.base.MessageDescription;

/**
 * This class represents the root class all vk elements
 */
public abstract class VKPhylum extends at.dms.util.base.Utils implements VKConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Create a new block element
   *
   * @param where	the token reference of this node
   */
  public VKPhylum(TokenReference where) {
    this.where = where;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the token reference of this node in the source text.
   * @return the entire token reference
   */
  public TokenReference getTokenReference() {
    return where;
  }

  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  /**
   * Adds a compiler error.
   * Redefine this method to change error handling behaviour.
   * @param	desc		the message ident to be displayed
   * @param	params		the array of parameters
   *
   */
  protected void fail(MessageDescription desc, Object[] params)
    throws PositionedError
  {
    throw new PositionedError(getTokenReference(), desc, params);
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	cond		the condition to verify
   * @param	desc		the message ident to be displayed
   * @param	params		the array of parameters
   */
  public final void check(boolean cond, MessageDescription desc, Object[] params)
    throws PositionedError
  {
    if (!cond) {
      fail(desc, params);
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	cond		the condition to verify
   * @param	desc		the message ident to be displayed
   * @param	param1		the first parameter
   * @param	param2		the second parameter
   */
  public final void check(boolean cond,
                          MessageDescription desc,
                          Object param1,
                          Object param2)
    throws PositionedError
  {
    check(cond, desc, new Object[] { param1, param2 });
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	cond		the condition to verify
   * @param	desc		the message ident to be displayed
   * @param	param		the parameter
   */
  public final void check(boolean cond, MessageDescription desc, Object param)
    throws PositionedError
  {
    check(cond, desc, new Object[] { param });
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	cond		the condition to verify
   * @param	desc		the message ident to be displayed
   */
  public final void check(boolean cond, MessageDescription desc)
    throws PositionedError
  {
    check(cond, desc, null);
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public abstract void genVKCode(VKPrettyPrinter p);

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genComments(VKPrettyPrinter p) {
    // !!! move to subnodes
    // p.printComment(((VKTokenReference)getTokenReference()).getComments());
    // !!! move to subnodes
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final TokenReference	where;		// position in the source text
}
