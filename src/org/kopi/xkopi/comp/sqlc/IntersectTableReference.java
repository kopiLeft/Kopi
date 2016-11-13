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

package org.kopi.xkopi.comp.sqlc;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * This class represents a reference to a table
 */
public class IntersectTableReference extends TableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	spec		the intersect specification
   * @param	left		the left reference
   * @param	right		the right reference
   */
  public IntersectTableReference(TokenReference ref,
				 IntersectSpec spec,
				 TableReference left,
				 TableReference right) {
    super(ref);
    this.spec = spec;
    this.left = left;
    this.right = right;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

// a une colonne qui correspond a ce nom
  public boolean hasColumn(String ident) {
    return (left.hasColumn(ident) && right.hasColumn(ident));
  }

  public String getTableForColumn(String column) {
    return null;
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
    visitor.visitIntersectTableReference(this, spec, left, right);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------1--------------------------------------

  private IntersectSpec		spec;
  private TableReference	left;
  private TableReference	right;
}
