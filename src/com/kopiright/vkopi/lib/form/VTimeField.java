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

package com.kopiright.vkopi.lib.form;

import java.sql.SQLException;

import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.list.VTimeColumn;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.xkopi.lib.base.KopiUtils;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.NotNullTime;
import com.kopiright.xkopi.lib.type.Time;

/**
 * !!! NEED COMMENTS
 * default values
 */
@SuppressWarnings("serial")
public class VTimeField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VTimeField() {
    super(5, 1);
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
    return VlibProperties.getString("time-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Time");
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
    value = new Time[2 * block.getBufferSize()];
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VTimeColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    if (s.length() > 5) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (! isTimeChar(s.charAt(i))) {
	return false;
      }
    }
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(Object o) throws VException {
    int         record = block.getActiveRecord();

    if (((String)o).equals("")) {
      setNull(record);
    } else {
      int	hours = -1;
      int	minutes	= 0;
      String	buffer = (String)o + '\0';
      int	bp = 0;
      int	state;

      for (state = 1; state > 0; bp += 1) {
	switch (state) {
	case 1: /* The first hours' digit */
	  if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	    hours = buffer.charAt(bp) - '0';
	    state = 2;
	  } else if (buffer.charAt(bp) == '\0') {
	    state = 0;
	  } else {
	    state = -1;
	  }
	  break;

	case 2: /* The second hours' digit */
	  if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	    hours = 10*hours + (buffer.charAt(bp) - '0');
	    state = 3;
	  } else if (buffer.charAt(bp) == ':') {
	    state = 4;
	  } else if (buffer.charAt(bp) == '\0') {
	    state = 0;
	  } else {
	    state = -1;
	  }
	  break;

	case 3: /* The point between hours and minutes */
	  if (buffer.charAt(bp) == ':') {
	    state = 4;
	  } else if (buffer.charAt(bp) == '\0') {
	    state = 0;
	  } else {
	    state = -1;
	  }
	  break;

	case 4: /* The first minutes' digit */
	  if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	    minutes = buffer.charAt(bp) - '0';
	    state = 5;
	  } else if (buffer.charAt(bp) == '\0') {
	    state = 0;
	  } else {
	    state = -1;
	  }
	  break;

	case 5: /* The second minutes' digit */
	  if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	    minutes = 10*minutes + (buffer.charAt(bp) - '0');
	    state = 6;
	  } else {
	    state = -1;
	  }
	  break;

	case 6: /* The end */
	  if (buffer.charAt(bp) == '\0') {
	    state = 0;
	  } else {
	    state = -1;
	  }
	}
      }

      if (state == -1) {
	throw new VFieldException(this, MessageCode.getMessage("VIS-00007"));
      }

      if (hours == -1) {
	setNull(record);
      } else {
	if (! isTime(hours, minutes)) {
	  throw new VFieldException(this, MessageCode.getMessage("VIS-00007"));
	}

	setTime(record, new NotNullTime(hours, minutes));
      }
    }
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setTime(r, null);
  }

  /**
   * Sets the field value of given record to a time value.
   */
  public void setTime(int r, Time v) {
    if (isChangedUI() 
        || value[r] == null 
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
    setTime(r, (Time)v);
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
      return query.getTime(column);
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a time value.
   */
  public Time getTime(int r) {
    return (Time) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r];
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

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private boolean isTime(int h, int m) {
    return h >= 0 && h < 24 && m >= 0 && m < 60;
  }

  private boolean isTimeChar(char c) {
    return (c >= '0' && c <= '9') || c == ':';
  }

//   /**
//    * autofill
//    * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may occur in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     if (list == null) {
//       setTime(Time.now());
//       if (gotoNextField) {
// 	getBlock().gotoNextField();
//       }
//     } else {
//       super.autofill(showDialog, gotoNextField);
//     }
//   }
  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (list == null) {
      setTime(block.getActiveRecord(), Time.now());
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
      setTime(record, Time.now());
    } else {
      // try to read time
      try {
	checkType(getText(record));
      } catch (VException e) {
	// not valid, get now
	setTime(record, Time.now());
      }
      setTime(record, getTime(record).add(desc ? -1 : 1));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Time[]		value;
}
