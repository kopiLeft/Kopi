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
 * $Id: Column.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.sqlc.Type;
import at.dms.xkopi.comp.sqlc.Expression;

/**
 * This class represents a column
 */
public class Column extends DbiPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public Column(TokenReference ref,
                String ident,
                Type type,
                boolean nullable,
                Expression defaultValue,
                String constraintName)
  {
    super(ref);
    this.ident = ident;
    this.type = type;
    this.nullable = nullable;
    this.defaultValue = defaultValue;
    this.constraintName = constraintName;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * getIdent
   */
  public String getIdent() {
    return ident;
  }

  /**
   * getType
   */
  public Type getType() {
    return type;
  }

  /**
   * isNullable
   */
  public boolean isNullable() {
    return nullable;
  }

  /**
   * getconstraintName
   */
  public String getConstraintName() {
    return constraintName;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  public boolean compareTo(Column otherColumn) {
    boolean equal = ident.equals(otherColumn.getIdent());

    equal &= type.equals(otherColumn.getType());
    equal &= nullable == otherColumn.isNullable();
    equal &= ((constraintName != null) ? constraintName.equals(otherColumn.getConstraintName()) :  true);

    if (equal) {
      return true;
    } else {
      DbCheck.addError(new PositionedError(getTokenReference(),
                                           DbiMessages.TYPE_NOT_EQUALS,
                                           type, otherColumn.getType()));
      return false;
    }
  }

  // ----------------------------------------------------------------------
  // GENERATE INSERT INTO THE DICTIONARY
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   */
  public void makeDBSchema(DBAccess a, int table, byte position, List keys)
    throws SQLException {
    byte	key = 1;
    boolean	isKey = false;

    //if (type instanceof KeyType) {
    //  key = (byte)((((KeyType)type).getPlace()).intValue());
    //}

    ListIterator        iterator = keys.listIterator();
    while (iterator.hasNext()) {
      if (ident.equals(iterator.next())) {
	isKey = true;
      } else {
	key++;
      }
    }
    if (!isKey) {
      key = -1;
    }

    byte	codeType = -1;

    if (type instanceof DbiType) {
      codeType = ((DbiType)type).getCode();
    }

    int column = a.addColumn(table, ident, nullable, codeType, key, position);

    if (type instanceof DbiType) {
      ((DbiType)type).makeDBSchema(a, column);
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
    visitor.visitColumn(this,
                        ident,
                        type,
                        nullable,
                        defaultValue,
                        constraintName,
                        this.getComment());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected String	ident;
  protected boolean	nullable;
  protected Type	type;
  protected Expression	defaultValue;
  protected String	constraintName;
}
