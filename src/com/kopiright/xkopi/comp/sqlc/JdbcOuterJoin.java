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

/**
 * This class represents a reference to an outer join
 */
public class JdbcOuterJoin extends SimpleTableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param     ref             the token reference for this clause
   * @param     leftTable       the left table reference
   * @param     type            the join type
   * @param     rightTable      the right table reference
   * @param     joinPred        the join pred.
   */
  public JdbcOuterJoin(TokenReference ref,
                   SimpleTableReference leftTable,
                   String type,
                   SimpleTableReference rightTable,
                   JoinPred joinPred)
  {
    super(ref);

    this.leftTable = leftTable;
    this.type = type;
    this.rightTable = rightTable;
    this.joinPred = joinPred;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true if it on of the join tables has this column.
   */
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
   * Returns the Alias of the table reference.
   */
  public String getAlias() {
    return null;
  }

  public SimpleTableReference getLeftTable() {
    return leftTable;
  }

  public SimpleTableReference getRightTable() {
    return rightTable;
  }

  private String getTableAliasOrTableName(SimpleTableReference table) {
    String      alias;
    
    alias = table.getAlias();
    
    if (alias == null &&
        (table instanceof TableName)) {
      alias = ((TableName)table).getTableName();
    }
    
    return alias;
  }

  public String  getLeftTableAliasOrTableName() {
    return getTableAliasOrTableName(leftTable);
  }
  
  public String  getRightTableAliasOrTableName() {
    return getTableAliasOrTableName(rightTable);
  }

  /**
   * Returns the current instance.
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
   * @param     visitor                 the visitor
   */
  public void accept(SqlVisitor visitor) throws PositionedError {
    visitor.visitJdbcOuterJoin(this, leftTable, type, rightTable, joinPred);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private SimpleTableReference          leftTable;
  private String                        type;
  private SimpleTableReference          rightTable;
  private JoinPred                      joinPred;
}
