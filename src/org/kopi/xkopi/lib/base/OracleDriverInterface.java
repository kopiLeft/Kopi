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

  // ----------------------------------------------------------------------
  // FUNCTIONS WHICH MUST BE IMPLEMENTED BY SUB-CLASSES
  // ----------------------------------------------------------------------

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_DAYS/2: Adds a specified number of days to a given valid character
   * string representation of a date.
   */
  protected String translateAddDays(String arg1, String arg2) throws SQLException {
    return "ADD_DAYS(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_YEARS/2:Adds a specified number of years to a given valid character
   * string representation of a date.
   */
  protected String translateAddYears(String arg1, String arg2) throws SQLException {
    return "ADD_YEARS(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CAST/2: Converts a value from one data type to a given target type.
   */
  protected String translateCast(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * COALESCE/2: Returns the first non-null expression of the two given
   * expression
   */
  protected String translateCoalesce(String arg1, String arg2) throws SQLException {
    return "NVL(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CONCAT/2: Returns a string that is the result of concatenating two string
   * values.
   */
  protected String translateConcat(String arg1, String arg2) throws SQLException {
    return "(" + arg1 + " || " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTDATE/0: Returns the current date in the connected user time zone.
   */
  protected String translateCurrentdate() throws SQLException {
    return "SYSDATE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIME/0: Returns the current database system time as a datetime
   * value.
   */
  protected String translateCurrenttime() throws SQLException {
    return "SYSDATE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIMESTAMP/0: Returns the current database system timestamp as a
   * datetime value.
   */
  protected String translateCurrenttimestamp() throws SQLException {
    return "SYSDATE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * DATEDIFF/2: Calculates the difference between two given dates in days.
   */
  protected String translateDatediff(String arg1, String arg2) throws SQLException {
    return "TO_CHAR(" + arg1 + "-" + arg2 + ", 'DD')";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * EXTRACT/2: Extracts and returns the value of a specified datetime field
   * from a datetime.
   */
  protected String translateExtract(String arg1, String arg2) throws SQLException {
    if (arg2.length() != 4 || arg2.charAt(0) != '\'' || arg2.charAt(3) != '\'') {
      throw new SQLException("invalid argument to EXTRACT/2: " + arg2);
    }

    return "(TO_CHAR(" + arg1 + ", " + arg2 +"))";
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
    throw new SQLException("NOT YET IMPLEMENTED");
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
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LAST_DAY/1: Returns the date of the last day of the month that contains
   * the given date.
   */
  protected String translateLastDay(String arg1) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LENGTH/1: Returns the length of a given string.
   */
  protected String translateLength(String arg1) throws SQLException {
    return "LENGTH (" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LEFT/2: Returns the left part of a character string with the specified
   * number of characters.
   */
  protected String translateLeft(String arg1, String arg2) throws SQLException {
    return "SUBSTR (" + arg1 + ", " + "1" + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOCATE/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected String translateLocate(String arg1, String arg2) throws SQLException {
    return "INSTR (" + arg2 + ", " + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOWER/1: Converts all characters in the specified string to lowercase.
   */
  protected String translateLower(String arg1) throws SQLException {
    return "LOWER (" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LPAD/2: Returns an expression, left-padded to a given length with blanks
   * or, when the expression to be padded is longer than the length specified
   * after padding, only that portion of the expression that fits into the
   * specified length.
   */
  protected String translateLpad(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LPAD/3: Returns an expression, left-padded to a given length with a given
   * string or, when the expression to be padded is longer than the length
   * specified after padding, only that portion of the expression that fits
   * into the specified length.
   */
  protected String translateLpad(String arg1, String arg2, String arg3) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LTRIM/1: Removes all blanks from the left end of a given string.
   */
  protected String translateLtrim(String arg1) throws SQLException {
    return "LTRIM (" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * MOD/2: Returns the remainder of the second argument divided by the first
   * argument.
   */
  protected String translateMod(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * MONTH/2: Converts the given year and month (YYYY and MM respectively) into
   * an integer representing the combined month in the form YYYYMM.
   */
  protected String translateMonth(String arg1, String arg2) throws SQLException {
    return "((" + arg1 + ") * 100 + (" + arg2 + "))";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * NEXTVAL/1: Increments the given sequence and returns its new current
   * value.
   */
  protected String translateNextval(String arg1) throws SQLException {
    return  arg1 + ".NEXTVAL";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * POSITION/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected String translatePosition(String arg1, String arg2) throws SQLException {
    return "INSTR (" + arg2 + ", " + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPEAT/2: Returns a string by repeating a given count times a given
   * expression.
   */
  protected String translateRepeat(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPLACE/3: Returns a string with every occurrence of a given searched
   * string replaced with given replacement string
   */
  protected String translateReplace(String arg1, String arg2, String arg3) throws SQLException {
    return "REPLACE (" + arg3 + ", " + arg1 + " , " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RIGHT/2: Returns the right part of a character string with the specified
   * number of characters.
   */
  protected String translateRight(String arg1, String arg2) throws SQLException {
    return "SUBSTR (" + arg1 + ", " +  "(" + "LENGTH (" + arg1 + ")" + ") - " + arg2 + " + 1" + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/0: Returns the address or the ID of the row.
   */
  protected String translateRowid() throws SQLException {
    return "ROWID";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/1: Returns the address or the ID of the row in a given table.
   */
  protected String translateRowid(String arg1) throws SQLException {
    return  arg1 + ".ROWID";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/0: Returns the address or the ID of the row.
   */
  protected String translateRowidext() throws SQLException {
    return "ROWID";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/1: Returns the address or the ID of the row of a given external
   * table.
   */
  protected String translateRowidext(String arg1) throws SQLException {
    return "(" + arg1 + ".ROWID)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWNO/0: Returns the sequential number of a row within a partition of a
   * result set, starting at 1 for the first row in each partition.
   */
  protected String translateRowno() throws SQLException {
    return "ROWNUM";
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
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RTRIM/1: Removes all blanks from the right end of a given string.
   */
  protected String translateRtrim(String arg1) throws SQLException {
    return "RTRIM (" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SPACE/1: Returns a given number of spaces.
   */
  protected String translateSpace(String arg1) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * STRING2INT/1: Converts the given string number into an integer.
   */
  protected String translateString2int(String arg1) throws SQLException {
    return "TO_NUMBER(" + arg1 + ")";
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
    return "SUBSTR (" + arg1 + ", " + arg2 + ")";
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
    throw new SQLException("NOT YET IMPLEMENTED");
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
    throw new SQLException("NOT YET IMPLEMENTED");
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
    return "((TO_CHAR(" + arg1 + ", 'YYYY')) * 100 + (TO_CHAR(" + arg1 + ", 'MM')))";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_NUMBER/1: Converts a given expression to a value of Integer type.
   */
  protected String translateToNumber(String arg1) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUNC/2: Returns the first given number truncated to second number decimal
   * places.
   */
  protected String translateTrunc(String arg1, String arg2) throws SQLException {
    throw new SQLException("NOT YET IMPLEMENTED");
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUNC_DATE/1: returns date with the time portion of the day truncated.
   * The value returned is always of database date type.
   */
  protected String translateTruncDate(String arg1) throws SQLException {
    return "TRUNC(" + arg1 + ")";
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
    return "UPPER (" + arg1 + ")";
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
    return "((" + arg1 + ") * 100 + (" + arg1 + "))";
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------

}
