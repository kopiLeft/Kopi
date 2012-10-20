/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.lib.base;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.kopiright.util.base.InconsistencyException;

/**
 * A kopi connection maintain information about current context, underlying
 * JDBC connection, driver and user.
 */
public class Connection {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Creates a kopi connection from JDBC Connection
   *
   * @param	ctxt		the database context
   * @param	conn		the JDBC connection
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   */
  public Connection(DBContext ctxt,
                    java.sql.Connection conn,
                    boolean lookupUserId)
    throws SQLException
  {
    this.ctxt = ctxt;
    this.url = conn.getMetaData().getURL();
    this.userName = conn.getMetaData().getUserName();
    this.userID = !lookupUserId ? USERID_NO_LOOKUP : USERID_TO_DETERMINE;
    setDriverInterface();
    this.conn = conn;
    this.conn.setAutoCommit(false);

    setUserID();
  }

  /**
   * Creates a kopi connection from JDBC Connection
   *
   * @param	ctxt		the database context
   * @param	conn		the JDBC connection
   */
  public Connection(DBContext ctxt, java.sql.Connection conn)
    throws SQLException
  {
    this(ctxt, conn, true);
  }

  /**
   * Creates a kopi connection and opens it.
   *
   * @param	ctxt		the database context
   * @param	options		the parameters for the connection
   */
  public Connection(DBContext ctxt, ConnectionOptions options)
    throws DBException
  {
    this.ctxt = ctxt;
    this.url = options.database;
    this.userName = options.username;
    this.pass = options.password;

    this.userID = !options.lookupUserId ? USERID_NO_LOOKUP : USERID_TO_DETERMINE;

    setDriverInterface();

    open();
    if (options.schema != null) {
      setSchema(options.schema);
    }
    setUserID();
  }

  /**
   * Creates a kopi connection and opens it.
   *
   * @param	ctxt		the database context
   * @param	url		the URL of the database to connect to
   * @param	user		the name of the database user
   * @param	pass		the password of the database user
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   * @param     schema          the database schema to set as current schema
   */
  public Connection(DBContext ctxt,
		    String url,
		    String username,
		    String password,
                    boolean lookupUserId,
                    String schema)
    throws DBException
  {
    this.ctxt = ctxt;
    this.url = url;
    this.userName = username;
    this.pass = password;

    this.userID = !lookupUserId ? USERID_NO_LOOKUP : USERID_TO_DETERMINE;

    setDriverInterface();

    open();
    if (schema != null) {
      setSchema(schema);
    }
    setUserID();
  }

