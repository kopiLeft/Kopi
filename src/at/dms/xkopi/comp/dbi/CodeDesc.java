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

package at.dms.xkopi.comp.dbi;

import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.xkopi.comp.sqlc.Type;

public abstract class CodeDesc extends Type {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	name
   */
  public CodeDesc(TokenReference ref, String name) {
    super(ref);
    this.name = name;
 }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getName
   */
  public String getName() {
    return name;
  }


  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   * This method must be implemented because it is a sqlc/Statement.
   *
   * @param	visitor			the visitor
   */
  public void accept(at.dms.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
    accept((DbiVisitor)visitor);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  String	name;
}
