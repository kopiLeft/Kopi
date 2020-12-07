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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;
import org.kopi.xkopi.lib.type.Month;
import org.kopi.xkopi.lib.type.Time;
import org.kopi.xkopi.lib.type.Timestamp;
import org.kopi.xkopi.lib.type.Week;

/**
 * This class represents kopi fixed types (not null)
 */

public class KopiUtils {
  /**
   * Returns the string representation in SQL/JDBC format of a boolean value.
   */
  public static final String toSql(boolean b) {
    return b ? "{fn TRUE}" : "{fn FALSE}";
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Boolean value.
   */
  public static final String toSql(Boolean b) {
    return b == null ? NULL_LITERAL : toSql(b.booleanValue());
  }

  /**
   * Returns the string representation in SQL/JDBC format of a long value.
   */
  public static final String toSql(int l) {
    return "" + l;
  }

  /**
   * Returns the string representation in SQL/JDBC format of a long value.
   */
  public static final String toSql(long l) {
    return "" + l;
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Byte value.
   */
  public static final String toSql(Byte l) {
    return l == null ? NULL_LITERAL : l.toString();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Short value.
   */
  public static final String toSql(Short l) {
    return l == null ? NULL_LITERAL : l.toString();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a string value.
   */
  public static final String toSql(String l) {
    if (l == null) {
      return NULL_LITERAL;
    } else {
      StringBuffer b = new StringBuffer();

      b.append('\'');
      for (int i = 0 ; i < l.length() ; ++i) {
	char c = l.charAt(i);

	if (c == '\'') {
	  b.append((char)'\'');
	}
	b.append(c);
      }
      b.append('\'');
      return b.toString();
    }
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Integer value.
   */
  public static final String toSql(Integer l) {
    return l == null ? NULL_LITERAL : l.toString();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Long value.
   */
  public static final String toSql(Long l) {
    return l == null ? NULL_LITERAL : l.toString();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Fixed value.
   */
  public static final String toSql(Fixed f) {
    return f == null ? NULL_LITERAL : f.toSql();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a float value.
   */
  public static final String toSql(float f) {
    return "" + f;
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Float value.
   */
  public static final String toSql(Float f) {
    return f == null ? NULL_LITERAL : f.toString();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a double value.
   */
  public static final String toSql(double d) {
    return "" + d;
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Double value.
   */
  public static final String toSql(Double d) {
    return d == null ? NULL_LITERAL : d.toString();
  }

  /**
   * Returns the string representation in SQL/JDBC format of an Object value.
   */
  public static final String toSql(Object o) {
    System.err.println("==============================> toSql(Object)" + o.getClass().getName());
    Thread.dumpStack();
    return o == null ? NULL_LITERAL : "?";
  }

  /**
   * Returns the string representation in SQL/JDBC format of a byte array value.
   */
  public static final String toSql(byte[] ba) {
    return ba == null ? NULL_LITERAL : "?";
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Double value.
   */
  public static final String toSql(KopiSerializable ser) {
    return ser == null ? NULL_LITERAL : "?";
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Date value.
   */
  public static final String toSql(Date d) {
    return d == null ? NULL_LITERAL : d.toSql();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Month value.
   */
  public static final String toSql(Month m) {
    return m == null ? NULL_LITERAL : m.toSql();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Time value.
   */
  public static final String toSql(Time t) {
    return t == null ? NULL_LITERAL : t.toSql();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Time value.
   */
  public static final String toSql(Timestamp t) {
    return t == null ? NULL_LITERAL : t.toSql();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Week value.
   */
  public static final String toSql(Week t) {
    return t == null ? NULL_LITERAL : t.toSql();
  }

  /**
   * Returns the string representation in SQL/JDBC format of a char value.
   */
  public static final String toSql(char c) {
    return "" + c;
  }

  /**
   * Returns the string representation in SQL/JDBC format of a Character value.
   */
  public static final String toSql(Character c) {
    return c == null ? NULL_LITERAL : c.toString();
  }

  /**
   * Executes a query.
   */
  public static final int executeUpdate(Connection conn, String text, Object[] blobs)
    throws DBException
  {
    int                 count;
    long                timer;

    timer = System.currentTimeMillis();
    Query.traceQuery(Query.TRL_QUERY, "UPDATE", text);

    try {
      PreparedStatement stmt;

      stmt = conn.prepareStatement(conn.convertSql(text));

      if (blobs != null) {
        try {
          for (int i = 0; i < blobs.length; i++) {
            byte[]        data;

            if (blobs[i] instanceof KopiSerializable) {
              data = ((KopiSerializable)blobs[i]).toKopiData();
            } else if (blobs[i] instanceof byte[]) {
              data = (byte[])blobs[i];
            } else {
              ByteArrayOutputStream       outer;
              ObjectOutputStream          inner;

              outer = new ByteArrayOutputStream();
              inner = new ObjectOutputStream(outer);
              inner.writeObject(blobs[i]);
              inner.flush();
              outer.close();

              data = outer.toByteArray();
            }
            stmt.setBinaryStream(i + 1, new ByteArrayInputStream(data), data.length);
          }
        } catch (IOException e) {
          throw new DBInvalidDataException(e);
        }
      }

      count = stmt.executeUpdate();
      stmt.close();
    } catch (SQLException e) {
      throw conn.convertException(text, e);
    }

    Query.traceTimer(Query.TRL_QUERY, "UPDATE", timer);

    return count;
  }

  /**
   * Executes a query.
   */
  public static final int executeUpdate(Connection conn, String text) throws DBException {
    int                 count;
    long                timer;

    timer = System.currentTimeMillis();
    Query.traceQuery(Query.TRL_QUERY, "UPDATE", text);

    try {
      Statement           stmt;

      stmt = conn.createStatement();
      count = stmt.executeUpdate(conn.convertSql(text));
      stmt.close();
    } catch (SQLException e) {
      throw conn.convertException(text, e);
    }

    Query.traceTimer(Query.TRL_QUERY, "UPDATE", timer);

    return count;
  }

  /**
   * Creates the insert statement.
   */
  private static final String createKopiInsertStatement(Connection conn,
                                                        String table,
                                                        int id,
                                                        String[] columns,
                                                        String[] values)
  {
    StringBuffer	buffer = new StringBuffer();
    int			ts = (int)(System.currentTimeMillis()/1000);

    buffer.append("INSERT INTO " + table + "(ID, TS");
    for (int i = 0; i < columns.length; i++) {
      buffer.append(", " + columns[i]);
    }
    buffer.append(") VALUES (" + id + ", " + ts);
    for (int i = 0; i < columns.length; i++) {
      buffer.append(", " + values[i]);
    }
    buffer.append(")");

    return buffer.toString();
  }

  /**
   * Inserts a new record with ID and TS into the database.
   */
  public static final int executeKopiInsert(Connection conn,
					    String table,
					    String[] columns,
					    String[] values,
                                            Object[] blobs)
    throws DBException
  {
    int         id;

    id = getNextTableId(conn, table);
    executeUpdate(conn,
                  createKopiInsertStatement(conn, table, id, columns, values),
                  blobs);
    return id;
  }

  /**
   * Inserts a new record with ID and TS into the database.
   */
  public static final int executeKopiInsert(Connection conn,
					    String table,
					    String[] columns,
					    String[] values)
    throws DBException
  {
    int         id;

    id = getNextTableId(conn, table);
    executeUpdate(conn,
                  createKopiInsertStatement(conn, table, id, columns, values));
    return id;
  }

  /*
   * Returns first free ID of table.
   */
  public static final int getNextTableId(Connection conn, String table)
    throws DBException
  {
    String       getSeqNextVal;

    getSeqNextVal = "SELECT  {fn NEXTVAL(" + table + "Id)} FROM " + conn.getDualTableName();
    try {
      ResultSet		  rset;
      Statement           stmt;
      int		  id;

      stmt = conn.createStatement();
      rset = stmt.executeQuery(conn.convertSql(getSeqNextVal));
      if (!rset.next()) {
        stmt.close();
        throw new DBRuntimeException("Database Internal Error");
      }
      id = rset.getInt(1);
      stmt.close();
      return id;
    } catch (SQLException exc) {
      throw conn.convertException(getSeqNextVal, exc);
    }
  }

  /**
   * Creates the insert statement.
   */
  private static final String createInsertStatement(Connection conn,
                                                    String table,
                                                    String[] columns,
                                                    String[] values)
  {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("INSERT INTO " + table + "(");
    for (int i = 0; i < columns.length; i++) {
      if (i != 0) {
        buffer.append(", ");
      }
      buffer.append(columns[i]);
    }
    buffer.append(") VALUES (");
    for (int i = 0; i < columns.length; i++) {
      if (i != 0) {
        buffer.append(", ");
      }
      buffer.append(values[i]);
    }
    buffer.append(")");

    return buffer.toString();
  }

  /**
   * Inserts a new record into the database.
   */
  public static final int executeInsert(Connection conn,
                                        String table,
                                        String[] columns,
                                        String[] values,
                                        Object[] blobs)
    throws DBException
  {
    return executeUpdate(conn,
                         createInsertStatement(conn, table, columns, values),
                         blobs);
  }

  /**
   * Inserts a new record into the database.
   */
  public static final int executeInsert(Connection conn,
                                        String table,
                                        String[] columns,
                                        String[] values)
    throws DBException
  {
    return executeUpdate(conn,
                         createInsertStatement(conn, table, columns, values));
  }


  /*
   * Removes all white space characters from both ends of the specified
   * string and replaces multiple spaces between words by a single one.
   *
   * @param	input		the string to trim
   * @return	the trimmed string
   *
   * !!! Note: This method should be moved to org.kopi.xkopi.lib.base.Utils
   */
  public static String trimString(String input) {
    char[]	buffer = new char[input.length()];
    int		bufpos = 0;
    int		state  = 0;

    for (int i = 0; i < input.length(); i++) {
      char	c = input.charAt(i);

      if (Character.isWhitespace(c)) {
	state = (state == 0 ? 0 : 2);
      } else {
	if (state == 2) {
	  buffer[bufpos++] = ' ';
	}
	buffer[bufpos++] = c;
	state = 1;
      }
    }

    return bufpos == 0 ? "" : new String(buffer, 0, bufpos);
  }

  /*
   * Removes all white space characters from the end of the specified
   * string.
   *
   * @param	input		the string to trim
   * @return	the trailed string
   *
   * !!! Note: This method should be moved to org.kopi.xkopi.lib.base.Utils
   */
  public static String trailString(String input) {
    int			last = -1;

    for (int i = input.length() - 1; last == -1 && i >= 0; --i) {
      if (! Character.isWhitespace(input.charAt(i))) {
	last = i;
      }
    }

    if (last == -1) {
      return "";
    } else if (last == input.length()) {
      return input;
    } else {
      return input.substring(0, last + 1);
    }
  }

  /**
   * Check if the specified string is empty.
   */
  public static boolean isEmpty(String s) {
    return s == null || s.trim().length() == 0;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */
  public static final String NULL_LITERAL = "NULL";
}
