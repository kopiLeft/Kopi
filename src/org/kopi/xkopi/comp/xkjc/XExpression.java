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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.GenerationContext;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.KjcVisitor;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.InconsistencyException;

/**
 * Root class for all expressions
 */
public abstract class XExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public XExpression(TokenReference where) {
    super(where);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public CType getType(TypeFactory factory) {
    return null; // !!!
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param p the printwriter into the code is generated
   */
  public final void accept(KjcVisitor p) {
    genXKjcCode((XKjcPrettyPrinter)p);
  }

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public abstract void genXKjcCode(XKjcPrettyPrinter p);

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    throw new InconsistencyException("UNEXPECTED CODE GENERATION");
  }
}
