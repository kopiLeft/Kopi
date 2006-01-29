/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import com.kopiright.bytecode.classfile.PushLiteralInstruction;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class implements an array of expressions and array
 * initializers used to initialize arrays.
 */
public class JArrayInitializer extends JExpression {

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	elems		the elements of the initializer
   */
  public JArrayInitializer(TokenReference where, JExpression[] elems) {
    super(where);

    this.elems = elems;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Sets the type of this expression. Assume type is already checked.
   * @param	type		the type of this array
   */
  public void setType(CArrayType type) {
    verify(type.checked());
    this.type = type;
  }

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory factory = context.getTypeFactory();

    verify(type != null);

    CType	elementType = type.getElementType();

    if (elementType.isArrayType()) {
      for (int i = 0; i < elems.length; i++) {
	if (elems[i] instanceof JArrayInitializer) {
	  ((JArrayInitializer)elems[i]).setType((CArrayType)elementType);
	}
	elems[i] = elems[i].analyse(context);
	check(context, elems[i].isAssignableTo(context, elementType),
	      KjcMessages.ARRAY_INIT_BADTYPE, elementType, elems[i].getType(factory));
      }
    } else {
      for (int i = 0; i < elems.length; i++) {
	check(context,
	      !(elems[i] instanceof JArrayInitializer),
	      KjcMessages.ARRAY_INIT_NOARRAY, elementType);
	elems[i] = elems[i].analyse(context);
	check(context, elems[i].isAssignableTo(context, elementType),
	      KjcMessages.ARRAY_INIT_BADTYPE, elementType, elems[i].getType(factory));
	elems[i] = elems[i].convertType(context, elementType);
      }
    }

    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitArrayInitializer(this, elems);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    // create array instance
    code.plantInstruction(new PushLiteralInstruction(elems.length));
    code.plantNewArrayInstruction(type.getElementType());

    // initialize array
    int		opcode = type.getElementType().getArrayStoreOpcode();

    for (int i = 0; i < elems.length; i++) {
      code.plantNoArgInstruction(opc_dup);
      code.plantInstruction(new PushLiteralInstruction(i));
      elems[i].genCode(context, false);
      code.plantNoArgInstruction(opcode);
    }

    if (discardValue) {
      code.plantPopInstruction(getType(factory));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CArrayType		type;
  private JExpression[]		elems;
}
