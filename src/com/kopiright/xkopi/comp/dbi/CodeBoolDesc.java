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

package com.kopiright.xkopi.comp.dbi;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.xkopi.comp.sqlc.Type;

public class CodeBoolDesc extends CodeDesc {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	name
   * @param	value
   */
  public CodeBoolDesc(TokenReference ref, String name, Boolean value) {
    super(ref, name);
    this.value = value;
 }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------


  /**
   * getValue
   */
  public Boolean getValue() {
    return value;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * equals
   */
  public boolean equals(CodeBoolDesc otherCode) {
    return ((name == otherCode.getName()) && (value == otherCode.getValue()));
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitCodeBoolDesc(this, name, value);
  }

  /**
   * Accepts a visitor.
   * This method must be implemented because it is a sqlc/Statement.
   *
   * @param	visitor			the visitor
   */
  /* public void accept(com.kopiright.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
   * accept((DbiVisitor)visitor);
   * }
   */

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  Boolean	value;
}
