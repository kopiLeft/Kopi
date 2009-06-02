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

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;

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
   * Give an access to an user or create an user
   *
   *
   * @param     conn            the connection
   * @param     user            the login of the user
   */
  public void grantAccess(Connection conn, String user) throws SQLException {
    executeSQL(conn, "CREATE USER " + user + " PASSWORD 2change NOT EXCLUSIVE");
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
   * Change the password of the current user
   */
  public void changePassword(Connection conn,
                             String user,
                             String oldPassword,
                             String newPassword)
    throws SQLException
  {
    executeSQL(conn, "ALTER PASSWORD " + oldPassword + " TO " + newPassword);
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
  
  /**
   * Converts a function call in JDBC syntax to native syntax
   *
   * @param     function        the name of the function
   * @param     arguments       the arguments to the function
   * @return    a string representing the function call in native syntax
   *            or null no specific translation is defined for the function
   */
  public String convertFunctionCall(String functor, Vector arguments)
    throws SQLException
  {
    Object      function = functions.get(functor.toUpperCase() + "/" + arguments.size());

    if (function == null) {
      return null;
    } else {
      // ugly, but more efficient than creating a command class for each functor

      switch (((Integer)function).intValue()) {
      case 0:   // function has same syntax in native SQL
        {
          StringBuffer  buffer = new StringBuffer();

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

      case 1:   // TOMONTH/1
        return "(YEAR(" +  arguments.elementAt(0) + ") * 100 + MONTH(" + arguments.elementAt(0) + "))";

      case 2:   // ADD_DAYS/2
        return "(ADDDATE(" +  arguments.elementAt(0) + ","+ arguments.elementAt(1) + "))";

      case 4:   // MONTH/2
        return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 5:   // EXTRACT/2
        {
          String        arg1 = (String)arguments.elementAt(1);

          if (arg1.length() != 4 || arg1.charAt(0) != '\'' || arg1.charAt(3) != '\'') {
            throw new SQLException("invalid argument to EXTRACT/2: " + arg1);
          }

          return "(" + extract(arg1.substring(1, 3), arguments.elementAt(0)) + ")";
        }

      case 7:   // POSITION/2
        return "INDEX(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";

      case 8:   // SUBSTRING/2
        return "SUBSTRING(" + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 9:   // SUBSTRING/3
        return "SUBSTRING(" + arguments.elementAt(0) + "," + arguments.elementAt(1)  + "," + arguments.elementAt(2) + ")";

      case 10:  // SIZE_OF/1
        return "LENGTH(" + arguments.elementAt(0) + ")";

      case 11:  // UPPER/1
        return "UPPER(" + arguments.elementAt(0) + ")";

      case 12:  // LOWER/1
        return "LOWER(" + arguments.elementAt(0) + ")";

      case 13:  // LTRIM/1
        return "LTRIM(" + arguments.elementAt(0) + ")";

      case 14:  // RTRIM/1
        return "RTRIM(" + arguments.elementAt(0) + ")";

      case 15:  // REPLACE/3
        return "REPLACE(" + arguments.elementAt(2) + "," + arguments.elementAt(0) + "," + arguments.elementAt(1) + ")";

      case 16:  // REPEAT/2
        return "LFILL('','" + arguments.elementAt(0) + "'," + arguments.elementAt(1) + ")";

      case 17:  // CURRENTDATE
        return "DATE";

      case 18:  // USER
        return "LOWER(USER)";

      case 19:  // SPACE
        return "LFILL('',' '," + arguments.elementAt(1) + ")";

      case 20:  // RIGHT
        return "RFILL(" + arguments.elementAt(0) + ",' '," + arguments.elementAt(1) + ")";

      case 21: // LEFT
        return "LFILL(" + arguments.elementAt(0) + ",' '," + arguments.elementAt(1) + ")";

      case 22: // CONCAT
        // !!! coco 010201 : looks to be like it but we have to test it
        return arguments.elementAt(0) + "||" + arguments.elementAt(1);

      case 23: // LOCATE
        return "INDEX(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";

      case 24:  // TRUE
        return "TRUE";

      case 25:  // FALSE
        return "FALSE";

      case 26: // LENGTH
        return "LENGTH(" + arguments.elementAt(0) + ")";

      case 27:  // CURRENTTIME
        return "TIME";

      case 28:  // CURRENTTIMESTAMP
        return "TIMESTAMP";

      case 29:  // WEEK/2
        return "((" + arguments.elementAt(0) + ") * 100 + (" + arguments.elementAt(1) + "))";

      case 30: // DATEDIFF/2
        {
          String        left = (String)arguments.elementAt(0);
          String        right = (String)arguments.elementAt(1);
          StringBuffer  buffer = new StringBuffer();

          buffer.append("SIGN(");
          buffer.append("YEAR(" + left + ") * 1000 + ");
          buffer.append("DAYOFYEAR(" + left + ") - ");
          buffer.append("YEAR(" + right + ") * 1000 - ");
          buffer.append("DAYOFYEAR(" + right + ")) * ");
          buffer.append("DATEDIFF(" + left + "," + right + ")");

          return buffer.toString();
        }
      case 31: // COALESCE/2
        return "VALUE(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 32: // ROWNO/0
        return "ROWNO";

      case 33: // STRING2INT/1
        return "(0 + NUM(" + arguments.elementAt(0) + "))";
      case 34: // GREATEST/1
	return "GREATEST(" + arguments.elementAt(0) + ", " +  arguments.elementAt(1) + ")";
      case 35:
        return  arguments.elementAt(0) + " || ''";
      case 36: // MOD/2
        return  "MOD(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";
      case 37: // NEXTVAL/1
        return  arguments.elementAt(0) + ".NEXTVAL";
      
      case 38: // CAST/2
        return  cast((String)arguments.elementAt(0), (String)arguments.elementAt(1));
      case 39: // NVL/2
        return "VALUE(" + arguments.elementAt(0) + ", " + arguments.elementAt(1) + ")";

      case 40: // TO_NUMBER/1
        return "NUM(" + arguments.elementAt(0) + ")";

      case 41: // INSTR/2
        return "INDEX(" + arguments.elementAt(1) + "," + arguments.elementAt(0) + ")";
        
      case 42:  // SYSDATE/0
        return "DATE";

      case 43:  // TRUNC_DATE/1
        return "DATE(" + arguments.elementAt(0) + ")";
        
      case 44:  // TO_CHAR/1
        return "CHR(" + arguments.elementAt(0) + ")";

      case 45:  // TO_CHAR/2
        String format = (String)arguments.elementAt(1);
                
        if (format.equalsIgnoreCase("'YYMMDD'")) {
          return "SUBSTR(CHR(YEAR(" + arguments.elementAt(0) + ")), 3, 2) || LFILL(CHR(MONTH(" + arguments.elementAt(0) + ")), '0', 2) || LFILL(CHR(DAY(" + arguments.elementAt(0) + ")), '0', 2)";
        } else if (format.equalsIgnoreCase("'YYMM'")) {
          return "SUBSTR(CHR(YEAR(" + arguments.elementAt(0) + ")), 3, 2) || LFILL(CHR(MONTH(" + arguments.elementAt(0) + ")), '0', 2)";
        } else if (format.equalsIgnoreCase("'YYYYMMDD'")) {
          return "CHR(YEAR(" + arguments.elementAt(0) + ")) || LFILL(CHR(MONTH(" + arguments.elementAt(0) + ")), '0', 2) || LFILL(CHR(DAY(" + arguments.elementAt(0) + ")), '0', 2)";
        } else {
          throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR {fn " + functor.toUpperCase() + ", " + arguments.elementAt(1) + "}");
        }
        
      case 46:  // TO_DATE/1
        return "DATE(" + arguments.elementAt(0) + ")";
        
      case 47:  // TO_DATE/2
        format = (String)arguments.elementAt(1);
        
        if (format.equalsIgnoreCase("'YYMM'")) {
          return "DATE('20' || SUBSTR(" + arguments.elementAt(0) + ", 1, 2) || '-' || SUBSTR(" + arguments.elementAt(0) + ", 3, 2) || '-01')";
        } else if (format.equalsIgnoreCase("'YYMMDD'")) {
          return "DATE('20' || SUBSTR(" + arguments.elementAt(0) + ", 1, 2) || '-' || SUBSTR(" + arguments.elementAt(0) + ", 3, 2) || '-' || SUBSTR(" + arguments.elementAt(0) + ", 5, 2))";
        } else {
          throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR {fn " + functor.toUpperCase() + ", " + arguments.elementAt(1) + "}");
        }

      case 48: // UCASE/1
        return "UPPER(" + arguments.elementAt(0) + ")";
        
      case 49: // LPAD/3
        return "LFILL(" + arguments.elementAt(0) + ", " + arguments.elementAt(2) + ", " + arguments.elementAt(1) + ")";

      case 50: // RPAD/3
        return "RFILL(" + arguments.elementAt(0) + ", " + arguments.elementAt(2) + ", " + arguments.elementAt(1) + ")";
      
      case 51: // ROWID/0
        return "HEX(SYSKEY)";

      case 52: // ROWID/1
        String tablename;

        tablename = ((String)arguments.elementAt(0)).replaceAll("'","");
        return "HEX(" + tablename + ".SYSKEY)";

      case 53: // LAST_DAY/1
        String date = (String)arguments.elementAt(0);
        
        return "(SUBDATE(MAKEDATE(YEAR(" + date + "), DAYOFYEAR(" + date + ") - DAYOFMONTH(" + date + ") + 32), DAYOFMONTH(MAKEDATE(YEAR(" + date + "), DAYOFYEAR(" + date + ") - DAYOFMONTH(" + date + ") + 32))))";
        
      default:
        throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR " + functor.toUpperCase() +
                                         "/" + arguments.size());
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private static Hashtable      functions;

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
    functions.put("GREATEST/2", new Integer(34));
    functions.put("INT2STRING/1", new Integer(35));
    functions.put("MOD/2", new Integer(36));
    functions.put("NEXTVAL/1", new Integer(37));
    functions.put("CAST/2", new Integer(38));
    functions.put("NVL/2", new Integer(39));
    functions.put("TO_NUMBER/1", new Integer(40));
    functions.put("INSTR/2", new Integer(41));
    functions.put("SYSDATE/0", new Integer(42));
    functions.put("TRUNC_DATE/1", new Integer(43));
    functions.put("TO_CHAR/1", new Integer(44));
    functions.put("TO_CHAR/2", new Integer(45));
    functions.put("TO_DATE/1", new Integer(46));
    functions.put("TO_DATE/2", new Integer(47));
    functions.put("UCASE/1", new Integer(48));
    functions.put("LPAD/3", new Integer(49));
    functions.put("RPAD/3", new Integer(50));
    functions.put("ROWID/0", new Integer(51));
    functions.put("ROWID/1", new Integer(52));
    functions.put("LAST_DAY/1", new Integer(53));
  }
}
