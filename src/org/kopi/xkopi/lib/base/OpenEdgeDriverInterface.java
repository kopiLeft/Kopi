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
 * This class encapsulates specific properties of the POSTGRES JDBC driver.
 */
public class OpenEdgeDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public OpenEdgeDriverInterface() {
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
   * Returns the name of the system table DUAL.
   *
   * @return    the name of the system table DUAL.
   */
  public String getDualTableName() {
    return "SYSPROGRESS.SYSCALCTABLE";
  }

  /**
   * Give an access to a user or create a user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   * @param     password        the initial password
   */
  public void grantAccess(Connection conn, String user, String password) throws SQLException {
    executeSQL(conn, "CREATE USER '" + user + "', '" + password + "'");
  }

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param	conn		the connection
   * @param	user		the login of the user
   */
  public void revokeAccess(Connection conn, String user) throws SQLException {
    executeSQL(conn, "DROP USER '" + user + "'");
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
    // if user is not provided => get the connected user
    if (user == null) {
      user = conn.getMetaData().getUserName();
    } 
    executeSQL(conn, "ALTER USER '" + user + "', '" + oldPassword + "', '" + newPassword + "'");
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
    executeSQL(conn, "LOCK TABLE " + tableName + " IN EXCLUSIVE MODE");
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
      OpenEdgeParser	parser = new OpenEdgeParser(from);
      String            nativeSql = parser.getText().trim();

      if (trace) {
        System.err.println("-- NATIVE SQL: ");
        System.err.println(nativeSql + ";");
        System.err.println("--");
      }

      return nativeSql;
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
      // Connection Exception
    case 20045:	// File does not exist or not accessible.
    case 20053:	// Database not opened.
    case 20054: // Database not specified or improperly specified.
    case 20055: // Database not specified or database not started.
    case 20064: // Invalid attach type. 
    case 20067: // More than one database cannot be attached locally.
    case 20096: // Connect String not specified/incorrect.
    case 20108: // Remote Network Server not started.
    case 20110: // TCP/IP Remote HostName is unknown.
    case 20212: // SQL client bind to daemon failed.
    case 20213: // SQL client bind to SQL server failed
    case 20214: // SQL NETWORK service entry is not available.
    case 20215: // Invalid TCP/IP hostname.
    case 20217: // Network error on server.
    case 20218: // Invalid protocol.
    case 20220: // Duplicate connection name.
    case 20221: // No active connection.
    case 20222: // No environment defined database.
    case 20223: // Multiple local connections.
    case 20224: // Invalid protocol in connect_string.
    case 20225: // Exceeding permissible number of connections.
    case 20227: // Invalid host name in connect string.
    case 20241: // Service in use.
    case 210001: // Failure to acquire share schema lock during connect.
    case 210002: // Failure in finding DLC environment variable.
    case 210003: // DLC environment variable exceeds maximum size <max_size> -> <DLC path>.
    case 210004: // Error opening convmap.cp file <filename> <path>.
    case 210013: // Unable to complete server connection. <function_name>; reason <summary_of_reason>.
      return new DBDeadLockException(query, from);
      // Integrity Constraint 
    case 10102: // Duplicate primary/index key value.      
    case 20101:	// No references for the table.
    case 20102: // Primary/Candidate key column defined null.
    case 20103: // No matching key defined for the referenced table.
    case 20104: // Keys in reference constraint incompatible.
    case 20116: // Constraint violation.
    case 20117: // Table definition not complete.
    case 20119: // Constraint name not found.
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

  /**
   * Checks which outer join syntax (JDBC or Oracle) should be used.
   *
   * @return    true if Oracle outer join syntax should be used.
   */
  public boolean useOracleOuterJoinSyntax() {
    return false;
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES METHODS
  // ----------------------------------------------------------------------

}

/**
 * This class translates SQL queries from JDBC format to native format.
 */
/*package*/ class OpenEdgeParser extends JdbcParser {
  /**
   * Constructor
   */
  public OpenEdgeParser(String input) {
    super(input);
  }

  /**
   * Parses WHERE CURRENT OF (and WHERE ...)
   */
  // FIXME ??
  protected String parseWhereCurrent() throws SQLException {
    String      result;

    result = super.parseWhereCurrent();
    if (result != null) {
      result += " OF " + getCursor();
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

    result = "";
    if (type.equals("YY")) {
      result += "YEAR ";
    } else if (type.equals("MO")) {
      result += "MONTH ";
    } else if (type.equals("DD")) {
      result += "DAY ";
    } else if (type.equals("HH")) {
      result += "HOUR ";
    } else if (type.equals("MI")) {
      result += "MINUTE ";
    } else if (type.equals("SS")) {
      result += "SECOND ";
    } else {
      throw new IllegalArgumentException(type + " is not a good type of data to extract");
    }

    return result + " ( " + date + ")";
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
	return "((TO_CHAR(" + arguments.elementAt(0) + ", 'YYYY')) * 100 + (TO_CHAR(" + arguments.elementAt(0) + ", 'MM')))";

      case 2:	// ADD_DAYS/2
	return "(" +  arguments.elementAt(0) + " + " + arguments.elementAt(1) + ")";

      case 4:	// MONTH/2
	return "(" +  arguments.elementAt(0) + " * 100 + (" + arguments.elementAt(1) + "))";

      case 5:	// EXTRACT/2
	{
	  String	arg1 = (String)arguments.elementAt(1);

	  if (arg1.length() != 4 || arg1.charAt(0) != '\'' || arg1.charAt(3) != '\'') {
	    throw new SQLException("invalid argument to EXTRACT/2: " + arg1);
	  }

	  return "(" + extract(arg1.substring(1, 3), arguments.elementAt(0)) + ")";
	}

      case 7:	// POSITION/2
	return "INSTR(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";

      case 8:	// SUBSTRING/2
	return "SUBSTR(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 9:	// SUBSTRING/3
	return "SUBSTR(" + arguments.elementAt(0) + "," + arguments.elementAt(1)  + "," + arguments.elementAt(2) + ")";

      case 10:	// SIZE_OF/1
	return "LENGTH(" + arguments.elementAt(0) + ")";

      case 11:	// UPPER/1
	return "UPPER(" + arguments.elementAt(0) + ")";

      case 12:	// LOWER/1
	return "LOWER(" + arguments.elementAt(0) + ")";

      case 13:	// LTRIM/1
	return "LTRIM(" + arguments.elementAt(0) + ")";

      case 14:	// RTRIM/1
	return "RTRIM(" + arguments.elementAt(0) + ")";

      case 15:	// REPLACE/3
	return "REPLACE(" + arguments.elementAt(2) + "," + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";
			 // FIXME ???? ordre params
      case 16:	// REPEAT/2
	return "REPEAT(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 17:	// CURRENTDATE/0
	return "CURDATE()";

      case 18:	// USER/0
	return "USER";

      case 19:	// SPACE/1
//	return "LFILL('',' '," + arguments.elementAt(1) + ")";
	return "REPEAT (' ', " + arguments.elementAt(1) + ")";

      case 20:	// RIGHT/2
	return "RIGHT(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 21: // LEFT/2
        return "LEFT(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 22: // CONCAT/2
	return "CONCAT (" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 23: // LOCATE/2
        return "INSTR(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";

      case 24:	// TRUE/0
	return "1";

      case 25:	// FALSE/0
	return "0";

      case 26: // LENGTH/1
	return "LENGTH(" + arguments.elementAt(0) + ")";

      case 27:	// CURRENTTIME/0
	return "CURTIME()";

      case 28:	// CURRENTTIMESTAMP/0
	return "NOW()";

      case 29:	// WEEK/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 30: // DATEDIFF/2
	return "TO_CHAR(" + arguments.elementAt(0) + "-" + arguments.elementAt(1) + ", 'DD')";

      case 31: // COALESCE/2
	return "COALESCE(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
      
      case 32: // NEXTVAL/1
	return arguments.elementAt(0) + ".NEXTVAL";

      case 33 :  // STRING2INT/1
	return "TO_NUMBER( " + arguments.elementAt(0) + ")";
	 
      case 34 : // GREATEST/2
	return "GREATEST(" + arguments.elementAt(0) + ", " +  arguments.elementAt(1) + ")";
	
      case 35 : // MOD/2
        return  "MOD(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
        
      case 36 : // CAST/2
        return cast(arguments.elementAt(0), (String)arguments.elementAt(1));
	 
      case 37 : // INT2STRING/1
	return cast(arguments.elementAt(0), "CHAR");

      case 38 : // NVL/2
	return "NVL(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
	
      case 39 : // SYSDATE/0	
	return "SYSDATE()";
	
      case 40 : // TO_DATE/1	
	return "TO_DATE(" + arguments.elementAt(0) + ")";

      case 41 : // UCASE/1
	return "UCASE(" + arguments.elementAt(0) + ")";
	
      case 42 : // LPAD/2
	return "LPAD(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 43 : // RPAD/2 
	return "RPAD(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
	
      case 44 : // ROWID/0
	return  "ROWID";
	
      case 45 : // LAST_DAY/1
	return "LAST_DAY (" + arguments.elementAt(0) + ")";
	
      default:
	throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR " + functor.toUpperCase() +
				   "/" + arguments.size());
      }
    }
  }
  
