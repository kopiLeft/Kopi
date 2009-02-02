/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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
 * JLS 14.17: Throw Statement
 *
 * A throw statement causes an exception to be thrown.
 */
public class JThrowStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to throw.
   */
  public JThrowStatement(TokenReference where,
			 JExpression expr,
			 JavaStyleComment[] comments)
  {
    super(where, comments);
    this.expr = expr;
    this.location = this;
  }

  /**
   * Construct a node in the parsing tree. Used for mechanicaly created code
   *
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to throw.
   * @param     location        location of cause (used instead of this statement)
   */
  public JThrowStatement(TokenReference where,
			 JExpression expr,
                         JPhylum location,
			 JavaStyleComment[] comments)
  {
    super(where, comments);
    this.expr = expr;
    this.location = (location != null) ? location : this;
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
    TypeFactory         factory = context.getTypeFactory();

    expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context,
	  expr.getType(factory).isReference()
	  && expr.isAssignableTo(context, factory.createReferenceType(TypeFactory.RFT_THROWABLE)),
	  KjcMessages.THROW_BADTYPE, expr.getType(factory));

    if (expr.getType(factory).isCheckedException(context)) {
      context.addThrowable(new CThrowableInfo((CReferenceType)expr.getType(factory), location));
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
    p.visitThrowStatement(this, expr);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    expr.genCode(context, false);
    code.plantNoArgInstruction(opc_athrow);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		expr;
  private JPhylum               location;
}
