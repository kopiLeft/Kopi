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

package at.dms.bytecode.ssa;

/**
 * A way to acess to a QInst array for an instruction
 */
public class QInstArrayAccessor {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a new array accessor
   *
   * @param array the array
   * @param index the index in the array
   */
  public QInstArrayAccessor(QInstArray array, int index) {
    this.array = array;
    this.index = index;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the basic block associated with the array
   */
  public BasicBlock getBasicBlock() {
    return array.getBasicBlock();
  }

  /**
   * Change the current instruction by a new instruction
   *
   * @param newInstruction
   */
  public void replaceCurrentInstruction(QInst newInstruction) {
    array.replaceInstruction(index, newInstruction);
  }

  /**
   * Remove the current instruction in SSA form
   */
  public void removeSSAInstruction() {
    array.removeSSAInstruction(index);
  }

  /**
   * Add an instruction after the curent instruction
   *
   * @param instruction the instruction to add
   */
  public void insertAfter(QInst instruction) {
    array.insertAfter(index, instruction);
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected QInstArray array;
  protected int index;
}
