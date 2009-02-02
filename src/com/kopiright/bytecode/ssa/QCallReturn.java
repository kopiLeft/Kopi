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
 * An abstract class to represent expression that can throw exceptions
 */
public abstract class QCallReturn extends QExpression {

  // -------------------------------------------------------------------
  // PROPERTIES
  // -------------------------------------------------------------------

  /**
   * Test if the operand may throw an exception
   */
  public boolean mayThrowException() {
    return true;
  }

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

  // -------------------------------------------------------------------
  // GENERATION
  // -------------------------------------------------------------------

  /**
   * Generate the classfile instructions for this operand
   *
   * @param codeGen the code generator
   */
  public abstract void generateInstructions(CodeGenerator codeGen);

}
