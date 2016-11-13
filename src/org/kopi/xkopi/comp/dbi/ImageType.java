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
import org.kopi.xkopi.comp.database.DatabaseImageColumn;
import org.kopi.xkopi.comp.sqlc.BlobType;

public class ImageType extends BlobType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	width
   * @param	height
   */
  public ImageType(TokenReference ref, int width, int height) {
    super(ref);
    this.width = width;
    this.height = height;
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
   * getHeight
   */
  public int getHeight() {
    return height;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  public byte getCode() {
    return TYP_IMAGE;
  }

 /**
   * Insert the type of a column in the data dictionary
   */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
    a.addImageType(column, width, height);
  }

  /**
   * equals
   */
  public boolean equals(Object o) {
    if (o instanceof ImageType) {
      return (width == ((ImageType)o).getWidth() &&
	      height == ((ImageType)o).getHeight());
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
    return new DatabaseImageColumn(nullable);
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
    visitor.visitImageType(this, this.width, this.height);
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

  int		width;
  int		height;
}
