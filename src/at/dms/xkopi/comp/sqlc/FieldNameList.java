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

import java.util.List;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.CWarning;

public class FieldNameList extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		           the token reference for this statement
   * @param	exprs	                   the vector of fields
   */
  public FieldNameList(TokenReference ref, List fields) {
    super(ref);
    this.fields = fields;
  }

 // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public boolean hasColumn(String ident) {
    for(int i = 0; i < fields.size(); i++) {
      if (ident.equals(fields.get(i))) {
	return true;
      }
    }
    return false;
  }

 /**
   * getFields
   */
  public List getFields() {
    return fields;
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  public boolean equals(Object otherFieldNameList) {
    if (otherFieldNameList instanceof FieldNameList) {
      List	otherFields = ((FieldNameList)otherFieldNameList).getFields();
      if (fields.size() == otherFields.size()) {
	for (int i = 0; i < fields.size(); i++) {
	  if (!fields.get(i).equals(otherFields.get(i))) {
	    return false;
	  }
	}
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
  public void accept(SqlVisitor visitor) throws PositionedError {
    visitor.visitFieldNameList(this, fields);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * Return the number of elements in the vector
   */
  public int elemNumber() {
    int number = 0;
    for (int i = 0; i < fields.size(); i++) {
      number++;
    }
    return number;
  }

  /**
   * This function check if all the idents are unique
   */
  public boolean checkIdentsAreUnique(SqlContext context) {
    for(int i = 0; i < fields.size(); i++) {
      String field = (String)fields.get(i);
      for (int j = i + 1; j < fields.size(); j++) {
	if (field.equals(fields.get(j))) {
	  context.reportTrouble(new CWarning(getTokenReference(), SqlcMessages.COLUMN_NOT_UNIQUE, field));
	  return false;
	}
      }
    }
    return true;
  }
/*
!!!
  public JExpression checkSql(SqlContext context, JExpression left, StringBuffer current, TableReference table) {
    for (int i = 0; i < fields.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      current.append(fields.get(i));
    }

    // check if the columns are in the table
    //if (context.getTables().size() == 1) {
      //TableReference table = (TableReference)context.getTables().get(0);
      for(int i = 0; i < fields.size(); i++) {
	if (!table.hasColumn((String)fields.get(i))) {
	  context.reportTrouble(new CWarning(getTokenReference(), "x-column-not-resolvable", fields.get(i)));
	}
      }
   // } else {
   //   context.reportTrouble(new CWarning(getTokenReference(), "x-more-than-one-name"));
  //  }
    return left;
  }
*/

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private List          fields;
}
