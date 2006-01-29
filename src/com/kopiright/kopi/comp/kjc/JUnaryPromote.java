/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

/**
 * This class convert arithmetics expression from types to types
 */
public final class JUnaryPromote extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public JUnaryPromote(CTypeContext context, JExpression expr, CType type) {
    super(expr.getTokenReference());

    this.expr = expr;
    this.type = type;

    if (!expr.isAssignableTo(context, type)) {
      needCheck = true;
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
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
    TypeFactory factory = context.getTypeFactory();

    if (type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))
	&& expr.getType(factory).isReference()
	&& expr.getType(factory) != factory.getNullType()) {
      return new JMethodCallExpression(getTokenReference(),
				       new JCheckedExpression(getTokenReference(), expr),
				       "toString",
				       JExpression.EMPTY).analyse(context);
    } else {
      return this;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (needCheck) {
      p.visitUnaryPromoteExpression(this, expr, type);
    } else {
      expr.accept(p);
    }
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    expr.genCode(context, false);

    if (type.isNumeric()) {
      ((CNumericType)expr.getType(factory)).genCastTo((CNumericType)type, context);
    } else if (needCheck) {
      code.plantClassRefInstruction(opc_checkcast, ((CReferenceType)type).getQualifiedName());
    }

    if (discardValue) {
      code.plantPopInstruction(type);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		expr;
  private CType			type;
  private boolean		needCheck;
}
