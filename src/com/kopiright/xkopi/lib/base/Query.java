/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;
import java.util.ListIterator;

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
      stmt = conn.getJDBCConnection().createStatement();

      if (supportsCursorNames()) {
        name = "C" + nextCursorId++;
	stmt.setCursorName(name);
      }
      buildText(format, "OPEN " + name);

      rset = stmt.executeQuery(conn.convertSql(text));
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   *
   */
  public boolean next() throws DBException {
    if (Query.getTraceLevel() >= TRL_ALL) {
      Query.traceQuery("QUERY " + name + " FETCH", rset);
    }
    try {
      return rset.next();
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   *
   */
  public boolean previous() throws DBException {
    if (Query.getTraceLevel() >= TRL_ALL) {
      Query.traceQuery("QUERY " + name + " FETCH", rset);
    }
    try {
      return rset.previous();
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   *
   */
  public boolean hasMore() throws DBException {
    if (Query.getTraceLevel() >= TRL_ALL) {
      Query.traceQuery("QUERY " + name + " HAS MORE", rset);
    }
    try {
      return rset.isLast();
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   * Positioned update
   */
  public int update(String format) throws DBException {
    if (! supportsCursorNames()) {
      throw new DBRuntimeException("operation not supported by JDBC driver");
    }

    try {
      Statement	updater;
      int		count;

      buildText(format, "UPDPOS");

      if (!lobs.isEmpty()) {
	updater = createFilledPreparedStatement(text + " WHERE CURRENT OF " + rset.getCursorName());

        count = ((PreparedStatement)updater).executeUpdate();
        updater.close();
      } else {
	updater = conn.getJDBCConnection().createStatement();
	count = updater.executeUpdate(conn.convertSql(text + " WHERE CURRENT OF " + rset.getCursorName()));
	updater.close();
      }
      return count;
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   * Positioned delete
   */
  public int delete(String format) throws DBException {
    if (! supportsCursorNames()) {
      throw new DBRuntimeException("operation not supported by JDBC driver");
    }

    try {
      Statement	updater;
      int		count;

      buildText(format, "DELPOS");

      updater = conn.getJDBCConnection().createStatement();
      count = updater.executeUpdate(conn.convertSql(text + " WHERE CURRENT OF " + rset.getCursorName()));
      updater.close();

      return count;
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   *
   */
  public void close() throws DBException {
    try {
      if (stmt != null) {
	stmt.close();
      }
      stmt = null;
      rset = null;
      buildText("", "CLOSE " + name);
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   *
   */
  public int run(String format) throws DBException {
    try {
      buildText(format, "RUN");

      if (!lobs.isEmpty()) {
        stmt = createFilledPreparedStatement(text);

	return ((PreparedStatement)stmt).executeUpdate();
      } else {
	stmt = conn.getJDBCConnection().createStatement();
	return stmt.executeUpdate(conn.convertSql(text));
      }
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (primitive type)
  // ----------------------------------------------------------------------

  public boolean getBoolean(int pos) throws SQLException {
    boolean tmp = rset.getBoolean(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public byte getByte(int pos) throws SQLException {
    byte tmp = rset.getByte(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public short getShort(int pos) throws SQLException {
    short tmp = rset.getShort(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public int getInt(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public NotNullFixed getFixed(int pos) throws SQLException {
    java.math.BigDecimal        tmp;

    tmp = rset.getBigDecimal(pos);

    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullFixed(tmp);
  }

  public float getFloat(int pos) throws SQLException {
    float tmp = rset.getFloat(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public double getDouble(int pos) throws SQLException {
    double tmp = rset.getDouble(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public char getChar(int pos) throws SQLException {
    String tmp = rset.getString(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp.charAt(0);
  }

  public String getString(int pos) throws SQLException {
    return rset.getString(pos);
  }

  public NotNullMonth getMonth(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullMonth(tmp / 100, tmp % 100);
  }

  public NotNullTime getTime(int pos) throws SQLException {
    java.sql.Time tmp = rset.getTime(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullTime(tmp);
  }

  public NotNullTimestamp getTimestamp(int pos) throws SQLException {
    java.sql.Timestamp tmp = rset.getTimestamp(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullTimestamp(tmp);
  }

  public NotNullDate getDate(int pos) throws SQLException {
    java.sql.Date tmp = rset.getDate(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullDate(tmp);
  }

  public NotNullWeek getWeek(int pos) throws SQLException {
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
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
	throw new DBInvalidDataException(e);
      } catch (ClassNotFoundException e) {
	throw new DBInvalidDataException(e);
      } catch (IOException e) {
	throw new DBInvalidDataException(e);
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
      // graf 2004.01.24: breaks list fixed(14, 0)
      // !!! A HACK FOR ORACLE
      // if the scale is -1, the scale is not precised.
      // else we use integer when the scale is 0 and the precision is less or equal to 10.
      if (rset.getMetaData().getScale(pos) != 0 || rset.getMetaData().getPrecision(pos) > 10) {
        return new NotNullFixed((java.math.BigDecimal)obj);
      } else {
        return new Integer(((java.math.BigDecimal)obj).intValue());
      }
      // graf 2004.01.24: breaks list fixed(14, 0)
      //      return new NotNullFixed((java.math.BigDecimal)obj);
    } else if (obj instanceof java.sql.Date) {
      return new NotNullDate((java.sql.Date)obj);
    } else if (obj instanceof java.sql.Time) {
      return new NotNullTime((java.sql.Time)obj);
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
    switch (level) {
    case TRL_NONE:
    case TRL_QUERIES:
    case TRL_ALL:
      traceLevel = level;
      break;
    default:
      throw new IllegalArgumentException("Bad level value: " + level);
    }
  }

  /**
   * Gets the trace level
   *
   * @return    the trace level, see Query.TRL_
   */
  public static int getTraceLevel() {
    return traceLevel;
  }

  /**
   *
   */
  public static void traceQuery(String operation, Object detail) {
    System.out.print(System.currentTimeMillis());
    System.out.print(" ");
    System.out.print(operation);
    System.out.print(":" );
    System.out.println(detail.toString());
  }

  /*
   * ----------------------------------------------------------------------
   * PRIVATE METHODS
   * ----------------------------------------------------------------------
   */

  /*
   * Returns true if the underlying JDBC connection supports cursor names
   */
  private boolean supportsCursorNames() {
    try {
      return conn.getJDBCConnection().getMetaData().getMaxCursorNameLength() > 0;
    } catch (SQLException e) {
      return false;
    }
  }

  /*
   * Builds the query text.
   */
  private void buildText(String format, String type) {
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

    if (Query.getTraceLevel() >= TRL_QUERIES) {
      Query.traceQuery(type, text);
    }
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
    String	text;

    if (parameter == null) {
      text = "NULL";
    } else if (parameter instanceof Fixed) {
      text = KopiUtils.toSql((Fixed)parameter);
    } else if (parameter instanceof Boolean) {
      text = KopiUtils.toSql((Boolean)parameter);
    } else if (parameter instanceof Date) {
      text = KopiUtils.toSql((Date)parameter);
    } else if (parameter instanceof Integer) {
      text = KopiUtils.toSql((Integer)parameter);
    } else if (parameter instanceof String) {
      text = KopiUtils.toSql((String)parameter);
    } else if (parameter instanceof Time) {
      text = KopiUtils.toSql((Time)parameter);
    } else if (parameter instanceof Month) {
      text = KopiUtils.toSql((Month)parameter);
    } else if (parameter instanceof Week) {
      text = KopiUtils.toSql((Week)parameter);
    } else {
      throw new InconsistencyException("undefined parameter type " + parameter.getClass());
    }

    buffer.append(text);
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

    result = conn.getJDBCConnection().prepareStatement(text);
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

  public static final int       TRL_NONE                = 0;
  public static final int       TRL_QUERIES             = 1;
  public static final int       TRL_ALL                 = 2;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static int            traceLevel = TRL_NONE;

  private LinkedList		pars;
  private String 		text;
  private LinkedList		lobs;

  private Connection		conn;
  private Statement		stmt;
  private ResultSet		rset;
  private String                name;

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
