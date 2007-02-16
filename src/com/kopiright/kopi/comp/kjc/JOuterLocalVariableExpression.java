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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * Root class for all expressions
 */
public class JOuterLocalVariableExpression extends JLocalVariableExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JOuterLocalVariableExpression(TokenReference where,
				       JLocalVariable var,
				       CClass outer) {
    super(where, var);

    this.outer = outer;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    // First we have to find the right context
    CContext	body = context.getBodyContext();
    while (body.getClassContext().getCClass() != outer) {
      body = body.getClassContext().getParentContext();
    }

    CContext		parent = body.getClassContext().getParentContext();
    CExpressionContext  ctxt = parent instanceof CExpressionContext ? (CExpressionContext)parent : new CExpressionContext((CBodyContext)parent, context.getEnvironment());
    JExpression		expr = super.analyse(ctxt);

    if (! (expr instanceof JLiteral)) {
      check(context,
	    expr == this && getVariable().isFinal(),
	    KjcMessages.BAD_LOCAL_NOT_FINAL,
	    getVariable().getIdent());

      CSourceClass      local = (CSourceClass) context.getClassContext().getCClass();

      expr = local.getOuterLocalAccess(getTokenReference(),
                                       getVariable(),
                                       context.getMethodContext() instanceof CConstructorContext ?
                                       ((CConstructorContext)context.getMethodContext()).getCMethod() :
                                       null,
                                       (CSourceClass) outer);
      expr = expr.analyse(context);
    }

    return expr;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public CClass		outer;
}
