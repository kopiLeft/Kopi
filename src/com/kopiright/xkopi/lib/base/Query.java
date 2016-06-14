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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.Month;
import com.kopiright.xkopi.lib.type.NotNullDate;
import com.kopiright.xkopi.lib.type.NotNullFixed;
import com.kopiright.xkopi.lib.type.NotNullMonth;
import com.kopiright.xkopi.lib.type.NotNullTime;
import com.kopiright.xkopi.lib.type.NotNullTimestamp;
import com.kopiright.xkopi.lib.type.NotNullWeek;
import com.kopiright.xkopi.lib.type.Time;
import com.kopiright.xkopi.lib.type.Timestamp;
import com.kopiright.xkopi.lib.type.Week;

public class Query {

  /**
   * Creates a query for database connection <conn>.
   */
  public Query(Connection conn) {
    this.conn = conn;
    this.pars = new LinkedList();
    this.lobs = new LinkedList();
    this.name = "";
  }

  /**
   * Creates a query.
   */
  public Query(DBContextHandler env) {
    this(env.getDBContext().getDefaultConnection());
  }

  /**
   * Sets the next parameter to a null value.
   */
  public void addNull() {
    pars.add(null);
  }

  /**
   * Sets the next parameter to a fixed value.
   */
  public void addFixed(Fixed value) {
    if (value == null) {
      addNull();
    } else {
      pars.add(value);
    }
  }

  /**
   * Sets the next parameter to a boolean value.
   */
  public void addBoolean(boolean value) {
    pars.add(new Boolean(value));
  }

  /**
   * Sets the next parameter to a date value.
   */
  public void addDate(Date value) {
    if (value == null) {
      addNull();
    } else {
      pars.add(value);
    }
  }

  /**
   * Sets the next parameter to a month value.
   */
  public void addMonth(Month value) {
    if (value == null) {
      addNull();
    } else {
      pars.add(value);
    }
  }

  /**
   * Sets the next parameter to a week value.
   */
  public void addWeek(Week value) {
    if (value == null) {
      addNull();
    } else {
      pars.add(value);
    }
  }

  /**
   * Sets the next parameter to a int value.
   */
  public void addInt(int value) {
    pars.add(new Integer(value));
  }

  /**
   * Sets the next parameter to a string value.
   */
  public void addString(String value) {
    if (value == null) {
      addNull();
    } else {
      pars.add(value);
    }
  }

  /**
   * Sets the next parameter to a time value.
   */
  public void addTime(Time value) {
    if (value == null) {
      addNull();
    } else {
      pars.add(value);
    }
  }

  /**
   *
   */
  public void addBlob(InputStream is) {
    lobs.add(new LargeObject(is, true));
  }

  /**
   *
   */
  public void addClob(InputStream is) {
    lobs.add(new LargeObject(is, false));
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   *
   */
  public void open(String format) throws DBException {
    try {
      String            convertedSql;
      boolean           selectForUpdate;

      buildText(format);
      convertedSql = conn.convertSql(text);
      selectForUpdate = DBUtils.isSelectForUpdate(convertedSql);
      if (selectForUpdate) {
        stmt = conn.createStatement(ResultSet.CONCUR_UPDATABLE);
        if (conn.supportsCursorNames()) {
          name = "C" + nextCursorId++;
          stmt.setCursorName(name);
        }
      } else {
        stmt = conn.createStatement();
      }
      traceQuery(TRL_QUERY, "OPEN " + name);
      rset = stmt.executeQuery(convertedSql);
      traceTimer(TRL_QUERY, "OPEN " + name);
      //!!! wael 20090306 WORKAROUND FOR SAP DB BUG, this workaround is used also on Cursor.java
      if (conn.getDriverInterface() instanceof SapdbDriverInterface) {
        if (selectForUpdate) {
          rset.setFetchSize(1);
        }
      }
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("OPEN " + name, text), exc);
    }
  }

