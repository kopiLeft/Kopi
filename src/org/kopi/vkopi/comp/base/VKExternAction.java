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

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JExpressionStatement;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JNameExpression;
import org.kopi.kopi.comp.kjc.JStatement;
import org.kopi.xkopi.comp.xkjc.XNameExpression;
import org.kopi.xkopi.comp.xkjc.XReturnStatement;

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
   * @param ident               the identifier of the action to perform
   */
  public VKExternAction(TokenReference where, String ident) {
    super(where);

    this.ident = ident;
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

    name = XNameExpression.build(ref, ident.replace('.', '/'));
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
    p.printExternAction(ident);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final String          ident;
}
