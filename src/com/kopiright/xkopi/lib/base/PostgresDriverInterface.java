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
 * This class encapsulates specific properties of the POSTGRES JDBC driver.
 */
public class PostgresDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public PostgresDriverInterface() {
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
    executeSQL(conn, "CREATE USER " + user + " WITH PASSWORD '" + password + "'");
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
    // if user is not provided => get the connected user
    if (user == null) {
      user = conn.getMetaData().getUserName();
    } 
    executeSQL(conn, "ALTER USER " + user + " WITH PASSWORD " + newPassword);
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
      PostgresParser	parser = new PostgresParser(from);

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
    int                 errorCode; // SQLState is used in postgres. Error code is always 0

    try {
      // SQLState is a char sequence.
      // For common errors it would be an integer code.
      // In other cases, -1 will be returned indicating that is an unspecified exception.
      errorCode = Integer.parseInt(from.getSQLState()); // SQLState is used in postgres.
    } catch (NumberFormatException e) {
      errorCode = -1; // This means that we will treat the error as unspecified exception.
    }
    switch (errorCode) {
      // Class 08 — Connection Exception
    case 8000:	// connection_exception
    case 8003:	// connection_does_not_exist
    case 8006:	// connection_failure
    case 8001:	// sqlclient_unable_to_establish_sqlconnection
    case 8004:  // sqlserver_rejected_establishment_of_sqlconnection
    case 8007:  // transaction_resolution_unknown
      return new DBDeadLockException(query, from);
      // Class 23 — Integrity Constraint Violation
    case 23505:	// unique_violation
      return parseDuplicateIndex(query, from);

    case 23503:	// foreign_key_violation
      return parseIntegrityViolation(query, from);
      
    case 23000:	// integrity_constraint_violation
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

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES METHODS
  // ----------------------------------------------------------------------

  /**
   * Parses a PostgreSQL duplicate index exception
   * The message of a unique integrity vialoation has the following form:
   * ERROR: duplicate key value violates unique constraint {index_name}
   */
  private static DBDuplicateIndexException parseDuplicateIndex(String query, SQLException from) {
    // separate each word in the exception message in order to get the index name.
    String[]    exception = from.getMessage().split("\\s");

    // As described above, the index name is the word number 7
    // The message of the index is standard message from posgreSQL. Each change in the form
    // of the error should be followed by an update of this message partition.
    return new DBDuplicateIndexException(query, from, exception[7]);
  }

  /**
   * Parses a PostgreSQL integrity violation exception.
   * The message of an integrity violation has the following form:
   * </p><i>ERROR: update or delete on table {referenced_table} violates foreign key constraint {fk_name} on table {reference_table}</i></p>
   * We need to extract the FK name and the tables in relation to create the corresponding {@link DBForeignKeyException} instance.
   */
  private static DBForeignKeyException parseIntegrityViolation(String query, SQLException from) {
    // separate each word in the exception message in order to get tables in relation.
    String[]    exception = from.getMessage().split("\\s");
    
    // As described above: 
    // The referenced table will be the word number 6
    // The FK name will be the word number 11
    // The referencing table will be the word number 14
    // The message of the FK error is standard message from posgreSQL. Each change in the form
    // of the FK error should be followed by an update of this message partition.
    return new DBForeignKeyException(query, from, exception[11], exception[6], exception[14]);
  }
}


/**
 * This class translates SQL queries from JDBC format to native format.
 */
/*package*/ class PostgresParser extends JdbcParser {
  /**
   * Constructor
   */
  public PostgresParser(String input) {
    super(input);
  }

  /**
   * Parses WHERE CURRENT OF (and WHERE ...)
   */
  protected String parseWhereCurrent() throws SQLException {
    String      result;

    result = super.parseWhereCurrent();
    if (result != null) {
      result += " OF " + getCursor();
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

    result = "EXTRACT (";
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

    return result + " FROM " + date + ")";
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
	return "(EXTRACT(YEAR FROM " +  arguments.elementAt(0) + ") * 100 + EXTRACT(MONTH FROM " + arguments.elementAt(0) + ")) ";

      case 2:	// ADD_DAYS/2
	return "(ADDDATE(" +  arguments.elementAt(0) + ","+ arguments.elementAt(1) + "))";

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
	return "INDEX(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";

      case 8:	// SUBSTRING/2
	return "SUBSTRING(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 9:	// SUBSTRING/3
	return "SUBSTRING(" + arguments.elementAt(0) + "," + arguments.elementAt(1)  + "," + arguments.elementAt(2) + ")";

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

      case 16:	// REPEAT/2
	return "LFILL('','" + arguments.elementAt(0) + "'," + arguments.elementAt(1) + ")";

      case 17:	// CURRENTDATE
	return "CURRENT_DATE";

      case 18:	// USER
	return "USER";

      case 19:	// SPACE
	return "LFILL('',' '," + arguments.elementAt(1) + ")";

      case 20:	// RIGHT
	return "RFILL(" + arguments.elementAt(0) + ",' '," + arguments.elementAt(1) + ")";

      case 21: // LEFT
        return "LFILL(" + arguments.elementAt(0) + ",' '," + arguments.elementAt(1) + ")";

      case 22: // CONCAT
	// !!! coco 010201 : looks to be like it but we have to test it
	return arguments.elementAt(0) + "||" + arguments.elementAt(1);

      case 23: // LOCATE
        return "INDEX(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";

      case 24:	// TRUE
	return "TRUE";

      case 25:	// FALSE
	return "FALSE";

      case 26: // LENGTH
	return "LENGTH(" + arguments.elementAt(0) + ")";

      case 27:	// CURRENTTIME
	return "TIME";

      case 28:	// CURRENTTIMESTAMP
	return "CURRENT_TIMESTAMP";

      case 29:	// WEEK/2
	return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 30: // DATEDIFF/2
	return "DATEDIFF(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 31: // COALESCE/2
	return "COALESCE(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
      
      case 32: // NEXTVAL/1
	return "NEXTVAL('" + arguments.elementAt(0) + "')";
	
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
    functions.put("NEXTVAL/1", new Integer(32));
  }
}
