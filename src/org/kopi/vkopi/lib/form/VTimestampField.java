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

package org.kopi.vkopi.lib.form;

import java.sql.SQLException;

import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.list.VTimestampColumn;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.base.KopiUtils;
import org.kopi.xkopi.lib.base.Query;
import org.kopi.xkopi.lib.type.Timestamp;

/**
 * !!! graf 20100914 complete input methods
 */
@SuppressWarnings("serial")
public class VTimestampField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VTimestampField() {
    super(10 + 1 + 8, 1); // yyyy-MM-dd hh:mm:ss
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
    return VlibProperties.getString("Timestamp");
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
  
  public boolean isNumeric() {
    return true;
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
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(int rec, Object o) throws VException {
    if (((String)o).equals("")) {
      setNull(rec);
    } else {
      setTimestamp(rec, Timestamp.now()); // !!! laurent : TO MODIFY
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
    return (Timestamp) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r];
  }
  
  @Override
  public String toText(Object o) {
    if (o == null) {
      return VConstants.EMPTY_TEXT;
    } else {
      String            text;

      text = o.toString();
      // this is work around to display the timestamp in yyyy-MM-dd hh:mm:ss format
      // The proper way is to change the method Timestamp#toString(Locale) but this
      // will affect the SQL representation of the timestamp value.
      return text.substring(0, Math.min(getWidth(), text.length()));
    }
  }
  
  @Override
  public Object toObject(String s) throws VException {
    if (s.equals("")) {
      return null;
    } else {
      return Timestamp.parse(s, "yyyy-MM-dd HH:mm:ss"); // !!! laurent : TO MODIFY
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    if (value[r] == null) {
      return VConstants.EMPTY_TEXT;
    } else {
      String            text;

      text = value[r].toString();
      // this is work around to display the timestamp in yyyy-MM-dd hh:mm:ss format
      // The proper way is to change the method Timestamp#toString(Locale) but this
      // will affect the SQL representation of the timestamp value.
      return text.substring(0, Math.min(getWidth(), text.length()));
    }
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
    Timestamp           oldValue;
    
    oldValue = value[t];
    value[t] = value[f];
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < getBlock().getBufferSize()
        && ((oldValue != null && value[t] == null)
            || (oldValue == null && value[t] != null)
            || (oldValue != null && !oldValue.equals(value[t]))))
    {
      fireValueChanged(t);
    }
  }
  
  /**
   * Returns the data type handled by this field.
   */
  public Class getDataType() {
    return Timestamp.class;
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
