/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.dbi;

import java.sql.SQLException;
import java.util.ArrayList;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.xkopi.comp.database.DatabaseBooleanColumn;
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.xkopi.comp.sqlc.BoolType;

public class CodeBoolType extends BoolType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	list		the list of elements
   */
  public CodeBoolType(TokenReference ref, ArrayList list) {
    super(ref);
    this.list = list;
 }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getList
   */
  public ArrayList getList() {
    return list;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  public byte getCode() {
    return TYP_CODE;
  }

 /**
  * Insert the type of a column in the data dictionary
  */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
  }

  /**
   * compareTo
   */
  public boolean compareTo(CodeBoolType otherCode) {
    ArrayList	otherList = otherCode.getList();

    if (list.size() != otherList.size()) {
      return false;
    } else {
      for (int i = 0; i < list.size(); i++) {
	if (((CodeDesc)list.get(i)).equals((CodeDesc)otherList.get(i))) {
	  return false;
	}
      }
      return true;
    }
  }

  /**
   * Returns the type for the type-checking mechanism of dbi. This type
   * is used as type of the field in the Database.k class. 
   *
   * @param nullable 
   * @return the columninfo
   */
  public DatabaseColumn getColumnInfo(boolean nullable) {
    return new DatabaseBooleanColumn(nullable);
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
    visitor.visitCodeBoolType(this, this.list);
  }

  /**
   * Accepts a visitor.
   * This method must be implemented because it is a sqlc/Statement.
   *
   * @param	visitor			the visitor
   */
  public void accept(com.kopiright.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
    accept((DbiVisitor)visitor);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final ArrayList		list;
}
