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

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * JLS 14.2: Block
 *
 * TA block is a sequence of statements and local variable declaration
 * statements within braces.
 */
public class JBlock extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param     where           the line of this node in the source code
   * @param     body            the statements contained in the block
   * @param     comment         other comments in the source code
   */
  public JBlock(TokenReference where,
                JStatement[] body,
                JavaStyleComment[] comments)
  {
    super(where, comments);

    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Tests whether the block is empty.
   *
   * @return    true iff the block is empty.
   */
  public boolean isEmpty() {
    return body.length == 0;
  }

  public JStatement[] getBody() {
    return body;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param     context         the analysis context
   * @exception PositionedError the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
     
    // if change: don't forget KopiImplicitReturnBlock
    CBlockContext       self = new CBlockContext(context, context.getEnvironment());

    for (int i = 0; i < body.length; i++) {
      if (!self.isReachable() && 
          !context.getEnvironment().ignoreUnreachableStatement()) {
        throw new CLineError(body[i].getTokenReference(), KjcMessages.STATEMENT_UNREACHABLE);
      }
      try {
        if (body[i] instanceof JClassFieldDeclarator) {
          // here, if elements of the body are field declarations,
          // the context is already set in an earlier step,
          // otherwise we set it.
          if (!((JClassFieldDeclarator) body[i]).hasBodyContext()) {
            ((JClassFieldDeclarator) body[i]).analyse(self);
          }
          ((JClassFieldDeclarator) body[i]).analyse();
        } else {
          body[i].analyse(self);
        }
      } catch (CLineError e) {
         self.reportTrouble(e);
      }
    }

    self.close(getTokenReference());
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param     p               the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitBlockStatement(this, body, getComments());
  }

  /**
   * Generates a sequence of bytescodes
   * @param     code            the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    for (int i = 0; i < body.length; i++) {
      body[i].genCode(context);
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  protected final JStatement[]  body;
}
