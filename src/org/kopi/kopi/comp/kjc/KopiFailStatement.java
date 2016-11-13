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

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.TokenReference;

/**
 * Extensions with Fail Statement: <code> @fail; </code> 
 * <code> @fail expr; </code>.  This statement throw a runtime 
 * exception with an optional message.
 */
public class KopiFailStatement extends JStatement{
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param     cond            the condition
   * @param	expr		the expression to throw.
   * @param     standard        true if it is a java assert (JSR 14)
   * @param	comment		the statement comment.
   */
  public KopiFailStatement(TokenReference where, 
                             JExpression expr,
                             JavaStyleComment[] comments) {
        super(where, comments);
        this.expr = expr;
  }

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CBodyContext	assertContext = new CSimpleBodyContext(context, 
                                                               context.getEnvironment(), 
                                                               context);
    TokenReference      tokenReference = getTokenReference();
    CReferenceType      throwIt;
    JExpression         classArg = null;

    throwIt = context.getTypeFactory().createType(KOPI_ERROR_ASSERT, false);

    if (expr == null) {
      // default: return the name of the file and the line number
      expr = new JStringLiteral(tokenReference, tokenReference.getName() + ": " + tokenReference.getLine());
      impl = parseFail(tokenReference, 
                         throwIt, 
                         classArg != null ? new JExpression [] { classArg, expr } :  new JExpression [] { expr });
    } else {
      //      expr = expr.analyse(new CExpressionContext(assertContext, context.getEnvironment()));
      impl = parseFail(tokenReference, 
                         throwIt, 
                         classArg != null ? new JExpression [] { classArg, expr } :  new JExpression[] {expr});
    }
    impl.analyse(assertContext);
    context.merge(assertContext);
    context.setReachable(false);
    
  } 

  JStatement parseFail(TokenReference ref, CReferenceType throwThis, JExpression[] arg) {
    return new JThrowStatement(ref,
                               new JUnqualifiedInstanceCreation(ref, 
                                                                throwThis, 
                                                                arg), 
                               null);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (impl == null) {
      p.visitFailStatement(this, expr);
    } else {
      impl.accept(p);
    }
  }
  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    impl.genCode(context);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  private JExpression   expr;
  private JStatement    impl;
}
