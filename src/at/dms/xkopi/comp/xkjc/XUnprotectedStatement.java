/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.JavaStyleComment;

/**
 * This class represents an unprotected transaction
 */
public class XUnprotectedStatement extends XStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new #unprotected statement.
   * @param	where		the line of this node in the source code
   * @param	message		the message to display during execution
   * @param	dbContext	the database context
   * @param	body		the statement block to execute
   * @param	comments	comments in the source code
   */
  public XUnprotectedStatement(TokenReference where,
			       JExpression message,
			       JExpression dbContext,
			       JStatement[] body,
			       JavaStyleComment[] comments)
  {
    super(where, comments);
    this.message = message;
    this.dbContext = dbContext;
    this.body = new JBlock(where, body, null);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   *
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public JStatement checkAndConvert(CBodyContext context) throws PositionedError {
    TokenReference      ref = getTokenReference();
    TypeFactory         factory = context.getTypeFactory();

    if (dbContext != null) {
      dbContext = dbContext.analyse(new CExpressionContext(context, context.getEnvironment()));
      check(context,
            dbContext.isAssignableTo(context,
                         factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER)),
	    XKjcMessages.PROTECTED_BAD_CONTEXT,
            dbContext.getType(factory));
    } else {
      check(context,
            context.getClassContext().getCClass().descendsFrom(factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER).getCClass()),
	    XKjcMessages.PROTECTED_NO_CONTEXT,
            context.getClassContext().getCClass().getAbstractType());
      dbContext = new JThisExpression(ref).analyse(new CExpressionContext(context,
                                                                          context.getEnvironment()));
    }

    if (message == null) {
      message = new JStringLiteral(ref, "");
    }

    JStatement stmt =  new JForStatement(ref,
					 null,
					 null,
					 null,
					 parseUnprotected(context,
                                                          ref, 
                                                          body, 
                                                          new JCheckedExpression(ref, dbContext), 
                                                          message),
                                         null);
    stmt.analyse(context);
    context.close(getTokenReference());

    return stmt;
  }

  private static JStatement parseUnprotected(CTypeContext context, TokenReference ref, JStatement body, JExpression exprs, JExpression message) {
    JStatement                  startProtected;
    JMethodCallExpression       defaultConnection1;
    JMethodCallExpression       defaultConnection2;
    JStatement                  transaction;
    JStatement                  transactionIsolationUncommit;
    JStatement                  transactionIsolationSerial;
    JIntLiteral                 transReadUncommitted;
    JIntLiteral                 transSerial;
    JStatement                  commitProtected;
    //    JStatement                  abortProtected;
    JStatement                  systemExit;
    //    JCatchClause                catchWithAbort;
    JFormalParameter            exceptionVar;
    JCatchClause                catchWithPrint;
    CType                       exceptionType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_EXCEPTION);
    CType                       systemType = context.getTypeFactory().createType("java/lang/System", false);
    CType                       sqlType = context.getTypeFactory().createType("java/sql/SQLException", false);

    startProtected =  new JExpressionStatement(ref, 
                                               new JMethodCallExpression(ref, 
                                                                         exprs, 
                                                                         "startProtected", 
                                                                         new JExpression[] {message}), 
                                               null);

    defaultConnection1 = new JMethodCallExpression(ref, 
                                                  new JMethodCallExpression(ref, 
                                                                            exprs, 
                                                                            "getDBContext", 
                                                                            JExpression.EMPTY), 
                                                  "getDefaultConnection", 
                                                  JExpression.EMPTY);
    defaultConnection2 = new JMethodCallExpression(ref, 
                                                  new JMethodCallExpression(ref, 
                                                                            exprs, 
                                                                            "getDBContext", 
                                                                            JExpression.EMPTY), 
                                                  "getDefaultConnection", 
                                                  JExpression.EMPTY);

    transReadUncommitted = new JIntLiteral(ref, java.sql.Connection.TRANSACTION_READ_UNCOMMITTED);

    transactionIsolationUncommit = new JExpressionStatement(ref, 
                                                    new JMethodCallExpression(ref, 
                                                                              defaultConnection1, 
                                                                              "setTransactionIsolation", 
                                                                              new JExpression[] {transReadUncommitted}), 
                                                    null);
    commitProtected = new JExpressionStatement(ref, 
                                               new JMethodCallExpression(ref, 
                                                                         exprs, 
                                                                         "commitProtected", 
                                                                         JExpression.EMPTY), 
                                               null);
    transSerial = new JIntLiteral(ref, java.sql.Connection.TRANSACTION_SERIALIZABLE);
    transactionIsolationSerial = new JExpressionStatement(ref, 
                                                          new JMethodCallExpression(ref, 
                                                                                    defaultConnection2, 
                                                                                    "setTransactionIsolation", 
                                                                                    new JExpression[] {transSerial}), 
                                                          null);
    systemExit = new JExpressionStatement(ref,
                                          new JMethodCallExpression(ref, 
                                                                    new JTypeNameExpression(ref, (CReferenceType) systemType), 
                                                                    "exit", 
                                                                    new JExpression[] {new JIntLiteral(ref, 1)}), 
                                          null);

    transaction = new XProtectedExceptionHandler(ref,
                                                 new JBlock(ref,
                                                            new JStatement[] {
                                                              startProtected,
                                                              transactionIsolationUncommit,
                                                              body,
                                                              commitProtected,
                                                              new JBreakStatement(ref, null, null)},
                                                            null), 
                                                 new XProtectedExceptionHandler.DefaultCatchClauseFactory(ref, exprs), 
                                                 null);

    exceptionVar = new JFormalParameter(ref, 
                                        JLocalVariable.DES_PARAMETER, 
                                        sqlType, 
                                        "bigProblem", 
                                        false);

    catchWithPrint = new JCatchClause(ref, 
                                      exceptionVar, 
                                      new JBlock(ref, 
                                                 new JStatement[] {
                                                   new JExpressionStatement(ref, 
                                                                            new JMethodCallExpression(ref, 
                                                                                                      new JLocalVariableExpression(ref, exceptionVar), 
                                                                                                      "printStackTrace", 
                                                                                                      JExpression.EMPTY), 
                                                                            null),
                                                   systemExit}, 
                                                 null));
    return  
      new JTryFinallyStatement(ref ,
                               new JBlock(ref, 
                                          new JStatement[] { transaction }, 
                                          null), 
                               new JBlock(ref, new JStatement[] {
                                 new JTryCatchStatement(ref ,
                                                        new JBlock(ref, 
                                                                   new JStatement[] {transactionIsolationSerial}, 
                                                                   null), 
                                                        new JCatchClause[]{catchWithPrint}, 
                                                        null)}, 
                                          null), 
                               null);
}

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * Get the connection from a context handler
   */
  public static JExpression buildConnection(TokenReference ref, JExpression handler) {
    return new JMethodCallExpression(ref,
				     new JMethodCallExpression(ref,
							       handler,
							       "getDBContext",
							       JExpression.EMPTY),
				     "getDefaultConnection",
				     JExpression.EMPTY);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genXKjcCode(XKjcPrettyPrinter p) {
    p.visitUnprotectedStatement(this, message, dbContext, body);
  }

  /**
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public final void visitOther(KjcVisitor p) {
    body.accept(p);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression	message;
  private JExpression	dbContext;
  private JBlock	body;
}
