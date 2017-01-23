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
import java.util.Hashtable;
import java.util.Vector;

import org.kopi.util.base.InconsistencyException;

/**
 * This class encapsulates specific properties of the SAPDB JDBC driver.
 */
public class IngresDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public IngresDriverInterface() {
    super();
  }

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   *
   * @return	the maximum number of characters in the ORDER BY of the driver
   */
  public int getMaximumCharactersCountInOrderBy() {
    return 250;
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
    return 16;
  }

  /**
   * Returns the name of the system table DUAL.
   *
   * @return    the name of the system table DUAL.
   */
  public String getDualTableName() {
    throw new InconsistencyException("not implemented");
  }

  /**
   * Give an access to an user or create an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   * @param     password        the initial password
   */
  public void grantAccess(Connection conn, String user, String password) throws SQLException {
    executeSQL(conn, "CREATE USER " + user + " WITH PASSWORD='" + password + "'");
  }

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public void revokeAccess(Connection conn, String user) throws SQLException {
    executeSQL(conn, "DROP USER " + user);
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
    executeSQL(conn, "ALTER USER " + user + "WITH OLDPASSWORD = '"+oldPassword+", PASSWORD ='" + newPassword + ")");
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
    executeSQL(conn, "SET LOCKMODE ON " + tableName + "WHERE LEVEL = TABLE,READLOCK = EXCLUSIVE");
  }

  /**
   * Transforms a SQL query string from KOPI syntax to the syntax
   * accepted by the corresponding driver
   *
   * @param	from		the SQL query string in KOPI syntax
   * @return	the string in the syntax of the driver
   */
  public String convertSql(String from) {
    try {
      IngresParser	parser = new IngresParser(from);

      System.err.println("-- NATIVE SQL: ");
      System.err.println(parser.getText() + ";");
      System.err.println("--");

      return parser.getText().trim();
    } catch (SQLException e) {
      throw new DBRuntimeException(e.getMessage());
    }
  }
  /**
   * Transforms an SQLException into its corresponding kopi DBException
   *
   * @param     query           the sql query which generated the exception
   * @param     from            the SQLException
   * @return    the corresponding kopi DBException
   */
  public DBException convertException(String query, SQLException from) {
    switch (from.getErrorCode()) {
    case -50:	// Lock request timeout1
    case -51:	// Lock request timeout2
    case 500:	// Lock request timeout3
    case -307:	// Connection down, session released
    case 700:	// Session inactivity timeout (work rolled back)
      return new DBDeadLockException(query, from);

    case 250:	// Duplicate secondary key
    case -6008:	// Duplicate index name
    case -8018:	// Index name must be unique
      return parseDuplicateIndex(query, from);

    case -32:	// Integrity violation
      return parseIntegrityViolation(query, from);

    case -1402:	// Integrity violation2
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
    // operation supported by Ingres using Linux interuption ctrl+c
  }

  /**
   * Checks which outer join syntax (JDBC or Oracle) should be used.
   *
   * @return    true iff Oracle outer join syntax should be used.
   */
  public boolean useOracleOuterJoinSyntax() {
    return false;
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES METHODS
  // ----------------------------------------------------------------------

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
  private static DBForeignKeyException parseIntegrityViolation(String query, SQLException from) {
    String	mesg = from.getMessage();
    int		index1 = mesg.indexOf("'");
    int		index2 = mesg.lastIndexOf("'");

    return new DBForeignKeyException(query, from, mesg.substring(index1 + 1, index2));
  }
}


/**
 * This class translates SQL queries from JDBC format to native format.
 */
/*package*/ class IngresParser extends JdbcParser {
  /**
   * Constructor
   */
  public IngresParser(String input) {
    super(input);
  }

  /**
   * Parses WHERE CURRENT OF (and WHERE ...)
   */
  protected String parseWhereCurrent() throws SQLException {
    String      result;

    result = super.parseWhereCurrent();
    if (result != null) {
      result += " OF \"" + getCursor() + "\"";
    }
    return result;
  }

  /**
   * Parses FOR UPDATE
   */
  protected String parseForUpdate() throws SQLException {
    String      result;

    result = super.parseForUpdate();
    if (result != null) {
      result += " OF ";
    }
    return result;
  }

  /**
   * Extract the data of the date
   * @param	type	type of data to extract ('YY', 'MO', ...)
   * @param	date	date
   * @return	a string representing the data extracted
   *
   * !!! fix problem with ns (ms and leading 0s)
   */
  public String extract(String type, Object date) {
    String result = null;

    if (type.equals("YY")) {
      result = "YEAR (";
    } else if (type.equals("MO")) {
      result = "MONTH (";
    } else if (type.equals("DD")) {
      result = "DAY (";
    } else if (type.equals("HH")) {
      result = "HOUR (";
    } else if (type.equals("MI")) {
      result = "MINUTE (";
    } else if (type.equals("SS")) {
      result = "SECOND (";
    } else {
      throw new IllegalArgumentException(type + " is not a good type of data to extract");
    }

    return result + date + ")";
  }

  /**
   * Converts a function call in JDBC syntax to native syntax
   *
   * @param	function	the name of the function
   * @param	arguments	the arguments to the function
   * @return	a string representing the function call in native syntax
   *		or null no specific translation is defined for the function
   */
  public String convertFunctionCall(String functor, Vector arguments)
    throws SQLException
  {
    Object	function = functions.get(functor.toUpperCase() + "/" + arguments.size());

    if (function == null) {
      return null;
    } else {
      // ugly, but more efficient than creating a command class for each functor

      switch (((Integer)function).intValue()) {
      case 0:	// function has same syntax in native SQL
	{
	  StringBuffer		buffer = new StringBuffer();

	  buffer.append(functor.toUpperCase());
	  if (arguments.size() != 0) {
	    buffer.append("(");
	    for (int i = 0; i < arguments.size(); i++) {
	      if (i != 0) {
		buffer.append(", ");
	      }
	      buffer.append(arguments.elementAt(i));
	    }
	    buffer.append(")");
	  }

	  return buffer.toString();
	}
        // wael 20080804 : to be verified
      case 1:	// TOMONTH/1
	return "INT4(100 * DATE_PART('YEAR', (" +  arguments.elementAt(0) + ")) + DATE_PART('MONTH', (" + arguments.elementAt(0) + ")))";
      case 2:	// ADD_DAYS/2
	return "(DATE(" +  arguments.elementAt(0) + ") + '"+ arguments.elementAt(1) + " days')";
        // wael 20080804 : to be verified
      case 4:	// MONTH/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";
      case 5:	// EXTRACT/2
	{
	  String	arg1 = (String)arguments.elementAt(1);

	  if (arg1.length() != 4 || arg1.charAt(0) != '\'' || arg1.charAt(3) != '\'') {
	    throw new SQLException("invalid argument to EXTRACT/2: " + arg1);
	  }

	  return "(" + extract(arg1.substring(1, 3), arguments.elementAt(0)) + ")";
	}
      case 7:	// POSITION/2
	return "LOCATE(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";
      case 8:	// SUBSTRING/2
	return "SUBSTRING(" + arguments.elementAt(0) + " FROM " + arguments.elementAt(1) + ")";
      case 9:	// SUBSTRING/3
	return "SUBSTRING(" + arguments.elementAt(0) + " FROM " + arguments.elementAt(1)  + " FOR " + arguments.elementAt(2) + ")";
      case 10:	// SIZE_OF/1
	return "LENGTH(" + arguments.elementAt(0) + ")";
      case 11:	// UPPER/1
	return "UPPER(" + arguments.elementAt(0) + ")";
      case 12:	// LOWER/1
	return "LOWER(" + arguments.elementAt(0) + ")";
      case 13:	// LTRIM/1
        return "TRIM(LEADING FROM "  + arguments.elementAt(0) + ")";
      case 14:	// RTRIM/1
        // !!! wael 20080724: not working,
        return "TRIM(TRAILING FROM "  + arguments.elementAt(0) + ")";
      case 15:	// REPLACE/3  
	return "REPLACE(" + arguments.elementAt(2) + "," + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";
        // wael 20080804 : to be verified, this function is not used in apps
      case 16:	// REPEAT/2
	return "LFILL('','" + arguments.elementAt(0) + "'," + arguments.elementAt(1) + ")";
      case 17:	// CURRENTDATE
	return "DATE(CURRENT_DATE)";
      case 18:	// USER
	return "USER";
        // wael 20080804 : to be verified, this function is not used in apps
      case 19:	// SPACE
	return "LFILL('',' '," + arguments.elementAt(1) + ")";
        // wael 20080804 : to be verified, this function is not used in apps
      case 20:	// RIGHT
	return "RFILL(" + arguments.elementAt(0) + ",' '," + arguments.elementAt(1) + ")";
        // wael 20080804 : to be verified, this function is not used in apps
      case 21: // LEFT
        return "LFILL(" + arguments.elementAt(0) + ",' '," + arguments.elementAt(1) + ")";
      case 22: // CONCAT
	return arguments.elementAt(0) + " || " + arguments.elementAt(1);
      case 23: // LOCATE
        return "LOCATE(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";
      case 24:	// TRUE
	return "1";
      case 25:	// FALSE
	return "0";
      case 26: // LENGTH
	return "LENGTH(" + arguments.elementAt(0) + ")";
      case 27:	// CURRENTTIME
	return "DATE(CURRENT_TIME)";
      case 28:	// CURRENTTIMESTAMP
	return "DATE(CURRENT_TIMESTAMP)";
        // wael 20080804 : to be verified, this function is not used in apps
      case 29:	// WEEK/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";
        // wael 20080804 : to be verified
      case 30: // DATEDIFF/2
        {
          String        left = (String)arguments.elementAt(0);
          String        right = (String)arguments.elementAt(1);
          return "CHAR(INTERVAL('DAYS', DATE(" + (String)arguments.elementAt(0) + ") - " + "DATE(" + (String)arguments.elementAt(1) + ")))";  
        }
      case 31: // COALESCE/2
	return "IFNULL(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
      case 33: // STRING2INT/1
        return "INT4(" + arguments.elementAt(0) + ")";
      case 37: // NEXTVAL/1
        return  arguments.elementAt(0) + ".NEXTVAL";
      case 38: // TO_CHAR/1
	return "CHAR(" + arguments.elementAt(0) + ")";
      case 39: // TO_DATE/1
	return "DATE(" + arguments.elementAt(0) + ")";
      case 40: // TRUNC/2
	return "DATE_TRUNC(" + arguments.elementAt(1) + ", " + arguments.elementAt(0) + ")";
      default:
	throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR " + functor.toUpperCase() +
                                         "/" + arguments.size());
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static Hashtable	functions;

  static {
    functions = new Hashtable();

    functions.put("TOMONTH/1", new Integer(1));
    functions.put("ADD_DAYS/2", new Integer(2));
    //unused
    functions.put("MONTH/2", new Integer(4));
    functions.put("EXTRACT/2", new Integer(5));
    //unused
    functions.put("POSITION/2", new Integer(7));
    functions.put("SUBSTRING/2", new Integer(8));
    functions.put("SUBSTRING/3", new Integer(9));
    functions.put("SIZE_OF/1", new Integer(10));
    functions.put("UPPER/1", new Integer(11));
    functions.put("LOWER/1", new Integer(12));
    functions.put("LTRIM/1", new Integer(13));
    functions.put("RTRIM/1", new Integer(14));
    functions.put("REPLACE/3", new Integer(15));
    functions.put("REPEAT/2", new Integer(16));
    functions.put("CURRENTDATE/0", new Integer(17));
    functions.put("USER/0", new Integer(18));
    functions.put("SPACE/1", new Integer(19));
    functions.put("RIGHT/2", new Integer(20));
    functions.put("LEFT/2", new Integer(21));
    functions.put("CONCAT/2", new Integer(22));
    functions.put("LOCATE/2", new Integer(23));
    functions.put("TRUE/0", new Integer(24));
    functions.put("FALSE/0", new Integer(25));
    functions.put("LENGTH/1", new Integer(26));
    functions.put("CURRENTTIME/0", new Integer(27));
    functions.put("CURRENTTIMESTAMP/0", new Integer(28));
    functions.put("WEEK/2", new Integer(29));
    functions.put("DATEDIFF/2", new Integer(30));
    functions.put("COALESCE/2", new Integer(31));
    // 32 to be verified
    functions.put("STRING2INT/1", new Integer(33));
    // 34, 35, 36 to be verified.
    functions.put("NEXTVAL/1", new Integer(37));
    functions.put("TO_CHAR/1", new Integer(38));
    functions.put("TO_DATE/1", new Integer(39));
    functions.put("TRUNC/2", new Integer(40));
  }
}