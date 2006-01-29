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
import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;

/**
 * JLS 14.9: If Statement
 *
 * The if statement executes an expression and a statement repeatedly
 * until the value of the expression is false.
 */
public class JIfStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate
   * @param	thenClause	the statement to execute if the condition is true
   * @param	elseClause	the statement to execute if the condition is false
   */
  public JIfStatement(TokenReference where,
		      JExpression cond,
		      JStatement thenClause,
		      JStatement elseClause,
		      JavaStyleComment[] comments)
  {
    super(where, comments);

    this.cond = cond;
    this.thenClause = thenClause;
    this.elseClause = elseClause;
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

    cond = cond.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context,
	  cond.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN),
	  KjcMessages.IF_COND_NOTBOOLEAN, cond.getType(factory));
    if (cond instanceof JAssignmentExpression) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.ASSIGNMENT_IN_CONDITION));
    }

    CBodyContext	thenContext = new CSimpleBodyContext(context, 
                                                             context.getEnvironment(), 
                                                             context);

    thenClause.analyse(thenContext);

    if (elseClause == null) {
      context.merge(thenContext);
    } else {
      elseClause.analyse(context);
      if (thenContext.isReachable() && context.isReachable()) {
	context.merge(thenContext);
      } else if (thenContext.isReachable()) {
	context.adopt(thenContext);
      }
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
    p.visitIfStatement(this, cond, thenClause, elseClause);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    if (cond.isConstant()) {
      if (cond.booleanValue()) {
	thenClause.genCode(context);
      } else if (elseClause != null) {
	elseClause.genCode(context);
      }
    } else {
      CodeLabel		elseLabel = new CodeLabel();
      CodeLabel		nextLabel = new CodeLabel();

      cond.genBranch(false, context, elseLabel);   //		COND IFEQ else
      thenClause.genCode(context);			//		THEN CODE
      code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
      code.plantLabel(elseLabel);		//	else:
      if (elseClause != null) {
	elseClause.genCode(context);		//		ELSE CODE
      }
      code.plantLabel(nextLabel);		//	next	...
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		cond;
  private JStatement		thenClause;
  private JStatement		elseClause;
}
