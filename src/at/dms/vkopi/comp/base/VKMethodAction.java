/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.comp.base;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.xkjc.XReturnStatement;

/**
 * This class represents an action, ie a list of java statement
 */
public class VKMethodAction extends VKBlockAction implements at.dms.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param modes		the events that this trigger listen
   * @param action		the action to perform
   */
  public VKMethodAction(TokenReference where, JCompoundStatement stmt, JFormalParameter[] params, String name) {
    super(where, stmt, name);

    this.params = params;
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

    if (!hasMethod) {
      JFormalParameter	param = (JFormalParameter)params[0];

      JVariableDefinition	def = new JVariableDefinition(ref,
							      param.isFinal() ? ACC_FINAL : 0,
							      param.getType(),
							      param.getIdent(),
							      act.getThis(ref));

      JVariableDeclarationStatement	decl;
      decl = new JVariableDeclarationStatement(ref, def, null);
      stmt = new JCompoundStatement(stmt.getTokenReference(),
				    new JStatement[] {decl, stmt});


      return stmt;
    } else {
      JExpression		expr;

      expr = new JMethodCallExpression(ref,
				       access,
				       name,
				       new JExpression[] {act.getThis(ref)});

      if (wantReturn) {
	return new XReturnStatement(ref, expr, null);
      } else {
	return new JExpressionStatement(ref, expr, null);
      }
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
				  params,
				  VKUtils.TRIGGER_EXCEPTION, // !!! ADD VException
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
    p.printMethodAction(params, stmt);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private JFormalParameter[] params;
}
