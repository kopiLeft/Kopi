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

package org.kopi.vkopi.comp.form;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JReturnStatement;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.BaseMessages;
import org.kopi.vkopi.comp.base.Commandable;
import org.kopi.vkopi.comp.base.VKActor;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKPhylum;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKType;
import org.kopi.vkopi.comp.base.VKUtils;

/**
 * This class represents a type for a field (an link to a typer defintition)
 */
public abstract class VKFieldType extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a link to a field definition
   *
   * @param where		the token reference of this node
   */
  public VKFieldType(TokenReference where) {
    super(where);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the type definition
   */
  public abstract VKType getDef();

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKField field, Commandable commandable) throws PositionedError {
    checkAutofills(context, field, commandable);
    getDef().checkCode(context);
    if (getDef().getList() != null) {
      JExpression expr = new org.kopi.xkopi.comp.xkjc.XSqlExpr(getTokenReference(), getDef().getList().getTable());

      actionNumber = commandable.addTrigger(new JReturnStatement(getTokenReference(), expr, null), 0, org.kopi.vkopi.lib.form.VConstants.TRG_OBJECT);
      if (getDef().getList().getAction() != null) {
	listActionNumber = commandable.addTrigger(getDef().getList().getAction().getStatement(), 0, org.kopi.vkopi.lib.form.VConstants.TRG_OBJECT);
      } else {
	listActionNumber = -1;
      }
    }
  }

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  protected void checkAutofills(VKContext context, VKField field, Commandable commandable) throws PositionedError {
    VKDefinitionCollector       collector;

    collector = field.getBlock().getDefinitionCollector();
    if (getDef().hasAutofill()) {
      //autofill = field.getBlock().getCommandNumber(CMD_AUTOFILL);
      actorAutofill = collector.getActorDef(CMD_AUTOFILL);
      check(actorAutofill != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_AUTOFILL); // $$$ DO IT ONE TIME
      actorAutofill.checkCode(context, collector);
    }
    if (getDef().hasShortcut()) {
      //shortcut = field.getBlock().getCommandNumber(CMD_SHORTCUT);
      actorShortcut = collector.getActorDef(CMD_SHORTCUT);
      check(actorShortcut != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_SHORTCUT);
      actorShortcut.checkCode(context, collector);
    }
    if (getDef().hasNewItem()) {
      //newitem = field.getBlock().getCommandNumber(CMD_NEWITEM);
      actorNewitem = collector.getActorDef(CMD_NEWITEM);
      actorNewitem.checkCode(context, collector);
      check(actorNewitem != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_NEWITEM);

      //edititem = field.getBlock().getCommandNumber(CMD_EDITITEM);
      actorEdititem = collector.getActorDef(CMD_EDITITEM);
      check(actorEdititem != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_EDITITEM);
      actorEdititem.checkCode(context, collector);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructor() throws PositionedError {
    return getDef().genConstructor();
  }

  /**
   * Returns the list
   */
  public JExpression genCode() {
    if (getDef().getList() == null) {
      return VKUtils.nullLiteral(getTokenReference());
    } else {
      return getDef().getList().genCode(actionNumber, listActionNumber);
    }
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p, TypeFactory factory) {
    genComments(p);
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private	VKActor		actorAutofill;
  private	VKActor		actorShortcut;
  private	VKActor		actorNewitem;
  private	VKActor		actorEdititem;
  protected	int		actionNumber;
  protected	int		listActionNumber;
}
