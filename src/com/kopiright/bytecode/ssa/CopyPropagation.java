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

import java.util.Iterator;

/**
 * To perform copy propagations
 * in a control flow graph in SSA form
 */
public class CopyPropagation {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param nodes the basic blocks with instruction in SSA form
   */
  public CopyPropagation(Node[] nodes) {
    this.nodes = nodes;
  }

  // -------------------------------------------------------------------
  // OPTIMIZATIONS
  // -------------------------------------------------------------------

  /**
   * Perform copy propagation on all copy instructions
   */
  public void propagate() {
    for (int i = 0; i < nodes.length; ++i) {
      BasicBlock bb = (BasicBlock) nodes[i];

      Iterator insts = bb.getAllSSAInstructions();
      while (insts.hasNext()) {
        QInst inst = (QInst) insts.next();
        if (isACopy(inst)) {
          SSAVar newVar = getSource(inst);
          QInst newVarDefinition = newVar.getDefinition().getInstruction();
          while (isACopy(newVarDefinition)) {
            newVar = getSource(newVarDefinition);
            newVarDefinition = newVar.getDefinition().getInstruction();
          }
          SSAVar oldVar = getTarget(inst);
          replaceUses(oldVar, newVar);
          if (ControlFlowGraph.DEBUG) {
            System.out.println("Copy Propagation : " + inst);
          }
        }
      }
    }
  }

  // -------------------------------------------------------------------
  // PROTECTED METHODS
  // -------------------------------------------------------------------

  /**
   * Test if an instruction is a copy
   *
   * @param inst the instruction to test
   * @return true iff the instruction is a copy
   */
  protected boolean isACopy(QInst inst) {
    if (inst instanceof QPhi) {
      //it is a copy if all phi function operands are the same.
      boolean sameOperands = false;
      QOperandBox[] uses = inst.getUses();
      if (uses.length >= 1
          && uses[0].getOperand() instanceof QSSAVar)
        {
          sameOperands = true;
          SSAVar operand1 =((QSSAVar)uses[0].getOperand()).getSSAVar();
          for (int op = 1; op < uses.length; ++op) {
            if (! (uses[op].getOperand() instanceof QSSAVar)
                || ((QSSAVar)uses[op].getOperand()).getSSAVar() != operand1)
              {
                sameOperands = false;
                break;
              }
          }
        }
      return sameOperands;
    } else {
      if (inst instanceof QAssignment) {
        QOperand target = inst.getDefined().getOperand();
        QExpression expr = ((QAssignment) inst).getExpression();
        if (expr instanceof QSimpleExpression) {
          QOperand source = expr.getUses()[0].getOperand();
          if (target instanceof QSSAVar &&
              source instanceof QSSAVar) {
            return true;
          }
        }
      }
      return false;
    }
  }

  /**
   * Get the source variable of a copy
   * Precondition: isACopy(copy) == true.
   *
   * @param copy the copy instruction
   * @return the source variable
   */
  protected SSAVar getSource(QInst copy) {
    return ((QSSAVar)copy.getUses()[0].getOperand()).getSSAVar();
  }

  /**
   * Get the target variable of a copy
   * Precondition: isACopy(copy) == true.
   *
   * @param copy the copy instruction
   * @return the target variable
   */
  protected SSAVar getTarget(QInst copy) {
    return ((QSSAVar)copy.getDefined().getOperand()).getSSAVar();
  }

  /**
   *
   */
  protected void replaceUses(SSAVar from, SSAVar to) {
    Iterator uses = from.getUses();

    while (uses.hasNext()) {
      QOperandBox use = (QOperandBox) uses.next();
      QSSAVar.newSSAVarUse(use, to, to.getType());
    }
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected Node[] nodes;
}
