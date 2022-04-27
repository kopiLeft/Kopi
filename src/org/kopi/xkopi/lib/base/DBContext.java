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

import org.kopi.util.base.InconsistencyException;

public class DBContext {

  /**
   * Registers a JDBC driver.
   *
   * @param     name            The class name of the JDBC driver to register.
   */
  public static void registerDriver(String name) {
    try {
      Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new DBRuntimeException("can't access driver " + name);
    }
  }

  /**
   * Connects to database and logs on.
   *
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     pass            the password of the database user
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   * @param     schema          the current database schema
   * @return    a new kopi connection
   */
  public Connection createConnection(String url,
                                     String user,
                                     String pass,
                                     boolean lookupUserId,
                                     String schema)
          throws SQLException
  {
    Connection  conn;

    conn = new Connection(this, url, user, pass, lookupUserId, schema);
    setConnection(conn);

    return conn;
  }

  public Connection createConnection(java.sql.Connection connexion,
                                     boolean lookupUserId,
                                     String schema)
          throws SQLException
  {
    Connection  conn;

    conn = new Connection(this, connexion, lookupUserId, schema);
    setConnection(conn);

    return conn;
  }

  /**
   * Connects to database and logs on.
   *
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     pass            the password of the database user
   * @return    a new kopi connection
   */
  public Connection createConnection(String url, String user, String pass)
          throws SQLException
  {
    return createConnection(url, user, pass, true, null);
  }

  /**
   * Sets the underlying JDBC connection.
   *
   * @throws SQLException when creating more than one connection.
   */
  private void setConnection(Connection con) throws SQLException {
    if (connection == null) {
      connection = con;
    } else if (connection != con) {
      throw new SQLException("Too many database connections");
    }
  }

  /**
   * Closes the underlying JDBC connections.
   */
  public void close() throws DBException {
    connection.close();
    connection = null;
  }

  /**
   * Closes the underlying JDBC connections.
   */
  public void close(Connection conn) throws DBException {
    conn.close();
    connection = null;
  }

  /**
   * Returns the underlying default JDBC connection.
   *
   * @deprecated use the method <code>getConnection()</code> instead
   */
  public Connection getDefaultConnection() {
    return connection;
  }

  /**
   * Returns the underlying default JDBC connection.
   */
  public Connection getConnection() {
    return connection;
  }

  /**
   * sets the underlying default JDBC connection.
   *
   * @deprecated do not use this method as <code>createConnection()</code> sets the connection.
   */
  public void setDefaultConnection(Connection con) {}

  /**
   * Starts a new transaction.
   * Note: JDBC opens transactions implicitly
   */
  public final void startWork() {
    while (true) {
      synchronized(this) {
        if (inTransaction) {
          try {
            wait();
          } catch (InterruptedException e) {
            throw new InconsistencyException();
          }
        } else {
          inTransaction = true;
          return;
        }
      }
    }
  }

  /**
   * Commits the current transaction.
   */
  public final void commitWork() throws SQLException {
    synchronized(this) {
      inTransaction = true; /* In case we are doing a commit wo start work */

      try {
        connection.commit();
      } catch (SQLException exc) {
        throw connection.convertException(exc);
      }
      inTransaction = false;
      notify();
    }
  }

  /**
   * Aborts the current transaction.
   */
  public final void abortWork() throws SQLException {
    synchronized(this) {
      inTransaction = true; /* In case we are doing an abort wo start work */

      try {
        connection.rollback();
      } catch (SQLException exc) {
        //throw conn.convertException(exc); (if the error has already closed the transaction)
      }
      inTransaction = false;
      notify();
    }
  }

  /**
   * Returns if there is a transaction started
   */
  public boolean isInTransaction() {
    return inTransaction;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private Connection    connection;
  private boolean       inTransaction;

  // ----------------------------------------------------------------------
  // INITIALIZER
  // ----------------------------------------------------------------------

  {
    inTransaction = false;
  }

}
