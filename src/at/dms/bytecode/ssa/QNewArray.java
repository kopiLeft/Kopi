/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: QNewArray.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.bytecode.ssa;

import at.dms.bytecode.classfile.NewarrayInstruction;
import at.dms.bytecode.classfile.Constants;
/**
 * A class to represent instruction newarray
 */
public class QNewArray extends QCallReturn {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the instruction
   *
   * @param nai the new array instruction in the source.
   * @param size the array size
   */
  public QNewArray(NewarrayInstruction nai, QOperand size) {
    this.nai = nai;
    this.size = new QOperandBox(size, this);
  }

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the instruction has side effects.
   */
  public boolean hasSideEffects() {
    return true;
  }

  /**
   * Return the type of the expression
   */
  public byte getType() {
    return Constants.TYP_REFERENCE;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the operands of the instruction
   */
  public QOperandBox[] getUses() {
    return new QOperandBox[] {size };
  }

  /**
   * A representation of the instruction
   */
  public String toString() {
    return "new " + nai.getType() + " [" + size + "]";
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
    size.getOperand().generateInstructions(codeGen);
    codeGen.addInstruction(nai);
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected NewarrayInstruction nai;
  protected QOperandBox size;
}
