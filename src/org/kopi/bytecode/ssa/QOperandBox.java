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

/**
 * A class containing a QOperand
 *
 * The modification of an operand of an 3-adress instruction is then
 * simplified.
 *
 * The Operand box is associated with one instruction.
 */
public class QOperandBox {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a new operand box used in an expression
   *
   * @param op the operand
   * @param exp the expression
   */
  /*package*/ QOperandBox(QOperand op, QExpression exp) {
    this.isDefinition = false;
    this.inst = null;
    this.op = op;
    this.exp = exp;
  }

  /**
   * Construct a new operand box used in an instruction
   *
   * @param op the operand
   * @param inst the instruction
   */
  public QOperandBox(QOperand op, QInst inst) {
    this(op, inst, false);
  }

  /**
   * Construct a new operand box used in an instruction
   *
   * @param op the operand
   * @param inst the instruction
   * @param isDefinition true iff the operand is modified by the instruction
   */
  /*package*/ QOperandBox(QOperand op, QInst inst, boolean isDefinition) {
    this.op = op;
    this.isDefinition = isDefinition;
    this.inst = inst;
    this.exp = null;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the instruction of the operand box
   */
  public QInst getInstruction() {
    if (inst != null) {
      return inst;
    }
    return exp.getInstruction();
  }

  /**
   * Change the operand
   *
   * @param op the new operand
   */
  public void setOperand(QOperand op) {
    this.op = op;
  }

  /**
   * Get the operand
   *
   * @return the operand
   */
  public QOperand getOperand() {
    return op;
  }

  /**
   * Return a string representation of the operand
   */
  public String toString() {
    if (op == null) {
      return "";
    } else {
      return op.toString();
    }
  }

  /**
   * Test if the operand is modified by the instruction
   */
  public boolean isDefinition() {
    return isDefinition;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperand op;
  protected boolean isDefinition;
  protected QInst inst;
  protected QExpression exp;
}
