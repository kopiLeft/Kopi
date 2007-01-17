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

package com.kopiright.vkopi.lib.form;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.list.VMonthColumn;
import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.Month;
import com.kopiright.xkopi.lib.type.NotNullMonth;

public class VMonthField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VMonthField() {
    super(7, 1);
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return true;
  }

  /**
   * Returns true if this field implements "enumerateValue"
   */
  public boolean hasNextPreviousEntry() {
    return true;
  }

  /**
   * Just after loading, construct record
   */
  public void build() {
    super.build();
    value = new Month[2 * block.getBufferSize()];
  }

  /**
   * Returns the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("month-type-field");
  }

  /**
   * Return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Month");
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VMonthColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    if (s.length() > 7) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (!isMonthChar(s.charAt(i))) {
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

    String s = (String)o;
    if (s.equals("")) {
      setNull(record);
    } else {
      if (s.indexOf(".") != -1 && s.indexOf(".") == s.lastIndexOf(".")) {
	// one "." and only one
	try {
	  int		month = Integer.parseInt(s.substring(0, s.indexOf(".")));
	  int		year  = Integer.parseInt(s.substring(s.indexOf(".") + 1));

	  if (year < 50) {
	    year += 2000;
	  } else if (year < 100) {
	    year += 1900;
	  }

	  if (isMonth(month, year)) {
	    setMonth(record, new NotNullMonth(year, month));
	  } else {
	    throw new VFieldException(this, Message.getMessage("month_format"));
	  }
	} catch (Exception e) {
	  throw new VFieldException(this, Message.getMessage("month_format"));
	}
      } else if (s.indexOf(".") == -1) {
	// just the month, complete
	try {
	  int		month = Integer.parseInt(s);
	  int		year = new GregorianCalendar().get(Calendar.YEAR);

	  if (isMonth(month, year)) {
	    setMonth(record, new NotNullMonth(year, month));
	  } else {
	    throw new VFieldException(this, Message.getMessage("month_format"));
	  }
	} catch (Exception e) {
	  throw new VFieldException(this, Message.getMessage("month_format"));
	}
      } else {
	throw new VFieldException(this, Message.getMessage("month_format"));
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
    setMonth(r, null);
  }

  /**
   * Sets the field value of given record to a month value.
   */
  public void setMonth(int r, Month v) {
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
    setMonth(r, (Month)v);
  }

// graf 010512 : remove this
//  /**
//   * Sets the field value of given record to a int value.
//   */
//  public void setInt(int r, Integer v) {
//    setMonth(r, v == null ? null : new NotNullMonth(v.intValue()));
//  }
//
//  /**
//   * Sets the field value of given record.
//   * Warning:	This method will become inaccessible to kopi users in next release
//   * @kopi	inaccessible
//   */
//  public void setObject(int r, Object v) {
//    // !!! WHAT A KLUDGE !!! graf 990406
//    if (v == null) {
//      setNull(r);
//    } else if (v instanceof Month) {
//      setMonth(r, (Month)v);
//    } else if (v instanceof Date) {
//      setMonth(r, new NotNullMonth((Date)v));
//    } else {
//      throw new InconsistencyException("BAD TYPE: " + v);
//    }
//  }

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
      return query.getMonth(column);
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a date value.
   */
  public Month getMonth(int r) {
    return (Month) getObject(r);
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
    return value[r] == null ? "" : value[r].toString();
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return com.kopiright.xkopi.lib.base.KopiUtils.toSql(value[r]);
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    value[t] = value[f];
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a date value wrt the field type.
   */
  protected String formatMonth(Month value) {
    return value.toString();
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private boolean isMonth(int m, int y) {
    if (y < 1 || m < 1 || m > 12) {
      return false;
    }

    return true;
  }

  private boolean isMonthChar(char c) {
    return ((c >= '0') && (c <= '9')) || (c == '.');
  }

//   /**
//    * autofill
//    * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     if (list == null) {
//       setMonth(Month.now());
//       if (gotoNextField) {
// 	getBlock().gotoNextField();
//       }
//     } else {
//       super.autofill(showDialog, gotoNextField);
//     }
//   }

  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (list == null) {
      setMonth(block.getActiveRecord(), Month.now());
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
      setMonth(record, Month.now());
    } else {
      // try to read date
      try {
	checkType(getText(record));
      } catch (VException e) {
	// not valid, get now
	setMonth(record, Month.now());
      }

      setMonth(record, getMonth(record).add(desc ? -1 : 1));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Month[]		value;
}
