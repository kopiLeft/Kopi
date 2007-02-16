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
 * A 'ArrayLength' expression
 */
public class JArrayLengthExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	prefix		the left expression like t.this
   */
  public JArrayLengthExpression(TokenReference where, JExpression prefix) {
    super(where);
    this.prefix = prefix;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return factory.getPrimitiveType(TypeFactory.PRM_INT);
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

    verify(prefix != null);
    prefix = prefix.analyse(context);
    check(context, prefix.getType(factory).isArrayType(), KjcMessages.ARRAY_LENGTH_BADTYPE);

    // JLS 6.6.1
    // An array type is accessible if and only if its element type is accessible. 
    check(context,
          ((CArrayType)prefix.getType(factory)).getBaseType().isPrimitive() 
          || ((CArrayType)prefix.getType(factory)).getBaseType().getCClass().isAccessible(context.getClassContext().getCClass()),
          KjcMessages.CLASS_NOACCESS, 
          ((CArrayType)prefix.getType(factory)).getBaseType());        

    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitArrayLengthExpression(this, prefix);
 }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    prefix.genCode(context, false);
    if (discardValue) {
      code.plantNoArgInstruction(opc_pop);
    } else {
      code.plantNoArgInstruction(opc_arraylength);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	JExpression	prefix;
}