  /**
   * Creates a kopi connection from an JDBC connection
   *
   * @param	ctxt		the database context
   * @param	url		the URL of the database to connect to
   * @param	user		the name of the database user
   * @param	pass		the password of the database user
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   */
  public Connection(DBContext ctxt,
		    String url,
		    String username,
		    String password)
    throws DBException
  {
    this(ctxt, url, username, password, true, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public DBContext getDBContext() {
    return ctxt;
  }

  /**
   *
   */
  public String getUserName() {
    return userName;
  }

  /**
   *
   */
  public String getPassword() {
    return pass;
  }

  /**
   *
   */
  public String getURL() {
    return url;
  }

  /**
   *
   */
  public int getUserID() {
    if (userID == USERID_NO_LOOKUP) {
      throw new InconsistencyException("user id must not be queried");
    }
    if (userID == USERID_TO_DETERMINE) {
      throw new InconsistencyException("user id not yet determined");
    }
    return userID;
  }

  // ----------------------------------------------------------------------
  // CONNECTION MANAGEMENT
  // ----------------------------------------------------------------------

  /**
   * Opens the connection to the database.
   */
  public void open() throws DBException {
    try {
      conn = DriverManager.getConnection(url, userName, pass);
      conn.setAutoCommit(false);
    } catch (SQLException e) {
      throw convertException(e);
    }
  }

  /**
   * Closes the connection to the database.
   */
  public void close() throws DBException {
    try {
      conn.rollback(); // !!! what happens if closed by user ?
      conn.close();
      conn = null;
    } catch (SQLException e) {
      throw convertException(e);
    }
  }

  /**
   * Interrupts the current transaction.
   */
  public void interrupt() {
    driver.interrupt(conn);
  }

  /**
   *
   */
  public java.sql.Connection getJDBCConnection() {
    if (!ctxt.isInTransaction()) {
      throw new InconsistencyException(">>>>>>>>>>> SQL STATEMENT OUTSIDE OF ANY TRANSACTION <<<<<<<<<<<<");
    }
    return conn;
  }

  /**
   *
   */
  public void setTransactionIsolation(int isolation) throws SQLException {
    conn.setTransactionIsolation(isolation);
  }

  // ----------------------------------------------------------------------
  // DRIVER SPECIFIC METHODS
  // ----------------------------------------------------------------------

  /**
   * Sets the current database schema.
   *
   * @param     name            the schema name.
   */
  private void setSchema(String name) throws DBException {
    try {
      java.sql.Statement	stmt;

      stmt = conn.createStatement();
      stmt.executeUpdate("SET SCHEMA " + name);
    } catch (SQLException e) {
      throw convertException(e);
    }
  }

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   *
   * @return	the maximum number of characters in an ORDER BY clause
   */
  public int getMaximumCharactersCountInOrderBy() {
    return driver.getMaximumCharactersCountInOrderBy();

  }

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   * NOTE : A function exist for it :
   * java.sql.DatabaseMetaData.getMaximumColumnsInOrderBy()
   *
   * @return	the maximum number of columns in an ORDER BY clause
   */
  public int getMaximumColumnsInOrderBy() {
    return driver.getMaximumColumnsInOrderBy();

  }

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param	user		the login of the user
   */
  public void revokeAccess(String user) throws SQLException {
    driver.revokeAccess(conn, user);
  }

  /**
   * Give an access to an user or create an user
   *
   *
   * @param	user		the login of the user
   * @param     password        the initial password
   */
  public void grantAccess(String user, String password) throws SQLException {
    driver.grantAccess(conn, user, password);
  }

  /**
   * Give an access to an user or create an user
   *
   * @param     user            the username
   * @param     oldPassword     the oldPassword
   * @param     newPassword     the newPassword
   */
  public void changePassword(String user,
                             String oldPassword,
                             String newPassword)
    throws SQLException
  {
    driver.changePassword(conn, user, oldPassword, newPassword);
  }

  /**
   * Locks a table in exclusive mode.
   *
   * @param     tableName       the name of the table to lock
   */
  public void lockTable(String tableName) throws SQLException
  {
    driver.lockTable(conn, tableName);
  }


  /**
   * Transforms an SQLException into its corresponding kopi DBException
   *
   * @param     exc     the SQLException
   * @return    the corresponding kopi DBException
   */
  public DBException convertException(java.sql.SQLException exc) {
    return driver.convertException(exc);
  }

  /**
   * Transforms an SQLException into its corresponding kopi DBException
   *
   * @param     query   the sql query which generated the exception
   * @param     exc     the SQLException
   * @return    the corresponding kopi DBException
   */
  public DBException convertException(String query, java.sql.SQLException exc) {
    return driver.convertException(query, exc);
  }

  /**
   *
   */
  public String convertSql(String sql) throws DBException {
    return driver.convertSql(sql);
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * Retrieves the driver interface for the JDBC driver
   */
  private void setDriverInterface() {
    Driver	d;

    try {
      d = DriverManager.getDriver(url);
    } catch (SQLException exc) {
      System.err.println("can't create connection to " + url + " {" + exc.getMessage() + "}");
      throw new DBRuntimeException("can't create connection to " + url + " {" + exc.getMessage() + "}");
    }

    final String driverName = d.getClass().getName();

    if (driverName.equals("com.kopiright.kconnect.Driver")) {
      driver = new KconnectDriverInterface();
    } else if (driverName.equals("COM.ibm.db2.jdbc.app.DB2Driver")) {
      driver = new Db2UdbDriverInterface();
    } else if (driverName.equals("com.ibm.as400.access.AS400JDBCDriver")) {
      driver = new As400DriverInterface();
    } else if (driverName.equals("com.sap.dbtech.jdbc.DriverSapDB")) {
      driver = new SapdbDriverInterface();
    } else if (driverName.equals("transbase.jdbc.Driver")) {
      driver = new TbxDriverInterface();
    } else if (driverName.equals("com.mysql.jdbc.Driver")) {
      driver = new MysqlDriverInterface();
    } else if (driverName.equals("org.postgresql.Driver")) {
      driver = new PostgresDriverInterface();
    } else if (driverName.equals("ca.edbc.jdbc.EdbcDriver")) {
      driver = new IngresDriverInterface();
    } else {
      throw new InconsistencyException("no appropriate driver found");
    }
  }

  public DriverInterface getDriverInterface() {
    return driver;
  }

  /**
   * Retrieves the user ID of the current user
   */
  private void setUserID() {
    if (userID != USERID_NO_LOOKUP) {
      if (getUserName().equals("root") || getUserName().equals("lgvplus") || getUserName().equals("tbadmin") || getUserName().equals("dba")) {
        userID = USERID_NO_LOOKUP;
      } else {
        try {
          Query	query;

	  ctxt.startWork();	// !!! BEGIN_SYNC

          query = new Query(this);
          query.open("SELECT ID FROM KOPI_USERS WHERE Kurzname = '" + getUserName() + "'");
          if (!query.next()) {
            throw new SQLException("user unknown");
          }
          this.userID = query.getInt(1);
          if (query.next()) {
            throw new SQLException("different users with same name");
          }
          query.close();

          ctxt.commitWork();
          // manually commit ourself since we are not in the connection list of the context
          conn.commit();
        } catch (java.sql.SQLException e) {
          throw new InconsistencyException(e.getMessage());
        }
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

        // -1 ... not yet determined
  private static final int              USERID_TO_DETERMINE = -1;
        // -2 ... do not lookup user ID
  private static final int              USERID_NO_LOOKUP = -2;

  private DriverInterface		driver;
  private java.sql.Connection		conn;
  private DBContext			ctxt;
  private String			url;
  private String			userName;
  private int                           userID;
  private String			pass;
}
