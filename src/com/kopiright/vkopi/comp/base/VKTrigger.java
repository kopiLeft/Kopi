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

package com.kopiright.vkopi.comp.base;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents a trigger, ie an action to be executed on events
 */
public class VKTrigger extends VKPhylum implements com.kopiright.vkopi.lib.form.VConstants {


  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the events that this trigger listen
   * @param action		the action to perform
   */
  public VKTrigger(TokenReference where, int events, VKAction action) {
    super(where);

    this.action = action;
    this.events = events;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public int getEvents() {
    return events;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, Commandable window) throws PositionedError {
    action.checkDefinition(context/*, window*/);

    type = -1;
    int[]	TRG_TYPES = context.getTriggers();
    for (int i = 0; i < TRG_TYPES.length; i++) {
      if ((events >> i & 1) > 0) {
	if (type == -1) {
	  type = TRG_TYPES[i];
	} else if (TRG_TYPES[i] != type) {
	  throw new PositionedError(getTokenReference(), BaseMessages.TRIGGER_DIFFERENT_RETURN, TRG_NAMES[i]);
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void genCode(Commandable commandable) {
    boolean	wantReturn;
    switch (type) {
    case TRG_PRTCD:
    case TRG_VOID:
      wantReturn = false;
      break;
    case TRG_OBJECT:
    case TRG_BOOLEAN:
    case TRG_INT:
      wantReturn = true;
      break;
    default:
      throw new InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG " + type);
    }

    commandable.addTrigger(action.genAction(commandable, wantReturn), events, type);
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
    p.printTrigger(events, action);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int			type;
  private int			events;
  private VKAction		action;
}
