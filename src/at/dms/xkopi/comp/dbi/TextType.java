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

import java.sql.SQLException;
import  at.dms.xkopi.comp.database.DatabaseColumn;
import  at.dms.xkopi.comp.database.DatabaseTextColumn;
import  at.dms.kopi.comp.kjc.CStdType;
import  at.dms.kopi.comp.kjc.CType;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.sqlc.BlobType;

public class TextType extends BlobType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	width
   * @param	height
   */
  public TextType(TokenReference ref, int width, int height) {
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
    return TYP_TEXT;
  }

 /**
   * Insert the type of a column in the data dictionary
   */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
    a.addTextType(column, width, height);
  }

  /**
   * equals
   */
  public boolean equals(Object o) {
    if (o instanceof TextType) {
      return (width == ((TextType)o).getWidth() &&
	      height == ((TextType)o).getHeight());
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
    if ((width != -1) && (height != -1))
      new DatabaseTextColumn(nullable, 
                             (width == -1) ? null : new Integer(width), 
                             (height == -1) ? null : new Integer(height));
    return new DatabaseTextColumn(nullable);
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
    visitor.visitTextType(this, this.width, this.height);
  }

  /**
   * Accepts a visitor.
   * This method must be implemented because it is a sqlc/Statement.
   *
   * @param	visitor			the visitor
   */
  public void accept(at.dms.xkopi.comp.sqlc.SqlVisitor visitor) throws PositionedError {
    accept((DbiVisitor)visitor);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  int		width;
  int		height;
}
