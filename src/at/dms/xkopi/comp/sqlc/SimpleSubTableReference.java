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
 * $Id: SimpleSubTableReference.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.sqlc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents a reference to a sub table expression
 */
public class SimpleSubTableReference extends SimpleTableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	table		the table expression
   * @param	alias		the alias for this reference
  */
  public SimpleSubTableReference(TokenReference ref, TableReference table, TableAlias alias) {
    super(ref);
    this.alias = alias;
    this.table = table;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

// a une colonne qui correspond a ce nom
  public boolean hasColumn(String ident) {
    return alias.getFieldNameList() == null ?
      table.hasColumn(ident):
      getTable().hasColumn(ident);
  }

  public String getTableForColumn(String column) {
    return table.getTableForColumn(column);
  }

 // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true if the table reference has an alias
   */
  public String getAlias() {
    return alias.getIdent();
  }

  /**
   * Returns a table name from an alias name or null
   */
  public TableReference getTable() {
    FieldNameList list = alias.getFieldNameList();
    if (list != null) {
      if (fieldNameListTableReference == null) {
	fieldNameListTableReference = new FieldNameListTableReference(getTokenReference(), list);
      }
      return fieldNameListTableReference;
    }
    return table;
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
    visitor.visitSimpleSubTableReference(this, alias, table);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private TableAlias		alias;
  private TableReference	table;
  private FieldNameListTableReference		fieldNameListTableReference;
}
