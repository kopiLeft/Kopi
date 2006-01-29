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
import java.util.StringTokenizer;

import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.NotNullDate;
import com.kopiright.xkopi.lib.type.NotNullWeek;
import com.kopiright.xkopi.lib.type.Week;

public class VDateField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VDateField() {
    super(10, 1);
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new Date[2 * block.getBufferSize()];
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return true;
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("date-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Date");
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VDateColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    if (s.length() > 10) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (!isDateChar(s.charAt(i))) {
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
    String	s = (String)o;

    if (s.equals("")) {
      setNull(block.getActiveRecord());
    } else {
      parseDate(s);
    }
  }

  private static int stringToInt(String in) {
    try {
      return Integer.valueOf(in).intValue();
    } catch (Exception e) {
      return -1;
    }
  }

  private void parseDate(String s) throws VFieldException {
    int                 day = 0;
    int                 month = 0;
    int                 year = -2;
    StringTokenizer     tokens = new StringTokenizer(s, "/.#");

    if (!tokens.hasMoreTokens()) {
      throw new VFieldException(this, Message.getMessage("date_format"));
    }
    day = stringToInt(tokens.nextToken());
    if (tokens.hasMoreTokens()) {
      month = stringToInt(tokens.nextToken());
    }
    if (tokens.hasMoreTokens()) {
      year = stringToInt(tokens.nextToken());
    }
    if (tokens.hasMoreTokens() || day == -1 || month == -1 || year == -1) {
      throw new VFieldException(this, Message.getMessage("date_format"));
    }

    if (month == 0) {
      NotNullDate	now = NotNullDate.now();
      month = now.getMonth();
      year  = now.getYear();
    } else if (year == -2) {
      NotNullDate	now = NotNullDate.now();
      year  = now.getYear();
    } else if (year < 50) {
      year += 2000;
    } else if (year < 100) {
      year += 1900;
    } else if (year < 1000) {
      // less than 4 digits cause an error in database while paring the 
      // sql statement
      throw new VFieldException(this, Message.getMessage("date_format"));      
    }

    // INVERSE DAY AND MONTH FOR US DATE  ENGLISH USED FOR THE MOMENT SO TBR
    if (java.util.Locale.getDefault().equals(java.util.Locale.ENGLISH) ||
	java.util.Locale.getDefault().equals(java.util.Locale.US)) {
      int i = day;
      day = month;
      month = i;
    }

    if (!isDate(day, month, year)) {
      throw new VFieldException(this, Message.getMessage("date_format"));
    }

    setDate(block.getActiveRecord(), new NotNullDate(year, month, day));
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setDate(r, null);
  }

  /**
   * Sets the field value of given record to a date value.
   */
  public void setDate(int r, Date v) {
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
    setDate(r, (Date)v);
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
      return query.getDate(column);
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
  public Date getDate(int r) {
    return (Date) getObject(r);
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
    if (value[r] == null) {
      return "";
    } else {
      return toText(value[r]);
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return value[r] == null ? "NULL" : com.kopiright.xkopi.lib.base.KopiUtils.toSql(value[r]);
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
  protected String formatDate(Date value) {
    return toText(value);
  }

  /**
   *
   */
  public static String toText(Date value) {
    return value.toString();
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private boolean isDate(int d, int m, int y) {
    if (y < 1 || m < 1 || m > 12 || d < 1) {
      return false;
    } else {
      switch (m) {
      case 2:
	return d <= (isLeapYear(y) ? 29 : 28);

      case 4:
      case 6:
      case 9:
      case 11:
	return d <= 30;

      default:
	return d <= 31;
      }
    }
  }

  private boolean isLeapYear(int year) {
    return ((year % 4) == 0) && (((year % 100) != 0) || ((year % 400) == 0));
  }

  private boolean isDateChar(char c) {
    return ((c >= '0') && (c <= '9')) || (c == '.') || (c == '/');
  }

  // REPLACED BY fillField
//   /**
//    * autofill
//    * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may occur in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     if (list != null) {
//       super.autofill(showDialog, gotoNextField);
//     } else {
//       boolean force = false;

//       try {
// 	String	text = (String)getDisplayedValue();
// 	checkType(text);
// 	force = text == null || getText() == null || getText().equals("") || (!text.equals(getText()));
//       } catch (Exception e) {
// 	force = true;
//       }
//       if (!showDialog || force) {
// 	setDate(Date.now());
//       } else {
// 	setDate(DateChooser.getDate(getForm().getDisplay(), getDisplay(), getDate()));
//       }
//       if (gotoNextField) {
// 	getBlock().gotoNextField();
//       }
//     }
//   }

  /**
   * autofill
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may occur in gotoNextField
   */
  public boolean fillField(PredefinedValueHandler handler) throws VException {
    int         record = block.getActiveRecord();

    if (list != null) {
      return super.fillField(handler);
    } else {
      boolean   force = false;

      try {
	String	oldText;
        String  newText;

        oldText = (String)getDisplayedValue(true);
	checkType(oldText);
        newText = getText(block.getActiveRecord());
	force = oldText == null || newText == null || newText.equals("") || !oldText.equals(newText);
      } catch (Exception e) {
	force = true;
      }
      if (handler == null || force) {
	setDate(record, Date.now());
      } else {
	setDate(record, handler.selectDate(getDate(record)));
      }
      return true;
    }
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  public boolean hasNextPreviousEntry() {
    return true;
  }

  /**
   * Checks that field value exists in list
   */
  protected void enumerateValue(boolean desc) throws VException {
    int         record = block.getActiveRecord();

    if (list != null) {
      super.enumerateValue(desc);
    } else if (isNull(record)) {
      autofill();
    } else {
      // try to read date
      try {
	checkType(getText(record));
      } catch (VException e) {
	// not valid, get now
	setDate(record, Date.now());
      }
      setDate(record, getDate(record).add(desc ? -1 : 1));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Date[]			value;
}
