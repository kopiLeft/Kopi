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

package com.kopiright.bytecode.ssa;

import com.kopiright.util.base.InconsistencyException;

/**
 * A simple expression : a variable or a constant.
 */
public class QSimpleExpression extends QExpression {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a simple expression.
   *
   * Precondition : expr is not instance of QExpression.
   *
   * @param expr the expression
   */
  public QSimpleExpression(QOperand expr) {
    if (expr instanceof QExpression) {
      throw new InconsistencyException("Not a simple expression " + expr);
    }
    this.expr = new QOperandBox(expr, this);

  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the operand may throw an exception
   */
  public boolean mayThrowException() {
    return false;
  }

  /**
   * Return the type of the operand.
   */
  public byte getType() {
    return expr.getOperand().getType();
  }

  /**
   * Test if the expression is simple (a variable or a constant).
   */
  public boolean isSimple() {
    return true;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {expr };
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return expr.toString();
  }

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this operand
   *
   * @param codeGen the code generator
   */
  public void generateInstructions(CodeGenerator codeGen) {
    expr.getOperand().generateInstructions(codeGen);
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox expr;
}
