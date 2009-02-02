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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

public class SelectElemExpression extends SelectElem {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	columnExpr	the expression of this SelectElem
   */
  public SelectElemExpression (TokenReference ref, Expression columnExpr) {
    super(ref);
    this.columnExpr = columnExpr;
    this.newColumnName = null;
  }

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	columnExpr	the expression of this SelectElem
   * @param	newColumnName	the alias of this expression
   */
  public SelectElemExpression (TokenReference ref, Expression columnExpr, String newColumnName) {
    super(ref);
    this.columnExpr = columnExpr;
    this.newColumnName = newColumnName;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public Expression getExpression() {
    return columnExpr;
  }

  public String getNewColumnName() {
    return newColumnName;
  }

  /**
   * Test if the element correspond to a given columnName
   *
   * @param	columnName      the name to test
   */
  public boolean isColumn(String columnName, ArrayList tables) {
    return (columnName.equals(newColumnName) ||
            (columnExpr instanceof FieldReference &&
             columnName.equals(((FieldReference) columnExpr).getFieldName())));
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
    visitor.visitSelectElemExpression(this, columnExpr, newColumnName);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Expression	columnExpr;
  private String	newColumnName;
}
