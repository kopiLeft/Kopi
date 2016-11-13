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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class encapsulates specific JDBC driver properties.
 */
public class KconnectDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public KconnectDriverInterface(String url) {
    super();
    if (url.startsWith("jdbc:kconnect:ora:")) {
      this.database = "ora";
    } else if (url.startsWith("jdbc:kconnect:tb:")) {
      this.database = "tb";
    } else {
      throw new RuntimeException("Unsupported url: " + url);
    }
  }

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   *
   * @return	the maximum number of characters in the ORDER BY of the driver
   */
  public int getMaximumCharactersCountInOrderBy() {
      return Integer.MAX_VALUE;
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
    return Integer.MAX_VALUE;
  }

  /**
   * Returns the name of the system table DUAL.
   *
   * @return    the name of the system table DUAL.
   */
  public String getDualTableName() {
    // TransBase has no predefined DUAL table - kopi adds a table named DUMMY.
    return "DUMMY";
  }

  /**
   * Give an access to an user or create an user
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   * @param     password        the initial password
   */
  public void grantAccess(Connection conn, String user, String password) throws SQLException {
    executeSQL(conn,
               "GRANT CONNECT TO " + user + " IDENTIFIED BY \"" + password + "\"", // Oracle
               "GRANT ACCESS TO '" + user + "'"); // Transbase
  }

  /**
   * Removed an access to an user or delete an user
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public void revokeAccess(Connection conn, String user) throws SQLException {
     executeSQL(conn,
                "DROP USER " + user, // Oracle
                "REVOKE ACCESS FROM '" + user + "'"); // Transbase
  }

  /**
   * Change the password of the current user
   */
  public void changePassword(Connection conn,
                             String user,
                             String oldPassword,
                             String newPassword)
    throws SQLException
  {
    executeSQL(conn,
               "ALTER USER " + user + " IDENTIFIED BY \"" + newPassword + "\"", // Oracle
               "ALTER PASSWORD FROM '" + oldPassword + "' TO '" + newPassword + "'"); // Transbase
  }

  /**
   * Locks a table in exclusive mode.
   *
   * @param	conn		the connection
   * @param     tableName       the name of the table to lock
   */
  public void lockTable(Connection conn, String tableName)
    throws SQLException
  {
    executeSQL(conn, "LOCK " + tableName + " EXCLUSIVE");
  }

  /**
   * Transforms a SQL query string from KOPI syntax to the syntax
   * accepted by the corresponding driver
   *
   * @param	from		the SQL query string in KOPI syntax
   * @return	the string in the syntax of the driver
   */
  public String convertSql(String from) {
    // already in a form accepted by the kconnect driver
    return from;
  }

  /**
   * Transforms an SQLException into its corresponding kopi DBException
   *
   * @param     query   the sql query which generated the exception
   * @param	from		the SQLException
   * @return	the corresponding kopi DBException
   */
  public DBException convertException(String query, SQLException from) {
    // If it's an Oracle error message.
    if (from.getMessage().indexOf(": ORA-") != -1) {
      switch (from.getErrorCode()) {
      case 60:          // ORA-00060
      case 104:         // ORA-00104
      case 4020:        // ORA-04020
        return new DBDeadLockException(query, from);

      case 1:           // ORA-00001
      case 2268:        // ORA-02268
      case 2270:        // ORA-02270
      case 2272:        // ORA-02272
      case 2273:        // ORA-02273
      case 2274:        // ORA-02274
      case 2275:        // ORA-02275
        return new DBConstraintException(query, from);

      default:
        return new DBUnspecifiedException(query, from);
      }
    }

    switch (from.getErrorCode()) {
    case 5:	//FOREIGN_KEY_VIOLATION
      return parseForeignKeyViolation(query, from);

    case 1:     // ABORTSIGNAL :asynchronous abort -> handle like deadlock
    case 2414:	// LM_DB_LOCKED
    case 2416:	// LOCKINFO_FAULT
    case 2418:	// HARD_LOCKS_NOT_GRANTABLE
    case 2420:	// HARD_LOCK_TIMEOUT
    case 11004: // CT_TROUBLE
    case 16002:	// LOCKS_NOT_GRANTABLE
    case 16003:	// LOCK_TIMEOUT
    case 16023:	// COMMIT_TIMEOUT
      return new DBDeadLockException(query, from);

    case 9147:	// DUPLIC_IN_INDEX
      return parseDuplicateIndex(query, from);

    case 9146:	// INTEGRITY_VIOL2
      return parseIntegrityViolation(query, from);

    case 9094:	// INTEGRITY_VIOL1
    case 9148:	// NULL_ERROR
    case 9149:	// NULL_ERROR_POS
    case 9150:	// NULL_ERROR_VIEW
    case 9151:	// REL_TUP_INS
      return new DBConstraintException(query, from);

    default:
      return new DBUnspecifiedException(query, from);
    }
  }

  /**
   * Interrupts the current transaction. Does nothing if this operation
   * is not supported by the underlying driver.
   *
   * @param     connection      the JDBC connection
   */
  public void interrupt(Connection connection) {
    // graf 20130813: we do not support this method anymore
    //try {
    //  ((org.kopi.kconnect.tb.Connection)connection).sendInterrupt();
    //} catch (SQLException e) {
    //  // ignore it
    //}
  }

  /**
   * Checks which outer join syntax (JDBC or Oracle) should be used.
   *
   * @return    true iff Oracle outer join syntax should be used.
   */
  public boolean useOracleOuterJoinSyntax() {
    return database.equals("ora");
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITY METHODS
  // ----------------------------------------------------------------------

  private void executeSQL(Connection conn, String oraSQL, String tbSQL)
    throws SQLException
  {
    if (database.equals("ora")) {
      executeSQL(conn, oraSQL);
    } else if (database.equals("tb")) {
      executeSQL(conn, tbSQL);
    } else {
      throw new RuntimeException("Unsupported connection class: " + conn.getClass());
    }
  }


  /**
   * Parses a foreign key violation exception
   */
  // method never used locally 
  /*
  private static DBForeignKeyException parseForeignKeyViolation(SQLException from) {
    return parseForeignKeyViolation(null, from);
  }
   */
  /**
   * Parses a foreign key violation exception
   */
  private static DBForeignKeyException parseForeignKeyViolation(String query, SQLException from) {
    String	mesg = from.getMessage();
    int		index1 = mesg.indexOf("'");
    int		index2 = mesg.lastIndexOf("'");

    return new DBForeignKeyException(query, from, mesg.substring(index1 + 1, index2));
  }

  /**
   * Parses a transbase duplicate index exception
   */
  // method not used locally
  /*
  private static DBDuplicateIndexException parseDuplicateIndex(SQLException from) {
    return parseDuplicateIndex(null, from);
  }
  */
  /**
   * Parses a transbase duplicate index exception
   */
  private static DBDuplicateIndexException parseDuplicateIndex(String query, SQLException from) {
    String	mesg = from.getMessage();
    int		index = mesg.lastIndexOf("index ");

    return new DBDuplicateIndexException(query, from, mesg.substring(index + 6, mesg.length() - 2));
  }

  /**
   * Parses a transbase integrity violation exception
   */
  // method not used locally
  /*
  private static DBForeignKeyException parseIntegrityViolation(SQLException from) {
    return parseIntegrityViolation(null, from);
  }
  */
  /**
   * Parses a transbase integrity violation exception
   */
  private static DBForeignKeyException parseIntegrityViolation(String query, SQLException from) {
    String	mesg = from.getMessage();
    int		index1 = mesg.indexOf("'");
    int		index2 = mesg.lastIndexOf("'");

    return new DBForeignKeyException(query, from, mesg.substring(index1 + 1, index2));
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String          database;
}
