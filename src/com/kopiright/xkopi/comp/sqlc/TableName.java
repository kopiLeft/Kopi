/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

/**
 * This class represents a reference to a sub table expression
 */
public class TableName extends SimpleTableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	alias		tha alias of the reference
   * @param	name		the table name
   */
  public TableName(TokenReference ref, Expression name, TableAlias alias) {
    super(ref);
    this.name = name;
    this.alias = alias;
  }


  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

// a une colonne qui correspond a ce nom
  public boolean hasColumn(String ident) {
    if (name instanceof SimpleIdentExpression) {
      String tableName = ((SimpleIdentExpression)name).getIdent();
      return XUtils.columnExists(tableName, ident);
    }
    return true;
  }

  public String getTableForColumn(String column) {
    return (hasColumn(column) && (name instanceof SimpleIdentExpression)) ?
      ((SimpleIdentExpression)name).getIdent() :
      null;
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
    return this;
  }

  /**
   * Returns the name of the table
   */
  public String getTableName() {
    if (name instanceof SimpleIdentExpression) {
      return ((SimpleIdentExpression)name).getIdent();
    }
    return null;
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
    visitor.visitTableName(this, name, alias);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Expression	name;
  private TableAlias	alias;
}
