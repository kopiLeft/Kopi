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
 * $Id: FromClause.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.sqlc;

import java.util.ArrayList;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.CWarning;

/**
 * This class represents an From clause
 */
public class FromClause extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	tableRefs	a list of TableReference
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

      if (elem instanceof TableName &&
          alias.equals(((TableName) elem).getTableName())) {
        return elem.getTable();
      }

    }
    return null;
  }

  /**
   * Returns true if all alias are unique
   */
  public boolean checkAliasAreUnique(SqlContext context) {
    for (int i = 0; i < tableRefs.size(); i++) {
      SimpleTableReference      elem = (SimpleTableReference)tableRefs.get(i);
      String                    alias = elem.getAlias();

      for (int j = i + 1; j < tableRefs.size(); j++) {
	if(alias.equals(((SimpleTableReference)tableRefs.get(j)).getAlias())) {
	  context.reportTrouble(new CWarning(getTokenReference(), SqlcMessages.ALIAS_NOT_UNIQUE, alias));
	  return false;
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
   * @param	visitor			the visitor
   */
  public void accept(SqlVisitor visitor) throws PositionedError {
    visitor.visitFromClause(this, tableRefs);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ArrayList	tableRefs;
}
