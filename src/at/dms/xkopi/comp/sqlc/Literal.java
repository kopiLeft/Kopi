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

package at.dms.xkopi.comp.sqlc;

import at.dms.compiler.base.TokenReference;

/**
 * This class represents a literal value (SQL literal)
 */
public abstract class Literal extends Expression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public Literal(TokenReference ref) {
    super(ref);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the value of this literal (in Java)
   */
  public abstract Object getValue();

  /**
   * Returns true since it is a literal
   */
  public final boolean isLiteral() {
    return true;
  }
}
