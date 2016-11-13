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

package org.kopi.xkopi.comp.dbi;

import java.sql.SQLException;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.xkopi.comp.database.DatabaseColumn;
import org.kopi.xkopi.comp.database.DatabaseIntegerColumn;
import org.kopi.xkopi.comp.sqlc.IntegerType;

public class IntType extends IntegerType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	min		the smallest possible value
   * @param	max		the largest possible value
   */
  public IntType(TokenReference ref, Integer min, Integer max) {
    super(ref);
    this.min = min;
    this.max = max;
 }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  public byte getCode() {
    return TYP_INT;
  }

  /**
   * Insert the type of a column in the data dictionary
   */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
     int minVal;
     int maxVal;

    if (min == null) {
       minVal = Integer.MIN_VALUE + 1;
    } else {
      minVal = min.intValue();
    }
    if (max == null) {
      maxVal = Integer.MAX_VALUE;
    } else {
      maxVal = max.intValue();
    }

    a.addIntType(column, minVal, maxVal);
  }

  /**
   * equals
   */
  public boolean equals(Object o) {
    if (! (o instanceof IntType)) {
      return false;
    }

    IntType		other = (IntType)o;

    if ((min == null) != (other.min == null)) {
      return false;
    }
    if (min != null && min.intValue() != other.min.intValue()) {
      return false;
    }

    if ((max == null) != (other.max == null)) {
      return false;
    }
    if (max != null && max.intValue() != other.max.intValue()) {
      return false;
    }

    return true;
  }

  /**
   * Returns the type for the type-checking mechanism of dbi. This type
   * is used as type of the field in the Database.k class. 
   *
   * @param nullable 
   * @return the type
   */
  public DatabaseColumn getColumnInfo(boolean nullable) {
    if ((max!=null) || (min!=null)) {
      return new DatabaseIntegerColumn(nullable, min, max);
    }
    else 
      return new DatabaseIntegerColumn(nullable);
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
    visitor.visitIntType(this, this.min, this.max);
  }

  /**
   * Accepts a visitor.
   * This method must be implemented because it is a sqlc/Statement.
   *
   * @param	visitor			the visitor
   */
  public void accept(org.kopi.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
    accept((DbiVisitor)visitor);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  final Integer		min;
  final Integer		max;
}
