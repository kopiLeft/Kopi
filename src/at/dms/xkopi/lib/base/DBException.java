/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package at.dms.xkopi.lib.base;

import java.sql.SQLException;

public abstract class DBException extends java.sql.SQLException {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param	original		the original SQLException
   */
  public DBException(SQLException original) {
    super(original.getMessage(), original.getSQLState(), original.getErrorCode());
    this.original = original;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get the vendor specific exception code
   *
   * @return	the vendor's error code
   */
  public int getErrorCode() {
    return original.getErrorCode();
  }

  /**
   * Get the SQLState
   *
   * @return	the SQLState value
   */
  public String getSQLState() {
    return original.getSQLState();
  }

  /**
   * Get the original SQLException
   *
   * @return	the original SQLException
   */
  public SQLException getSQLException() {
    return original;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	SQLException	original;
}
