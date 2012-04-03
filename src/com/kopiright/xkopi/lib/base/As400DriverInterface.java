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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;

/**
 * This class encapsulates specific JDBC driver properties.
 */
public class As400DriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public As400DriverInterface() {
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
   * @param     password        the initial password
   */
  public void grantAccess(Connection conn, String user, String password) throws SQLException {
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
    throw new RuntimeException("NOT YET IMPLEMENTED");
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
    throw new RuntimeException("NOT YET IMPLEMENTED");
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
      As400Parser	parser = new As400Parser(from);

      System.err.println("-- NATIVE SQL: ");
      System.err.println(parser.getText() + ";");
      System.err.println("--");

      return parser.getText();
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
    return new DBUnspecifiedException(query, from);
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

}


/**
 * This class translates SQL queries from JDBC format to native format.
 */
/*package*/ class As400Parser extends JdbcParser {
// !!! Coco 230301 : The where is not parsed with the AS400 in the function GetEscape

  /**
   * Constructor
   */
  public As400Parser(String input) {
    super(input);
   
  }


  // ----------------------------------------------------------------------
  // FUNCTIONS WHICH MUST BE IMPLEMENTED BY SUB-CLASSES
  // ----------------------------------------------------------------------

  /**
   * Converts a function call in JDBC syntax to native syntax
   *
   * @param	function	the name of the function
   * @param	arguments	the arguments to the function
   * @return	a string representing the function call in native syntax
   *		or null no specific translation is defined for the function
   */
  protected String convertFunctionCall(String functor, Vector arguments)
    throws SQLException
  {
    Object	function = functions.get(functor.toUpperCase() + "/" + arguments.size());

    if (function == null) {
      return null;
    } else {
      // ugly, but more efficient than creating a command class for each functor

      switch (((Integer)function).intValue()) {
      case 1:	// TOMONTH/1
	return "((" + arguments.elementAt(0) + ") CAST DATETIME[YY:MO])";

      case 2:	// ADD_DAYS/2
	return "((" + arguments.elementAt(0) + ") + CONSTRUCT TIMESPAN[DD]( " + arguments.elementAt(1) + "))";

      case 4:	// MONTH/2
	return "DATETIME[YY:MO](" + arguments.elementAt(0) + "-" + arguments.elementAt(1) + ")";

      case 5:	// EXTRACT/2
	{
	  String	arg1 = (String)arguments.elementAt(1);

	  if (arg1.length() != 4 || arg1.charAt(0) != '\'' || arg1.charAt(3) != '\'') {
	    throw new SQLException("invalid argument to EXTRACT/2: " + arg1);
	  }

	  return "(" + arg1.substring(1, 3) + " OF (" + arguments.elementAt(0) + "))";
	}

      case 7:	// POSITION/2
	return "POSITION (" + arguments.elementAt(0) + " IN " + arguments.elementAt(1) + ")";

      case 8:	// SUBSTRING/2
	return "SUBSTRING (" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 9:	// SUBSTRING/3
	return "SUBSTRING (" + arguments.elementAt(0) + ", " + arguments.elementAt(1)  + ", " +
	  arguments.elementAt(2) + ")";

      case 10:	// SIZE_OF/1
	return "SIZE OF (" + arguments.elementAt(0) + ")";

      case 11:	// UPPER/1
	return "UPPER (" + arguments.elementAt(0) + ")";

      case 12:	// LOWER/1
	return "(" + arguments.elementAt(0) + ")";	// !!! kann er nicht

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
	return "CURRENT_DATE";

      case 18:	// USER
	return "USER";

      case 19:	// SPACE
	return "REPEAT (" + "' '" + ", " + arguments.elementAt(0) + ")";

      case 20:	// RIGHT
        return "SUBSTRING (" + arguments.elementAt(0) + " FROM " +  "(" +
	  "SIZE OF (" + arguments.elementAt(0) + ")" + ") - " + arguments.elementAt(1) + " + 1" + ")";

      case 21 : // LEFT
        return "SUBSTRING (" + arguments.elementAt(0) + " FROM " + "1" + " FOR " + arguments.elementAt(1) + ")";

      case 22 : // CONCAT
	return arguments.elementAt(0) + " + " + arguments.elementAt(1);

      case 23 : // LOCATE
        return "POSITION (" + arguments.elementAt(0) + " IN " + arguments.elementAt(1) + ")";

      case 24:	// TRUE
	return "1";

      case 25:	// FALSE
	return "0";

      case 26: // LENGTH
	return "LENGTH(" + arguments.elementAt(0) + ")";

      case 27:	// CURRENTTIME
	return "CURRENT_TIME";

      case 28:	// CURRENTTIMESTAMP
	return "CURRENT_TIMESTAMP";

      case 29:	// WEEK/2
	return "((" + arguments.elementAt(0) + ") * 53 + (" + arguments.elementAt(1) + ") - 1)";

      case 30: //DATEDIFF
	return "DATEDIFF(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      default:
	throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR " + functor.toUpperCase() +
				   "/" + arguments.size());
      }
    }
  }

  private static Hashtable	functions;

  // ----------------------------------------------------------------------
  // STATIC INITIALIZER
  // ----------------------------------------------------------------------

  static {
    functions = new Hashtable();

    /*
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
    */
    functions.put("TRUE/0", new Integer(24));
    functions.put("FALSE/0", new Integer(25));
    /*
    functions.put("LENGTH/1", new Integer(26));
    functions.put("CURRENTTIME/0", new Integer(27));
    functions.put("CURRENTTIMESTAMP/0", new Integer(28));
    functions.put("WEEK/2", new Integer(29));
    functions.put("DATEDIFF/2", new Integer(30));
    */
  }
}
