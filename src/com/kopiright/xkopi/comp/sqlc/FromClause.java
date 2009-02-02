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

import java.util.ArrayList;
import java.util.Iterator;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents an From clause
 */
public class FromClause extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param     ref             the token reference for this clause
   * @param     tableRefs       a list of TableReference
   */
  public FromClause(TokenReference ref, ArrayList tableRefs) {
    super(ref);
    this.tableRefs = tableRefs;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the number of table reference
   */
  public ArrayList getTables() {
    return tableRefs;
  }

  /**
   * Returns a table name from an alias name or null
   */
  public TableReference getTableFromAlias(String alias) {
    // go throught the vector of table ref.
    for (int i = 0; i < tableRefs.size(); i++) {
      SimpleTableReference      elem = (SimpleTableReference)tableRefs.get(i);

      if (alias.equals(elem.getAlias())) {
        return elem.getTable();
      }

      if (elem instanceof TableName
          && alias.equals(((TableName) elem).getTableName())) {
        return elem.getTable();
      }

      // if it's an OUTER JOIN table reference we check all used tables
      if (elem instanceof OuterJoin) {
        OuterJoin       oj = (OuterJoin) elem;

        if (alias.equals(oj.getTableAliasOrTableName())) {
          return oj.getTable();
        }
        SubOuterJoin[] subOj = oj.getSubOuterJoin();
        
        for (int j = 0; j < subOj.length ; j++) {
          if (alias.equals(subOj[j].getTable().getAlias())) {
            return subOj[j].getTable();
          }  
        }
        
      }

      // if it's an Jdbc OUTER JOIN table reference we check both used table
      if (elem instanceof JdbcOuterJoin) {
        JdbcOuterJoin       oj = (JdbcOuterJoin) elem;

        if (alias.equals(oj.getLeftTableAliasOrTableName())) {
          return oj.getLeftTable();
        }

        if (alias.equals(oj.getRightTableAliasOrTableName())) {
          return oj.getRightTable();
        }
      }
    }
    return null;
  }

  /**
   * Returns true if all alias are unique
   */
  public boolean checkAliasAreUnique(SqlContext context) {
    ArrayList     tables = (ArrayList)tableRefs.clone();
    
    // if a table reference is an outer join
    // replace it with the left and right tables withi it
    for (Iterator i = tableRefs.iterator(); i.hasNext(); ) {
      SimpleTableReference      elem = (SimpleTableReference)i.next();
      if (elem instanceof JdbcOuterJoin) {
        tables.add(((JdbcOuterJoin)elem).getLeftTable());
        tables.add(((JdbcOuterJoin)elem).getRightTable());
        tables.remove(elem);
      } else if (elem instanceof OuterJoin) {
        tables.add(((OuterJoin)elem).getTable());
        SubOuterJoin[] subOj = ((OuterJoin)elem).getSubOuterJoin();
        for (int j = 0; j < subOj.length; j++) {
          tables.add(subOj[j].getTable());
        }
        tables.remove(elem);
      }        
    }
    
    for (int i = 0; i < tables.size(); i++) {
      SimpleTableReference      elem = (SimpleTableReference)tables.get(i);
      String                    alias = elem.getAlias();

      if (alias != null) {
        for (int j = i + 1; j < tables.size(); j++) {
          if (alias.equals(((SimpleTableReference)tables.get(j)).getAlias())) {
            context.reportTrouble(new CWarning(getTokenReference(),
                                               SqlcMessages.ALIAS_NOT_UNIQUE,
                                               alias));
            return false;
          }
        }
      }
    }
    return true;
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
    visitor.visitFromClause(this, tableRefs);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ArrayList     tableRefs;
}
