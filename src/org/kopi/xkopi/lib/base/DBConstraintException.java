/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
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

package org.kopi.xkopi.lib.base;

import java.sql.SQLException;

public class DBConstraintException extends DBException {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  
/**
   * Constructor
   *
   * @param	original		the original SQLException
   * @param	constraintName		the violated constraint
   */
  public DBConstraintException(SQLException original) {
    this(null, original, "unspecified");
  }

  /**
   * Constructor
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   * @param     constraintName          the violated constraint
   */
  public DBConstraintException(String query, SQLException original) {
    this(query, original, "unspecified");
  }

  /**
   * Constructor
   *
   * @param	original		the original SQLException
   * @param	constraintName		the violated constraint
   */
  public DBConstraintException(SQLException original, String constraintName) {
    this(null, original, constraintName);
  }

  /**
   * Constructor
   *
   * @param     query                   the sql query which generated the exception
   * @param	original		the original SQLException
   * @param	constraintName		the violated constraint
   */
  public DBConstraintException(String query, SQLException original, String constraintName) {
    super(query, original);
    this.constraintName = constraintName;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the index name
   */
  public String getConstraint() {
    return constraintName;
  }

  /**
   * Returns the index name
   */
  public String getDescription() {
    return "DBConstraintException: '" + constraintName + "'";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String constraintName;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 3729998951971315389L;
}
