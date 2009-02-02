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

package com.kopiright.xkopi.comp.sqlc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

public class SimpleWhenClause extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		           the token reference for this statement
   * @param	whenExpr	           the Sql Expression for the when
   * @param	thenExpr	           the Sql Expression for the else
   */
  public SimpleWhenClause (TokenReference ref, Expression whenExpr, Expression thenExpr) {
    super(ref);
    this.whenExpr = whenExpr;
    this.thenExpr = thenExpr;
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
    visitor.visitSimpleWhenClause(this, whenExpr, thenExpr);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Expression              whenExpr;
  private Expression              thenExpr;
}
