/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.xkopi.lib.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import at.dms.util.base.InconsistencyException;
import at.dms.util.base.NotImplementedException;
import at.dms.xkopi.lib.type.Date;
import at.dms.xkopi.lib.type.Fixed;
import at.dms.xkopi.lib.type.Month;
import at.dms.xkopi.lib.type.NotNullDate;
import at.dms.xkopi.lib.type.NotNullFixed;
import at.dms.xkopi.lib.type.NotNullMonth;
import at.dms.xkopi.lib.type.NotNullTime;
import at.dms.xkopi.lib.type.NotNullTimestamp;
import at.dms.xkopi.lib.type.NotNullWeek;
import at.dms.xkopi.lib.type.Time;
import at.dms.xkopi.lib.type.Timestamp;
import at.dms.xkopi.lib.type.Week;

/**
 * Execute a query and maintain a pointer to the retreived data
 * this data can be accessed from java with the good primitive type.
 * Thanks to kopic overloaded operator it is completly transparent.
 * Warning: Trying to access to a null value as a primitve type will
 * throw a null pointer exception
 *
 * Rmq 1: All exception are cached in order to find from the vendor specific
 * tag which error it is and if possible we rethrow the good exception
 * (DBDeadLock, ...) else we rethrow a general DBException
 *
 * Rmq 2: This class is a runtime utility for the kopi files, it is not intented
 * to use them directly in Java. You may use them from Java but there is
 * not any documentation or help to do this.
 */
public class Cursor {

  protected Cursor() {
    this.name = "";
    this.rset = null;
    this.isFetched = false;
  }

  /**
   * Open the cursor with a sql statement (SELECT)
   */
  public void open(String sql, Connection conn)
    throws DBException
  {
    if (rset != null) {
      throw new DBCursorException("This cursor (" + this.name + ") is already opened");
    } else {
      try {
        stmt = conn.getJDBCConnection().createStatement();
        this.conn = conn;

        if (supportsCursorNames()) {
          this.name = "K" + nextCursorId++;
          stmt.setCursorName(name);
        }
        trace(Query.TRL_QUERIES, "OPEN", sql);

        rset = stmt.executeQuery(conn.convertSql(sql));
        //!!! GRAF 020811 : WORKAROUND FOR SAP DB BUG !!! CHANGE THIS
        rset.setFetchSize(1);
      } catch (SQLException exc) {
        throw conn.convertException(exc);
      }
    }
  }

  /**
   * Open the cursor with a sql statement (SELECT) but use default connection
   */
  public void openWOCursor(String sql) throws DBException {
    this.open(sql, this.conn);
  }

  // ----------------------------------------------------------------------
  // CONNECTION AND FETCH
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void setDefaultConnection(Connection conn) {
    this.conn = conn;
  }

