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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

import at.dms.xkopi.lib.base.DBContext;

/**
 * This class encapsulates specific JDBC driver properties.
 */
public abstract class DriverInterface {

  /**
   * Constructs a new driver interface object.
   */
  public DriverInterface() {
  }

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   *
   * @return	the maximum number of characters in an ORDER BY clause
   */
  public abstract int getMaximumCharactersCountInOrderBy();

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   * NOTE : A function exist for it :
   * java.sql.DatabaseMetaData.getMaximumColumnsInOrderBy()
   *
   * @return	the maximum number of columns in an ORDER BY clause
   */
  public abstract int getMaximumColumnsInOrderBy();

  /**
   * Give an access to an user or create an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public abstract void grantAccess(Connection conn, String user)
    throws SQLException;

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public abstract void revokeAccess(Connection conn, String user)
    throws SQLException;

  /**
   * Change the password of the current user
   */
  public abstract void changePassword(Connection conn,
                                      String user,
                                      String oldPassword,
                                      String newPassword)
    throws SQLException;

  /**
   * Locks a table in exclusive mode.
   *
   * @param	conn		the connection
   * @param     tableName       the name of the table to lock
   */
  public abstract void lockTable(Connection conn, String tableName)
    throws SQLException;

  /**
   * Transforms a SQL query string from KOPI syntax to the syntax
   * accepted by the corresponding driver.
   *
   * @param	from		the SQL query string in KOPI syntax
   * @return	the string in the syntax of the driver
   */
  public abstract String convertSql(String from) throws DBException;

  /**
   * Transforms an SQLException into its corresponding kopi DBException.
   *
   * @param	from		the SQLException
   * @return	the corresponding kopi DBException
   */
  public abstract DBException convertException(SQLException from);

  /**
   * Interrupts the current transaction. Does nothing if this operation
   * is not supported by the underlying driver.
   */
  public abstract void interrupt(Connection connection);

  // ----------------------------------------------------------------------
  // PROTECTED UTILITIES
  // ----------------------------------------------------------------------

  protected final void executeSQL(Connection conn, String sql)
    throws SQLException
  {
    Statement	stmt;

    stmt = conn.createStatement();
    stmt.executeUpdate(sql);
    stmt.close();
  }


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

}