  /**
   *
   */
  public boolean next() throws DBException {
    try {
      boolean   ret;

      traceQuery(TRL_FETCH, "FETCH NEXT " + name);
      ret = rset.next();
      traceTimer(TRL_FETCH, "FETCH NEXT " + name);

      return ret;
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("FETCH NEXT " + name, rset), exc);
    }
  }

  /**
   *
   */
  public boolean previous() throws DBException {
    try {
      boolean   ret;

      traceQuery(TRL_FETCH, "FETCH PREV " + name);
      ret = rset.previous();
      traceTimer(TRL_FETCH, "FETCH PREV " + name);

      return ret;
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("FETCH PREV " + name, rset), exc);
    }
  }

  /**
   *
   */
  public boolean hasMore() throws DBException {
    try {
      boolean   ret;

      traceQuery(TRL_FETCH, "HAS MORE " + name);
      ret = rset.isLast();
      traceTimer(TRL_FETCH, "HAS MORE " + name);

      return ret;
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("HAS MORE " + name, rset), exc);
    }
  }

  /**
   * Positioned update
   */
  public int update(String format) throws DBException {
    if (! conn.supportsCursorNames()) {
      throw new DBRuntimeException(buildQueryForTrace("UPDPOS " + name, rset),
                                   "operation not supported by JDBC driver");
    }

    try {
      Statement         updater;
      int		count;

      buildText(format);

      traceQuery(TRL_QUERY, "UPDPOS " + name);

      if (!lobs.isEmpty()) {
	updater = createFilledPreparedStatement(text + " WHERE CURRENT OF " + rset.getCursorName());
        count = ((PreparedStatement)updater).executeUpdate();
        updater.close();
      } else {
	updater = conn.createStatement();
	count = updater.executeUpdate(conn.convertSql(text + " WHERE CURRENT OF " + rset.getCursorName()));
	updater.close();
      }

      traceTimer(TRL_QUERY, "UPDPOS " + name);

      return count;
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("UPDPOS " + name, text), exc);
    }
  }

  /**
   * Positioned delete
   */
  public int delete(String format) throws DBException {
    if (! conn.supportsCursorNames()) {
      throw new DBRuntimeException(buildQueryForTrace("DELPOS " + name, rset),
                                   "operation not supported by JDBC driver");
    }

    try {
      Statement	updater;
      int		count;

      buildText(format);

      traceQuery(TRL_QUERY, "DELPOS " + name);

      updater = conn.createStatement();
      count = updater.executeUpdate(conn.convertSql(text + " WHERE CURRENT OF " + rset.getCursorName()));
      updater.close();

      traceTimer(TRL_QUERY, "DELPOS " + name);

      return count;
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("DELPOS " + name, text), exc);
    }
  }

  /**
   *
   */
  public void close() throws DBException {
    try {
      traceQuery(TRL_FETCH, "CLOSE " + name);

      if (stmt != null) {
	stmt.close();
      }
      stmt = null;
      rset = null;

      traceTimer(TRL_FETCH, "CLOSE " + name);
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("CLOSE " + name, text), exc);
    }
  }

  /**
   *
   */
  public int run(String format) throws DBException {
    try {
      int		count;

      buildText(format);

      traceQuery(TRL_QUERY, "RUN");

      if (!lobs.isEmpty()) {
        stmt = createFilledPreparedStatement(conn.convertSql(text));
	count = ((PreparedStatement)stmt).executeUpdate();
        stmt.close();
      } else {
	stmt = conn.createStatement();
	count = stmt.executeUpdate(conn.convertSql(text));
        stmt.close();
      }

      traceTimer(TRL_QUERY, "RUN");

      return count;
    } catch (SQLException exc) {
      throw conn.convertException(buildQueryForTrace("QUERY " + name + " RUN", text), exc);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (primitive type)
  // ----------------------------------------------------------------------

  public boolean getBoolean(int pos) throws SQLException {
    boolean tmp = rset.getBoolean(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET BOOLEAN", rset));
    }
    return tmp;
  }

  public byte getByte(int pos) throws SQLException {
    byte tmp = rset.getByte(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET BYTE", rset));
    }
    return tmp;
  }

  public short getShort(int pos) throws SQLException {
    short tmp = rset.getShort(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET SHORT", rset));

    }
    return tmp;
  }

  public int getInt(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET SHORT", rset));
    }
    return tmp;
  }

  public NotNullFixed getFixed(int pos) throws SQLException {
    java.math.BigDecimal        tmp;

    tmp = rset.getBigDecimal(pos);

    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET FIXED", rset));
    }
    return new NotNullFixed(tmp);
  }

  public float getFloat(int pos) throws SQLException {
    float tmp = rset.getFloat(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET FLOAT", rset));
    }
    return tmp;
  }

  public double getDouble(int pos) throws SQLException {
    double tmp = rset.getDouble(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET DOUBLE", rset));
    }
    return tmp;
  }

  public char getChar(int pos) throws SQLException {
    String tmp = rset.getString(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET CHAR", rset));
    }
    return tmp.charAt(0);
  }

  public String getString(int pos) throws SQLException {
    return rset.getString(pos);
  }

  /**
   * java.sql.ResultSet.getString(int) always reads data as "ISO-8859-1"
   * when 'isUnicode' is set to true this will reads it as "UTF-8"
   */
  public String getString(int pos, boolean isUnicode) throws SQLException {
    if (!isUnicode) {
      return getString(pos);
    }

    String res;

    res = rset.getString(pos);
    if (res == null) {
      return null;
    }

    try {
      res = new String(res.getBytes("ISO-8859-1"), "UTF-8");
    } catch (java.io.UnsupportedEncodingException e) {
      throw new InconsistencyException(e);
    }

    return res;
  }

  public NotNullMonth getMonth(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET MONTH", rset));
    }
    return new NotNullMonth(tmp / 100, tmp % 100);
  }

  public NotNullTime getTime(int pos) throws SQLException {
    java.sql.Time tmp = rset.getTime(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET TIME", rset));
    }
    return new NotNullTime(tmp);
  }

  public NotNullTimestamp getTimestamp(int pos) throws SQLException {
    java.sql.Timestamp tmp = rset.getTimestamp(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET TIMESTAMP", rset));
    }
    return new NotNullTimestamp(tmp);
  }

  public NotNullDate getDate(int pos) throws SQLException {
    java.sql.Date tmp = rset.getDate(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET DATE", rset));
    }
    return new NotNullDate(tmp);
  }

  public NotNullWeek getWeek(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos
                             + " : " + buildQueryForTrace("QUERY " + name + " GET WEEK", rset));
    }
    return new NotNullWeek(tmp / 100, tmp % 100);
  }

  public Boolean getNullableBoolean(int pos) throws SQLException {
    boolean tmp = rset.getBoolean(pos);
    return rset.wasNull() ? null : new Boolean(tmp);
  }

  public Byte getNullableByte(int pos) throws SQLException {
    byte tmp = rset.getByte(pos);
    return rset.wasNull() ? null : new Byte(tmp);
  }

  public Short getNullableShort(int pos) throws SQLException {
    short tmp = rset.getShort(pos);
    return rset.wasNull() ? null : new Short(tmp);
  }

  public Integer getNullableInt(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    return rset.wasNull() ? null : new Integer(tmp);
  }

  public Fixed getNullableFixed(int pos) throws SQLException {
    java.math.BigDecimal        tmp;

    tmp = rset.getBigDecimal(pos);

    if (rset.wasNull()) {
      return null;
    }
    return new NotNullFixed(tmp);
  }

  public Float getNullableFloat(int pos) throws SQLException {
    float tmp = rset.getFloat(pos);
    return rset.wasNull() ? null : new Float(tmp);
  }

  public Double getNullableDouble(int pos) throws SQLException {
    double tmp = rset.getDouble(pos);
    return rset.wasNull() ? null : new Double(tmp);
  }

  public Character getNullableChar(int pos) throws SQLException {
    String tmp = rset.getString(pos);
    return rset.wasNull() ? null : new Character(tmp.charAt(0));
  }

  public String getNullableString(int pos) throws SQLException {
    return getString(pos);
  }

  public Time getNullableTime(int pos) throws SQLException {
    java.sql.Time tmp = rset.getTime(pos);
    if (rset.wasNull()) {
      return null;
    }
    return new NotNullTime(tmp);
  }

  public Timestamp getNullableTimestamp(int pos) throws SQLException {
    java.sql.Timestamp tmp = rset.getTimestamp(pos);
    if (rset.wasNull()) {
      return null;
    }
    return new NotNullTimestamp(tmp);
  }

  public Date getNullableDate(int pos) throws SQLException {
    java.sql.Date tmp = rset.getDate(pos);
    if (rset.wasNull()) {
      return null;
    }
    return new NotNullDate(tmp);
  }

  public Month getNullableMonth(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      return null;
    } else {
      return new NotNullMonth(tmp / 100, tmp % 100);
    }
  }

  public Week getNullableWeek(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      return null;
    } else {
      return new NotNullWeek(tmp / 100, tmp % 100);
    }
  }

  /**
   * Reads the stream in a byte array
   */
  public byte[] getByteArray(int pos) throws SQLException {
    InputStream	is = rset.getBinaryStream(pos);
    if (is == null) {
      return null;
    }

    ByteArrayOutputStream     out = new ByteArrayOutputStream();
    byte[]                    buf = new byte[2048];
    int                       nread;

    try {
      while ((nread = is.read(buf)) != -1) {
        out.write(buf, 0, nread);
      }
      return out.toByteArray();
    } catch (IOException e) {
      throw new InconsistencyException("INPUT STREAM BROKEN:" + e.getMessage());
    }
  }

  /**
   * Returns an object stored in database
   */
  public Object getSerializedObject(int pos) throws SQLException {
    InputStream is = rset.getBinaryStream(pos);

    if (is == null) {
      return null;
    } else {
      try {
	Object	result;

	result = new ObjectInputStream(is).readObject();
	is.close();
	return result;
      } catch (OptionalDataException e) {
	throw new DBInvalidDataException(buildQueryForTrace("QUERY " + name + " GET SERIALIZEDOBJECT",
                                                            rset),
                                         e);
      } catch (ClassNotFoundException e) {
	throw new DBInvalidDataException(buildQueryForTrace("QUERY " + name + " GET SERIALIZEDOBJECT",
                                                            rset),
                                         e);
      } catch (IOException e) {
	throw new DBInvalidDataException(buildQueryForTrace("QUERY " + name + " GET SERIALIZEDOBJECT",
                                                            rset),
                                         e);
      } finally {
      }
    }
  }

  /**
   * !!! A HACK FOR ORACLE
   */
  public Object getObject(int pos) throws SQLException {
    Object	obj = rset.getObject(pos);

    if (obj == null) {
      return null;
    } else if (obj instanceof java.math.BigDecimal) {
      // !!! A HACK FOR ORACLE
      // if the scale is -1, the scale is not precised.
      // else we use integer when the scale is 0 and the precision is less or equal to 10.

      BigDecimal        value = (java.math.BigDecimal)obj;

      if (rset.getMetaData().getScale(pos) != 0 || rset.getMetaData().getPrecision(pos) > 11) {
        return new NotNullFixed(value);
      } else if (rset.getMetaData().getPrecision(pos) == 11) {
        // graf 2007.07.12 - Terrible hack
        // Check whether the content fits into an integer
        if ((long)value.intValue() == value.longValue()) {
          return new Integer(value.intValue());
        } else {
          return new NotNullFixed(value);
        }
      } else {
        return new Integer(value.intValue());
      }
    } else if (obj instanceof java.sql.Date) {
      return new NotNullDate((java.sql.Date)obj);
    } else if (obj instanceof java.sql.Time) {
      return new NotNullTime((java.sql.Time)obj);
    } else if (obj instanceof java.sql.Timestamp) {
      return new NotNullTimestamp((java.sql.Timestamp)obj);
    } else if (obj instanceof Byte) {
      return new Integer(((Byte)obj).byteValue());
    } else if (obj instanceof Short) {
      return new Integer(((Short)obj).shortValue());
    } else {
      return obj;
    }
  }

  public Blob getBlob(int pos) throws SQLException {
    return rset.getBlob(pos);
  }

  public Clob getClob(int pos) throws SQLException {
    return rset.getClob(pos);
  }


  public boolean isNull(int pos) throws SQLException {
    return rset.getObject(pos) == null;
  }

  /**
   * Set the trace level
   *
   * @param     level   one of the Query.TRL_
   */
  public static void setTraceLevel(int level) {
    if (level < 0 || level >= (1 << (TRL_MAX + 1))) {
      throw new IllegalArgumentException("Bad level value: " + level);
    }
    traceLevel = level;
  }

  /**
   * Gets the trace level
   *
   * @return    the trace level, see Query.TRL_
   */
  private static boolean hasTraceLevel(int level) {
    return (traceLevel & (1 << level)) != 0;
  }

  /**
   * Traces the sql query
   */
  public static void traceQuery(int level, String operation, Object detail) {
    if (Query.hasTraceLevel(level)) {
      System.out.println(buildQueryForTrace(operation, detail));
    }
  }

  /**
   * Traces the sql query duration
   */
  public static void traceTimer(int level, String operation, long startTime) {
    if (Query.hasTraceLevel(level) && Query.hasTraceLevel(TRL_TIMER)) {
      System.out.println(buildQueryForTrace(operation + " TIME", (System.currentTimeMillis() - startTime) + " ms"));
    }
  }

  /**
   * Build the sql query string for tracing
   */
  public static String buildQueryForTrace(String operation, Object detail) {
    StringBuffer        buffer;

    buffer = new StringBuffer();
    buffer.append(System.currentTimeMillis());
    buffer.append(" ");
    buffer.append(operation);
    if (detail != null) {
      buffer.append(" ");
      buffer.append(detail.toString());
    }
    return buffer.toString();
  }

  /**
   * Traces the sql query
   */
  public void traceQuery(int level, String operation) {
    traceQuery(level, operation, level == TRL_QUERY ? this.text : null);
    queryStartTime = System.currentTimeMillis();
  }

  /**
   * Traces the sql query duration
   */
  public void traceTimer(int level, String operation) {
    traceTimer(level, operation, queryStartTime);
  }


  /*
   * ----------------------------------------------------------------------
   * PRIVATE METHODS
   * ----------------------------------------------------------------------
   */

  /*
   * Builds the query text.
   */
  private void buildText(String format) {
    final int           IN_FORMAT	= 1;	// in format string
    final int           IN_QUOTE	= 2;	// in quoted string
    final int           IN_SQL          = 3;	// expand param to SQL
    final int           IN_TEXT         = 4;	// expand param to text
    final int           AT_END          = 5;	// end of string

    final char[]	fpChars = format.toCharArray();
    int                 fp = 0;
    final StringBuffer  buffer = new StringBuffer((int)(fpChars.length * 1.5));
    int                 state;

    state = IN_FORMAT;

    do {
      switch (state) {
      case IN_FORMAT:
        if (fp == fpChars.length) {
          text = buffer.toString();
          state = AT_END;
        } else {
          switch (fpChars[fp]) {
          case '\'':
            buffer.append(fpChars[fp++]);
            state = IN_QUOTE;
            break;

          case '#':
            fp += 1;
            state = IN_SQL;
            break;

          case '$':
            fp += 1;
            state = IN_TEXT;
            break;

          default:
            buffer.append(fpChars[fp++]);
          }
        }
        break;

      case IN_QUOTE:
        if (fp == fpChars.length) {
          throw new InconsistencyException("unexpected end of query");
        }
        switch (fpChars[fp]) {
        case '\'':
          buffer.append(fpChars[fp++]);
          state = IN_FORMAT;
          break;

        default:
          buffer.append(fpChars[fp++]);
        }
        break;

      case IN_SQL:
      case IN_TEXT:
        {
          int	parno = 0;

          while (fp < fpChars.length  && fpChars[fp] >= '0' && fpChars[fp] <= '9') {
            parno = 10 * parno + (fpChars[fp++] - '0');
          }

          if (state == IN_SQL) {
            getSql(parno - 1, buffer);
          } else {
            getText(parno - 1, buffer);
          }
          state = IN_FORMAT;
        }
        break;
      }
    } while (state != AT_END);

    // release parameters
    pars = new LinkedList();
  }

  /**
   *
   */
  private void getText(int param, StringBuffer buffer) {
    Object      parameter = pars.get(param);

    if (parameter != null) {
      buffer.append(parameter.toString());
    }
  }

  /**
   *
   */
  private void getSql(int param, StringBuffer buffer) {
    Object	parameter = pars.get(param);
    String	localText;

    if (parameter == null) {
      localText = "NULL";
    } else if (parameter instanceof Fixed) {
      localText = KopiUtils.toSql((Fixed)parameter);
    } else if (parameter instanceof Boolean) {
      localText = KopiUtils.toSql((Boolean)parameter);
    } else if (parameter instanceof Date) {
      localText = KopiUtils.toSql((Date)parameter);
    } else if (parameter instanceof Integer) {
      localText = KopiUtils.toSql((Integer)parameter);
    } else if (parameter instanceof String) {
      localText = KopiUtils.toSql((String)parameter);
    } else if (parameter instanceof Time) {
      localText = KopiUtils.toSql((Time)parameter);
    } else if (parameter instanceof Month) {
      localText = KopiUtils.toSql((Month)parameter);
    } else if (parameter instanceof Week) {
      localText = KopiUtils.toSql((Week)parameter);
    } else {
      throw new InconsistencyException("undefined parameter type " + parameter.getClass());
    }

    buffer.append(localText);
  }

  /**
   * Creates a preparedStatement and add the large objects to it.
   *
   * @return the preparedStatement
   */
  private PreparedStatement createFilledPreparedStatement(String text)
    throws SQLException
  {
    PreparedStatement   result;

    result = conn.prepareStatement(text);
    try {
      int               index = 1;
      ListIterator      iterator = lobs.listIterator();

      while (iterator.hasNext()) {
        LargeObject     lob = (LargeObject)iterator.next();

        lob.addToPreparedStatement(result, index);
        index++;
      }
    } catch (IOException e) {
      throw new InconsistencyException(e.getMessage());
    }

    return result;
  }

  // ----------------------------------------------------------------------
  // CONSTANTS
  // ----------------------------------------------------------------------

  public static final int       TRL_QUERY               = 0;
  public static final int       TRL_FETCH               = 1;
  public static final int       TRL_TIMER               = 2;
  private static final int      TRL_MAX                 = TRL_TIMER;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static int            traceLevel = 0;

  private LinkedList		pars;
  private String 		text;
  private LinkedList		lobs;

  private Connection		conn;
  private Statement		stmt;
  private ResultSet		rset;
  private String                name;
  private long                  queryStartTime;

  private static int		nextCursorId = 0;

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  private static class LargeObject {
    public LargeObject(InputStream stream, boolean isBinary) {
      this.stream = stream;
      this.isBinary = isBinary;
    }

    public void addToPreparedStatement(PreparedStatement stmt,
                                       int index)
      throws IOException, SQLException
    {
      if (stream == null) {
        if (isBinary) {
          stmt.setNull(index, Types.BLOB);
        } else {
          stmt.setNull(index, Types.CLOB);
        }
      } else {
        if (isBinary) {
          stmt.setBinaryStream(index,
                               stream,
                               stream.available());
        } else {
          stmt.setAsciiStream(index,
                              stream,
                              stream.available());
        }
      }
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private final InputStream    stream;
    private final boolean        isBinary;
  }
}