  private String cast(Object object, String type) {
    System.out.println("--------------> type ==== " + type);
    if (type.toUpperCase().startsWith("INT") || type.toUpperCase().startsWith("NUMERIC") || type.toUpperCase().startsWith("FIXED")) {
      return "TO_NUMBER(" + object + ")";
    } else if (type.toUpperCase().startsWith("STRING") || type.toUpperCase().startsWith("CHAR")) {
      return "CAST(" + object + " AS CHAR(" + object.toString().length() + "))";
    } else {
      return object.toString();
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
    functions.put("NEXTVAL/1", new Integer(32));
    functions.put("STRING2INT/1", new Integer(33));
    functions.put("GREATEST/2", new Integer(34));
    functions.put("MOD/2", new Integer(35));
    functions.put("CAST/2", new Integer(36));
    functions.put("INT2STRING/1", new Integer(37));
    functions.put("NVL/2", new Integer(38));
    functions.put("SYSDATE/0", new Integer(39));
    functions.put("TO_DATE/1", new Integer(40));
    functions.put("UCASE/1", new Integer(41));
    functions.put("LPAD/2", new Integer(42));
    functions.put("RPAD/2", new Integer(43));
    functions.put(" ROWID/0", new Integer(44));
    functions.put("LAST_DAY/1", new Integer(45));
  }
}
