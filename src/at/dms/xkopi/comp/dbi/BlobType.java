/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: BlobType.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import java.sql.SQLException;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.database.*;
import at.dms.kopi.comp.kjc.CType;

public class BlobType extends at.dms.xkopi.comp.sqlc.BlobType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   */
  public BlobType(TokenReference ref) {
    super(ref);
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  public byte getCode() {
    return TYP_BLOB;
  }

 /**
   * Insert the type of a column in the data dictionary
   */
  public void makeDBSchema(DBAccess a, int column) throws SQLException {
  }

  /**
   * equals
   */
//  public boolean equals(Object o) {
//    return o instanceof BlobType;
//  }

  /**
   * Returns the type for the type-checking mechanism of dbi. This type
   * is used as type of the field in the Database.k class. 
   *
   * @param nullable
   * @return the type
   */
  public DatabaseColumn getColumnInfo(boolean nullable) {
    return new DatabaseBlobColumn(nullable);
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
    visitor.visitBlobType(this);
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
}
