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

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class implements the assignment operation
 */
public class JAssignmentExpression extends JBinaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JAssignmentExpression(TokenReference where,
			       JExpression left,
			       JExpression right)
  {
    super(where, left, right);
  }
  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JAssignmentExpression(TokenReference where,
			       JExpression left,
			       JExpression right,
                               CType type)
  {
    super(where, left, right);
    this.type = type;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true iff this expression can be used as a statement (JLS 14.8)
   */
  public boolean isStatementExpression() {
    return true;
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
    TypeFactory         factory = context.getTypeFactory();

    if (left instanceof JParenthesedExpression) {
      context.reportTrouble(new CWarning(getTokenReference(), KjcMessages.PARENTHESED_LVALUE));
    }
    left = left.analyse(new CExpressionContext(context, 
                                               context.getEnvironment(),
                                               true, 
                                               true));

    right = right.analyse(new CExpressionContext(context, 
                                                 context.getEnvironment(), 
                                                 false, 
                                                 false));
    if (right instanceof JTypeNameExpression) {
      check(context, false, KjcMessages.VAR_UNKNOWN, ((JTypeNameExpression)right).getQualifiedName());
    }

    check(context, left.isLValue(context), KjcMessages.ASSIGNMENT_NOTLVALUE);
    check(context, !left.checkForLoop(context) || !left.isFinal(), KjcMessages.FINAL_IN_LOOP, left.getIdent());

    
      left.setInitialized(context);
   

    check(context,
	  right.isAssignableTo(context, left.getType(factory)),
	  KjcMessages.ASSIGNMENT_BADTYPE, right.getType(factory), left.getType(factory));
    if (left.equals(right)) {
      context.reportTrouble(new CWarning(getTokenReference(), KjcMessages.SELF_ASSIGNMENT));
    }

    type = left.getType(factory);
    right = right.convertType(context, type);

    if (left.requiresAccessor()) {
      JExpression accessorExpr = left.getAccessor(new JExpression[]{right}, OPE_SIMPLE);
      accessorExpr.analyse(context);
      return accessorExpr;
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
    p.visitAssignmentExpression(this, left, right);
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

    left.genStartStoreCode(context);
    right.genCode(context, false);
    left.genEndStoreCode(context, discardValue);
  }
}
