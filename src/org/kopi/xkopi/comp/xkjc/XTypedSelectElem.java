/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.xkopi.comp.xkjc;

import java.util.ArrayList;

import org.kopi.kopi.comp.kjc.*;
import org.kopi.xkopi.comp.sqlc.*;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.util.base.InconsistencyException;

/**
 * This class represents a typed column in a Sql SELECT
 */
public class XTypedSelectElem extends SelectElem {

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	type		the type of this expression or column in database
   * @param	expr		an optional SQL expression (count(*))
   * @param	field		the name for this field
   */
  public XTypedSelectElem(TokenReference where,
			  CType type,
			  Expression expr,
			  FieldReference field)
  {
    super(where);

    if (field == null) {
      this.expr = expr;
      hasAs = false;
      ident = "Anonymous";
    } else {
      this.ident = field.toString();
      if (expr == null) {
        this.expr = field;
        hasAs = false;
      } else {
        this.expr = expr;
        hasAs = true;
      }
    }
    // !!! graf 03.05.29
    // !!! WRONG ASSUMPTION
    // !!! WE DO NOT CHECK '$' BECAUSE IT IS NOT VALID IN SQL IDENT
    var = new JVariableDefinition(where,
                                  0,/*Constants.ACC_PUBLIC*/
                                  type,
                                  this.ident.replace('.', '$'),
                                  null);
  }

  /**
   *
   */
  public JVariableDefinition getVariableDefinition() {
    return var;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  public Expression getExpression() {
    return expr;
  }

  /**
   * Returns the sql statement.
   */
  public SqlPhylum getSql() {
    return expr;
  }

  /**
   * Returns the sql ident
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Test if the select elem has an alias
   */
  public boolean hasAlias() {
    return hasAs;
  }

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param p the printwriter into the code is generated
   */
  public void accept(KjcVisitor p) {
    throw new InconsistencyException();
  }

  /**
   * Test if the element correspond to a given columnName
   *
   * @param	columnName      the name to test
   */
  public boolean isColumn(String columnName, ArrayList tables) {
    return columnName.equals(ident);
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(SqlVisitor visitor)
    throws PositionedError
  {
    ((XSqlVisitor)visitor).visitTypedSelectElem(this, ident, var, expr, hasAs);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		ident;
  private Expression		expr;
  private JVariableDefinition	var;
  private boolean		hasAs;
}
