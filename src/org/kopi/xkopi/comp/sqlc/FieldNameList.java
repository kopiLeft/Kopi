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

package org.kopi.xkopi.comp.sqlc;

import java.util.List;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.CWarning;

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
    return fields.size();
  }

  /**
   * This function check if all the idents are unique
   */
  public boolean checkIdentsAreUnique(SqlContext context) {
    for (int i = 0; i < fields.size(); i++) {
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

  /**
   * Verifies whether each field in the list is defined in the specified table.
   */
  public void checkColumnsExist(TokenReference ref,
                                SqlContext context,
                                String table)
  {
    for (int i = 0; i < fields.size(); i++) {
      FieldReference.checkColumnExists(ref, context, table, (String)fields.get(i));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private List          fields;
}
