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

package com.kopiright.vkopi.comp.base;

import java.util.Vector;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represent a command, ie a link between an actor and
 * an action
 */
public class VKCommandBody extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the menu name
   * @param name		the item name
   * @param action		the action to execute
   * @param block		block the UI during execution of this command
   */
  public VKCommandBody(TokenReference where, String actor, VKAction action) {
    super(where);

    this.actor = actor;
    this.action = action;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param handle		the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, Commandable commandable) throws PositionedError {
    actorVK = commandable.getDefinitionCollector().getActorDef(actor);
    check(actorVK != null, BaseMessages.UNDEFINED_ACTION, actor);
    actorVK.checkCode(context);
    // !!!check(number != -1, "vk-item-not-found", actor);
  }

  /**
   * CheckDefinition
   */
  public void checkDefinition(VKContext context) {
    action.checkDefinition(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genCode(Commandable commandable) {
    return action.genAction(commandable, false);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructorCall(VKDefinitionCollector coll, int modes, int pos) {
    TokenReference	ref = getTokenReference();
    JExpression		expr;

    expr = new JMethodCallExpression(ref,
				     null,
				     "getActor",
				     new JExpression[] {
				       VKUtils.toExpression(ref, coll.getActorPos(actor))
				     });
    return new JUnqualifiedInstanceCreation(ref,
				    VKStdType.VCommand,
				    new JExpression[] {
				      VKUtils.toExpression(ref, modes),
				      new JThisExpression(ref),
				      expr,
				      VKUtils.toExpression(ref, pos),
				      VKUtils.toExpression(ref, actor)});
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
    p.printCommandBody(actor, action);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private String		actor;
  private VKActor		actorVK;
  private VKAction		action;
}
