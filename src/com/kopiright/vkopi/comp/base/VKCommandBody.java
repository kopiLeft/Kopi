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

package com.kopiright.vkopi.comp.base;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JThisExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;

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
   * @param triggers		The triggers owned by this command
   */
  public VKCommandBody(TokenReference where, String actor, VKAction action, VKTrigger[] triggers) {
    super(where);

    this.actor = actor;
    this.action = action;
    this.triggers = triggers;
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
    actorDef = commandable.getDefinitionCollector().getActorDef(actor);
    check(actorDef != null, BaseMessages.UNDEFINED_ACTOR, actor);
    actorDef.checkCode(context, commandable.getDefinitionCollector());
    this.commandable = new Commandable(actor, commandable);
    this.commandable.checkCode(context, commandable.getDefinitionCollector(), new VKCommand[0], triggers);
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
    for (int i = 0; i < triggers.length; i++) {
      triggers[i].genCode(getCommandable());
    }
    
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
   *
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    p.printCommandBody(actor, action);
  }
  
  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------
  
  /**
   * Returns the triggers array.
   */
  public int[] getTriggerArray() {
    return commandable.getTriggerArray();
  }

  /**
   * Returns commandable
   */
  public Commandable getCommandable() {
    return commandable;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private String		actor;
  private VKActor		actorDef;
  private VKAction		action;
  private VKTrigger[] 		triggers;
  private Commandable		commandable;
}
