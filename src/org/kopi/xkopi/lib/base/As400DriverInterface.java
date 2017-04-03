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

import org.kopi.util.base.InconsistencyException;

/**
 * This class encapsulates specific properties of the AS/400 JDBC driver.
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
   * @return    the maximum number of characters in the ORDER BY of the driver
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
   * @return    the maximum number of columns in an ORDER BY clause
   */
  public int getMaximumColumnsInOrderBy() {
    return 10000;
  }

  /**
   * Returns the name of the system table DUAL.
   *
   * @return    the name of the system table DUAL.
   */
  public String getDualTableName() {
    return "SYSIBM.SYSDUMMY1";
  }

  /**
   * Give an access to an user or create an user
   *
   *
   * @param     conn            the connection
   * @param     user            the login of the user
   * @param     password        the initial password
   */
  public void grantAccess(Connection conn, String user, String password) throws SQLException {
    //!!! graf FIXME 20150126 - avoid SQL injection
    // !!! coco 050102 : verify the syntax
    executeSQL(conn, "GRANT ACCESS TO '" + user + "'");
  }

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param     conn            the connection
   * @param     user            the login of the user
   */
  public void revokeAccess(Connection conn, String user) throws SQLException {
    //!!! graf FIXME 20150126 - avoid SQL injection
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
   * @param     conn            the connection
   * @param     tableName       the name of the table to lock
   */
  public void lockTable(Connection conn, String table)
    throws SQLException
  {
    //!!! graf FIXME 20150126 - avoid SQL injection
    executeSQL(conn, "LOCK TABLE " + table + " IN EXCLUSIVE MODE");
  }

  /**
   * Transforms a SQL query string from KOPI syntax to the syntax
   * accepted by the corresponding driver
   *
   * @param     from            the SQL query string in KOPI syntax
   * @return    the string in the syntax of the driver
   */
  public String convertSql(String from) {
    try {
      As400Parser       parser = new As400Parser(from);

      if (trace) {
        System.err.println("-- NATIVE SQL: ");
        System.err.println(parser.getText() + ";");
        System.err.println("--");
      }

      return parser.getText();
    } catch (SQLException e) {
      e.printStackTrace();
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
    case -913:  // Deadlock or timeout occurred without automatic rollback.
      return new DBDeadLockException(query, from);
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
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_DAYS/2: Adds a specified number of days to a given valid character
   * string representation of a date.
   */
  protected String translateAddDays(String arg1, String arg2) throws SQLException {
    return "((" + arg1 + ") + ( " + arg2 + ") DAYS)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_YEARS/2:Adds a specified number of years to a given valid character
   * string representation of a date.
   */
  protected String translateAddYears(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CAST/2: Converts a value from one data type to a given target type.
   */
  protected String translateCast(String arg1, String arg2) throws SQLException {
    String      type = arg2.replaceAll("\'", "");
    
    if (type.equalsIgnoreCase("INT") || type.startsWith("NUMERIC") || type.startsWith("FIXED")) {
      return "INTEGER(" + arg1 + ")";
    } else if (type.startsWith("STRING") || type.startsWith("CHAR")) {
      return "CHAR(" + arg1 + ")";
    } else if (type.equals("DATETIME[YY:DD]")|| type.equalsIgnoreCase("YYYYMMDD")) {
      return "RTRIM(CHAR(YEAR(" + arg1 + "))) || REPEAT('0', 2 - LENGTH(RTRIM(CHAR(MONTH(" + arg1 + "))))) || RTRIM(CHAR(MONTH(" + arg1 + "))) || REPEAT('0', 2 - LENGTH(RTRIM(CHAR(DAY(" + arg1 + "))))) || RTRIM(CHAR(DAY(" + arg1 + ")))";
    } else if (type.equals("TIMESPAN[MI]")|| type.equalsIgnoreCase("MM")) {
      return "REPEAT('0', 2 - LENGTH(RTRIM(CHAR(MONTH(" + arg1 + "))))) || RTRIM(CHAR(MONTH(" + arg1 + ")))";
    } else {
      return arg1;
    }
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * COALESCE/2: Returns the first non-null expression of the two given
   * expression
   */
  protected String translateCoalesce(String arg1, String arg2) throws SQLException {
    return "VALUE(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CONCAT/2: Returns a string that is the result of concatenating two string
   * values.
   */
  protected String translateConcat(String arg1, String arg2) throws SQLException {
    return "(" + arg1 + " + " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTDATE/0: Returns the current date in the connected user time zone.
   */
  protected String translateCurrentdate() throws SQLException {
    return "CURRENT_DATE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIME/0: Returns the current database system time as a datetime
   * value.
   */
  protected String translateCurrenttime() throws SQLException {
    return "CURRENT_TIME";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIMESTAMP/0: Returns the current database system timestamp as a
   * datetime value.
   */
  protected String translateCurrenttimestamp() throws SQLException {
    return "CURRENT TIMESTAMP";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * DATEDIFF/2: Calculates the difference between two given dates in days.
   */
  protected String translateDatediff(String arg1, String arg2) throws SQLException {
    return "DATEDIFF(" + arg1 + "," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * EXTRACT/2: Extracts and returns the value of a specified datetime field
   * from a datetime.
   */
  protected String translateExtract(String arg1, String arg2) throws SQLException {
    final String[][]    portions = {
      { "'YY'", "YEAR" },
      { "'MO'", "MONTH" },
      { "'DD'", "DAY" },
      { "'HH'", "HOUR" },
      { "'MI'", "MINUTE" },
      { "'SS'", "SECOND" }
    };

    for (String[] p : portions) {
      if (p[0].equals(arg2)) {
        return p[1] + "(" + arg1 + ")";
      }
    }
    
    throw new SQLException("invalid argument to EXTRACT/2: " + arg2);
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * FALSE/0: Returns the boolean false value.
   */
  protected String translateFalse() throws SQLException {
    return "0";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * GREATEST/2: Returns the greatest of the given list of two expressions.
   */
  protected String translateGreatest(String arg1, String arg2) throws SQLException {
     return "MAX(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * INSTR/2: Returns the position of the first occurrence of a substring in a
   * host string.
   */
  protected String translateInstr(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * INT2STRING/1: Converts a given number to a value of string datatype.
   */
  protected String translateInt2string(String arg1) throws SQLException {
    return  "CHAR(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LAST_DAY/1: Returns the date of the last day of the month that contains
   * the given date.
   */
  protected String translateLastDay(String arg1) throws SQLException {
     return " LAST_DAY(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LENGTH/1: Returns the length of a given string.
   */
  protected String translateLength(String arg1) throws SQLException {
    return "LENGTH(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LEFT/2: Returns the left part of a character string with the specified
   * number of characters.
   */
  protected String translateLeft(String arg1, String arg2) throws SQLException {
    return "SUBSTRING (" + arg1 + " FROM 1 FOR " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOCATE/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected String translateLocate(String arg1, String arg2) throws SQLException {
    return "POSITION (" + arg1 + " IN " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOWER/1: Converts all characters in the specified string to lowercase.
   */
  protected String translateLower(String arg1) throws SQLException {
    return "LCASE(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LPAD/2: Returns an expression, left-padded to a given length with blanks
   * or, when the expression to be padded is longer than the length specified
   * after padding, only that portion of the expression that fits into the
   * specified length.
   */
  protected String translateLpad(String arg1, String arg2) throws SQLException {
    return "REPEAT(' ', " + arg2 + " - LENGTH(" + arg1 + ")) || " + arg1;
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LPAD/3: Returns an expression, left-padded to a given length with a given
   * string or, when the expression to be padded is longer than the length
   * specified after padding, only that portion of the expression that fits
   * into the specified length.
   */
  protected String translateLpad(String arg1, String arg2, String arg3) throws SQLException {
    int         len = Integer.parseInt(arg2);

    if (len >= (arg1.length() - 2)) {
      return " REPEAT(" + arg3 + "," + arg2 + " - LENGTH(" + arg1 + ")) || " + arg1;
    } else{
      throw new InconsistencyException("Assignment impossible, text value too long");
    }
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LTRIM/1: Removes all blanks from the left end of a given string.
   */
  protected String translateLtrim(String arg1) throws SQLException {
    return "LTRIM(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * MOD/2: Returns the remainder of the second argument divided by the first
   * argument.
   */
  protected String translateMod(String arg1, String arg2) throws SQLException {
    return "MOD(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * MONTH/2: Converts the given year and month (YYYY and MM respectively) into
   * an integer representing the combined month in the form YYYYMM.
   */
  protected String translateMonth(String arg1, String arg2) throws SQLException {
    return "DATETIME[YY:MO](" + arg1 + "-" + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * NEXTVAL/1: Increments the given sequence and returns its new current
   * value.
   */
  protected String translateNextval(String arg1) throws SQLException {
    return  "NEXT VALUE FOR " + arg1;
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * POSITION/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected String translatePosition(String arg1, String arg2) throws SQLException {
    return "POSITION (" + arg1 + " IN " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPEAT/2: Returns a string by repeating a given count times a given
   * expression.
   */
  protected String translateRepeat(String arg1, String arg2) throws SQLException {
    return "REPEAT (" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPLACE/3: Returns a string with every occurrence of a given searched
   * string replaced with given replacement string
   */
  protected String translateReplace(String arg1, String arg2, String arg3) throws SQLException {
    return "REPLACE (" + arg1 + " BY " + arg2 + " IN " + arg3 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RIGHT/2: Returns the right part of a character string with the specified
   * number of characters.
   */
  protected String translateRight(String arg1, String arg2) throws SQLException {
    return "SUBSTRING (" + arg1 + " FROM " +  "(SIZE OF (" + arg1 + ")) - " + arg2 + " + 1)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/0: Returns the address or the ID of the row.
   */
  protected String translateRowid() throws SQLException {
    return "row_number() over()";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/1: Returns the address or the ID of the row in a given table.
   */
  protected String translateRowid(String arg1) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/0: Returns the address or the ID of the row.
   */
  protected String translateRowidext() throws SQLException {
    return "row_number() over()";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/1: Returns the address or the ID of the row of a given external
   * table.
   */
  protected String translateRowidext(String arg1) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWNO/0: Returns the sequential number of a row within a partition of a
   * result set, starting at 1 for the first row in each partition.
   */
  protected String translateRowno() throws SQLException {
    return "row_number() over()";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RPAD/2: Returns an expression, right-padded to a given length with blanks
   * or, when the expression to be padded is longer than the length specified
   * after padding, only that portion of the expression that fits into the
   * specified length.
   */
  protected String translateRpad(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RPAD/3: Returns an expression, right-padded to a given length with a given
   * string or, when the expression to be padded is longer than the length
   * specified after padding, only that portion of the expression that fits
   * into the specified length.
   */
  protected String translateRpad(String arg1, String arg2, String arg3) throws SQLException {
    int         len = Integer.parseInt(arg2);

    if (len >= (arg1.length() - 2)) {
      return arg1 + "|| REPEAT(" + arg3 + "," + arg2 + " - LENGTH(" + arg1 + "))";
    } else{
      throw new InconsistencyException("Assignment impossible, text value too long");
    }
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RTRIM/1: Removes all blanks from the right end of a given string.
   */
  protected String translateRtrim(String arg1) throws SQLException {
    return "RTRIM(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SPACE/1: Returns a given number of spaces.
   */
  protected String translateSpace(String arg1) throws SQLException {
    return "REPEAT (' ', " + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * STRING2INT/1: Converts the given string number into an integer.
   */
  protected String translateString2int(String arg1) throws SQLException {
    return "INTEGER(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * STRPOS/2: Returns the position of the first occurrence of a substring in a
   *  host string.
   */
  protected String translateStrpos(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SUBSTRING/2: Returns a portion of a given string beginning at a given
   * position
   */
  protected String translateSubstring(String arg1, String arg2) throws SQLException {
    return "SUBSTRING (" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SUBSTRING/3: Returns a portion of a given string beginning at a given
   * position having the given length (third parameter).
   */
  protected String translateSubstring(String arg1, String arg2, String arg3) throws SQLException {
    return "SUBSTRING (" + arg1 + ", " + arg2  + ", " + arg3 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_CHAR/1: Converts a given number to a value of string datatype.
   */
  protected String translateToChar(String arg1) throws SQLException {
    return "CHAR(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_CHAR/2: Converts a given valid character string representation of a
   * date into a given format and returns a date in database date format.
   */
  protected String translateToChar(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_DATE/1: Converts a given datetime into a date format.
   */
  protected String translateToDate(String arg1) throws SQLException {
    return "DATE('" + arg1.replaceAll("\'", "") + "')";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_DATE/2: Converts a given valid character string representation of a
   * date into a given format and returns a date in database date format.
   */
  protected String translateToDate(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TOMONTH/1: Returns the month (as integer) of a date in the form YYYYMM,
   * the argument must be a valid character string representation of a date in
   * the form YYYY-MM-DD.
   */
  protected String translateTomonth(String arg1) throws SQLException {
    return "((" + arg1 + ") CAST DATETIME[YY:MO])";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_NUMBER/1: Converts a given expression to a value of Integer type.
   */
  protected String translateToNumber(String arg1) throws SQLException {
    return "INTEGER(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUNC/2: Returns the first given number truncated to second number decimal
   * places.
   */
  protected String translateTrunc(String arg1, String arg2) throws SQLException {
    return "TRUNC(" + arg1 + "," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUNC_DATE/1: returns date with the time portion of the day truncated.
   * The value returned is always of database date type.
   */
  protected String translateTruncDate(String arg1) throws SQLException {
    return "DATE(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUE/0: Returns the boolean true value.
   */
  protected String translateTrue() throws SQLException {
    return "1";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * UPPER/1: Converts all characters in the specified string to uppercase.
   */
  protected String translateUpper(String arg1) throws SQLException {
    return "UCASE(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * USER/0:Returns the name of the current user.
   */
  protected String translateUser() throws SQLException {
    return "USER";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * WEEK/1: Returns the week number (from 1 to 53) for a given date.
   */
  protected String translateWeek(String arg1) throws SQLException {
    return "((" + arg1 + ") * 53 + (" + arg1 + ") - 1)";
  }

  protected String parseWhereCurrent() throws SQLException {
    String      result;

    result = super.parseWhereCurrent();
    if (result != null) {
      result += " OF \"" + getCursor() + "\"";
    }
    return result;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

}
