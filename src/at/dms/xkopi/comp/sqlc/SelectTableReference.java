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

import java.util.Iterator;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a reference to a table
 */
public class SelectTableReference extends TableReference {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	expr		the reference
   */
  public SelectTableReference(TokenReference ref, SelectExpression expr) {
    super(ref);
    this.expr = expr;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getColumnNumber
   */
  public int getColumnNumber() {
    return expr.getColumnNumber();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

// a une colonne qui correspond a ce nom
  public boolean hasColumn(String ident) {
    Iterator    columns = expr.getColumns().iterator();

    while (columns.hasNext()) {
      SelectElem        elem = (SelectElem) columns.next();

      if (elem.isColumn(ident, expr.getTables())) {
        return true;
      }
    }

    return false;
  }

  public String getTableForColumn(String column) {
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
    visitor.visitSelectTableReference(this, expr);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------1--------------------------------------

  private SelectExpression	expr;
}
