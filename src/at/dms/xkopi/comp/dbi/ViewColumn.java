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
import at.dms.xkopi.comp.sqlc.Type;

/**
 * This class represents an SqlExpression
 */
public class ViewColumn extends DbiPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public ViewColumn(TokenReference ref, String ident, Type type, boolean nullable) {
    super(ref);
    this.ident = ident;
    this.type = type;
    this.nullable = nullable;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * getIdent
   */
  public String getIdent() {
    return ident;
  }

  /**
   * getType
   */
  public Type getType() {
    return type;
  }

  /**
   * isNullable
   */
  public boolean isNullable() {
    return nullable;
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
    visitor.visitViewColumn(this, ident, type, nullable, this.getComment());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected String	ident;
  protected boolean	nullable;
  protected Type	type;
}
