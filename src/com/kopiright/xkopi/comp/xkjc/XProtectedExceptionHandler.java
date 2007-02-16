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

package com.kopiright.xkopi.comp.xkjc;

import java.util.Enumeration;

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.kopi.comp.kjc.CBodyContext;
import com.kopiright.kopi.comp.kjc.CCatchContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CSimpleBodyContext;
import com.kopiright.kopi.comp.kjc.CThrowableInfo;
import com.kopiright.kopi.comp.kjc.CTryContext;
import com.kopiright.kopi.comp.kjc.CodeLabel;
import com.kopiright.kopi.comp.kjc.CodeSequence;
import com.kopiright.kopi.comp.kjc.GenerationContext;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JBooleanLiteral;
import com.kopiright.kopi.comp.kjc.JCatchClause;
import com.kopiright.kopi.comp.kjc.JConditionalOrExpression;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JExpressionStatement;
import com.kopiright.kopi.comp.kjc.JFormalParameter;
import com.kopiright.kopi.comp.kjc.JIfStatement;
import com.kopiright.kopi.comp.kjc.JLocalVariable;
import com.kopiright.kopi.comp.kjc.JLocalVariableExpression;
import com.kopiright.kopi.comp.kjc.JLogicalComplementExpression;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JPhylum;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JThrowStatement;
import com.kopiright.kopi.comp.kjc.KjcVisitor;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.base.XInterruptProtectedException;

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
public class XProtectedExceptionHandler extends JStatement {


  /**
   * Creates the catch clauses;
   */
  public interface CatchClauseFactory {

