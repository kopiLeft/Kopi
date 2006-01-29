/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

public class GenerationContext {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a GenerationContext that will be passed
   * as a parameter of the genCode methods
   * @param	factory         the TypeFactory
   * @param	sequence        the CodeSequence
   */
  public GenerationContext(TypeFactory factory, CodeSequence sequence) {
    this.factory        = factory;
    this.sequence       = sequence;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return the TypeFactory member of this
   */
  public TypeFactory getTypeFactory() {
    return factory;
  }

  /**
   * Return the CodeSequence member of this
   */
  public CodeSequence getCodeSequence() {
    return sequence;
  }
  

  // ----------------------------------------------------------------------
  // MEMBERS
  // ----------------------------------------------------------------------
  
  private TypeFactory   factory;
  private CodeSequence  sequence;
  
}
