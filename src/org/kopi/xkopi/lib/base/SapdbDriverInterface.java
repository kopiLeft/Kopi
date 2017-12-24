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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.kopi.util.base.InconsistencyException;

/**
 * This class encapsulates specific properties of the SAPDB JDBC driver.
 */
public class SapdbDriverInterface extends DriverInterface {

  /**
   * Constructs a new driver interface object
   */
  public SapdbDriverInterface() {
    super();
  }

  /**
   * Count the maximum size for an ORDER BY clause on the
   * driver (data base)
   *
   * @return    the maximum number of characters in the ORDER BY of the driver
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
   * @return    the maximum number of columns in an ORDER BY clause
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
   * @param     conn            the connection
   * @param     user            the login of the user
   * @param     password        the initial password
   */
  public void grantAccess(Connection conn, String user, String password) throws SQLException {
    executeSQL(conn, "CREATE USER " + user + " PASSWORD " + password + " NOT EXCLUSIVE");
  }

  /**
   * Removed an access to an user or delete an user
   *
   *
   * @param     conn            the connection
   * @param     user            the login of the user
   */
  public void revokeAccess(Connection conn, String user) throws SQLException {
    executeSQL(conn, "DROP USER " + user);
  }

  /**
   * Change the password of the specified user. If user = NULL change the password
   * of the current user.
   */
  public void changePassword(Connection conn,
                             String user,
                             String oldPassword,
                             String newPassword)
    throws SQLException
  {
    if (user == null) {
      //  user changes its password
      executeSQL(conn, "ALTER PASSWORD " + oldPassword + " TO " + newPassword);
    } else {
      // admin resets password
      executeSQL(conn, "ALTER PASSWORD " +  user + " " + newPassword);
    }
  }

