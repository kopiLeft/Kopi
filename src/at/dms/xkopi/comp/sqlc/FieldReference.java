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

import java.util.ArrayList;
import java.util.ListIterator;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.CWarning;
import at.dms.kopi.comp.kjc.*;

public class FieldReference extends Expression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		   the token reference for this statement
   * @param	table	           the table name if any
   * @param	field	           the field ident
   * @param	hasPlus		   has an Oracle (+) outer join
   */
  public FieldReference(TokenReference ref, 
			String table, 
			String field,
			boolean hasPlus) {
    super(ref);
    this.table = table;
    this.field = field;
    this.hasPlus = hasPlus;
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
    visitor.visitFieldReference(this, table, field, hasPlus);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * test if the alias exists // la table a la colonne ident ?
   */
  public static String checkColumnExists(TokenReference ref, SqlContext context, String table, String field) {
    if (table != null) {
      TableReference  tableReference = context.getTableFromAlias(table);
      if (tableReference != null) {
	if (!tableReference.hasColumn(field)) {
	  context.reportTrouble(new CWarning(ref, SqlcMessages.COLUMN_NOT_IN_TABLE, field, table));
	}
	return tableReference.getTableForColumn(field);
      } else {
	  context.reportTrouble(new CWarning(ref, SqlcMessages.ALIAS_NOT_FOUND, table));
	  return null;
      }
    } else {
      ListIterator      iter = context.getTables().listIterator();

      while (iter.hasNext()) {
	TableReference newTable = (TableReference)iter.next();

	if (newTable.hasColumn(field)) {
          return newTable.getTableForColumn(field);
        }
      }
      context.reportTrouble(new CWarning(ref, SqlcMessages.IDENT_NOT_RESOLVABLE, field));
      return null;
    }
  }


  /**
   * Returuns the type of the field in the databasetable or null
   */
  public TableReference getTableReference(SqlContext context) {
    TableReference      tableReference;

    if (table != null) {
      tableReference = context.getTableFromAlias(table);
      if (tableReference == null) {
        return null;
      }
    } else {
      ArrayList         tableList = context.getTables();

      if (tableList.size() == 1) {
	tableReference = (TableReference) tableList.get(0);

	if (!(tableReference.hasColumn(field))) {
	  return null;
	}
      } else {
        return null;
      }
    }
    // set name of the table
    table = tableReference.getTableForColumn(field);
    return tableReference;
  }

  public String getFieldName() {
    return field;
  }

  public String toString() {
    return (table == null ?"" : table +".") + field + 
      (hasPlus ? "(+)" : "");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	table;
  private String	field;
  private boolean	hasPlus;
}
