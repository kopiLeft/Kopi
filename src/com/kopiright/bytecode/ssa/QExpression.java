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

package com.kopiright.bytecode.ssa;

/**
 * An abstract class to represent expression that can be the source
 * of one affectation in 3-adress code.
 */
public abstract class QExpression extends QOperand {

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the operand may throw an exception
   */
  public abstract boolean mayThrowException();

  /**
   * Test if the operand has side effects.
   */
  public boolean hasSideEffects() {
    return false;
  }

  /**
   * Return the type of the expression
   */
  public abstract byte getType();

  /**
   * Test if the expression is simple (a variable or a constant).
   */
  public boolean isSimple() {
    return false;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public abstract QOperandBox[] getUses();

  /**
   * Set the instruction in which the expression is used
   */
  /*package*/ void setInstruction(QInst inst) {
    this.inst = inst;
  }

  /**
   * Get the instruction in which the expression is used
   */
  public QInst getInstruction() {
    return inst;
  }

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this operand
   *
   * @param codeGen the code generator
   */
  public abstract void generateInstructions(CodeGenerator codeGen);

  /**
   * Generate the classfile instructions for save in
   * this operand the value actually son the stack.
   */
  public void generateStore(CodeGenerator codeGen) {
    /* Not destination expressions */
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QInst inst;
}
