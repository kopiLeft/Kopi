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

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * Root class for all literals expression
 */
public abstract class JLiteral extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JLiteral(TokenReference where) {
    super(where);
  }

  /**
   * Constructs a real (float or double) literal
   * @param	where		the line of this node in the source code
   * @param	image		the textual representation of this literal
   */
  public static JLiteral parseReal(TokenReference where, String image)
    throws PositionedError
  {
    switch (image.charAt(image.length() - 1)) {
    case 'f':
    case 'F':
      return new JFloatLiteral(where, image.substring(0, image.length() - 1));

    case 'd':
    case 'D':
      return new JDoubleLiteral(where, image.substring(0, image.length() - 1));

    default:
      return new JDoubleLiteral(where, image);
    }
  }

  /**
   * Constructs an integer (int or long) literal
   * @param	where		the line of this node in the source code
   * @param	image		the textual representation of this literal
   */
  public static JLiteral parseInteger(TokenReference where, String image)
    throws PositionedError
  {
    switch (image.charAt(image.length() - 1)) {
    case 'l':
    case 'L':
      return new JLongLiteral(where, image.substring(0, image.length() - 1));

    default:
      return new JIntLiteral(where, image);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Tests whether this expression denotes a compile-time constant (JLS 15.28).
   *
   * @return	true iff this expression is constant
   */
  public boolean isConstant() {
    return true;
  }

  /**
   * Returns the literal value of an expression
   */
  public JLiteral getLiteral() {
    return this;
  }

  /**
   * Returns true iff the value of this literal is the
   * default value for this type (JLS 4.5.5).
   */
  public abstract boolean isDefault();

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
    return this;
  }
}
