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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A PhiCatch is a special phi function used in catch block.
 *
 * The operands of the phi function are all variables defined
 * in the protected blocks.
 */
public class QPhiCatch extends QPhi {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param var variable used in phi in non SSA form
   */
  public QPhiCatch(QVar var) {
    super(var);
    operands = new ArrayList();
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Add a new null operand at the phi catch
   * The operand must be then initialized.
   */
  public QOperandBox addNewOperand() {
    QOperandBox newOp = new QOperandBox(null, this);
    operands.add(newOp);
    return newOp;
  }

  /**
   * Test if one of the operand is a use of a given ssa variable
   */
  public boolean hasSSAVarAsOperand(SSAVar v) {
    Iterator ops = operands.iterator();
    while (ops.hasNext()) {
      QOperandBox op = (QOperandBox) ops.next();
      if (op.getOperand() instanceof QSSAVar &&
          ((QSSAVar)op.getOperand()).getSSAVar() == v) {
        return true;
      }
    }
    return false;
  }


  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    QOperandBox[] ops = new QOperandBox[operands.size()];
    operands.toArray(ops);
    return ops;
  }

  /**
   * Remove a given operand from the list of operands
   */
  public void removeOperand(QOperandBox op) {
    operands.remove(op);
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    String tmp = variableDefined + " = phiCatch(";
    Iterator it = operands.iterator();
    while (it.hasNext()) {
      QOperandBox op = (QOperandBox) it.next();
      tmp += op;
      if (it.hasNext()) {
        tmp += ", ";
      }
    }
    tmp += ")";
    return tmp;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected ArrayList operands; //all operands in SSA form
}
