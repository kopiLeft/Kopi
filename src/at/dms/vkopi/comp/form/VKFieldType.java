/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: VKFieldType.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JReturnStatement;
import at.dms.kopi.comp.kjc.TypeFactory;
import at.dms.vkopi.comp.base.*;
import at.dms.util.base.NotImplementedException;

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
      JExpression expr = new at.dms.xkopi.comp.xkjc.XSqlExpr(getTokenReference(), getDef().getList().getTable());

      actionNumber = commandable.addTrigger(new JReturnStatement(getTokenReference(), expr, null), 0, at.dms.vkopi.lib.form.VConstants.TRG_OBJECT);
    }
  }

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  protected void checkAutofills(VKContext context, VKField field, Commandable commandable) throws PositionedError {
    if (getDef().hasAutofill()) {
      //autofill = field.getBlock().getCommandNumber(CMD_AUTOFILL);
      actorAutofill = field.getBlock().getDefinitionCollector().getActorDef(CMD_AUTOFILL);
      check(actorAutofill != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_AUTOFILL); // $$$ DO IT ONE TIME
      actorAutofill.checkCode(context);
    }
    if (getDef().hasShortcut()) {
      //shortcut = field.getBlock().getCommandNumber(CMD_SHORTCUT);
      actorShortcut = field.getBlock().getDefinitionCollector().getActorDef(CMD_SHORTCUT);
      check(actorShortcut != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_SHORTCUT);
      actorShortcut.checkCode(context);
    }
    if (getDef().hasNewItem()) {
      //newitem = field.getBlock().getCommandNumber(CMD_NEWITEM);
      actorNewitem = field.getBlock().getDefinitionCollector().getActorDef(CMD_NEWITEM);
      actorNewitem.checkCode(context);
      check(actorNewitem != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_NEWITEM);
      //edititem = field.getBlock().getCommandNumber(CMD_EDITITEM);
      actorEdititem = field.getBlock().getDefinitionCollector().getActorDef(CMD_EDITITEM);
      check(actorEdititem != null, BaseMessages.COMMAND_DEF_NOT_FOUND, CMD_EDITITEM);
      actorEdititem.checkCode(context);
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
      return getDef().getList().genCode(actionNumber);
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

  private	int		autofill;
  private	int		shortcut;
  private	int		newitem;
  private	int		edititem;
  private	VKActor		actorAutofill;
  private	VKActor		actorShortcut;
  private	VKActor		actorNewitem;
  private	VKActor		actorEdititem;
  protected	int		actionNumber;
}
