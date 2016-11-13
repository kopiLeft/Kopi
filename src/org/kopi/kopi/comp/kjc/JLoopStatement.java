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

import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.JavaStyleComment;

/**
 * Loop Statement
 *
 * Root class for loop statement
 */
public abstract class JLoopStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate.
   * @param	body		the loop body.
   */
  public JLoopStatement(TokenReference where, JavaStyleComment[] comments) {
    super(where, comments);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return the end of this block (for break statement)
   */
  public CodeLabel getBreakLabel() {
    return endLabel;
  }

  /**
   * Return the beginning of this block (for continue statement)
   */
  public CodeLabel getContinueLabel() {
    return contLabel;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CodeLabel		contLabel = new CodeLabel();
  private CodeLabel		endLabel = new CodeLabel();
}
