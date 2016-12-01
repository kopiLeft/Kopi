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

package org.kopi.vkopi.comp.base;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.JExpression;

/**
 * This class represent a command, ie a link between an actor and
 * an action
 */
public abstract class VKCommand extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the menu name
   * @param name		the item name
   * @param action		the action to execute
   */
  public VKCommand(TokenReference where, int modes) {
    super(where);

    this.modes = modes;
  }

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the menu name
   * @param name		the item name
   * @param action		the action to execute
   */
  public VKCommand(TokenReference where, int modes, VKCommandBody body) {
    super(where);

    this.modes = modes;
    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public VKCommandBody getBody() {
    return body;
  }

  protected void setBody(VKCommandBody body) {
    this.body = body;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract void checkCode(VKContext context, Commandable window) throws PositionedError;

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void genCode(Commandable commandable) {
    pos = commandable.addCommand(body.genCode(commandable));
  }

  /**
   *
   */
  public JExpression genConstructorCall(Commandable commandable) {
    return body.genConstructorCall(commandable.getDefinitionCollector(), modes, pos);
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  protected	int			modes;
  private	VKCommandBody		body;
  private	int			pos;
}
