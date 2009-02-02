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

package com.kopiright.xkopi.comp.dbi;

import java.sql.SQLException;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.xkopi.comp.database.DatabaseFixedColumn;
import com.kopiright.xkopi.comp.sqlc.IntegerLiteral;
import com.kopiright.xkopi.comp.sqlc.NumericType;
import com.kopiright.xkopi.lib.type.Fixed;

public class FixedType extends NumericType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**p
   * Constructor
   * @param     ref             the token reference for this statement
   * @param     precision
   * @param     scale
   * @param     min
   * @param     max
   */
  public FixedType(TokenReference ref,
                   int precision,
                   int scale,
                   Fixed min,
                   Fixed max)
  {
    super(ref,
          new IntegerLiteral(ref, new Integer(precision)),
          new IntegerLiteral(ref, new Integer(scale)));

    this.precision = precision;
    this.scale = scale;
    this.min = min;
    this.max = max;
  }
  
  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getPrecision
   */
  public int getPrecision() {
    return precision;
  }

  /**
   * getScale
   */
  public int getScale() {
    return scale;
  }

  /**
   * getMin
   */
  public Fixed getMin() {
    return min;
  }

  /**
   * getMax
   */
  public Fixed getMax() {
    return max;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  public byte getCode() {
    return TYP_FIXED;
  }

  /**
   * Insert the type of a column in the data dictionary
   */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
    int        minVal;
    int        maxVal;

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
    a.addFixedType(column, precision, scale, minVal, maxVal);
  }

  /**
   * equals
   */
  public boolean equals(Object o) {
    if (o instanceof FixedType) {
      FixedType         other = (FixedType)o;

      if (precision != other.precision) {
        return false;
      }

      if (scale != other.scale) {
        return false;
      }

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
    } else if (o instanceof NumericType) {
      return o.equals(this);
    } else {
      return false;
    }
  }
  
  /**
   * Returns the type for the type-checking mechanism of dbi. This type
   * is used as type of the field in the Database.k class. 
   *
   * @param     nullable 
   * @return    the columninfo
   */
  public DatabaseColumn getColumnInfo(boolean nullable) {
    return new DatabaseFixedColumn(nullable, precision, scale, min, max);
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param     visitor         the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitFixedType(this, this.precision, this.scale, this.min, this.max);
  }

  /**
   * Accepts a visitor.
   * This method must be implemented because it is a sqlc/Statement.
   *
   * @param     visitor         the visitor
   */
  public void accept(com.kopiright.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
    accept((DbiVisitor)visitor);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  int           precision;
  int           scale;
  Fixed         min;
  Fixed         max;
}
