/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.xkjc;

import java.util.ArrayList;

import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.FieldReference;
import com.kopiright.xkopi.comp.sqlc.SelectElemExpression;
import com.kopiright.xkopi.comp.sqlc.SelectElem;
import com.kopiright.xkopi.comp.sqlc.SqlVisitor;
import com.kopiright.xkopi.comp.sqlc.SqlcMessages;
import com.kopiright.xkopi.comp.sqlc.TableReference;
import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents an SqlExpression
 */
public class XTypedSqlExpr extends SelectElem {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public XTypedSqlExpr(TokenReference ref, SelectElem expr, CType type, boolean first) {
    super(ref);
    this.expr = expr;
    this.type = type;
    this.first = first;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public SelectElem getSql() {
    return expr;
  }

  /**
   * Test if the element correspond to a given columnName
   *
   * @param	columnName      the name to test
   */
  public boolean isColumn(String columnName, ArrayList tables) {
    return expr.isColumn(columnName, tables);
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
    expr.accept(visitor);

    if (expr instanceof SelectElemExpression) {
      Expression	val = ((SelectElemExpression)expr).getExpression();

      if (val instanceof FieldReference) {
        XDatabaseColumn dc;
        CType           fieldType;
        TableReference  tableReference;
        String          fieldname;

        tableReference =  ((FieldReference) val).getTableReference(visitor.getContext());
        if (tableReference != null) {
          fieldname = ((FieldReference) val).getFieldName();
          dc = XUtils.getDatabaseColumn(tableReference.getTableForColumn(fieldname), fieldname);
          fieldType = (dc == null) ? null : dc.getStandardType();

          try {
            type = type.checkType(visitor.getContext().getTypeContext());
          } catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
          }

          if ((fieldType != null) && ! (fieldType.isAssignableTo(visitor.getContext().getTypeContext(), type))) {
            // FIX lackner 020219 use XMutableType ?
            visitor.getContext().reportTrouble(new CWarning(getTokenReference(),
                                                            SqlcMessages.BAD_DATABASE_TYPE,
                                                            val +"("+fieldType+")",
                                                            type));
          }
        }
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private SelectElem		expr;
  private CType			type;		//!!! graf 991208 not used remove
  private boolean		first;		//!!! graf 991208 not used remove
}
