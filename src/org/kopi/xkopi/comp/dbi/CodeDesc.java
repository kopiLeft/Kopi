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

package org.kopi.xkopi.comp.dbi;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.xkopi.comp.sqlc.Type;

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
  public void accept(org.kopi.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
    accept((DbiVisitor)visitor);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  String	name;
}
