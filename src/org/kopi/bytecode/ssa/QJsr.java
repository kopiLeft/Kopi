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

import org.kopi.bytecode.classfile.JumpInstruction;
import org.kopi.bytecode.classfile.Constants;

/**
 * A class to represent an jsr instruction
 */
public class QJsr extends QAbstractJumpInst {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a jsr instruction
   *
   * @param dest the destination of the jump
   */
  public QJsr(Edge dest) {
    this.dest = dest;
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the current instruction may throw an exception.
   */
  public  boolean mayThrowException() {
    return false;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[0];
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return  "jsr";
  }

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this quadruple instruction
   *
   * @param codeGen the code generator
   */
  public  void generateInstructions(CodeGenerator codeGen) {
    codeGen.addInstruction(new JumpInstruction(Constants.opc_jsr,
                                               new EdgeLabel(dest)));
  }

  /**
   * Simplify all consecutive jumps
   */
  public void simplifyAllJumps() {
    simplifyJump(dest);
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected Edge dest;

}
