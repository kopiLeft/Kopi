/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.kopi.comp.kjc;

import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * JLS 14.11: While Statement
 *
 * The while statement executes an expression and a statement repeatedly
 * until the value of the expression is false.
 */
public class JWhileStatement extends JLoopStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate.
   * @param	body		the loop body.
   */
  public JWhileStatement(TokenReference where,
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
      CLoopContext	condContext = new CLoopContext(context, context.getEnvironment(), this);
      TypeFactory       factory = context.getTypeFactory();

      cond = cond.analyse(new CExpressionContext(condContext,
                                                 context.getEnvironment()));
      condContext.close(getTokenReference());
      check(context,
	    cond.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN),
	    KjcMessages.WHILE_COND_NOTBOOLEAN, cond.getType(factory));

      if (!cond.isConstant()) {
	CBodyContext	neverContext = context.cloneContext();
	CLoopContext	bodyContext = new CLoopContext(context, 
                                                       context.getEnvironment(), 
                                                       this);

	body.analyse(bodyContext);
	bodyContext.close(getTokenReference());

	context.merge(neverContext);
     } else {
	// JLS 14.20 Unreachable Statements :
	// The contained statement [of a while statement] is reachable iff the while
	// statement is reachable and the condition expression is not a constant
	// expression whose value is false.
	check(context, cond.booleanValue(), KjcMessages.STATEMENT_UNREACHABLE);

	if (cond instanceof JAssignmentExpression) {
	  context.reportTrouble(new CWarning(getTokenReference(),
					     KjcMessages.ASSIGNMENT_IN_CONDITION));
	}

	CLoopContext	bodyContext;

	bodyContext = new CLoopContext(context, context.getEnvironment(), this);
	body.analyse(bodyContext);
	if (bodyContext.isBreakTarget()) {
	  bodyContext.adopt(bodyContext.getBreakContextSummary());
	}
	bodyContext.close(getTokenReference());
	context.setReachable(bodyContext.isBreakTarget());
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
    p.visitWhileStatement(this, cond, body);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.pushContext(this);

    if (cond.isConstant() && cond.booleanValue()) {
      // while (true)
      code.plantLabel(getContinueLabel());			//	body:
      body.genCode(context);					//		BODY CODE
      code.plantJumpInstruction(opc_goto, getContinueLabel());		//		GOTO body
    } else {
      CodeLabel		bodyLabel = new CodeLabel();

      code.plantJumpInstruction(opc_goto, getContinueLabel());		//		GOTO cont
      code.plantLabel(bodyLabel);				//	body:
      body.genCode(context);					//		BODY CODE
      code.plantLabel(getContinueLabel());			//	cont:
      cond.genBranch(true, context, bodyLabel);			//		EXPR CODE; IFNE body
    }
    code.plantLabel(getBreakLabel());				//	end:	...

    code.popContext(this);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		cond;
  private JStatement		body;
}
