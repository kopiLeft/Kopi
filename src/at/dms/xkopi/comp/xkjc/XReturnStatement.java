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

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.JavaStyleComment;

/**
 * JLS 14.16: Return Statement
 *
 * A return statement returns control to the invoker of a method or constructor.
 */
public class XReturnStatement extends JReturnStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to return.
   */
  public XReturnStatement(TokenReference where, JExpression expr, JavaStyleComment[] comments) {
    super(where, expr, comments);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CType	returnType = context.getMethodContext().getCMethod().getReturnType();
    TypeFactory factory = context.getTypeFactory();

    if (expr != null) {
      expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (!expr.getType(factory).isAssignableTo(context, returnType)) {
	check(context, false,
	      KjcMessages.RETURN_BADTYPE, expr.getType(factory), returnType);
      } else {
	expr = expr.convertType(new CExpressionContext(context, context.getEnvironment()), context.getMethodContext().getCMethod().getReturnType());
      }
    } else {
      check(context,
	    context.getMethodContext().getCMethod().getReturnType() == context.getTypeFactory().getVoidType(),
	    KjcMessages.RETURN_EMPTY_NONVOID);
    }

    if (expr != null) {
      check(context,
	    !expr.getType(factory).equals(factory.getVoidType()),
	    KjcMessages.RETURN_BADTYPE, expr.getType(factory), returnType);
    }

    checkProtected(context);

    context.setReachable(false);
  }

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  private void checkProtected(CBodyContext context) throws PositionedError {
    XProtectedContext	protectedC = getProtectedContext(context);
    check(context, protectedC == null, XKjcMessages.PROTECTED_RETURN);
    XUnprotectedContext	unprotectedC = getUnprotectedContext(context);
    check(context, unprotectedC == null, XKjcMessages.PROTECTED_RETURN);
  }

  private XProtectedContext getProtectedContext(CContext context) {
    if (context instanceof XProtectedContext) {
      return (XProtectedContext)context;
    } else if (context instanceof CBodyContext) {
      return getProtectedContext(context.getParentContext());
    } else {
      return null;
    }
  }

  private XUnprotectedContext getUnprotectedContext(CContext context) {
    if (context instanceof XUnprotectedContext) {
      return (XUnprotectedContext)context;
    } else if (context instanceof CBodyContext) {
      return getUnprotectedContext(context.getParentContext());
    } else {
      return null;
    }
  }
}
