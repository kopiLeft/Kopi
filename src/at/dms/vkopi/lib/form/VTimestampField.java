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

package at.dms.vkopi.lib.form;

import java.sql.SQLException;

import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.visual.VException;
import at.dms.xkopi.lib.base.KopiUtils;
import at.dms.xkopi.lib.base.Query;
import at.dms.xkopi.lib.type.NotNullTimestamp;
import at.dms.xkopi.lib.type.Timestamp;

/**
 * !!! NEED COMMENTS
 * default values
 */
public class VTimestampField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VTimestampField() {
    super(10 + 1 + 9 + 3 + 3, 1); // yyyy-MM-dd hh:mm:ss.000000
  }

  /**
   * !!! NEED COMMENTS
   */
  public boolean hasAutofill() {
    return true;
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("timestamp-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Timestamp");
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  public boolean hasNextPreviousEntry() {
    return true;
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new Timestamp[2 * block.getBufferSize()];
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VTimestampColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    return true; // !!! laurent : TO MODIFY
  }

  /**
   * verify that value is valid (on exit)
   * @exception	at.dms.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(Object o) throws VException {
    int         record = block.getActiveRecord();

    if (((String)o).equals("")) {
      setNull(record);
    } else {
      setTimestamp(record, NotNullTimestamp.now()); // !!! laurent : TO MODIFY
    }
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setTimestamp(r, null);
  }

  /**
   * Sets the field value of given record to a timestamp value.
   */
  public void setTimestamp(int r, Timestamp v) {
    if (isChangedUI() 
        || (value[r] == null && v != null)
        || (value[r] != null && !value[r].equals(v))) {
      // trails (backup) the record if necessary
      trail(r);
      // set value in the defined row
      value[r] = v;
      // inform that value has changed
      setChanged(r);
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(int r, Object v) {
    setTimestamp(r, (Timestamp)v);
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public Object retrieveQuery(Query query, int column)
    throws SQLException
  {
    if (query.isNull(column)) {
      return null;
    } else {
      return query.getTimestamp(column);
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a timestamp value.
   */
  public Timestamp getTimestamp(int r) {
    return value[r];
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return getTimestamp(r);
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    return value[r] == null ? VConstants.EMPTY_TEXT : value[r].toString();
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return KopiUtils.toSql(value[r]);
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    value[t] = value[f];
  }

  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (list == null) {
      setTimestamp(block.getActiveRecord(), Timestamp.now());
      return true;
    } else {
      return super.fillField(handler);
    }
  }



  /**
   * Checks that field value exists in list
   */
  protected void enumerateValue(boolean desc) throws VException {
    int         record = block.getActiveRecord();

    if (list != null) {
      super.enumerateValue(desc);
    } else if (isNull(record)) {
      setTimestamp(record, Timestamp.now());
    } else {
      // try to read timestamp
      try {
	checkType(getText(record));
      } catch (VException e) {
	// not valid, get now
	setTimestamp(record, Timestamp.now());
      }
      setTimestamp(record, getTimestamp(record).add(desc ? -1 : 1));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Timestamp[]		value;
}