  /**
   * Move to next row of result set
   */
  public boolean next() throws DBException {
    checkCursorIsOpened();
    try {
      trace(Query.TRL_ALL, "FETCH", rset);

      return (isFetched = rset.next());
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   * Update a row (DELETE, INSERT, UPDATE)
   */
  public void executeUpdate(String sql) throws DBException {
    try {
      trace(Query.TRL_QUERIES, "UPDATE", sql);

      if (stmt != null) {
	stmt.executeUpdate(conn.convertSql(sql));
      }
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   * Close this cursor
   */
  public void close() throws SQLException {
    try {
      if (stmt != null) {
        rset.close();
	stmt.close();
        rset = null;
        stmt = null;
        isFetched = false;
      }
      trace(Query.TRL_QUERIES, "CLOSE", "");
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   * Update positioned
   */
  public int update(String sql) throws DBException {
    return update(sql, null);
  }

  /**
   * Update positioned
   */
  // laurent 20021122 : This method only handles blobs. See to add clob support.
  public int update(String sql, Object[] blobs) throws DBException {
    try {
      if (! supportsCursorNames()) {
	throw new SQLException("operation not supported by JDBC driver");
      }
      trace(Query.TRL_QUERIES, "UPDATE", sql);

      PreparedStatement         updater;
      int                       count;

      sql += " WHERE CURRENT OF " + rset.getCursorName();
      updater = conn.getJDBCConnection().prepareStatement(conn.convertSql(sql));
      for (int i = 0; blobs != null && i < blobs.length; i++) {
        if (blobs[i] == null) {
          updater.setNull(i + 1, Types.BLOB);
        } else {
          byte[]        blob = (byte[])blobs[i];
          updater.setBinaryStream(i + 1, new ByteArrayInputStream(blob), blob.length);
        }
      }

      count = updater.executeUpdate();
      updater.close();

      return count;
    } catch (SQLException exc) {
      throw conn.convertException(exc);
    }
  }

  /**
   * Delete positioned
   */
  public int delete(String sql) throws DBException {
    return update(sql);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (primitive type)
  // ----------------------------------------------------------------------

  public boolean getBoolean(int pos) throws SQLException {
    checkCursorIsFetched();
    boolean tmp = rset.getBoolean(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public byte getByte(int pos) throws SQLException {
    checkCursorIsFetched();
    byte tmp = rset.getByte(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public short getShort(int pos) throws SQLException {
    checkCursorIsFetched();
    short tmp = rset.getShort(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public int getInt(int pos) throws SQLException {
    checkCursorIsFetched();
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public NotNullFixed getFixed(int pos) throws SQLException {
    checkCursorIsFetched();
    // laurent : Please use sapdb JDBC Driver >= 7.3.0.29
    java.math.BigDecimal        tmp = rset.getBigDecimal(pos);

    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullFixed(tmp);
  }

  public float getFloat(int pos) throws SQLException {
    checkCursorIsFetched();
    float tmp = rset.getFloat(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public double getDouble(int pos) throws SQLException {
    checkCursorIsFetched();
    double tmp = rset.getDouble(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp;
  }

  public char getChar(int pos) throws SQLException {
    checkCursorIsFetched();
    String tmp = rset.getString(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return tmp.charAt(0);
  }

  public String getString(int pos) throws SQLException {
    checkCursorIsFetched();
    return rset.getString(pos);
  }

  public NotNullMonth getMonth(int pos) throws SQLException {
    checkCursorIsFetched();
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullMonth(tmp / 100, tmp % 100);
  }

  public NotNullTime getTime(int pos) throws SQLException {
    checkCursorIsFetched();
    java.sql.Time tmp = rset.getTime(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullTime(tmp);
  }

  public NotNullTimestamp getTimestamp(int pos) throws SQLException {
    checkCursorIsFetched();
    java.sql.Timestamp tmp = rset.getTimestamp(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullTimestamp(tmp);
  }

  public NotNullDate getDate(int pos) throws SQLException {
    checkCursorIsFetched();
    java.sql.Date tmp = rset.getDate(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullDate(tmp);
  }

  public NotNullWeek getWeek(int pos) throws SQLException {
    checkCursorIsFetched();
    int tmp = rset.getInt(pos);
    if (rset.wasNull()) {
      throw new SQLException("null pointer exception at column " + pos);
    }
    return new NotNullWeek(tmp / 100, tmp % 100);
  }

  public Boolean getNullableBoolean(int pos) throws SQLException {
    checkCursorIsFetched();
    boolean tmp = rset.getBoolean(pos);
    return rset.wasNull() ? null : new Boolean(tmp);
  }

  public Byte getNullableByte(int pos) throws SQLException {
    checkCursorIsFetched();
    byte tmp = rset.getByte(pos);
    return rset.wasNull() ? null : new Byte(tmp);
  }

  public Short getNullableShort(int pos) throws SQLException {
    checkCursorIsFetched();
    short tmp = rset.getShort(pos);
    return rset.wasNull() ? null : new Short(tmp);
  }

  public Integer getNullableInt(int pos) throws SQLException {
    checkCursorIsFetched();
    int tmp = rset.getInt(pos);
    return rset.wasNull() ? null : new Integer(tmp);
  }

  public Fixed getNullableFixed(int pos) throws SQLException {
    checkCursorIsFetched();
    // laurent : Please use sapdb JDBC Driver >= 7.3.0.29
    java.math.BigDecimal        tmp = rset.getBigDecimal(pos);

    return rset.wasNull() ? null : new NotNullFixed(tmp);
  }

  public Float getNullableFloat(int pos) throws SQLException {
    checkCursorIsFetched();
    float tmp = rset.getFloat(pos);
    return rset.wasNull() ? null : new Float(tmp);
  }

  public Double getNullableDouble(int pos) throws SQLException {
    checkCursorIsFetched();
    double tmp = rset.getDouble(pos);
    return rset.wasNull() ? null : new Double(tmp);
  }

  public Character getNullableChar(int pos) throws SQLException {
    checkCursorIsFetched();
    String tmp = rset.getString(pos);
    return rset.wasNull() ? null : new Character(tmp.charAt(0));
  }

  public String getNullableString(int pos) throws SQLException {
    checkCursorIsFetched();
    return getString(pos);
  }

  public Time getNullableTime(int pos) throws SQLException {
    checkCursorIsFetched();
    java.sql.Time tmp = rset.getTime(pos);
    return rset.wasNull() ? null : new NotNullTime(tmp);
  }

  public Timestamp getNullableTimestamp(int pos) throws SQLException {
    checkCursorIsFetched();
    java.sql.Timestamp tmp = rset.getTimestamp(pos);
    return rset.wasNull() ? null : new NotNullTimestamp(tmp);
  }

  public Date getNullableDate(int pos) throws SQLException {
    checkCursorIsFetched();
    java.sql.Date tmp = rset.getDate(pos);
    return rset.wasNull() ? null : new NotNullDate(tmp);
  }

  public Month getNullableMonth(int pos) throws SQLException {
    checkCursorIsFetched();
    int tmp = rset.getInt(pos);
    return rset.wasNull() ? null : new NotNullMonth(tmp / 100, tmp % 100);
  }

  public Week getNullableWeek(int pos) throws SQLException {
    checkCursorIsFetched();
    int tmp = rset.getInt(pos);
    return rset.wasNull() ? null : new NotNullWeek(tmp / 100, tmp % 100);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS object
  // ----------------------------------------------------------------------

  /**
   * WARNING: You should read the entire data before accessing any other column
   */
  public InputStream getBinaryStream(int pos) throws SQLException {
    checkCursorIsFetched();
    return rset.getBinaryStream(pos);
  }

  /**
   * Reads the stream in a byte array
   */
  public byte[] getByteArray(int pos) throws SQLException {
    checkCursorIsFetched();
    try {
      InputStream	is = rset.getBinaryStream(pos);

      if (is == null) {
	return null;
      } else {
        byte[]                  buffer = new byte[1024];
        ByteArrayOutputStream   out = new ByteArrayOutputStream();
        int                     nread;

        while ((nread = is.read(buffer)) != -1) {
          out.write(buffer, 0, nread);
        }

	return out.toByteArray();
      }
    } catch (IOException e) {
      throw new InconsistencyException("INPUT STREAM BROKEN:" + e.getMessage());
    }
  }

  /**
   * Returns an object stored in database
   */
  public Object getNullableSerializedObject(int pos) throws SQLException {
    return getSerializedObject(pos);
  }

  /**
   * Returns an object stored in database
   */
  public Object getSerializedObject(int pos) throws SQLException {
    checkCursorIsFetched();
    InputStream is = rset.getBinaryStream(pos);

    if (is == null) {
      return null;
    } else {
      try {
	Object	obj = null;

	obj = new ObjectInputStream(is).readObject();
	is.close();

	return obj;
      } catch (OptionalDataException e1) {
	throw new DBInvalidDataException(e1);
      } catch (ClassNotFoundException e2) {
	throw new DBInvalidDataException(e2);
      } catch (IOException e3) {
	throw new DBInvalidDataException(e3);
      }
    }
  }

  // ----------------------------------------------------------------------
  // Kopi Serializable
  // ----------------------------------------------------------------------

  /**
   * We want to know if the object is null before create it !
   */
  public boolean isNull(int pos) throws SQLException {
    checkCursorIsFetched();
    cached = rset.getBinaryStream(pos);
    return cached == null;
  }

  /**
   * Reads the stream in a byte array
   */
  public byte[] getKopiSerializable(int pos) throws SQLException {
    checkCursorIsFetched();
    try {
      if (cached == null) {
	cached = rset.getBinaryStream(pos);
      }

      if (cached == null) {
	return null;
      } else {
	byte[]	b = new byte[cached.available()];

	cached.read(b);
	cached = null;
	return b;
      }
    } catch (IOException e) {
      throw new InconsistencyException("INPUT STREAM BROKEN:" + e.getMessage());
    }
  }

  public boolean rowFound() {
    return isFetched;
  }

  public int rowCount() {
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }

  public boolean isOpen() {
    return stmt != null && rset != null;
  }

  public Cursor createCopy() throws SQLException {
    throw new RuntimeException("SHOULD BE REDEFINED IN EACH CURSOR");
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private void checkCursorIsOpened() {
    if (!isOpen()) {
      throw new DBCursorException("The cursor is not opened.");
    }
  }

  private void checkCursorIsFetched() {
    if (!isFetched) {
      throw new DBCursorException("No value was fetched.");
    }
  }

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

  private void trace(int level, String action, Object obj) throws SQLException {
    if (Query.getTraceLevel() >= level) {
      Query.traceQuery("CURSOR " + name + " " + action, obj);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private InputStream		cached;
  private static int		nextCursorId = 0;
  private Connection		conn;
  private Statement		stmt;
  private String                name;

  protected ResultSet		rset;
  protected boolean             isFetched;
}
