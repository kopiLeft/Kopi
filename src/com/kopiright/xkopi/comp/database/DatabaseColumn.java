/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
package com.kopiright.xkopi.comp.database;

import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.xkopi.comp.xkjc.XDatabaseColumn;

/**
 * The type of a field which represents a column in Database.k. Subclasses 
 *  of this class are used for restricted columns. 
 */
public abstract class DatabaseColumn extends DatabaseMember implements XDatabaseColumn{
  
  /**
   * Creates a representation of a type of a column.
   *
   * @param nullable    true if empty entries are allowed
   * @param restricted true if the contraints should be checked
   * (max. length for Strings, min/Max value for integer, ...).
   */
  public DatabaseColumn(boolean isNullable,  boolean restricted) {
    this.isNullable = isNullable;
    this.restricted = restricted;
  }

  /**
   * Checks whether this field represents a column.
   *
   * @return    true iff this field represents a column
   */
  public final boolean isColumn() {
    return true;
  }

  /**
   * Returns true if empty entries are allowed in this column.
   *
   * @return    true iff empty entries allowed
   */
  public final boolean isNullable() {
    return isNullable;
  }

  /**
   * Returns true if the contraints should be checked (max. length for 
   * Strings, min/Max value for integer, ...). 
   *
   * @return    true if the contraints should be checked.
   */
  public final boolean isRestricted() {
    return restricted;
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return    the type
   */
  public CType getStandardType() {
    return getStandardType(isNullable);
  }
  /**
   * Returns the type in Java of this column. Uses parameter 
   * isNullable to choose the right representation.
   *
   * @return    the type
   * @param     isNullable 
   */
  protected abstract CType getStandardType(boolean isNullable);

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return    the expression
   */
  public abstract JExpression getCreationExpression();

  /**
   * Returns true if other (e.g. from formular) is equivalent with the current 
   * column (e.g. from database). 
   *
   * @param     other other column 
   * @param     int check spezifies the check
   */
  public final boolean isEquivalentTo(XDatabaseColumn other, int check) {
    if (! (other instanceof DatabaseColumn)) {
      return true;
    } else {
      return isEquivalentTo((DatabaseColumn) other, check);
    }
  }

  /**
   * Returns true if other (e.g. from formular) is equivalent with the current 
   * column (e.g. from database).
   *
   * @param     other   other column 
   * @param     check   specifies the check
   */
  public abstract boolean isEquivalentTo(DatabaseColumn other, int check);

  /**
   * Verifies only the nullable constraint. 
   *
   * @param     other   other column 
   * @param     check   specifies the check
   * @return    true    if the nullable constraint (defined by param check) is fullfilled.
   */
  protected final boolean verifyNullable(DatabaseColumn other, int check) {
    return (isNullable == other.isNullable
            || (((NULL_CHECK_NARROWING & check) > 0) && (other.isNullable == false))
            || (((NULL_CHECK_WIDENING & check) > 0) && (other.isNullable == true)));
  }  

  public String toString() {
    return getStandardType()
           + ((isNullable())? "(nullable, " : "(not null, ")
           + ((isRestricted())? "restricted)" : "not rest.) ");
  }  

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final boolean         isNullable;   
  private final boolean         restricted;

  public static final CReferenceType TYPE1 = 
    CReferenceType.lookup(XDatabaseColumn.class.getName().replace('.','/'));
}
