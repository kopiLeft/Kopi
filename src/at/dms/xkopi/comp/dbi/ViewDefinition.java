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

package at.dms.xkopi.comp.dbi;

import java.util.ArrayList;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.sqlc.SimpleIdentExpression;
import at.dms.xkopi.comp.sqlc.Expression;
import at.dms.xkopi.comp.sqlc.TableReference;

/**
 * This class represents a table definition
 */
public class ViewDefinition extends DbiStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	tableName	the name of the table
   * @param	columnNameList	the list of the columns mane
   * @param	reference	the table reference
   */
  public ViewDefinition(TokenReference ref,
			Expression tableName,
			ArrayList columnNameList,
			TableReference reference) {
    super(ref);
    this.tableName = tableName;
    this.columnNameList = columnNameList;
    this.reference = reference;
 }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getTableName
   */
  public Expression getTableName() {
    return tableName;
  }

  /**
   * getColumns
   */
  public ArrayList getColumns() {
    return columnNameList;
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  public boolean compareTo(ViewDefinition otherView) {
    Expression	otherTableName = otherView.getTableName();

    if (tableName instanceof SimpleIdentExpression && otherTableName instanceof SimpleIdentExpression) {
       if (((SimpleIdentExpression)tableName).compareTo((SimpleIdentExpression)otherTableName)) {
	 return true;
       }
    }
    return false;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitViewDefinition(this, tableName, columnNameList, reference);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Expression		tableName;
  private final ArrayList               columnNameList;
  private final TableReference		reference;
}
