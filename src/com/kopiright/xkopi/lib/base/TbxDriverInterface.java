/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.Utils;

/**
 * This class encapsulates specific JDBC driver properties.
 */
public class TbxDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public TbxDriverInterface() {
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
    executeSQL(conn, "ALTER PASSWORD FROM " + oldPassword + " TO " + newPassword);
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
    try {
      TbxParser parser = new TbxParser(from);

      System.err.println("-- NATIVE SQL: ");
      System.err.println(parser.getText() + ";");
      System.err.println("--");

      return parser.getText();
    } catch (SQLException e) {
      throw new DBRuntimeException(from + "\r\n" + e.getMessage(), e);
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
    // operation not (yet) supported by this driver
  }


  // ----------------------------------------------------------------------
  // PRIVATE UTILITY METHODS
  // ----------------------------------------------------------------------

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
/*package*/ class TbxParser extends JdbcParser {
  /**
   * Constructor
   */
  public TbxParser(String input) {
    super(input);
  }

  /**
   * Converts a date into native syntax
   * @param	yy	year
   * @param	mo	month
   * @param	dd	day
   * @return	a string representing the date in native syntax
   */
  public String convertDate(int yy, int mo, int dd) {
    return "DATETIME[yy:dd](" + yy + "-" + mo + "-" + dd + ")";
  }

  /**
   * Converts a time into native syntax
   * @param	hh	hour
   * @param	mi	minute
   * @param	ss	second
   * @return	a string representing the time in native syntax
   */
  public String convertTime(int hh, int mi, int ss) {
    return "DATETIME[hh:ss](" + hh + ":" + mi + ":" + ss + ")";
  }

  /**
   * Converts a timestamp into native syntax
   * @param	yy	year
   * @param	mo	month
   * @param	dd	day
   * @param	hh	hour
   * @param	mi	minute
   * @param	ss	second
   * @param	ns	nanosecond
   * @return	a string representing the timestamp in native syntax
   */
  public String convertTimestamp(int yy, int mo, int dd, int hh, int mi, int ss, int ys) {
    return "DATETIME[yy:ms](" +  + yy + "-" + mo + "-" + dd + " " + hh + ":" + mi + ":" + ss + "." + Utils.formatInteger(ys/1000, 3) + ")";
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

      case 1:	// TOMONTH/1
	return "((YY OF (" + arguments.elementAt(0) + ")) * 100 + (MO OF (" + arguments.elementAt(0) + ")))";

      case 2:	// ADD_DAYS/2
	return "((" + arguments.elementAt(0) + ") + CONSTRUCT TIMESPAN[DD]( " + arguments.elementAt(1) + "))";

      case 4:	// MONTH/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 5:	// EXTRACT/2
	{
	  String	arg1 = (String)arguments.elementAt(1);

	  if (arg1.length() != 4 || arg1.charAt(0) != '\'' || arg1.charAt(3) != '\'') {
	    throw new SQLException("invalid argument to EXTRACT/2: " + arg1);
	  }

	  return "(" + arg1.substring(1, 3) + " OF (" + arguments.elementAt(0) + "))";
	}

	/* no case 6 currently */

      case 7:	// POSITION/2
	return "POSITION (" + arguments.elementAt(0) + " IN " + arguments.elementAt(1) + ")";

      case 8:	// SUBSTRING/2
	return "SUBSTRING (" + arguments.elementAt(0) + " FROM " + arguments.elementAt(1) + ")";

      case 9:	// SUBSTRING/3
	return "SUBSTRING (" + arguments.elementAt(0) + " FROM " + arguments.elementAt(1)  + " FOR " +
	  arguments.elementAt(2) + ")";

      case 10:	// SIZE_OF/1
	return "SIZE OF (" + arguments.elementAt(0) + ")";

      case 11:	// UPPER/1
	return "UPPER (" + arguments.elementAt(0) + ")";

      case 12:	// LOWER/1
	return "LOWER (" + arguments.elementAt(0) + ")";

       case 13:	// LTRIM/1
	 return "TRIM ( LEADING" + " FROM " + arguments.elementAt(0) + ")";

       case 14:	// RTRIM/1
	 return "TRIM ( TRAILING" + " FROM " + arguments.elementAt(0) + ")";

       case 15:	// REPLACE/3
	 return "REPLACE (" + arguments.elementAt(0) + " BY " + arguments.elementAt(1) + " IN " +
	   arguments.elementAt(2) + ")";

       case 16:	// REPEAT/2
	 return "REPEAT (" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

       case 17:	// CURRENTDATE
	return "CURRENTDATE CAST DATETIME[YY:DD]";

       case 18:	// USER
	 return "USER";

       case 19:	// SPACE
	 return "REPEAT (" + "' '" + ", " + arguments.elementAt(0) + ")";

       case 20:	// RIGHT
        return "SUBSTRING (" + arguments.elementAt(0) + " FROM " +  "(" +
	  "SIZE OF (" + arguments.elementAt(0) + ")" + ") - " + arguments.elementAt(1) + " + 1" + ")";

      case 21: // LEFT
        return "SUBSTRING (" + arguments.elementAt(0) + " FROM " + "1" + " FOR " + arguments.elementAt(1) + ")";

      case 22: // CONCAT/2
	return arguments.elementAt(0) + " || " + arguments.elementAt(1);

      case 23: // LOCATE/2
        return "POSITION (" + arguments.elementAt(0) + " IN " + arguments.elementAt(1) + ")";

      case 24:	// TRUE/0
	return "TRUE";

      case 25:	// FALSE/0
	return "FALSE";

      case 26: // LENGTH/1
        return "SIZE (" + arguments.elementAt(0) + ")";

      case 27:	// CURRENTTIME
	return "CURRENTDATE CAST DATETIME[HH:SS]";

      case 28:	// CURRENTTIMESTAMP
	return "CURRENTDATE";

      case 29:	// WEEK/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 30: // DATEDIFF/2
	return "DD OF (" + arguments.elementAt(0) + "-" + arguments.elementAt(1) + ")";

      case 31: // COALESCE/2
	return "COALESCE(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 32: // ROWNO/0
	return "RESULTCOUNT";

      case 33: // STRING2INT/1
	return "CAST(" + arguments.elementAt(0) + " AS INTEGER)";

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
    functions.put("ROWNO/0", new Integer(32));
    functions.put("STRING2INT/1", new Integer(33));
  }
}
