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

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.GenerationContext;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JLiteral;
import org.kopi.kopi.comp.kjc.JStringLiteral;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.kopi.comp.kjc.KjcVisitor;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.InconsistencyException;
import org.kopi.xkopi.lib.type.NotNullFixed;

/**
 * Root class for all expressions
 */
public class XFixedLiteral extends JLiteral {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	image		the string representation of this literal
   */
  public XFixedLiteral(TokenReference where, String image) {
    super(where);
    this.image = image;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return factory.createReferenceType(XTypeFactory.PRM_PFIXED);
  }

  /**
   * Returns true iff the value of this literal is the
   * default value for this type (JLS 4.5.5).
   */
  public boolean isDefault() {
    return false;
  }

  /**
   * Tests whether this expression denotes a compile-time constant (JLS 15.28).
   * The value is known but we don't support constant folding.
   *
   * @return	true iff this expression is constant
   */
  public boolean isConstant() {
    return false;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory factory = context.getTypeFactory();

    try {
      new NotNullFixed(image);
    } catch (NumberFormatException e) {
      check(context, false, XKjcMessages.BAD_FIXED_FORMAT, image);
    }

    TokenReference	ref = getTokenReference();
    JExpression		expr = new JUnqualifiedInstanceCreation(ref,
							factory.createReferenceType(XTypeFactory.PRM_PFIXED),
							new JExpression[] {new JStringLiteral(ref, image)});

    return expr.analyse(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param p the printwriter into the code is generated
   */
  public void accept(KjcVisitor p) {
    ((XKjcPrettyPrinter)p).visitLiteral(image);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    throw new InconsistencyException("SHOULD NEVER BE CALLED");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String image;
}
