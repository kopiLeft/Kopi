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
 * $Id: VKExternAction.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JExpressionStatement;
import at.dms.kopi.comp.kjc.JMethodCallExpression;
import at.dms.kopi.comp.kjc.JNameExpression;
import at.dms.kopi.comp.kjc.JStatement;
import at.dms.xkopi.comp.xkjc.XNameExpression;
import at.dms.xkopi.comp.xkjc.XReturnStatement;

/**
 * This class represents an action, ie a list of java statement
 */
public class VKExternAction extends VKAction {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the events that this trigger listen
   * @param action		the action to perform
   */
  public VKExternAction(TokenReference where, String name) {
    super(where);

    this.name = name;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genAction(Commandable act, boolean wantReturn) {
    TokenReference	ref = getTokenReference();
    JNameExpression	name;
    JExpression		expr;

    name = XNameExpression.build(ref, this.name.replace('.', '/'));
    expr = new JMethodCallExpression(ref,
				     name.getPrefix(),
				     name.getName(),
				     new JExpression[]{ act.getThis(ref) });

    if (wantReturn) {
      return new XReturnStatement(ref, expr, null);
    } else {
      return new JExpressionStatement(ref, expr, null);
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
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    p.printExternAction(name);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private String	name;
}
