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

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;

/**
 * This statement is a root class for extension statements
 */
public abstract class XStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public XStatement(TokenReference where, JavaStyleComment[] comments) {
    super(where, comments);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public final void analyse(CBodyContext context) throws PositionedError {
    impl = checkAndConvert(context);
  }

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   *
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public abstract JStatement checkAndConvert(CBodyContext context) throws PositionedError;

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public final void accept(KjcVisitor p) {
    if (impl != null) {
      impl.accept(p);
    } else if (p instanceof XKjcPrettyPrinter) {
      if (getComments() != null) {
	p.visitComments(getComments());
      }
      genXKjcCode((XKjcPrettyPrinter)p);
    } else {
      visitOther(p);
    }
  }

  /**
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public abstract void visitOther(KjcVisitor p);

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public abstract void genXKjcCode(XKjcPrettyPrinter p);

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public final void genCode(GenerationContext context) {
    impl.genCode(context);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JStatement		impl;
}
