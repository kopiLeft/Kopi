/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: DropIndexStatement.java 4203 2004-07-28 18:43:29Z imad $
 */

package at.dms.xkopi.comp.dbi;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.sqlc.Expression;

/**
 * This class represents a table definition
 */
public class DropSequenceStatement extends DbiStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	indexName	the name of the index
   * @param	tableName	the name of the table
   */
  public DropSequenceStatement(TokenReference ref, Expression sequenceName) {
    super(ref);
    this.sequenceName = sequenceName;
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
    visitor.visitDropSequenceStatement(this, sequenceName);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  String	indexName;
  Expression	sequenceName;
}
