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

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;

/**
 * JLS 14.18: Synchronized Statement
 *
 * A synchronized statement acquires a mutual-exclusion lock on behalf
 * of the executing thread, executes a block, then releases the lock.
 */
public class JSynchronizedStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate.
   * @param	body		the loop body.
   */
  public JSynchronizedStatement(TokenReference where,
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
    TypeFactory         factory = context.getTypeFactory();

    localVar = new JGeneratedLocalVariable(null,
					   0,
					   factory.getPrimitiveType(TypeFactory.PRM_INT),
					   "sync$" + toString() /* unique ID */,
					   null);
    catchVar = new JGeneratedLocalVariable(null,
					   0,
					   context.getTypeFactory().createReferenceType(TypeFactory.RFT_THROWABLE),
					   "sync$catch" + toString() /* unique ID */,
					   null);
    try {
      context.addMonitorVariable(localVar);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    cond = cond.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context, cond.getType(factory).isReference(), KjcMessages.SYNCHRONIZED_NOTREFERENCE);
    check(context, cond.getType(factory) != factory.getNullType(), KjcMessages.SYNCHRONIZED_NOTREFERENCE);
    body.analyse(context);
    
    endReachable = context.isReachable();

    CBlockContext       blockcontext = new CBlockContext(context, context.getEnvironment());

    try {
      blockcontext.addVariable(catchVar);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
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
    p.visitSynchronizedStatement(this, cond, body);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genMonitorExit(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    code.plantLocalVar(opc_aload, localVar);
    code.plantNoArgInstruction(opc_monitorexit);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		nextLabel = new CodeLabel();

    cond.genCode(context, false);
    code.plantNoArgInstruction(opc_dup);
    code.plantLocalVar(opc_astore, localVar);
    code.plantNoArgInstruction(opc_monitorenter);

    code.pushContext(this);

    int		startPC = code.getPC();

    body.genCode(context);
    code.popContext(this);

    if (endReachable) {
      genMonitorExit(context);			//!!! CHECK : inside ?
    }

    int		endPC = code.getPC();

    if (endReachable) {
      code.plantJumpInstruction(opc_goto, nextLabel);
    }

    int         errorPC = code.getPC();

    catchVar.genStore(context);
    genMonitorExit(context);

    int         errorPCend = code.getPC();

    catchVar.genLoad(context);

    code.plantNoArgInstruction(opc_athrow);
    code.plantLabel(nextLabel);			//	next:	...

    // protect
    code.addExceptionHandler(startPC, endPC, errorPC, null);
    code.addExceptionHandler(errorPC, errorPCend, errorPC, null);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean               endReachable;
  private JExpression		cond;
  private JStatement		body;
  private JLocalVariable	localVar;
  private JLocalVariable	catchVar;
}
