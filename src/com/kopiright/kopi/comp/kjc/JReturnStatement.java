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
import com.kopiright.compiler.base.JavaStyleComment;

/**
 * JLS 14.16: Return Statement
 *
 * A return statement returns control to the invoker of a method or constructor.
 */
public class JReturnStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to return.
   */
  public JReturnStatement(TokenReference where, JExpression expr, JavaStyleComment[] comments) {
    super(where, comments);
    this.expr = expr;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------


  /**
   * Returns the type of this return statement
   */
  public CType getType(TypeFactory factory) {
    return expr != null ? expr.getType(factory) : factory.getVoidType();
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

    check(context,
          !(context.getMethodContext() instanceof CInitializerContext),
          KjcMessages.RETURN_INITIALIZER);
    

    if (expr != null) {
      check(context, returnType.getTypeID() != TID_VOID, KjcMessages.RETURN_NONEMPTY_VOID);

      CExpressionContext	expressionContext = new CExpressionContext(context, 
                                                                           context.getEnvironment());
      expr = expr.analyse(expressionContext);

      check(context, expr.isExpression(), KjcMessages.NOT_AN_EXPRESSION, expr);

      check(context,
	    expr.isAssignableTo(expressionContext, returnType),
	    KjcMessages.RETURN_BADTYPE, expr.getType(factory), returnType);
      expr = expr.convertType(expressionContext, returnType);
    } else {
      check(context, returnType.getTypeID() == TID_VOID, KjcMessages.RETURN_EMPTY_NONVOID);
    }

    context.setReachable(false);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitReturnStatement(this, expr);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    if (expr != null) {
      expr.genCode(context, false);

      code.plantReturn(this, context);
      code.plantNoArgInstruction(expr.getType(factory).getReturnOpcode());
    } else {
      code.plantReturn(this, context);
      code.plantNoArgInstruction(opc_return);
    }
  }

  /**
   * Load the value from a local var (after finally)
   */
  public void load(GenerationContext context, JLocalVariable var) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    code.plantLocalVar(expr.getType(factory).getLoadOpcode(), var);
  }

  /**
   * Load the value from a local var (after finally)
   */
  public void store(GenerationContext context, JLocalVariable var) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    code.plantLocalVar(expr.getType(factory).getStoreOpcode(), var);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected JExpression		expr;
}
