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

public class DBForeignKeyException extends DBConstraintException {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------

  /**
   * Constructor
   *
   * @param	original		the original SQLException
   * @param	constraintName		the violated constraint
   */
  public DBForeignKeyException(SQLException original, String constraint) {
    super(original, constraint);
  }
  
  /**
   * Constructor
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   * @param     constraintName          the violated constraint
   */
  public DBForeignKeyException(String query, SQLException original, String constraint) {
    super(query, original, constraint);
  }

  /**
   * Creates a new <code>DBForeignKeyException</code> that indicates the tables in relation.
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   * @param     constraintName          the violated constraint
   * @param     referenced              the referenced table
   * @param     referencing             the referencing table
   */
  public DBForeignKeyException(String query,
                               SQLException original,
                               String constraint,
                               String referenced,
                               String referencing)
  {
    super(query, original, constraint);
    this.referenced = referenced;
    this.referencing = referencing;
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  /**
   * Returns the referenced table in this FK exception.
   */
  public String getReferencedTable() {
    return referenced;
  }

  /**
   * Returns the referencing table in this FK exception.
   */
  public String getReferencingTable() {
    return referencing;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private String                        referenced; // The referenced table
  private String                        referencing; // The referencing table

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 7172366406602713556L;
}
