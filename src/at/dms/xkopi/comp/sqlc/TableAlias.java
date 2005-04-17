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

public class TableAlias extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		           the token reference for this statement
   */
  public TableAlias(TokenReference ref, String name, FieldNameList list) {
    super(ref);
    this.name = name;
    this.list = list;
   }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the FieldNameList
   */
  public FieldNameList getFieldNameList() {
    return list;
  }

  /**
   * Returns true if the table reference has an alias
   */
  public String getIdent() {
    return name;
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
    visitor.visitTableAlias(this, name, list);
  }

/*
!!!
  public JExpression checkSql(SqlContext context, JExpression left, StringBuffer current, TableReference table) {
    if (name != null) {
      current.append(" ");
      current.append(name);
    }
    if (list != null) {
      current.append("(");
      left = list.checkSql(context, left, current, table);
      current.append(")");
    }
    return left;
  }
*/

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String         name;
  private FieldNameList  list;
}