  /**
   * Locks a table in exclusive mode.
   *
   * @param     conn            the connection
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
   * @param     from            the SQL query string in KOPI syntax
   * @return    the string in the syntax of the driver
   */
  public String convertSql(String from) {
    try {
      SapdbParser       parser = new SapdbParser(from);
      String            nativeSql = parser.getText().trim();

      if (trace) {
        System.err.println("-- NATIVE SQL: ");
        System.err.println(nativeSql + ";");
        System.err.println("--");
      }
      // trace if system-property maxdb.tacer is set
      if (tracer != null) {
        try {
          tracer.println(nativeSql);
          tracer.flush();
        } catch (Exception e) {
        }
      }

      return nativeSql;
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
    case -50:   // Lock request timeout1
    case -51:   // Lock request timeout2
    case 500:   // Lock request timeout3
    case -307:  // Connection down, session released
    case 700:   // Session inactivity timeout (work rolled back)
    case 600:   //  Work rolled back,DEADLOCK DETECTED
      return new DBDeadLockException(query, from);

    case 250:   // Duplicate secondary key
    case -6008: // Duplicate index name
    case -8018: // Index name must be unique
      return parseDuplicateIndex(query, from);

    case -32:   // Integrity violation
      return parseIntegrityViolation(query, from);

    case -1402: // Integrity violation2
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
    return true;
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES METHODS
  // ----------------------------------------------------------------------

  /**
   * Parses a transbase duplicate index exception
   */
  private static DBDuplicateIndexException parseDuplicateIndex(String query, SQLException from) {
    String      mesg = from.getMessage();
    int         index = mesg.lastIndexOf("index ");

    return new DBDuplicateIndexException(query, from, mesg.substring(index + 6, mesg.length() - 2));
  }

  /**
   * Parses a transbase integrity violation exception
   */
  private static DBForeignKeyException parseIntegrityViolation(String query, SQLException from) {
    String      mesg = from.getMessage();
    int         index1 = mesg.indexOf("'");
    int         index2 = mesg.lastIndexOf("'");

    return new DBForeignKeyException(query, from, mesg.substring(index1 + 1, index2));
  }

  /**
   * with java -Dmaxdb.trace=FILENAME it is possible to log every
   * sql-statement
   */
  static {
    if (System.getProperty("maxdb.trace") != null) {
      try {
        tracer = new PrintStream(new FileOutputStream(System.getProperty("maxdb.trace"), true), true);
      } catch (Exception e) {
        //tracer = null;
      }
    } else {
      tracer = null;
    }
  }

  private static PrintStream      tracer;
}


/**
 * This class translates SQL queries from JDBC format to native format.
 */
/*package*/ class SapdbParser extends JdbcParser {
  /**
   * Constructor
   */
  public SapdbParser(String input) {
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
   * @param     type    type of data to extract ('YY', 'MO', ...)
   * @param     date    date
   * @return    a string representing the data extracted
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
   * Converts a date into native syntax
   * @param     yy      year
   * @param     mo      month
   * @param     dd      day
   * @return    a string representing the date in native syntax
   */
  protected String convertDate(int yy, int mo, int dd) {
    return "'" + formatInteger(yy, 4) +  "-" + formatInteger(mo, 2) +  "-" + formatInteger(dd, 2) + "'"; // ISO
  }

  /**
   * Converts a time into native syntax
   * @param     hh      hour
   * @param     mi      minute
   * @param     ss      second
   * @return    a string representing the time in native syntax
   */
  protected String convertTime(int hh, int mi, int ss) {
    return "'" + hh + ":" + mi + ":" + ss + "'"; // ISO
  }

  /**
   * Converts a timestamp into native syntax
   * @param     yy      year
   * @param     mo      month
   * @param     dd      day
   * @param     hh      hour
   * @param     mi      minute
   * @param     ss      second
   * @param     ns      nanosecond
   * @return    a string representing the timestamp in native syntax
   */
  protected String convertTimestamp(int yy, int mo, int dd, int hh, int mi, int ss, int ns) {
    return "'"
      + formatInteger(yy, 4) + "-" + formatInteger(mo, 2) + "-" + formatInteger(dd, 2)
      + " " + formatInteger(hh, 2) + ":" + formatInteger(mi, 2) + ":" + formatInteger(ss, 2)
      + "." + formatInteger(ns/1000, 6)
      + "'"; // ISO
  }

  private static String formatInteger(int value, int length) {
    StringBuffer        buffer = new StringBuffer(length);

    for (int i = length - ("" + value).length(); i > 0; i--) {
      buffer.append("0");
    }
    buffer.append(value);

    return buffer.toString();
  }
  
  private String cast(String object, String type) {
    if (type.startsWith("INT") || type.startsWith("NUMERIC") || type.startsWith("FIXED")) {
      return "NUM(" + object + ")";
    } else if (type.startsWith("STRING") || type.startsWith("CHAR")) {
      return "CHR(" + object + ")";
    } else if (type.equals("DATETIME[YY:DD]")) {
      return "CHAR(" + object + ", " + "'YYYY-MM-DD')";
    } else if (type.equals("TIMESPAN[MI]")) {
      return "CHAR(" + object + ", " + "'MM')";
    } else {
      return object;
    }
  }
  
  // ----------------------------------------------------------------------
  // FUNCTIONS WHICH MUST BE IMPLEMENTED BY SUB-CLASSES
  // ----------------------------------------------------------------------

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ABS/1: Computes the absolute value of a number.
   */
  protected String translateAbs(String arg1) {
    return "ABS(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_DAYS/2: Adds a specified number of days to a given valid character
   * string representation of a date.
   */
  protected String translateAddDays(String arg1, String arg2) throws SQLException {
    return "ADDDATE(" +  arg1 + ", " + arg2 + ")";
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
    return cast(arg1, arg2);
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
    return "(" + arg1 + "||" + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTDATE/0: Returns the current date in the connected user time zone.
   */
  protected String translateCurrentdate() throws SQLException {
    return "DATE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIME/0: Returns the current database system time as a datetime
   * value.
   */
  protected String translateCurrenttime() throws SQLException {
    return "TIME";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIMESTAMP/0: Returns the current database system timestamp as a
   * datetime value.
   */
  protected String translateCurrenttimestamp() throws SQLException {
    return "TIMESTAMP";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * DATEDIFF/2: Calculates the difference between two given dates in days.
   */
  protected String translateDatediff(String arg1, String arg2) throws SQLException {
    String        left = arg1;
    String        right = arg2;
    StringBuffer  buffer = new StringBuffer();
    
    buffer.append("SIGN(");
    buffer.append("YEAR(" + left + ") * 1000 + ");
    buffer.append("DAYOFYEAR(" + left + ") - ");
    buffer.append("YEAR(" + right + ") * 1000 - ");
    buffer.append("DAYOFYEAR(" + right + ")) * ");
    buffer.append("DATEDIFF(" + left + "," + right + ")");
          
    return buffer.toString();
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
    
    return "(" + extract(arg2.substring(1, 3), arg1) + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * FALSE/0: Returns the boolean false value.
   */
  protected String translateFalse() throws SQLException {
    return "FALSE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * GREATEST/2: Returns the greatest of the given list of two expressions.
   */
  protected String translateGreatest(String arg1, String arg2) throws SQLException {
    return "GREATEST(" + arg1 + ", " +  arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * INSTR/2: Returns the position of the first occurrence of a substring in a
   * host string.
   */
  protected String translateInstr(String arg1, String arg2) throws SQLException {
    return "INDEX(" + arg2 + "," + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * INT2STRING/1: Converts a given number to a value of string datatype.
   */
  protected String translateInt2string(String arg1) throws SQLException {
    return  "(" + arg1 + " || '')";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LAST_DAY/1: Returns the date of the last day of the month that contains
   * the given date.
   */
  protected String translateLastDay(String arg1) throws SQLException {
    return "(SUBDATE(MAKEDATE(YEAR(" + arg1 + "), DAYOFYEAR(" + arg1 + ") - DAYOFMONTH(" + arg1 + ") + 32), DAYOFMONTH(MAKEDATE(YEAR(" + arg1 + "), DAYOFYEAR(" + arg1 + ") - DAYOFMONTH(" + arg1 + ") + 32))))";
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
    return "LFILL(" + arg1 + ",' '," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOCATE/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected String translateLocate(String arg1, String arg2) throws SQLException {
    return "INDEX(" + arg2 + "," + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOWER/1: Converts all characters in the specified string to lowercase.
   */
  protected String translateLower(String arg1) throws SQLException {
    return "LOWER(" + arg1 + ")";
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
    return "LFILL(" + arg1 + ", " + arg3 + ", " + arg2 + ")";
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
    return  "MOD(" + arg1 + ", " + arg2 + ")";
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
    return "INDEX(" + arg2 + "," + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPEAT/2: Returns a string by repeating a given count times a given
   * expression.
   */
  protected String translateRepeat(String arg1, String arg2) throws SQLException {
    return "LFILL('','" + arg1 + "'," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPLACE/3: Returns a string with every occurrence of a given searched
   * string replaced with given replacement string
   */
  protected String translateReplace(String arg1, String arg2, String arg3) throws SQLException {
    return "REPLACE(" + arg3 + "," + arg1 + "," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RIGHT/2: Returns the right part of a character string with the specified
   * number of characters.
   */
  protected String translateRight(String arg1, String arg2) throws SQLException {
    return "RFILL(" + arg1 + ",' '," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROUND/2: Returns the number given as first argument rounded to the number of digits given
   * as second argument.
   */
  protected String translateRound(String arg1, String arg2) throws SQLException {
    return "ROUND(" + arg1 + ", " + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/0: Returns the address or the ID of the row.
   */
  protected String translateRowid() throws SQLException {
    return "SYSKEY";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/1: Returns the address or the ID of the row in a given table.
   */
  protected String translateRowid(String arg1) throws SQLException {
    String        tablename;
          
    tablename = arg1.replaceAll("'", "");
    return "(" + tablename + ".SYSKEY)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/0: Returns the address or the ID of the row.
   */
  protected String translateRowidext() throws SQLException {
    return "HEX(SYSKEY)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/1: Returns the address or the ID of the row of a given external
   * table.
   */
  protected String translateRowidext(String arg1) throws SQLException {
    String        tablename;
          
    tablename = arg1.replaceAll("'", "");
    return "HEX(" + tablename + ".SYSKEY)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWNO/0: Returns the sequential number of a row within a partition of a
   * result set, starting at 1 for the first row in each partition.
   */
  protected String translateRowno() throws SQLException {
    return "ROWNO";
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
    return "RFILL(" + arg1 + ", " + arg3 + ", " + arg2 + ")";
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
   * SIGN/1: Returns the sign of the number. The sign is -1 for negative numbers,
   * 0 for zero and +1 for positive numbers. 
   */
  protected String translateSign(String arg1) {
    return "SIGN(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SPACE/1: Returns a given number of spaces.
   */
  protected String translateSpace(String arg1) throws SQLException {
    return "LFILL('',' '," + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * STRING2INT/1: Converts the given string number into an integer.
   */
  protected String translateString2int(String arg1) throws SQLException {
    return "(0 + NUM(" + arg1 + "))";
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
    return "SUBSTRING(" + arg1 + "," + arg2 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SUBSTRING/3: Returns a portion of a given string beginning at a given
   * position having the given length (third parameter).
   */
  protected String translateSubstring(String arg1, String arg2, String arg3) throws SQLException {
    return "SUBSTRING(" + arg1 + "," + arg2  + "," + arg3 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_CHAR/1: Converts a given number to a value of string datatype.
   */
  protected String translateToChar(String arg1) throws SQLException {
    return "CHR(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_CHAR/2: Converts a given valid character string representation of a
   * date into a given format and returns a date in database date format.
   */
  protected String translateToChar(String arg1, String arg2) throws SQLException {
    if (arg2.equalsIgnoreCase("'YYMMDD'")) {
      return "SUBSTR(CHR(YEAR(" + arg1 + ")), 3, 2) || LFILL(CHR(MONTH(" + arg1 + ")), '0', 2) || LFILL(CHR(DAY(" + arg1 + ")), '0', 2)";
    } else if (arg2.equalsIgnoreCase("'YYMM'")) {
      return "SUBSTR(CHR(YEAR(" + arg1 + ")), 3, 2) || LFILL(CHR(MONTH(" + arg1 + ")), '0', 2)";
    } else if (arg2.equalsIgnoreCase("'YYYYMMDD'")) {
      return "CHR(YEAR(" + arg1 + ")) || LFILL(CHR(MONTH(" + arg1 + ")), '0', 2) || LFILL(CHR(DAY(" + arg1 + ")), '0', 2)";
    } else {
      throw new InconsistencyException("invalid format + " + arg2 + " for function TO_CHAR/2");
    }
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_DATE/1: Converts a given datetime into a date format.
   */
  protected String translateToDate(String arg1) throws SQLException {
    return "DATE(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_DATE/2: Converts a given valid character string representation of a
   * date into a given format and returns a date in database date format.
   */
  protected String translateToDate(String arg1, String arg2) throws SQLException {
    if (arg2.equalsIgnoreCase("'YYMM'")) {
      return "DATE('20' || SUBSTR(" + arg1 + ", 1, 2) || '-' || SUBSTR(" + arg1 + ", 3, 2) || '-01')";
    } else if (arg2.equalsIgnoreCase("'YYMMDD'")) {
      return "DATE('20' || SUBSTR(" + arg1 + ", 1, 2) || '-' || SUBSTR(" + arg1 + ", 3, 2) || '-' || SUBSTR(" + arg1 + ", 5, 2))";
    } else {
      throw new InconsistencyException("invalid format + " + arg2 + " for function TO_DATE/2");
    }
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TOMONTH/1: Returns the month (as integer) of a date in the form YYYYMM,
   * the argument must be a valid character string representation of a date in
   * the form YYYY-MM-DD.
   */
  protected String translateTomonth(String arg1) throws SQLException {
    return "(YEAR(" +  arg1 + ") * 100 + MONTH(" + arg1 + "))";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_NUMBER/1: Converts a given expression to a value of Integer type.
   */
  protected String translateToNumber(String arg1) throws SQLException {
    return "NUM(" + arg1 + ")";
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
    return "DATE(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUE/0: Returns the boolean true value.
   */
  protected String translateTrue() throws SQLException {
    return "TRUE";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * UPPER/1: Converts all characters in the specified string to uppercase.
   */
  protected String translateUpper(String arg1) throws SQLException {
    return "UPPER(" + arg1 + ")";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * USER/0:Returns the name of the current user.
   */
  protected String translateUser() throws SQLException {
    return "LOWER(USER)";
  }

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * WEEK/2: Returns the week number (from 1 to 53) for a given date.
   */
  protected String translateWeek(String arg1, String arg2) throws SQLException {
    return "((" + arg1 + ") * 100 + (" + arg2 + "))";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
}
