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

package org.kopi.kopi.comp.kjc;

import org.kopi.bytecode.classfile.AbstractInstructionAccessor;

/**
 * This class represents a position in the code array where the associated
 * instruction has not yet been generated.
 */
public class CodeLabel extends AbstractInstructionAccessor {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new code label.
   */
  public CodeLabel() {
    this.address = -1;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the address of the label in the code array.
   */
  public void setAddress(int address) {
    this.address = address;
  }

  /**
   * Returns the address of the label in the code array.
   */
  public int getAddress() {
    return address;
  }

  /**
   * Returns true iff the label has already been planted.
   */
  public boolean hasAddress() {
    return address != -1;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int				address;
}
