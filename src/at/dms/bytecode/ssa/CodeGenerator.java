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

import at.dms.bytecode.classfile.Instruction;
import at.dms.bytecode.classfile.JumpInstruction;

import java.util.Vector;

/**
 * To generate classfile instructions
 */
public abstract class CodeGenerator {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the code generator
   */
  public CodeGenerator() {
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Add an new instruction
   *
   * @param inst the instruction to add.
   */
  public abstract void addInstruction(Instruction inst);

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the current basic block
   */
  public BasicBlock getCurrentBasicBlock() {
    return current;
  }

  /**
   * Set the current basic block
   */
  public void setCurrentBasicBlock(BasicBlock bb) {
    current = bb;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected BasicBlock current;
}
