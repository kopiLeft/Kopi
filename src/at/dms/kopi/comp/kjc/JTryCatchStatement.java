/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: JTryCatchStatement.java,v 1.2 2004/09/28 10:34:01 graf Exp $
 */

package at.dms.kopi.comp.kjc;

import java.util.Enumeration;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.JavaStyleComment;

/**
 * JLS 14.19: Try Statement
 *
 * A try statement executes a block.
 * If a value is thrown and the try statement has one or more catch
 * clauses that can catch it, then control will be transferred to the
 * first such catch clause.
 * If the try statement has a finally clause, then another block of code
 * is executed, no matter whether the try block completes normally or abruptly,
 * and no matter whether a catch clause is first given control.
 *
 * In this implementation, the Try Statement is split into a Try-Catch Statement
 * and a Try-Finally Statement. A Try Statement where both catch and finally
 * clauses are present is rewritten as a Try-Catch Statement enclosed in a
 * Try-Finally Statement.
 */
public class JTryCatchStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where			the line of this node in the source code
   * @param	tryClause		the body
   * @param	catchClauses		a vector of catch clause
   */
  public JTryCatchStatement(TokenReference where,
			    JBlock tryClause,
			    JCatchClause[] catchClauses,
			    JavaStyleComment[] comments)
  {
    super(where, comments);

    this.tryClause = tryClause;
    this.catchClauses = catchClauses;
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
    /*
     * Analyse the try clause.
     */
    CTryContext		tryContext;
    CSimpleBodyContext  self = new CSimpleBodyContext(context, context.getEnvironment(), context);

    tryContext = new CTryContext(context, context.getEnvironment());
    tryClause.analyse(tryContext);
    if (tryContext.isReachable()) {
      self.merge(tryContext);
      context.adopt(tryContext);
    }
    context.setReachable(tryContext.isReachable());

    /*
     * JLS 14.20 :
     * A try-catch statement can complete normally iff :
     * - the try block can complete normally or
     * - any catch block can complete normally.
     */

    /*
     * JLS 14.20 :
     * The try block is reachable iff the try statement is reachable.
     * A catch block C is reachable iff both of the following are true :
     * - Some expression or throw statement in the try block is reachable
     *   and can throw an exception whose type is assignable to the parameter
     *   of the catch clause C.
     * - There is no earlier catch block A in the try statement such that
     *   the type of C's parameter is the same as or a subclass of the
     *   type of A's parameter.
     *
     * Note : as shown in http://www.ergnosis.com/java-spec-report,
     * - "is assignable" should be replaced by "is cast convertible"
     * - a catch clause is always reachable if its parameter's type is an
     *   unchecked exception class, Exception, or Throwable
     */

    /*
     * Analyse each catch clause. In a first step, assume that every
     * catch clause is reachable.
     */
    for (int i = 0; i < catchClauses.length; i++) {
      CCatchContext	catchContext;

      catchContext = new CCatchContext(self, self.getEnvironment());
      //      catchContext.adopt(tryContext);
      catchContext.setReachable(true);

      catchClauses[i].analyse(catchContext);
      if (catchContext.isReachable()) {
 	if (! self.isReachable()) {
// 	  context.adopt(catchContext);
// 	  context.setReachable(true);
 	} else {
	  context.merge(catchContext);
 	}
      }
      context.mergeThrowables(catchContext);
    }

    /*
     * Check that every catch clause is reachable.
     */
    boolean[]	catchReachable = new boolean[catchClauses.length];
    boolean[]	catchJLSReachable = new boolean[catchClauses.length];

    for (Enumeration elems = tryContext.getThrowables().elements(); elems.hasMoreElements(); ) {
      CThrowableInfo	info = (CThrowableInfo)elems.nextElement();
      CReferenceType	type = info.getThrowable();
      boolean		consumed = false;

      for (int i = 0; i < catchClauses.length; i++) {
        /*
         * JLS 11.3
         * The catch clause handles the exception if the type of its parameter 
         * is the class of the exception or a superclass of the class of the 
         * exception.
         */
	if (type.isCastableTo(context, catchClauses[i].getType())) {
          if (!consumed) {
            catchReachable[i] = true;
            consumed = type.getCClass().descendsFrom(catchClauses[i].getType().getCClass());
          }
          catchJLSReachable[i] = true;
	}
      }
      if (!consumed) {
	context.addThrowable(info);
      }
    }

    /*
     * Mark each catch clause reachable if its parameter's type is an
     * unchecked exception class, Exception, or Throwable.
     */
    for (int i = 0; i < catchClauses.length; i++) {
      CReferenceType            type = catchClauses[i].getType();
      TypeFactory               typeFactory = context.getTypeFactory();
      CReferenceType            throwableType = typeFactory.createReferenceType(TypeFactory.RFT_THROWABLE);
      CReferenceType            exceptionType = typeFactory.createReferenceType(TypeFactory.RFT_EXCEPTION);

      /*
       * JLS 11.2
       * The unchecked exceptions classes are the class RuntimeException 
       * and its subclasses, and the class Error and its subclasses. All
       * other exception classes are checked exception classes.
       */
      if (!catchReachable[i]
	  && (! type.isCheckedException(context)
	      || type.equals(throwableType)
	      || type.equals(exceptionType))) {
	catchReachable[i] = true;
	catchJLSReachable[i] = true;
      }
    }

    /*
     * Check there is no earlier catch clause of the try statement
     * which can handle the same exception.
     */
    for (int i = 0; i < catchClauses.length; i++) {
      if (catchJLSReachable[i]) {
	for (int j = i + 1; j < catchClauses.length; j++) {
	  if (catchJLSReachable[j]
	      && catchClauses[j].getType().isAssignableTo(context, catchClauses[i].getType())) {
	    catchReachable[j] = false;
	    catchJLSReachable[j] = false;
	  }
	}
      }
    }

    for (int i = 0; i < catchClauses.length; i++) {
      if (! catchJLSReachable[i] &&
          !context.getEnvironment().ignoreUnreachableStatement()) {
	context.reportTrouble(new PositionedError(catchClauses[i].getTokenReference(),
						  KjcMessages.CATCH_UNREACHABLE));
      } else if(! catchReachable[i]) {
	context.reportTrouble(new CWarning(catchClauses[i].getTokenReference(),
						  KjcMessages.CATCH_NOT_USED));
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
    p.visitTryCatchStatement(this, tryClause, catchClauses);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		nextLabel = new CodeLabel();

    int		startPC = code.getPC();
    tryClause.genCode(context);			//		TRY CODE
    int		endPC = code.getPC();
    code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
    for (int i = 0; i < catchClauses.length; i++) {
      catchClauses[i].genCode(context, startPC, endPC);
      code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
    }
    code.plantLabel(nextLabel);			//	next:	...
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JBlock		tryClause;
  private JCatchClause[]	catchClauses;
}
