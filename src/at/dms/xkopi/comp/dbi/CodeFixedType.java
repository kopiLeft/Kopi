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
 * $Id: CodeFixedType.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import java.util.ArrayList;

import  at.dms.kopi.comp.kjc.CStdType;
import  at.dms.kopi.comp.kjc.CType;
import  at.dms.xkopi.comp.xkjc.XStdType;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.database.*;
import at.dms.xkopi.comp.sqlc.IntegerLiteral;
import at.dms.xkopi.comp.sqlc.NumericType;
import java.sql.SQLException;

public class CodeFixedType extends at.dms.xkopi.comp.sqlc.NumericType implements DbiType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	list
   */
  public CodeFixedType(TokenReference ref,
		       int precision,
		       int scale,
		       ArrayList list) {
    super(ref,
	  new IntegerLiteral(ref, new Integer(precision)),
	  new IntegerLiteral(ref, new Integer(scale)));
    this.list = list;
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
   * getList
   */
  public ArrayList getList() {
    return list;
  }


  /**
   * Returns the type for the type-checking mechanism of dbi. This type
   * is used as type of the field in the Database.k class. 
   *
   * @param nullable 
   * @return the columninfo
   */
  public DatabaseColumn getColumnInfo(boolean nullable) {
    return new DatabaseFixedColumn(nullable);
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


  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitCodeFixedType(this, this.precision, this.scale, this.list);
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

  int				precision;
  int				scale;
  private final ArrayList		list;
}