    /**
     * Creates catch clause for the specified type of exception
     * @param exception       type of exception it should handle
     * @param location        location of the exception
     * @param context         context for evalutation
     */
    JCatchClause createCatchClause(CBodyContext context, CReferenceType exception, JPhylum location);

  }


  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		    the line of this node in the source code
   * @param	tryClause	    the body
   * @param	factory             creates the catch clause which are necessary
   */
  public XProtectedExceptionHandler(TokenReference where,
                                    JBlock tryClause,
                                    CatchClauseFactory factory,
                                    JavaStyleComment[] comments)
  {
    super(where, comments);
    this.tryClause = tryClause;
    this.factory = factory;
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

    CReferenceType[]    exceptions = new CReferenceType[tryContext.getThrowables().size()+2];
    JPhylum[]           exceptionLocations = new JPhylum[tryContext.getThrowables().size()+2];
    int                 size = 0;

    for (Enumeration enumThrowables = tryContext.getThrowables().elements(); enumThrowables.hasMoreElements(); ) {
      CThrowableInfo	info = (CThrowableInfo)enumThrowables.nextElement();
      CReferenceType	type = info.getThrowable();
      boolean		consumed = false;

      if  (type.isCheckedException(context)) {
        for (int i = 0; i < size; i++) {
          if (type.getCClass().descendsFrom(exceptions[i].getCClass())) {
            consumed = true;
            exceptions[i] = type;
            exceptionLocations[i] = info.getLocation();
            //            context.addThrowable(info);
            break;
          } else if (exceptions[i].getCClass().descendsFrom(type.getCClass())) {
            consumed = true;
            break;
          }
        }
        if (!consumed) {
          exceptions[size] = type;
          exceptionLocations[size++] = info.getLocation();
        }
      }
    }

    // check at least Error (AssertError, ...) and RuntimeException
    exceptions[size] = context.getTypeFactory().createReferenceType(TypeFactory.RFT_ERROR);
    exceptionLocations[size++] = this;
    exceptions[size] = context.getTypeFactory().createReferenceType(TypeFactory.RFT_RUNTIMEEXCEPTION);
    exceptionLocations[size++] = this;

    catchClauses = new JCatchClause[size];

    for (int i = 0; i < size; i++) {
      catchClauses[i] = factory.createCatchClause(context, exceptions[i], exceptionLocations[i]);
    }    

    /*
     * Analyse each catch clause. In a first step, assume that every
     * catch clause is reachable.
     */
    for (int i = 0; i < catchClauses.length; i++) {
      CCatchContext	catchContext;//                                                         null)


      catchContext = new CCatchContext(self, self.getEnvironment());
      catchContext.setReachable(true);

      catchClauses[i].analyse(catchContext);
      if (catchContext.isReachable()) {
 	if (self.isReachable()) {
	  context.merge(catchContext);
 	}
      }
      context.mergeThrowables(catchContext);
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
    p.visitTryCatchStatement(null, tryClause, catchClauses);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		nextLabel = new CodeLabel();
    int                 startPC = code.getPC();

    tryClause.genCode(context);			//		TRY CODE

    int                 endPC = code.getPC();

    code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next

    for (int i = 0; i < catchClauses.length; i++) {
      catchClauses[i].genCode(context, startPC, endPC);
      code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
    }

    code.plantLabel(nextLabel);			//	next:	...
  }

  public static class DefaultCatchClauseFactory implements CatchClauseFactory {

    public DefaultCatchClauseFactory(TokenReference ref, JExpression exprs) { 
      this.exprs = exprs;
      this.ref = ref;
    }

    /**
     * Creates catch clause for the specified type of exception
     * @param exception       type of exception it should handle
     * @param location        location of the exception
     * @param context         context for evalutation
     */
    public JCatchClause createCatchClause(CBodyContext context, CReferenceType exception, JPhylum location) {
      JCatchClause      catchIt;
      JStatement        abortIt;
      JStatement        ifStmt = null;
      JExpression       retryableAbort;
      JExpression       retryQuestion;
      boolean           interrupt = false;
      CReferenceType    interruptType;
      JFormalParameter  exceptionVar;
      String            interName;

      interName = XInterruptProtectedException.class.getName().replace('.','/');
      try {
        interruptType = (CReferenceType) context.getTypeFactory().createType(interName, false).checkType(context);
      } catch (UnpositionedError e) {
        throw new InconsistencyException("XProtectedExceptionHandler: "
                                         + XInterruptProtectedException.class.getName()
                                         + " should be available");
      }
      // is exception is assignable to exception XInterruptProtectedException, 
      // the transaction must be aborted
      if (exception.isAssignableTo(context, interruptType)) {
        interrupt = true;
      }
      abortIt = new JExpressionStatement(ref, 
                                         new JMethodCallExpression(ref, 
                                                                   exprs, 
                                                                   "abortProtected", 
                                                                   new JExpression[] {new JBooleanLiteral(ref, interrupt)}), 
                                         null);
      exceptionVar = new JFormalParameter(ref, 
                                          JLocalVariable.DES_PARAMETER | JLocalVariable.DES_GENERATED, 
                                          exception, 
                                          "exception", 
                                          false);
      retryableAbort = new JMethodCallExpression(ref, 
                                                 exprs, 
                                                 "retryableAbort", 
                                                 new JExpression[] {new JLocalVariableExpression(ref, exceptionVar)});
      retryQuestion = new JMethodCallExpression(ref, 
                                                exprs, 
                                                "retryProtected", 
                                                JExpression.EMPTY);
      ifStmt =  new JThrowStatement(ref,
                                    new JLocalVariableExpression(ref, exceptionVar),
                                    location,
                                    null);
      if (exception.isAssignableTo(context, context.getTypeFactory().createReferenceType(TypeFactory.RFT_EXCEPTION))) {
        ifStmt = new JIfStatement(ref, 
                                  new JConditionalOrExpression(ref,
                                                               new JLogicalComplementExpression(ref,
                                                                                                retryableAbort),
                                                               new JLogicalComplementExpression(ref,
                                                                                                retryQuestion)),
                                  ifStmt,
                                  null, 
                                  null);
      } 
      catchIt = new JCatchClause(ref, 
                                 exceptionVar, 
                                 new JBlock(ref, 
                                            new JStatement[] {abortIt, ifStmt}, 
                                            null));
      return catchIt;
    }

    JExpression         exprs;
    TokenReference      ref;
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JBlock		tryClause;
  private JCatchClause[]	catchClauses;
  private CatchClauseFactory    factory;
}
