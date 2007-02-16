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

package com.kopiright.xkopi.comp.sqlc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents a reference to a table
 */
public class UnidiffTableTerm extends SubTable {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	spec		the unidiff specification
   * @param	left		the left reference
   * @param	right		the right reference
   */
  public UnidiffTableTerm(TokenReference ref,
			  UnidiffSpec spec,
			  SubTable left,
			  SubTable right) {
    super(ref);
    this.spec = spec;
    this.left = left;
    this.right = right;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(SqlVisitor visitor) throws PositionedError {
    visitor.visitUnidiffTableTerm(this, spec, left, right);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------1--------------------------------------

  private UnidiffSpec		spec;
  private SubTable		left;
  private SubTable		right;
}
