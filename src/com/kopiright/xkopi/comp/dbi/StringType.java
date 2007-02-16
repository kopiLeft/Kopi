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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.xkopi.comp.database.DatabaseStringColumn;
import com.kopiright.xkopi.comp.sqlc.ArrayPrecision;
import com.kopiright.xkopi.comp.sqlc.CharType;
import com.kopiright.xkopi.comp.sqlc.IntegerLiteral;

public class StringType extends CharType implements DbiType {

  public static final int TYP_NONE = 0;
  public static final int TYP_UPPER = 1;
  public static final int TYP_LOWER = 2;
  public static final int TYP_NAME = 3;

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	fixed
   * @param	width
   * @param	height
   * @param	convert
   */
  public StringType(TokenReference ref,
		    boolean fixed,
		    int width,
		    int height,
		    int convert)
  {
    super(ref, new ArrayPrecision(ref,
				  (fixed) ? new IntegerLiteral(ref, new Integer(width * height)) : null));
    this.fixed = fixed;
    this.width = width;
    this.height = height;
    this.convert = convert;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getWidth
   */
  public int getWidth() {
    return width;
  }

  /**
   * isFixed
   */
  public boolean isFixed() {
    return fixed;
  }

  /**
   * getHeight
   */
  public int getHeight() {
    return height;
  }

  /**
   * getconvert
   */
  public int getConvert() {
    return convert;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  public byte getCode() {
    return TYP_STRING;
  }

 /**
   * Insert the type of a column in the data dictionary
   */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
    a.addStringType(column, fixed, width, height, (byte)convert);
  }

  /**
   * equals
   */
  public boolean equals(Object o) {
   if (o instanceof StringType) {
      return (fixed == ((StringType)o).fixed &&
	      width == ((StringType)o).width &&
	      height == ((StringType)o).height &&
	      convert == ((StringType)o).convert);
    } else {
      return false;
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
    if ((width != -1) || (height != -1)) return new DatabaseStringColumn(nullable, width, height);
    return new DatabaseStringColumn(nullable);
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
    visitor.visitStringType(this, this.fixed, this.width, this.height, this.convert);
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

  private final boolean		fixed;
  private final int		width;
  private final int		height;
  private final int		convert;
}
