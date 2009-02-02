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
public class JClassBlock extends JBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	isStatic	is this block a static initializer ?
   * @param	body		a list of statement
   */
  public JClassBlock(TokenReference where, boolean isStatic, JStatement[] body, JavaStyleComment[] comments) {
    super(where, body, comments);

    this.isStatic = isStatic;
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
    super.analyse(context);
    check(context, context.getClassContext().getCClass().canDeclareStatic() || !isStatic, KjcMessages.INNER_DECL_STATIC_MEMBER);
  }

   // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return	is this block a static initializer
   */
  public boolean isStaticInitializer() {
    return isStatic;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.hintOnCommingObject(KjcVisitor.OBJ_NEEDS_NEW_LINE);

    p.visitBlockStatement(this, body, getComments());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final boolean		isStatic;
}
