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

/**
 * This class encapsulates specific JDBC driver properties.
 */
public class KconnectDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public KconnectDriverInterface() {
    super();
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
   * Give an access to an user or create an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public void grantAccess(Connection conn, String user) throws SQLException {
    // !!! coco 050102 : verify the syntax
    executeSQL(conn, "GRANT ACCESS TO '" + user + "'");
  }

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public void revokeAccess(Connection conn, String user) throws SQLException {
    // !!! coco 050102 : verify the syntax
    executeSQL(conn, "REVOKE ACCESS FROM '" + user + "'");
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
    // !!! laurent 20020801 : verify the syntax
    executeSQL(conn, "ALTER PASSWORD FROM '" + oldPassword + "' TO '" + newPassword + "'");
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
   * @param	from		the SQLException
   * @return	the corresponding kopi DBException
   */
  public DBException convertException(SQLException from) {
    // If it's an Oracle error message.
    if (from.getMessage().indexOf(": ORA-") != -1) {
      switch (from.getErrorCode()) {
      case 60:          // ORA-00060
      case 104:         // ORA-00104
      case 4020:        // ORA-04020
        return new DBDeadLockException(from);

      case 1:           // ORA-00001
      case 2268:        // ORA-02268
      case 2270:        // ORA-02270
      case 2272:        // ORA-02272
      case 2273:        // ORA-02273
      case 2274:        // ORA-02274
      case 2275:        // ORA-02275
        return new DBConstraintException(from);

      default:
        return new DBUnspecifiedException(from);
      }
    }

    switch (from.getErrorCode()) {
    case 5:	//FOREIGN_KEY_VIOLATION
      return parseForeignKeyViolation(from);

    case 1:     // ABORTSIGNAL :asynchronous abort -> handle like deadlock
    case 2414:	// LM_DB_LOCKED
    case 2416:	// LOCKINFO_FAULT
    case 2418:	// HARD_LOCKS_NOT_GRANTABLE
    case 2420:	// HARD_LOCK_TIMEOUT
    case 11004: // CT_TROUBLE
    case 16002:	// LOCKS_NOT_GRANTABLE
    case 16003:	// LOCK_TIMEOUT
    case 16023:	// COMMIT_TIMEOUT
      return new DBDeadLockException(from);

    case 9147:	// DUPLIC_IN_INDEX
      return parseDuplicateIndex(from);

    case 9146:	// INTEGRITY_VIOL2
      return parseIntegrityViolation(from);

    case 9094:	// INTEGRITY_VIOL1
    case 9148:	// NULL_ERROR
    case 9149:	// NULL_ERROR_POS
    case 9150:	// NULL_ERROR_VIEW
    case 9151:	// REL_TUP_INS
      return new DBConstraintException(from);

    default:
      return new DBUnspecifiedException(from);
    }
  }

  /**
   * Interrupts the current transaction. Does nothing if this operation
   * is not supported by the underlying driver.
   *
   * @param     connection      the JDBC connection
   */
  public void interrupt(Connection connection) {
    try {
      ((at.dms.kconnect.tb.Connection)connection).sendInterrupt();
    } catch (SQLException e) {
      // ignore it
    }
  }


  // ----------------------------------------------------------------------
  // PRIVATE UTILITY METHODS
  // ----------------------------------------------------------------------

  /**
   * Parses a foreign key violation exception
   */
  private static DBForeignKeyException parseForeignKeyViolation(SQLException from) {
    String	mesg = from.getMessage();
    int		index1 = mesg.indexOf("'");
    int		index2 = mesg.lastIndexOf("'");

    return new DBForeignKeyException(from, mesg.substring(index1 + 1, index2));
  }

  /**
   * Parses a transbase duplicate index exception
   */
  private static DBDuplicateIndexException parseDuplicateIndex(SQLException from) {
    String	mesg = from.getMessage();
    int		index = mesg.lastIndexOf("index ");

    return new DBDuplicateIndexException(from, mesg.substring(index + 6, mesg.length() - 2));
  }

  /**
   * Parses a transbase integrity violation exception
   */
  private static DBForeignKeyException parseIntegrityViolation(SQLException from) {
    String	mesg = from.getMessage();
    int		index1 = mesg.indexOf("'");
    int		index2 = mesg.lastIndexOf("'");

    return new DBForeignKeyException(from, mesg.substring(index1 + 1, index2));
  }
}
