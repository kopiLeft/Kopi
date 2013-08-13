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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class encapsulates specific JDBC driver properties.
 */
public class OracleDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public OracleDriverInterface() {
    super();
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
      OracleParser	parser = new OracleParser(from);
      String             nativeSql = parser.getText().trim();
     
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
    return "DUAL";
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
    executeSQL(conn, "GRANT CONNECT TO " + user + " IDENTIFIED BY \"" + password + "\"");
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
    executeSQL(conn, "ALTER USER " + user + " IDENTIFIED BY \"" + newPassword + "\"");
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
   * Transforms an SQLException into its corresponding kopi DBException
   *
   * @param     query   the sql query which generated the exception
   * @param	from    the SQLException
   * @return	the corresponding kopi DBException
   */
  public DBException convertException(String query, SQLException from) {
    switch (from.getErrorCode()) {
    case 60:	// ORA-00060
    case 104:	// ORA-00104
    case 4020:	// ORA-04020
      return new DBDeadLockException(query, from);

    case 1:	// ORA-00001
    case 2268:	// ORA-02268
    case 2270:	// ORA-02270
    case 2272:	// ORA-02272
    case 2273:	// ORA-02273
    case 2274:	// ORA-02274
    case 2275:  // ORA-02275
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
   * @return    true iff Oracle outer join syntax should be used.
   */
  public boolean useOracleOuterJoinSyntax() {
    return false;
  }
}


/**
 * This class is parsing a string which are representing
 * an sql query at the jdbc's format.
 */
class OracleParser extends JdbcParser {

  /**
   * Constructor
   */
  public OracleParser(String input) {
    super(input);
  }

  /**
   * Parses WHERE CURRENT OF (and WHERE ...)
   */
  protected String parseWhereCurrent() throws SQLException {
    String      result;

    result = super.parseWhereCurrent();
    if (result != null) {
      result = "WHERE ROWID = :Row_id";
    }
    return result;
  }

  /**
   * Converts a date into native syntax
   * @param	yy	year
   * @param	mo	month
   * @param	dd	day
   * @return	a string representing the date in native syntax
   */
  protected String convertDate(int yy, int mo, int dd) {
    return "TO_DATE('" + yy + "-" + mo + "-" + dd + "', 'YYYY-MM-DD')";
  }

  /**
   * Converts a time into native syntax
   * @param	hh	hour
   * @param	mi	minute
   * @param	ss	second
   * @return	a string representing the time in native syntax
   */
  protected String convertTime(int hh, int mi, int ss) {
    return "TO_DATE('" + hh + ":" + mi + ":" + ss + "', 'HH24:MI:SS')";
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
  protected String convertTimestamp(int yy, int mo, int dd, int hh, int mi, int ss, int ns) {
    return "TO_DATE('" + yy + "-" + mo + "-" + dd + " " + hh + ":" + mi + ":" + ss + "', 'YYYY-MM-DD HH24:MI:SS')";
  }

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
      // use function as it.
      String  functionCall;

      functionCall = functor.toUpperCase();
      if (arguments.size() > 0) {
        functionCall += "(";

        Iterator args = arguments.iterator();

        while (args.hasNext()) {
          functionCall += args.next();
          if (args.hasNext()) {
            functionCall += ", ";
          }
        }

        functionCall += ")";
      }
      return functionCall;
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
	return "ADD_DAYS(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 3:	// ADD_YEARS/2
	return "ADD_YEARS(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 4:	// MONTH/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 5:	// EXTRACT/2
	{
	  String	arg1 = (String)arguments.elementAt(1);

	  if (arg1.length() != 4 || arg1.charAt(0) != '\'' || arg1.charAt(3) != '\'') {
	    throw new SQLException("invalid argument to EXTRACT/2: " + arg1);
	  }

	  return "(TO_CHAR(" + arguments.elementAt(0) + ", " + arg1 +"))";
	}

	/* no case 6 currently */

      case 7:	// POSITION/2
 	return "INSTR (" + arguments.elementAt(1) + ", " + arguments.elementAt(0) + ")";

      case 8:	// SUBSTRING/2
	return "SUBSTR (" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 9:	// SUBSTRING/3
	return "SUBSTRING (" + arguments.elementAt(0) + ", " + arguments.elementAt(1)  + ", " +
	  arguments.elementAt(2) + ")";

      case 10:	// SIZE_OF/1
	return "LENGTH (" + arguments.elementAt(0) + ")";

      case 11:	// UPPER/1
	return "UPPER (" + arguments.elementAt(0) + ")";

      case 12:	// LOWER/1
	return "LOWER (" + arguments.elementAt(0) + ")";

      case 13:	// LTRIM/1
        return "LTRIM (" + arguments.elementAt(0) + ")";

      case 14:	// RTRIM/1
        return "RTRIM (" + arguments.elementAt(0) + ")";

      case 15:	// REPLACE/3
        return "REPLACE (" + arguments.elementAt(2) + ", " + arguments.elementAt(0) + " , " +
          arguments.elementAt(1) + ")";

        /* TO SEE: REPEAT AND SPACE in Oracle ? */
        //        case 16:	// REPEAT/2
        // 	 return "REPEAT (" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 17:	// CURRENTDATE
	return "SYSDATE";

      case 18:	// USER
        return "USER";

        //        case 19:	// SPACE
        // 	 return "REPEAT (" + "' '" + ", " + arguments.elementAt(0) + ")";

      case 20:	// RIGHT
        return "SUBSTR (" + arguments.elementAt(0) + ", " +  "(" +
	  "LENGTH (" + arguments.elementAt(0) + ")" + ") - " + arguments.elementAt(1) + " + 1" + ")";

      case 21: // LEFT
        return "SUBSTR (" + arguments.elementAt(0) + ", " + "1" + ", " + arguments.elementAt(1) + ")";

      case 22: // CONCAT/2
	return arguments.elementAt(0) + " || " + arguments.elementAt(1);

      case 23: // LOCATE/2
        return "INSTR (" + arguments.elementAt(1) + ", " + arguments.elementAt(0) + ")";

      case 24:	// TRUE/0
	return "1";

      case 25:	// FALSE/0
	return "0";

      case 26: // LENGTH/1
        return "LENGTH (" + arguments.elementAt(0) + ")";

      case 27:	// CURRENTTIME
	return "SYSDATE";

      case 28:	// CURRENTTIMESTAMP
	return "SYSDATE";

      case 29:	// WEEK/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 30: //DATEDIFF
	return "TO_CHAR(" + arguments.elementAt(0) + "-" + arguments.elementAt(1) + ", 'DD')";

      case 31: // COALESCE/2
	return "NVL(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 32: // ROWNO/0
	return "ROWNUM";

      case 33: // STRING2INT/1
	return "TO_NUMBER(" + arguments.elementAt(0) + ")";

      case 37: // NEXTVAL/1
        return  arguments.elementAt(0) + ".NEXTVAL";

      case 38: // UCASE/1
        return  "UPPER(" + arguments.elementAt(0) + ")";

      case 39: // ROWID/0
        return  "ROWID";
      
      case 40: // ROWID/1
        return  arguments.elementAt(0) + ".ROWID";
        
      case 41: // TRUNC_DATE/1
        return  "TRUNC(" + arguments.elementAt(0) + ")";
        
      case 54: // ROWIDEXT/0
        return "ROWID";

      case 55: // ROWIDEXT/1
        return "(" + arguments.elementAt(0) + ".ROWID)";

      default:
        // use function as it.
        String  functionCall;

        functionCall = functor.toUpperCase();
        if (arguments.size() > 0) {
          functionCall += "(";

          Iterator args = arguments.iterator();

          while (args.hasNext()) {
            functionCall += args.next();
            if (args.hasNext()) {
              functionCall += ", ";
            }
          }

          functionCall += ")";
        }
        return functionCall;
      }
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
    
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private static Hashtable	functions;
  // ----------------------------------------------------------------------
  // STATIC INITIALIZER
  // ----------------------------------------------------------------------

  static {
    functions = new Hashtable();

    functions.put("TOMONTH/1", new Integer(1));
    functions.put("ADD_DAYS/2", new Integer(2));
    functions.put("ADD_YEARS/2", new Integer(3));
    functions.put("MONTH/2", new Integer(4));
    functions.put("EXTRACT/2", new Integer(5));
    // functions.put("MONTHCODE/1", new Integer(6));
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
    // 34 , 35 , 36 , to define
    functions.put("NEXTVAL/1", new Integer(37));
    functions.put("UCASE/1", new Integer(38));
    functions.put("ROWID/0", new Integer(39));
    functions.put("ROWID/1", new Integer(40));
    functions.put("TRUNC_DATE/1", new Integer(41));
    functions.put("ROWIDEXT/0", new Integer(54));
    functions.put("ROWIDEXT/1", new Integer(55));
  }
}
