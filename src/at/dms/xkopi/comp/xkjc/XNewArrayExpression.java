/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: XNewArrayExpression.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import at.dms.bytecode.classfile.MultiarrayInstruction;
import at.dms.bytecode.classfile.ClassRefInstruction;
import at.dms.bytecode.classfile.NewarrayInstruction;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.kopi.comp.kjc.*;

/**
 * JLS 15.10 Array Creation Expressions.
 *
 * An array instance creation expression is used to create new arrays.
 */
public class XNewArrayExpression extends JNewArrayExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	type		the type of elements of this array
   * @param	dims		the dimensions of the array
   * @param	init		an initializer for the array
   */
  public XNewArrayExpression(TokenReference where,
			     CType type,
			     JExpression[] dims,
			     JArrayInitializer init)
  {
    super(where, type, dims, init);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------


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
    if (init == null) {
      boolean	lastEmpty = false;	// last dimension empty ?
      boolean	hasBounds = false;	// expressions for some sizes ?

      for (int i = 0; i < dims.length; i++) {
        if (dims[i] == null) {
          lastEmpty = true;
        } else {
          check(context, !lastEmpty, KjcMessages.MULTIARRAY_BOUND_MISSING);
          hasBounds = true;
        }
      }
      check(context, hasBounds, KjcMessages.MULTIARRAY_BOUND_MISSING);

      try {
        type = (CArrayType)type.checkType(context);
      } catch (UnpositionedError e) {
        throw e.addPosition(getTokenReference());
      }

      XOverloadedMethodCallExpression overloadedm = XUtils.fetchOverloadedNewArray(context.getTypeFactory(), dims, type.getBaseType());

      overloaded = XUtils.fetchOverloadedOperator(context, overloadedm);

      if (overloaded != null) {
        overloaded = new JCastExpression(TokenReference.NO_REF, new JCheckedExpression(TokenReference.NO_REF, overloaded), type);
        return overloaded.analyse(context);
      } 
    }

    super.analyse(context);

    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    super.genCode(context, false);
  }

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    super.accept(p);
    // !!! FIX 03.09.2002    overInit.accept(p);;
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression           overloaded = null;

}
