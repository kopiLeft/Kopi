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

import org.kopi.kopi.comp.kjc.*;
import org.kopi.compiler.base.TokenReference;

/**
 * This class represents an action, ie a list of java statement
 */
public class VKBlockAction extends VKAction implements org.kopi.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the events that this trigger listen
   * @param action		the action to perform
   */
  public VKBlockAction(TokenReference where, JCompoundStatement stmt, String name) {
    super(where);

    this.stmt = stmt;
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
    if (!hasMethod) {
      return stmt;
    } else {
      return new JExpressionStatement(getTokenReference(),
				      new JMethodCallExpression(getTokenReference(),
								access,
								name,
								JExpression.EMPTY),
				      null);
    }
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration genMethod(JExpression access, boolean isStatic) {
    if (name == null || hasMethod) {
      return null;
    }
    this.isStatic = isStatic;
    this.hasMethod = true;
    this.access = access;
    return new JMethodDeclaration(getTokenReference(),
				  ACC_PUBLIC | ACC_FINAL | (isStatic ? ACC_STATIC : 0), 
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  name,
				  JFormalParameter.EMPTY,
				  VKUtils.TRIGGER_EXCEPTION,
				  new JBlock(getTokenReference(), new JStatement[] {stmt}, null),
				  null,
				  null);
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
    p.printBlockAction(stmt);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  protected boolean		isStatic;
  protected boolean		hasMethod;
  protected String		name;
  protected JCompoundStatement	stmt;
  protected JExpression		access;
}
