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
 * $Id: VKAction.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JStatement;
import at.dms.kopi.comp.kjc.JMethodDeclaration;

/**
 * This class represents an action, ie a list of java statement
 */
public abstract class VKAction extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the events that this trigger listen
   * @param action		the action to perform
   */
  public VKAction(TokenReference where) {
    super(where);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param decl	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkDefinition(VKContext context) {
    JExpression		access = context.getAccess(getTokenReference(), context.isInsertFile());
    JMethodDeclaration	decl = genMethod(access, context.isInsertFile());

    if (decl != null) {
      context.getClassContext().addMethodDeclaration(decl);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract JStatement genAction(Commandable act, boolean wantReturn);

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration genMethod(JExpression access, boolean isStatic) {
    return null;
  }
}
