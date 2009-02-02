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
import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * JLS 14.12: Do Statement
 *
 * The do statement executes an expression and a statement repeatedly
 * until the value of the expression is false.
 */
public class JDoStatement extends JLoopStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate
   * @param	body		the loop body
   * @param	comments	comments in the source text
   */
  public JDoStatement(TokenReference where,
		      JExpression cond,
		      JStatement body,
		      JavaStyleComment[] comments)
  {
    super(where, comments);

    this.cond = cond;
    this.body = body;
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
    try {
      CLoopContext	loopContext;
      TypeFactory       factory = context.getTypeFactory();

      loopContext = new CLoopContext(context, context.getEnvironment(), this);

      body.analyse(loopContext);

      if (loopContext.isContinueTarget()) {
	if (loopContext.isReachable()) {
	  loopContext.merge(loopContext.getContinueContextSummary());
	} else {
	  loopContext.adopt(loopContext.getContinueContextSummary());
	  loopContext.setReachable(true);
	}
      }

      cond = cond.analyse(new CExpressionContext(loopContext, context.getEnvironment()));
      check(loopContext,
	    cond.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN),
	    KjcMessages.DO_COND_NOTBOOLEAN, cond.getType(factory));
      if (cond instanceof JAssignmentExpression) {
	loopContext.reportTrouble(new CWarning(getTokenReference(),
					       KjcMessages.ASSIGNMENT_IN_CONDITION));
      }

      loopContext.close(getTokenReference());

      if (cond.isConstant() && cond.booleanValue()) {
	context.setReachable(false);
      }

      if (loopContext.isBreakTarget()) {
	if (context.isReachable()) {
	  context.merge(loopContext.getBreakContextSummary());
	} else {
	  context.adopt(loopContext.getBreakContextSummary());
	}
      }
    } catch (CBlockError e) {
      context.reportTrouble(e);
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
    p.visitDoStatement(this, cond, body);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.pushContext(this);

    CodeLabel		bodyLabel = new CodeLabel();

    code.plantLabel(bodyLabel);				//	body:
    body.genCode(context);					//		BODY CODE
    code.plantLabel(getContinueLabel());			//	cont:
    cond.genBranch(true, context, bodyLabel);		//		EXPR CODE; IFNE body
    code.plantLabel(getBreakLabel());				//	end:	...

    code.popContext(this);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		cond;
  private JStatement		body;
}
