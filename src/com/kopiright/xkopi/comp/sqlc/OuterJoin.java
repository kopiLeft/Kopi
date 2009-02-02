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
public class OuterJoin extends SimpleTableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param     ref             the token reference for this clause
   * @param     table           the table reference
   */
  public OuterJoin(TokenReference ref,
                   SimpleTableReference table)
  {
    super(ref);
    this.table = table;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true if it on of the join tables has this column.
   */
   public boolean hasColumn(String ident) {
     for (int i = 0; i < subOuterJoins.length; i++) {
       if (subOuterJoins[i].getTable().hasColumn(ident)) {
         return true;
       } 
     }
     return table.hasColumn(ident);
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

  public String getTableAliasOrTableName() {
    String      alias;
    
    alias = table.getAlias();
    if (alias == null &&
        (table instanceof TableName)) {
      alias = ((TableName)table).getTableName();
    }
    return alias;
  }
  
  /**
   * Returns the current instance.
   */
  public TableReference getTable() {
    return table;
  }

  public SubOuterJoin[] getSubOuterJoin() {
    return subOuterJoins;
  }

  
  public void  setSubOuterJoins(SubOuterJoin[] subOuterJoins) {
    this.subOuterJoins = subOuterJoins;
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
    visitor.visitOuterJoin(this, table, subOuterJoins);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private SimpleTableReference          table;
  private SubOuterJoin[]                subOuterJoins;                  
}
