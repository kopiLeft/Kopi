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

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents a reference to an outer join
 */
public class OuterJoin extends SimpleTableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	leftTable	the left table reference
   * @param	type		the join type
   * @param	rightTable	the right table reference
   * @param	joinPred	the join pred.
   */
  public OuterJoin(TokenReference ref,
		   TableReference leftTable,
		   String type,
		   TableReference rightTable,
		   JoinPred joinPred) {
    super(ref);
    this.leftTable = leftTable;
    this.type = type;
    this.rightTable = rightTable;
    this.joinPred = joinPred;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  // a une colonne qui correspond a ce nom
  public boolean hasColumn(String ident) {
    return leftTable.hasColumn(ident) ^ rightTable.hasColumn(ident);
  }

  public String getTableForColumn(String column) {
    return null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true if the table reference has an alias
   */
  public String getAlias() {
    return null;
  }

  /**
   * Returns a table name from an alias name or null
   */
  public TableReference getTable() {
    return this;
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
    visitor.visitOuterJoin(this, leftTable, type, rightTable, joinPred);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private TableReference	leftTable;
  private String		type;
  private TableReference	rightTable;
  private JoinPred		joinPred;
}
