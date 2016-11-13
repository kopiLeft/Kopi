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

package org.kopi.bytecode.ssa;

import org.kopi.util.base.InconsistencyException;

/**
 * An abstract phi function
 *
 * Used in SSA Form at the merge point of control flow graph.
 */
public abstract class QPhi extends QInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param varDef variable defined
   */
  public QPhi(QOperand varDef) {
    variableDefined = new QOperandBox(varDef, this, true);
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the current instruction may throw an exception.
   */
  public boolean mayThrowException() {
    return false;
  }

  /**
   * Test if the instruction define a local variable.
   */
  public boolean defVar() {
    return true;
  }

  /**
   * Get the operand defined if the instruction define one
   */
  public QOperandBox getDefined() {
    return variableDefined;
  }

  /**
   * Get the target of the phi
   */
  public QOperand getTarget() {
    return variableDefined.getOperand();
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public abstract QOperandBox[] getUses();

  /**
   * A representation of the instruction
   */
  public abstract String toString();

  /**
   * Remove current instruction from the basic block
   */
  public void remove() {
    arrayAccess.replaceCurrentInstruction(new QPhiNop());
  }


  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this quadruple instruction
   *
   * @param codeGen the code generator
   */
  public void generateInstructions(CodeGenerator codeGen) {
    throw new InconsistencyException("Can't generate a phi function");
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox variableDefined;
}
