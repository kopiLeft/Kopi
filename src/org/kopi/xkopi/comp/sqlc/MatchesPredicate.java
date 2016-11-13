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

public class MatchesPredicate extends Predicate {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	sens
   * @param	matchesExpr
   * @param	escapeExpr
   */
  public MatchesPredicate(TokenReference ref,
			  String sens,
			  Expression matchesExpr,
			  Expression escapeExpr) {
    super(ref);
    this.sens = sens;
    this.matchesExpr = matchesExpr;
    this.escapeExpr = escapeExpr;
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
    visitor.visitMatchesPredicate(this, sens, matchesExpr, escapeExpr);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String        sens;
  private Expression	matchesExpr;
  private Expression       escapeExpr;
}
