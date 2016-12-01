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
import org.kopi.util.base.InconsistencyException;
import org.kopi.util.base.MessageDescription;

/**
 * JLS 14.5: Statement
 * This class is the root class for all statement classes.
 */
public abstract class JStatement extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JStatement(TokenReference where, JavaStyleComment[] comments) {
    super(where);
    this.comments = comments;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public abstract void analyse(CBodyContext context) throws PositionedError;

  /**
   * Adds a compiler error.
   * @param	context		the context in which the error occurred
   * @param	key		the message ident to be displayed
   * @param	params		the array of parameters
   *
   */
  protected void fail(CContext context, MessageDescription key, Object[] params)
    throws PositionedError
  {
    throw new CLineError(getTokenReference(), key, params);
  }

  // ----------------------------------------------------------------------
  // BREAK/CONTINUE HANDLING
  // ----------------------------------------------------------------------

  /**
   * Returns a label at end of this statement (for break statement)
   */
  public CodeLabel getBreakLabel() {
    throw new InconsistencyException("NO END LABEL");
  }

  /**
   * Returns the beginning of this block (for continue statement)
   */
  public CodeLabel getContinueLabel() {
    throw new InconsistencyException("NO CONTINUE LABEL");
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public abstract void genCode(GenerationContext context);

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (comments != null) {
      p.visitComments(comments);
    }
  }

  /**
   * Returns the comments
   */
  public JavaStyleComment[] getComments() {
    return comments;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  private JavaStyleComment[]	comments;
  public static JStatement[]    EMPTY = new JStatement[0];
}
