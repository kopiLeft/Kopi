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

import com.kopiright.bytecode.classfile.IincInstruction;

/**
 * A abstract class to represent instruction :
 *  defVar = expression
 * with expression of type QExpression.
 */
public class QAssignment extends QInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construction the instruction
   *
   * @param dest destination of the affectation
   * @param source source of the affectation
   */
  public QAssignment(QOperand dest, QExpression source) {
    this.defVar = new QOperandBox(dest, this, true);
    this.expression = source;
    source.setInstruction(this);
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the current instruction may throw an exception.
   */
  public boolean mayThrowException() {
    return expression.mayThrowException();
  }

  /**
   * Test if the instruction has side effects.
   */
  public boolean hasSideEffects() {
    return expression.hasSideEffects();
  }

  /**
   * Test if the instruction define a local variable.
   */
  public boolean defVar() {
    return true;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return expression.getUses();
  }

  /**
   * Get the operand defined
   */
  public QOperandBox getDefined() {
    return defVar;
  }

  /**
   * Get the expression
   */
  public QExpression getExpression() {
    return expression;
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return defVar + " = " + expression;
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
    //test if it could be a iinc
    if (expression instanceof QBinaryOperation) {
      QBinaryOperation expBin = (QBinaryOperation) expression;
      if (defVar.getOperand() instanceof QVar && expBin.isIadd()) {
        QOperandBox[] ops = expBin.getUses();
        QVar var = (QVar) defVar.getOperand();
        QConstant constant = null;
        if (var.equals(ops[0].getOperand()) &&
            ops[1].getOperand() instanceof QConstant) {
          constant = (QConstant) ops[1].getOperand();
        } else if (var.equals(ops[1].getOperand()) &&
                   ops[0].getOperand() instanceof QConstant) {
          constant = (QConstant) ops[0].getOperand();
        }
        if (constant != null) {
          Object value = constant.getValue();
          if (value  instanceof Integer) {
            int increment = ((Integer) value).intValue();
            if (increment >= -128 && increment < 128) {
              codeGen.addInstruction(new IincInstruction(var.getRegister(), increment));
              return;
            }
          }
        }
      }
    }
    //if not a iinc
    expression.generateInstructions(codeGen);
    defVar.getOperand().generateStore(codeGen);
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QOperandBox defVar;
  protected QExpression expression;
}
