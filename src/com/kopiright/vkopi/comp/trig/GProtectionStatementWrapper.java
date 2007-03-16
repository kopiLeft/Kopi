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

package com.kopiright.vkopi.comp.trig;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JCatchClause;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JFormalParameter;
import com.kopiright.kopi.comp.kjc.JLocalVariable;
import com.kopiright.kopi.comp.kjc.JLocalVariableExpression;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JStringLiteral;
import com.kopiright.kopi.comp.kjc.JThrowStatement;
import com.kopiright.kopi.comp.kjc.JTryCatchStatement;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.xkopi.comp.xkjc.XNameExpression;

/**
 * wrapps a protected statement as following
 *
 * 
 * try {
 *   #protected {
 *      ...
 *   }
 * } catch (DBDeadLookException e) {
 *   throws new VExecFailedException("Transaktion abgebrochen"); 
 * } catch (DBDeadLookException e) {
 *   throws new VExecFailedException("Transaktion abgebrochen");
 * } catch (SQLException e) {
 *   throws new IncosistencyException(e);
 * }
 */
public class GProtectionStatementWrapper extends JTryCatchStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	protectedStatement	protected or unprotected statement
   */
  public GProtectionStatementWrapper(TokenReference where,
                                     JStatement protectedStatement,
                                     CReferenceType exceptionVException,
                                     CReferenceType exceptionInconsistency,
                                     CReferenceType exceptionSQLType,
                                     CReferenceType exceptionDBDeadLock,
                                     CReferenceType exceptionDBInterrupted)
  {
    super(where, 
          wrapProtectedStatement(where, protectedStatement), 
          buildAllCatchClause(where,
                              exceptionVException,
                              exceptionInconsistency,
                              exceptionSQLType, 
                              exceptionDBDeadLock, 
                              exceptionDBInterrupted),
          null);
  }

  private static JBlock wrapProtectedStatement(TokenReference sourceRef, JStatement protectedStatement) {
    return new JBlock(sourceRef, 
                      new JStatement[] { protectedStatement }, 
                      null);
  }

  private static JCatchClause[] buildAllCatchClause(TokenReference sourceRef,
                                                    CReferenceType exceptionVException,
                                                    CReferenceType exceptionInconsistency,
                                                    CReferenceType exceptionSQLType,
                                                    CReferenceType exceptionDBDeadLock,
                                                    CReferenceType exceptionDBInterrupted) {
    JCatchClause          catchItDB;
    JCatchClause          catchItInter;
    JCatchClause          catchIt;
    
    catchItDB = buildVExceptionCatchClause(sourceRef, exceptionDBDeadLock, exceptionVException);
    catchItInter = buildVExceptionCatchClause(sourceRef, exceptionDBInterrupted, exceptionVException);
    catchIt = buildInconsistencyCatchClause(sourceRef, exceptionSQLType, exceptionInconsistency);
    return new JCatchClause[]{ catchItDB, catchItInter, catchIt };
  }

  private static JCatchClause buildInconsistencyCatchClause(TokenReference sourceRef, 
                                                            CReferenceType exceptionCatched,
                                                            CReferenceType exceptionInconsistency) {
    JFormalParameter    exceptionVar;
    JExpression         throwThis;
    
    exceptionVar = new JFormalParameter(sourceRef, 
                                        JLocalVariable.DES_PARAMETER | JLocalVariable.DES_GENERATED, 
                                        exceptionCatched, 
                                        "exception", 
                                        false);
    throwThis = new JUnqualifiedInstanceCreation(sourceRef, 
                                                 exceptionInconsistency,
                                                 new JExpression[]{
                                                   new JLocalVariableExpression(sourceRef, 
                                                                                exceptionVar)});
    return new JCatchClause(sourceRef, 
                            exceptionVar, 
                            new JBlock(sourceRef, 
                                       new JStatement[] {
                                         new JThrowStatement(sourceRef, throwThis, null)
                                           },
                                       null)); 
  }

  private static JCatchClause buildVExceptionCatchClause(TokenReference sourceRef, 
                                                         CReferenceType exceptionCatched,
                                                         CReferenceType exceptionThrown) {
    JFormalParameter    exceptionVar;
    JExpression         throwThis;

    exceptionVar = new JFormalParameter(sourceRef, 
                                        JLocalVariable.DES_PARAMETER | JLocalVariable.DES_GENERATED, 
                                        exceptionCatched, 
                                        "exception", 
                                        false);
    throwThis = buildMessageException(sourceRef, exceptionThrown);
    return new JCatchClause(sourceRef, 
                            exceptionVar, 
                            new JBlock(sourceRef, 
                                       new JStatement[] {
                                         new JThrowStatement(sourceRef, throwThis, null)
                                           },
                                       null)); 
  }

  // CREATES: new VExecFailedException(MessageCode.getMessage("VIS-00058"))
  private static JExpression buildMessageException(TokenReference sourceRef,
                                                   CReferenceType exceptionThrown) {
    JExpression         messageExpression;
    JExpression         whichMessage;
    
    whichMessage = new JStringLiteral(sourceRef, "abort_transaction");
    messageExpression = new JMethodCallExpression(sourceRef,
                                                  XNameExpression.build(sourceRef, 
                                                                        Message.class.getName().replace('.','/')),
                                                  "getMessage",
                                                  new JExpression[]{ whichMessage });
    return new JUnqualifiedInstanceCreation(sourceRef, 
                                            exceptionThrown,
                                            new JExpression[]{ messageExpression });
  }
                                                  
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

}
